from __future__ import annotations

import re
from pathlib import PurePosixPath

from .models import FileCategory, Language

TEST_PATH_PATTERNS = (
    re.compile(r"(^|/)src/(androidTest|test)/", re.I),
    re.compile(r"(^|/)test/", re.I),
    re.compile(r"(^|/)tests/", re.I),
    re.compile(r"Test\.(java|kt)$", re.I),
    re.compile(r"Tests\.(java|kt)$", re.I),
    re.compile(r"(^|/)mock/", re.I),
    re.compile(r"(^|/)fakes?/", re.I),
)

MAIN_SOURCE_PATTERNS = (
    re.compile(r"(^|/)src/main/(java|kotlin)/", re.I),
    re.compile(r"(^|/)src/(java|kotlin)/", re.I),
)

GRADLE_PATTERNS = (
    re.compile(r"\.gradle(\.kts)?$", re.I),
    re.compile(r"(^|/)gradle/", re.I),
    re.compile(r"(^|/)settings\.gradle(\.kts)?$", re.I),
    re.compile(r"(^|/)gradle\.properties$", re.I),
    re.compile(r"(^|/)local\.properties$", re.I),
)

RESOURCE_PATTERNS = (
    re.compile(r"(^|/)res/", re.I),
    re.compile(r"(^|/)assets/", re.I),
    re.compile(r"(^|/)drawable", re.I),
    re.compile(r"(^|/)layout/", re.I),
    re.compile(r"(^|/)values/", re.I),
)


def normalize_path(path: str) -> str:
    return path.replace("\\", "/")


def classify_file(path: str) -> FileCategory:
    p = normalize_path(path)
    if p.endswith("AndroidManifest.xml"):
        return FileCategory.MANIFEST
    for pat in GRADLE_PATTERNS:
        if pat.search(p):
            return FileCategory.GRADLE
    for pat in TEST_PATH_PATTERNS:
        if pat.search(p):
            return FileCategory.SOURCE_TEST
    for pat in MAIN_SOURCE_PATTERNS:
        if pat.search(p):
            return FileCategory.SOURCE_MAIN
    if p.endswith((".java", ".kt", ".kts")) and "/src/" in p:
        if "test" in p.lower():
            return FileCategory.SOURCE_TEST
        return FileCategory.SOURCE_MAIN
    for pat in RESOURCE_PATTERNS:
        if pat.search(p):
            return FileCategory.RESOURCE
    return FileCategory.OTHER


def detect_language(path: str, category: FileCategory) -> Language:
    p = normalize_path(path).lower()
    if p.endswith(".java"):
        return Language.JAVA
    if p.endswith(".kt"):
        if category == FileCategory.GRADLE or p.endswith(".gradle.kts") or "/gradle/" in p:
            return Language.KOTLIN_DSL
        return Language.KOTLIN
    if p.endswith(".gradle"):
        return Language.GROOVY
    if p.endswith(".gradle.kts"):
        return Language.KOTLIN_DSL
    if p.endswith(".xml"):
        return Language.XML
    return Language.OTHER


def is_parseable_source(path: str, category: FileCategory) -> bool:
    if category not in (FileCategory.SOURCE_MAIN, FileCategory.SOURCE_TEST):
        return False
    return normalize_path(path).lower().endswith((".java", ".kt"))
