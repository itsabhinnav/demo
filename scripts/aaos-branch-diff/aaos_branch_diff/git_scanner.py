from __future__ import annotations

import logging
import os
import re
from pathlib import Path
from typing import Callable, Optional

import git
from git import Repo

from .file_classifier import classify_file, detect_language, is_parseable_source, normalize_path
from .models import (
    ApiReference,
    BranchSnapshot,
    FileCategory,
    FileSnapshot,
    Language,
    ProjectSummary,
)
from .parsers import parse_java, parse_kotlin
from .platform_utils import decode_git_blob, find_git_executable

logger = logging.getLogger(__name__)

DEFAULT_MAX_FILE_BYTES = 2 * 1024 * 1024  # 2 MiB

# Paths typically not worth parsing in AAOS trees
DEFAULT_SKIP_GLOBS = (
    "**/build/**",
    "**/.gradle/**",
    "**/generated/**",
    "**/.idea/**",
    "**/node_modules/**",
)


class GitScannerError(Exception):
    """Raised when git operations fail with a user-friendly message."""


class GitScanner:
    def __init__(
        self,
        repo_path: str | Path,
        max_file_bytes: int = DEFAULT_MAX_FILE_BYTES,
        skip_globs: tuple[str, ...] = DEFAULT_SKIP_GLOBS,
        verbose: bool = False,
    ):
        self.repo_path = Path(repo_path).resolve()
        self.max_file_bytes = max_file_bytes
        self.skip_globs = skip_globs
        self.verbose = verbose
        self.warnings: list[str] = []
        self._file_cache: dict[str, dict[str, str]] = {}

        git_exe = find_git_executable()
        if not git_exe:
            raise GitScannerError(
                "git executable not found. Install Git and ensure it is on PATH.\n"
                "Windows: https://git-scm.com/download/win"
            )

        os.environ.setdefault("GIT_TERMINAL_PROMPT", "0")
        os.environ["GIT_PYTHON_GIT_EXECUTABLE"] = git_exe
        try:
            self.repo: Repo = Repo(str(self.repo_path))
        except git.exc.InvalidGitRepositoryError as exc:
            raise GitScannerError(f"Not a valid git repository: {self.repo_path}") from exc
        except git.exc.GitCommandError as exc:
            raise GitScannerError(f"Failed to open repository: {exc}") from exc

        if self.repo.bare:
            raise GitScannerError("Bare repositories are not supported.")

    def _warn(self, message: str) -> None:
        self.warnings.append(message)
        logger.warning(message)
        if self.verbose:
            print(f"Warning: {message}")

    def resolve_ref(self, ref: str) -> str:
        try:
            return self.repo.git.rev_parse("--verify", ref).strip()
        except git.exc.GitCommandError as exc:
            local = [h.name for h in self.repo.heads]
            remote = [r.name for r in self.repo.remotes]
            hint = ""
            if local:
                hint = f"\nLocal branches: {', '.join(local[:20])}"
                if len(local) > 20:
                    hint += f" … (+{len(local) - 20} more)"
            raise GitScannerError(
                f"Cannot resolve branch/ref '{ref}'. "
                f"Fetch it first (e.g. git fetch origin {ref}).{hint}"
            ) from exc

    def merge_base(self, ref_a: str, ref_b: str) -> str:
        try:
            base = self.repo.git.merge_base(ref_a, ref_b).strip()
            if not base:
                self._warn(
                    f"No merge-base between {ref_a} and {ref_b} "
                    "(unrelated histories?). Using empty base."
                )
                return "0000000"
            return base.splitlines()[0].strip()
        except git.exc.GitCommandError as exc:
            self._warn(f"merge-base failed: {exc}")
            return "0000000"

    def _should_skip(self, path: str) -> bool:
        p = normalize_path(path)
        for glob in self.skip_globs:
            # Simple glob: **/segment/**
            segment = glob.strip("*").strip("/")
            if segment and f"/{segment}/" in f"/{p}/":
                return True
        return False

    def list_files(self, ref: str, *, use_cache: bool = True) -> dict[str, str]:
        """Return path -> blob sha for all tracked files at ref."""
        if use_cache and ref in self._file_cache:
            return self._file_cache[ref]

        try:
            commit = self.repo.commit(ref)
        except (git.exc.BadName, ValueError) as exc:
            raise GitScannerError(f"Invalid commit for ref '{ref}'") from exc

        files: dict[str, str] = {}
        for item in commit.tree.traverse():
            if item.type != "blob":
                continue
            path = normalize_path(item.path)
            if self._should_skip(path):
                continue
            files[path] = item.hexsha

        if use_cache:
            self._file_cache[ref] = files
        return files

    def read_file(self, ref: str, path: str) -> Optional[str]:
        norm = normalize_path(path)
        try:
            raw = self.repo.git.show(f"{ref}:{norm}")
            if isinstance(raw, str):
                data = raw.encode("utf-8", errors="surrogateescape")
            else:
                data = raw
        except git.exc.GitCommandError:
            # Git on Windows may need forward slashes; retry with backslashes
            if os.name == "nt":
                try:
                    alt = norm.replace("/", "\\")
                    raw = self.repo.git.show(f"{ref}:{alt}")
                    data = raw.encode("utf-8", errors="surrogateescape") if isinstance(raw, str) else raw
                except git.exc.GitCommandError:
                    return None
            else:
                return None

        if len(data) > self.max_file_bytes:
            self._warn(f"Skipped large file ({len(data)} bytes): {norm}")
            return None

        return decode_git_blob(data)

    def diff_name_status(self, ref_a: str, ref_b: str) -> list[tuple[str, str, Optional[str]]]:
        try:
            raw = self.repo.git.diff("--name-status", "-M", ref_a, ref_b)
        except git.exc.GitCommandError as exc:
            raise GitScannerError(f"git diff failed between {ref_a} and {ref_b}: {exc}") from exc

        results: list[tuple[str, str, Optional[str]]] = []
        for line in raw.splitlines():
            if not line.strip():
                continue
            parts = line.split("\t")
            if len(parts) < 2:
                continue
            status_code = parts[0]
            if status_code.startswith(("R", "C")) and len(parts) >= 3:
                old_path, new_path = parts[1], parts[2]
                results.append((status_code[0], normalize_path(new_path), normalize_path(old_path)))
            else:
                results.append((status_code, normalize_path(parts[1]), None))
        return results

    def diff_stat(self, ref_a: str, ref_b: str) -> dict[str, tuple[int, int]]:
        try:
            raw = self.repo.git.diff("--numstat", ref_a, ref_b)
        except git.exc.GitCommandError:
            return {}
        stats: dict[str, tuple[int, int]] = {}
        for line in raw.splitlines():
            parts = line.split("\t")
            if len(parts) >= 3:
                ins = int(parts[0]) if parts[0] != "-" else 0
                dels = int(parts[1]) if parts[1] != "-" else 0
                stats[normalize_path(parts[2])] = (ins, dels)
        return stats

    def log_oneline(self, ref_a: str, ref_b: str, limit: int = 20) -> list[str]:
        try:
            raw = self.repo.git.log(f"{ref_a}..{ref_b}", "--oneline", f"-{limit}")
            return raw.splitlines()
        except git.exc.GitCommandError:
            return []

    def build_snapshot(
        self,
        ref: str,
        paths: Optional[set[str]] = None,
        progress: Callable[[int, int, str], None] | None = None,
    ) -> BranchSnapshot:
        commit = self.resolve_ref(ref)
        all_files = self.list_files(ref)
        if paths is not None:
            all_files = {p: sha for p, sha in all_files.items() if p in paths}

        project_packages = self._discover_packages(all_files.keys())
        try:
            branch = self.repo.git.rev_parse("--abbrev-ref", ref)
        except git.exc.GitCommandError:
            branch = ref[:12]
        snapshot = BranchSnapshot(branch=branch, commit=commit[:12], files={})

        items = sorted(all_files.items())
        total = len(items)
        for idx, (path, blob_sha) in enumerate(items, start=1):
            if progress:
                progress(idx, total, path)
            elif self.verbose and idx % 100 == 0:
                print(f"  Parsing {idx}/{total} files …")

            snapshot.files[path] = self._build_file_snapshot(
                ref, path, blob_sha, project_packages
            )
        return snapshot

    def _build_file_snapshot(
        self,
        ref: str,
        path: str,
        blob_sha: str,
        project_packages: set[str],
    ) -> FileSnapshot:
        category = classify_file(path)
        language = detect_language(path, category)
        content: str | None = None
        classes = []
        apis: list[ApiReference] = []
        line_count = 0

        try:
            content = self.read_file(ref, path)
            line_count = len(content.splitlines()) if content else 0

            if content and is_parseable_source(path, category):
                if language == Language.JAVA:
                    classes, apis = parse_java(content, path, project_packages)
                elif language == Language.KOTLIN:
                    classes, apis = parse_kotlin(content, path, project_packages)
            elif content and category == FileCategory.GRADLE:
                apis = self._parse_gradle_deps(content, path, project_packages)
        except Exception as exc:
            self._warn(f"Failed to parse {path}: {exc}")

        return FileSnapshot(
            path=path,
            category=category,
            language=language,
            exists=True,
            blob_sha=blob_sha,
            classes=classes,
            apis=apis,
            line_count=line_count,
        )

    def _discover_packages(self, paths) -> set[str]:
        packages: set[str] = set()
        for path in paths:
            m = re.search(
                r"src[/\\](?:main|test|androidTest)[/\\](?:java|kotlin)[/\\](.+)",
                normalize_path(path),
                re.I,
            )
            if m:
                pkg = m.group(1).rsplit("/", 1)[0].replace("/", ".")
                if pkg:
                    packages.add(pkg)
        return packages

    @staticmethod
    def _parse_gradle_deps(content: str, path: str, project_packages: set[str]) -> list[ApiReference]:
        from .parsers.java_parser import _classify_api

        apis: list[ApiReference] = []
        patterns = [
            re.compile(r"implementation\s*\(?['\"]([^'\"]+)['\"]"),
            re.compile(r"api\s*\(?['\"]([^'\"]+)['\"]"),
            re.compile(r"compileOnly\s*\(?['\"]([^'\"]+)['\"]"),
            re.compile(r"runtimeOnly\s*\(?['\"]([^'\"]+)['\"]"),
        ]
        seen: set[str] = set()
        for pat in patterns:
            for m in pat.finditer(content):
                coord = m.group(1)
                if ":" in coord:
                    group = coord.split(":")[0]
                    if group not in seen:
                        seen.add(group)
                        apis.append(
                            ApiReference(
                                qualified_name=coord,
                                kind=_classify_api(group, project_packages),
                                file_path=path,
                                line=0,
                                usage_context="gradle_dependency",
                            )
                        )
        return apis


def summarize_branch(snapshot: BranchSnapshot) -> ProjectSummary:
    summary = ProjectSummary()
    summary.total_files = len(snapshot.files)
    for f in snapshot.files.values():
        cat = f.category.value
        lang = f.language.value
        summary.by_category[cat] = summary.by_category.get(cat, 0) + 1
        summary.by_language[lang] = summary.by_language.get(lang, 0) + 1
        summary.class_count += len(f.classes)
        for c in f.classes:
            summary.method_count += len(c.methods)
        summary.api_count += len(f.apis)
    return summary
