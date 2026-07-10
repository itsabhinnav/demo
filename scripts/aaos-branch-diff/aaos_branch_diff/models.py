from __future__ import annotations

from dataclasses import dataclass, field
from enum import Enum
from typing import Optional


class FileCategory(str, Enum):
    SOURCE_MAIN = "source_main"
    SOURCE_TEST = "source_test"
    GRADLE = "gradle"
    RESOURCE = "resource"
    MANIFEST = "manifest"
    OTHER = "other"


class Language(str, Enum):
    JAVA = "java"
    KOTLIN = "kotlin"
    GROOVY = "groovy"
    KOTLIN_DSL = "kotlin_dsl"
    XML = "xml"
    OTHER = "other"


class ApiKind(str, Enum):
    AOSP = "aosp"
    AAOS = "aaos"
    ANDROIDX = "androidx"
    JAVA_STD = "java_std"
    KOTLIN_STD = "kotlin_std"
    THIRD_PARTY = "third_party"
    VENDOR = "vendor"
    PROJECT = "project"


class ChangeStatus(str, Enum):
    ADDED = "added"
    REMOVED = "removed"
    MODIFIED = "modified"
    UNCHANGED = "unchanged"
    RENAMED = "renamed"


@dataclass
class MethodInfo:
    name: str
    signature: str
    class_name: str
    file_path: str
    language: Language
    line_start: int
    line_end: int
    body_hash: str
    is_test: bool = False


@dataclass
class ClassInfo:
    name: str
    qualified_name: str
    file_path: str
    language: Language
    kind: str  # class, interface, object, enum
    methods: list[MethodInfo] = field(default_factory=list)
    imports: list[str] = field(default_factory=list)
    is_test: bool = False


@dataclass
class ApiReference:
    qualified_name: str
    kind: ApiKind
    file_path: str
    line: int
    usage_context: str  # import, extends, implements, call, annotation


@dataclass
class FileSnapshot:
    path: str
    category: FileCategory
    language: Language
    exists: bool
    blob_sha: Optional[str]
    classes: list[ClassInfo] = field(default_factory=list)
    apis: list[ApiReference] = field(default_factory=list)
    line_count: int = 0


@dataclass
class FileDiff:
    path: str
    status: ChangeStatus
    category: FileCategory
    language: Language
    old_path: Optional[str] = None
    similarity: Optional[float] = None
    methods_added: list[str] = field(default_factory=list)
    methods_removed: list[str] = field(default_factory=list)
    methods_modified: list[str] = field(default_factory=list)
    methods_unchanged: list[str] = field(default_factory=list)
    classes_added: list[str] = field(default_factory=list)
    classes_removed: list[str] = field(default_factory=list)
    apis_added: list[str] = field(default_factory=list)
    apis_removed: list[str] = field(default_factory=list)


@dataclass
class BranchSnapshot:
    branch: str
    commit: str
    files: dict[str, FileSnapshot] = field(default_factory=dict)


@dataclass
class ProjectSummary:
    total_files: int = 0
    by_category: dict[str, int] = field(default_factory=dict)
    by_language: dict[str, int] = field(default_factory=dict)
    class_count: int = 0
    method_count: int = 0
    api_count: int = 0


@dataclass
class DiffReport:
    repo_path: str
    branch_a: str
    branch_b: str
    commit_a: str
    commit_b: str
    merge_base: str
    summary: dict = field(default_factory=dict)
    file_diffs: list[FileDiff] = field(default_factory=list)
    methods_only_in_a: list[MethodInfo] = field(default_factory=list)
    methods_only_in_b: list[MethodInfo] = field(default_factory=list)
    methods_in_both: list[tuple[str, str]] = field(default_factory=list)
    apis_only_in_a: list[ApiReference] = field(default_factory=list)
    apis_only_in_b: list[ApiReference] = field(default_factory=list)
    apis_in_both: list[str] = field(default_factory=list)
    snapshot_a: Optional[BranchSnapshot] = None
    snapshot_b: Optional[BranchSnapshot] = None
