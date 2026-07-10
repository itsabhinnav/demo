from __future__ import annotations

import hashlib
import re
from typing import Optional

from .java_parser import _classify_api, _body_hash
from ..file_classifier import classify_file
from ..models import ApiReference, ClassInfo, FileCategory, Language, MethodInfo

# Kotlin declaration patterns
CLASS_PATTERN = re.compile(
    r"(?:(?:public|internal|private|protected)\s+)?"
    r"(?:(?:data|sealed|abstract|open|inner)\s+)*"
    r"(class|interface|object|enum\s+class)\s+(\w+)",
    re.M,
)
FUN_PATTERN = re.compile(
    r"(?:(?:public|internal|private|protected)\s+)?"
    r"(?:(?:suspend|inline|override|operator|infix)\s+)*"
    r"fun\s+(?:<[^>]+>\s+)?"
    r"(?:[\w.]+\.)?(\w+)\s*\(([^)]*)\)",
    re.M,
)
PACKAGE_PATTERN = re.compile(r"^\s*package\s+([\w.]+)", re.M)
IMPORT_PATTERN = re.compile(r"^\s*import\s+([\w.*]+)", re.M)
EXTENDS_PATTERN = re.compile(r":\s*([\w.]+(?:\s*\([^)]*\))?(?:\s*,\s*[\w.]+)*)", re.M)


def parse_kotlin(
    content: str,
    file_path: str,
    project_packages: Optional[set[str]] = None,
) -> tuple[list[ClassInfo], list[ApiReference]]:
    project_packages = project_packages or set()
    category = classify_file(file_path)
    is_test = category == FileCategory.SOURCE_TEST
    classes: list[ClassInfo] = []
    apis: list[ApiReference] = []

    pkg_m = PACKAGE_PATTERN.search(content)
    pkg = pkg_m.group(1) if pkg_m else ""

    for imp in IMPORT_PATTERN.finditer(content):
        q = imp.group(1).replace("*", "").rstrip(".")
        if q:
            apis.append(
                ApiReference(q, _classify_api(q, project_packages), file_path, 0, "import")
            )

    for m in CLASS_PATTERN.finditer(content):
        kind_raw, name = m.group(1), m.group(2)
        kind = kind_raw.replace(" enum class", "").strip()
        qn = f"{pkg}.{name}" if pkg else name

        # Scope methods to class body (approximate)
        start = m.end()
        next_decl = CLASS_PATTERN.search(content, start)
        end = next_decl.start() if next_decl else len(content)
        body = content[start:end]

        methods = _extract_kotlin_methods(body, qn, file_path, is_test)

        # Super types / interfaces from header line
        header_end = content.find("{", m.start())
        if header_end == -1:
            header_end = min(m.start() + 500, len(content))
        header = content[m.start():header_end]
        for super_m in re.finditer(r":\s*([^({]+)", header):
            for part in super_m.group(1).split(","):
                t = part.strip().split("(")[0].strip()
                if t and t not in ("Any", "Serializable"):
                    resolved = _resolve_kotlin_type(t, pkg, content)
                    apis.append(
                        ApiReference(
                            resolved,
                            _classify_api(resolved, project_packages),
                            file_path,
                            0,
                            "extends",
                        )
                    )

        classes.append(
            ClassInfo(
                name=name,
                qualified_name=qn,
                file_path=file_path,
                language=Language.KOTLIN,
                kind=kind,
                methods=methods,
                is_test=is_test,
            )
        )

    # Top-level functions
    top_level_funs = _extract_kotlin_methods(content, pkg or "(top-level)", file_path, is_test)
    if top_level_funs and not classes:
        classes.append(
            ClassInfo(
                name="(file)",
                qualified_name=pkg or file_path,
                file_path=file_path,
                language=Language.KOTLIN,
                kind="file",
                methods=top_level_funs,
                is_test=is_test,
            )
        )

    # API calls: android.* / vendor patterns in code
    for call in re.finditer(r"\b((?:android|androidx|java|javax|kotlin|kotlinx|vendor|oem)\.[\w.]+)", content):
        q = call.group(1)
        apis.append(
            ApiReference(q, _classify_api(q, project_packages), file_path, 0, "call")
        )

    return classes, apis


def _resolve_kotlin_type(name: str, pkg: str, content: str) -> str:
    if "." in name:
        return name.split("<")[0].strip()
    # Check imports
    for imp in IMPORT_PATTERN.finditer(content):
        short = imp.group(1).split(".")[-1].replace("*", "")
        if short == name:
            return imp.group(1).replace("*", "").rstrip(".")
    return f"{pkg}.{name}" if pkg else name


def _extract_kotlin_methods(
    body: str,
    class_name: str,
    file_path: str,
    is_test: bool,
) -> list[MethodInfo]:
    methods: list[MethodInfo] = []
    for m in FUN_PATTERN.finditer(body):
        name, params = m.group(1), m.group(2).strip()
        sig = f"fun {name}({params})"
        line_start = body[: m.start()].count("\n") + 1
        snippet = body[m.start() : m.start() + 300]
        methods.append(
            MethodInfo(
                name=name,
                signature=sig,
                class_name=class_name,
                file_path=file_path,
                language=Language.KOTLIN,
                line_start=line_start,
                line_end=line_start,
                body_hash=_body_hash(snippet),
                is_test=is_test,
            )
        )
    return methods
