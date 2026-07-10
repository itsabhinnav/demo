"""Phase 1: full AST indexing of a git branch before diff."""

from __future__ import annotations

import re
from dataclasses import dataclass, field
from typing import Callable, Optional

from .file_classifier import classify_file, detect_language, is_parseable_source, normalize_path
from .git_scanner import GitScanner
from .models import (
    ApiReference,
    BranchSnapshot,
    FileCategory,
    FileSnapshot,
    Language,
    MethodInfo,
    ProjectSummary,
)
from .parsers.ast_parser import AstParseResult, parse_source_ast


@dataclass
class ParseStats:
    total_files: int = 0
    source_files: int = 0
    parsed_ast: int = 0
    parsed_fallback: int = 0
    parse_failed: int = 0
    skipped_binary: int = 0
    java_files: int = 0
    kotlin_files: int = 0
    by_parser: dict[str, int] = field(default_factory=dict)

    def to_dict(self) -> dict:
        return {
            "total_files": self.total_files,
            "source_files": self.source_files,
            "parsed_ast": self.parsed_ast,
            "parsed_fallback": self.parsed_fallback,
            "parse_failed": self.parse_failed,
            "skipped_binary": self.skipped_binary,
            "java_files": self.java_files,
            "kotlin_files": self.kotlin_files,
            "by_parser": dict(self.by_parser),
        }


@dataclass
class BranchIndex:
    branch: str
    commit: str
    snapshot: BranchSnapshot
    parse_stats: ParseStats
    project_packages: set[str] = field(default_factory=set)


def _discover_packages(paths) -> set[str]:
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


def _record_parse(stats: ParseStats, result: AstParseResult) -> None:
    stats.by_parser[result.parser] = stats.by_parser.get(result.parser, 0) + 1
    if result.parser.startswith("tree-sitter"):
        stats.parsed_ast += 1
    elif "fallback" in result.parser:
        stats.parsed_fallback += 1
    elif not result.success:
        stats.parse_failed += 1


def index_branch(
    scanner: GitScanner,
    ref: str,
    *,
    progress: Callable[[int, int, str, str], None] | None = None,
) -> BranchIndex:
    """
    Phase 1 — parse every source file on the branch with tree-sitter AST
    (Java/Kotlin) before any diff is computed.
    """
    commit = scanner.resolve_ref(ref)
    all_files = scanner.list_files(ref)
    project_packages = _discover_packages(all_files.keys())

    try:
        branch = scanner.repo.git.rev_parse("--abbrev-ref", ref)
    except Exception:
        branch = ref[:12]

    stats = ParseStats(total_files=len(all_files))
    snapshot = BranchSnapshot(branch=branch, commit=commit[:12], files={})

    parseable = [
        (path, sha)
        for path, sha in sorted(all_files.items())
        if is_parseable_source(path, classify_file(path))
    ]
    stats.source_files = len(parseable)
    total = len(parseable)

    for idx, (path, blob_sha) in enumerate(parseable, start=1):
        if progress:
            progress(idx, total, path, "ast")
        elif scanner.verbose and idx % 100 == 0:
            print(f"  AST parse {idx}/{total} …")

        category = classify_file(path)
        language = detect_language(path, category)
        if language == Language.JAVA:
            stats.java_files += 1
        elif language == Language.KOTLIN:
            stats.kotlin_files += 1

        content = scanner.read_file(ref, path)
        if content is None:
            stats.skipped_binary += 1
            snapshot.files[path] = FileSnapshot(
                path=path,
                category=category,
                language=language,
                exists=True,
                blob_sha=blob_sha,
                line_count=0,
            )
            continue

        try:
            result = parse_source_ast(content, path, language, project_packages)
            _record_parse(stats, result)
            if not result.success:
                scanner._warn(f"AST parse failed for {path}: {result.error}")
        except Exception as exc:
            result = AstParseResult(success=False, parser="error", error=str(exc))
            stats.parse_failed += 1
            scanner._warn(f"AST parse failed for {path}: {exc}")

        snapshot.files[path] = FileSnapshot(
            path=path,
            category=category,
            language=language,
            exists=True,
            blob_sha=blob_sha,
            classes=result.classes,
            apis=result.apis,
            line_count=len(content.splitlines()),
        )

    # Non-source tracked files (gradle, resources) — metadata only, no AST
    for path, blob_sha in sorted(all_files.items()):
        if path in snapshot.files:
            continue
        category = classify_file(path)
        language = detect_language(path, category)
        content = scanner.read_file(ref, path)
        apis = []
        if content and category == FileCategory.GRADLE:
            apis = scanner._parse_gradle_deps(content, path, project_packages)
        snapshot.files[path] = FileSnapshot(
            path=path,
            category=category,
            language=language,
            exists=True,
            blob_sha=blob_sha,
            apis=apis,
            line_count=len(content.splitlines()) if content else 0,
        )

    return BranchIndex(
        branch=branch,
        commit=commit[:12],
        snapshot=snapshot,
        parse_stats=stats,
        project_packages=project_packages,
    )


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
