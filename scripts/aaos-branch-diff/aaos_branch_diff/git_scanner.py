from __future__ import annotations

import re
from pathlib import Path
from typing import Optional

import git
from git import Repo

from .file_classifier import classify_file, detect_language, is_parseable_source, normalize_path
from .models import (
    ApiReference,
    BranchSnapshot,
    ChangeStatus,
    FileCategory,
    FileDiff,
    FileSnapshot,
    Language,
    ProjectSummary,
)
from .parsers import parse_java, parse_kotlin


class GitScanner:
    def __init__(self, repo_path: str | Path):
        self.repo_path = Path(repo_path).resolve()
        self.repo: Repo = Repo(str(self.repo_path))

    def resolve_ref(self, ref: str) -> str:
        return self.repo.git.rev_parse("--verify", ref)

    def merge_base(self, ref_a: str, ref_b: str) -> str:
        return self.repo.git.merge_base(ref_a, ref_b).strip()

    def list_files(self, ref: str) -> dict[str, str]:
        """Return path -> blob sha for all tracked files at ref."""
        commit = self.repo.commit(ref)
        files: dict[str, str] = {}
        for item in commit.tree.traverse():
            if item.type == "blob":
                files[normalize_path(item.path)] = item.hexsha
        return files

    def read_file(self, ref: str, path: str) -> Optional[str]:
        try:
            return self.repo.git.show(f"{ref}:{path}")
        except git.exc.GitCommandError:
            return None

    def diff_name_status(self, ref_a: str, ref_b: str) -> list[tuple[str, str, Optional[str]]]:
        """
        Compare ref_a..ref_b (changes in B relative to A).
        Returns list of (status, path, old_path).
        Status: A added, D deleted, M modified, R renamed, C copied.
        """
        raw = self.repo.git.diff("--name-status", "-M", ref_a, ref_b)
        results: list[tuple[str, str, Optional[str]]] = []
        for line in raw.splitlines():
            if not line.strip():
                continue
            parts = line.split("\t")
            status_code = parts[0]
            if status_code.startswith("R") or status_code.startswith("C"):
                old_path, new_path = parts[1], parts[2]
                results.append((status_code[0], normalize_path(new_path), normalize_path(old_path)))
            else:
                results.append((status_code, normalize_path(parts[1]), None))
        return results

    def diff_stat(self, ref_a: str, ref_b: str) -> dict[str, tuple[int, int]]:
        """path -> (insertions, deletions)"""
        raw = self.repo.git.diff("--numstat", ref_a, ref_b)
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
            raw = self.repo.git.log(
                f"{ref_a}..{ref_b}",
                f"--oneline",
                f"-{limit}",
            )
            return raw.splitlines()
        except git.exc.GitCommandError:
            return []

    def build_snapshot(self, ref: str, paths: Optional[set[str]] = None) -> BranchSnapshot:
        commit = self.resolve_ref(ref)
        all_files = self.list_files(ref)
        if paths is not None:
            all_files = {p: sha for p, sha in all_files.items() if p in paths}

        project_packages = self._discover_packages(all_files.keys())
        branch = self.repo.git.rev_parse("--abbrev-ref", ref) if not ref.startswith("commit") else ref[:7]
        snapshot = BranchSnapshot(branch=branch, commit=commit[:12], files={})

        for path, blob_sha in sorted(all_files.items()):
            category = classify_file(path)
            language = detect_language(path, category)
            content = self.read_file(ref, path)
            line_count = len(content.splitlines()) if content else 0

            classes = []
            apis: list[ApiReference] = []

            if content and is_parseable_source(path, category):
                if language == Language.JAVA:
                    classes, apis = parse_java(content, path, project_packages)
                elif language == Language.KOTLIN:
                    classes, apis = parse_kotlin(content, path, project_packages)
            elif content and category == FileCategory.GRADLE:
                apis = self._parse_gradle_deps(content, path, project_packages)

            snapshot.files[path] = FileSnapshot(
                path=path,
                category=category,
                language=language,
                exists=True,
                blob_sha=blob_sha,
                classes=classes,
                apis=apis,
                line_count=line_count,
            )
        return snapshot

    def _discover_packages(self, paths) -> set[str]:
        packages: set[str] = set()
        for path in paths:
            m = re.search(r"src/(?:main|test|androidTest)/(?:java|kotlin)/(.+)", normalize_path(path))
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
            re.compile(r"""['"]([a-zA-Z][\w.:-]+)['"]"""),
            re.compile(r"implementation\s*\(?['\"]([^'\"]+)['\"]"),
            re.compile(r"api\s*\(?['\"]([^'\"]+)['\"]"),
            re.compile(r"compileOnly\s*\(?['\"]([^'\"]+)['\"]"),
        ]
        seen: set[str] = set()
        for pat in patterns:
            for m in pat.finditer(content):
                coord = m.group(1)
                if ":" in coord:
                    # Maven coordinate group:artifact
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
