from __future__ import annotations

import hashlib
import re
from typing import Optional

import javalang

from ..file_classifier import classify_file
from ..models import ApiKind, ApiReference, ClassInfo, FileCategory, Language, MethodInfo


def _body_hash(text: str) -> str:
    return hashlib.sha256(text.encode("utf-8", errors="replace")).hexdigest()[:16]


def _classify_api(qualified: str, project_packages: set[str]) -> ApiKind:
    q = qualified.strip()
    if not q:
        return ApiKind.THIRD_PARTY
    if any(q == p or q.startswith(p + ".") for p in project_packages):
        return ApiKind.PROJECT
    if q.startswith("android.car.") or q.startswith("android.hardware.automotive."):
        return ApiKind.AAOS
    if q.startswith("android.") or q.startswith("com.android."):
        return ApiKind.AOSP
    if q.startswith("androidx."):
        return ApiKind.ANDROIDX
    if q.startswith(("java.", "javax.", "jdk.")):
        return ApiKind.JAVA_STD
    if q.startswith("kotlin.") or q.startswith("kotlinx."):
        return ApiKind.KOTLIN_STD
    # Heuristic: vendor/OEM packages common in AAOS
    vendor_prefixes = (
        "com.google.android.apps.auto",
        "com.google.android.gms",
        "vendor.",
        "oem.",
        "car.",
    )
    if any(q.startswith(p) for p in vendor_prefixes):
        return ApiKind.VENDOR
  # Short top-level packages often OEM-specific in automotive
    top = q.split(".")[0] if "." in q else q
    if top in {"vendor", "oem", "car", "ivi", "hmi"}:
        return ApiKind.VENDOR
    return ApiKind.THIRD_PARTY


def _extract_project_packages(paths: list[str]) -> set[str]:
    packages: set[str] = set()
    for path in paths:
        m = re.search(r"src/(?:main|test|androidTest)/(?:java|kotlin)/(.+)", path.replace("\\", "/"))
        if m:
            pkg = m.group(1).rsplit("/", 1)[0].replace("/", ".")
            if pkg:
                packages.add(pkg)
    return packages


def parse_java(
    content: str,
    file_path: str,
    project_packages: Optional[set[str]] = None,
) -> tuple[list[ClassInfo], list[ApiReference]]:
    project_packages = project_packages or set()
    category = classify_file(file_path)
    is_test = category == FileCategory.SOURCE_TEST
    classes: list[ClassInfo] = []
    apis: list[ApiReference] = []

    try:
        tree = javalang.parse.parse(content)
    except (javalang.parser.JavaSyntaxError, RecursionError, TypeError, ValueError):
        return _parse_java_fallback(content, file_path, is_test, project_packages)

    pkg = tree.package.name if tree.package else ""
    import_map = {imp.path.split(".")[-1]: imp.path for imp in (tree.imports or [])}

    for imp in tree.imports or []:
        apis.append(
            ApiReference(
                qualified_name=imp.path,
                kind=_classify_api(imp.path, project_packages),
                file_path=file_path,
                line=0,
                usage_context="import",
            )
        )

    for type_decl in tree.types:
        cls = _java_type_to_class(type_decl, pkg, file_path, is_test, content, import_map)
        if cls:
            classes.append(cls)
            for ext in getattr(type_decl, "extends", None) or []:
                qn = _resolve_java_type(ext.name, pkg, import_map)
                apis.append(ApiReference(qn, _classify_api(qn, project_packages), file_path, 0, "extends"))
            for impl in getattr(type_decl, "implements", None) or []:
                qn = _resolve_java_type(impl.name, pkg, import_map)
                apis.append(ApiReference(qn, _classify_api(qn, project_packages), file_path, 0, "implements"))

    return classes, apis


def _resolve_java_type(name: str, pkg: str, import_map: dict[str, str]) -> str:
    if "." in name:
        return name
    if name in import_map:
        return import_map[name]
    return f"{pkg}.{name}" if pkg else name


def _java_type_to_class(
    type_decl,
    pkg: str,
    file_path: str,
    is_test: bool,
    content: str,
    import_map: dict[str, str],
) -> Optional[ClassInfo]:
    name = type_decl.name
    qn = f"{pkg}.{name}" if pkg else name
    kind = "interface" if isinstance(type_decl, javalang.tree.InterfaceDeclaration) else "class"
    if isinstance(type_decl, javalang.tree.EnumDeclaration):
        kind = "enum"

    methods: list[MethodInfo] = []
    lines = content.splitlines()

    for member in type_decl.body or []:
        if isinstance(member, javalang.tree.MethodDeclaration):
            params = ", ".join(f"{p.type.name} {p.name}" for p in member.parameters)
            sig = f"{member.name}({params})"
            line_start = member.position.line if member.position else 0
            line_end = line_start
            body_text = ""
            if member.body:
                body_text = "\n".join(lines[line_start - 1 : min(len(lines), line_start + 50)])
            methods.append(
                MethodInfo(
                    name=member.name,
                    signature=sig,
                    class_name=qn,
                    file_path=file_path,
                    language=Language.JAVA,
                    line_start=line_start,
                    line_end=line_end,
                    body_hash=_body_hash(body_text or sig),
                    is_test=is_test,
                )
            )

    return ClassInfo(
        name=name,
        qualified_name=qn,
        file_path=file_path,
        language=Language.JAVA,
        kind=kind,
        methods=methods,
        is_test=is_test,
    )


def _parse_java_fallback(
    content: str,
    file_path: str,
    is_test: bool,
    project_packages: set[str],
) -> tuple[list[ClassInfo], list[ApiReference]]:
    classes: list[ClassInfo] = []
    apis: list[ApiReference] = []
    pkg_m = re.search(r"^\s*package\s+([\w.]+)\s*;", content, re.M)
    pkg = pkg_m.group(1) if pkg_m else ""

    for imp in re.finditer(r"^\s*import\s+(?:static\s+)?([\w.]+(?:\.\*)?)\s*;", content, re.M):
        q = imp.group(1).replace(".*", "")
        apis.append(ApiReference(q, _classify_api(q, project_packages), file_path, 0, "import"))

    for m in re.finditer(
        r"(?:public\s+)?(?:abstract\s+)?(?:class|interface|enum)\s+(\w+)",
        content,
    ):
        name = m.group(1)
        qn = f"{pkg}.{name}" if pkg else name
        methods = []
        for mm in re.finditer(
            r"(?:public|protected|private)\s+[\w<>,\s\[\]]+\s+(\w+)\s*\([^)]*\)\s*(?:throws\s+[\w\s,]+)?\s*\{",
            content[m.start() : m.start() + 8000],
        ):
            sig = mm.group(0).split("{")[0].strip()
            methods.append(
                MethodInfo(
                    name=mm.group(1),
                    signature=sig,
                    class_name=qn,
                    file_path=file_path,
                    language=Language.JAVA,
                    line_start=0,
                    line_end=0,
                    body_hash=_body_hash(sig),
                    is_test=is_test,
                )
            )
        classes.append(
            ClassInfo(name, qn, file_path, Language.JAVA, "class", methods, is_test=is_test)
        )
    return classes, apis
