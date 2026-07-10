"""Tree-sitter based AST parsing for Java and Kotlin."""

from __future__ import annotations

import hashlib
import re
from dataclasses import dataclass, field
from typing import Optional

import tree_sitter_java as tsjava
import tree_sitter_kotlin as tskotlin
from tree_sitter import Language, Parser

from ..file_classifier import classify_file
from ..models import ApiKind, ApiReference, ClassInfo, FileCategory, Language as Lang, MethodInfo
from ..platform_utils import normalize_line_endings
from .java_parser import _classify_api, parse_java as parse_java_javalang
from .kotlin_parser import parse_kotlin as parse_kotlin_regex

_JAVA_PARSER = Parser(Language(tsjava.language()))
_KOTLIN_PARSER = Parser(Language(tskotlin.language()))


@dataclass
class AstParseResult:
    classes: list[ClassInfo] = field(default_factory=list)
    apis: list[ApiReference] = field(default_factory=list)
    parser: str = "unknown"  # tree-sitter-java, tree-sitter-kotlin, javalang, regex
    success: bool = True
    error: Optional[str] = None


def _body_hash(text: str) -> str:
    normalized = re.sub(r"\s+", " ", text.strip())
    return hashlib.sha256(normalized.encode("utf-8", errors="replace")).hexdigest()[:16]


def _node_text(source: bytes, node) -> str:
    return source[node.start_byte : node.end_byte].decode("utf-8", errors="replace")


def parse_source_ast(
    content: str,
    file_path: str,
    language: Lang,
    project_packages: Optional[set[str]] = None,
) -> AstParseResult:
    """Parse a source file using tree-sitter AST; fall back to legacy parsers."""
    project_packages = project_packages or set()
    content = normalize_line_endings(content)

    if language == Lang.JAVA:
        result = _parse_java_ts(content, file_path, project_packages)
        if result.success and result.classes:
            return result
        fallback = parse_java_javalang(content, file_path, project_packages)
        return AstParseResult(
            classes=fallback[0],
            apis=fallback[1],
            parser="javalang-fallback",
            success=True,
        )

    if language == Lang.KOTLIN:
        result = _parse_kotlin_ts(content, file_path, project_packages)
        if result.success and (result.classes or result.apis):
            return result
        fallback = parse_kotlin_regex(content, file_path, project_packages)
        return AstParseResult(
            classes=fallback[0],
            apis=fallback[1],
            parser="kotlin-regex-fallback",
            success=True,
        )

    return AstParseResult(parser="skipped", success=False, error="not a source language")


def _parse_java_ts(
    content: str,
    file_path: str,
    project_packages: set[str],
) -> AstParseResult:
    category = classify_file(file_path)
    is_test = category == FileCategory.SOURCE_TEST
    source = content.encode("utf-8")
    classes: list[ClassInfo] = []
    apis: list[ApiReference] = []

    try:
        tree = _JAVA_PARSER.parse(source)
        if tree.root_node.has_error:
            return AstParseResult(parser="tree-sitter-java", success=False, error="syntax errors in AST")

        pkg = ""
        for child in tree.root_node.children:
            if child.type == "package_declaration":
                pkg = _collect_identifiers(child, source)
                break

        for child in tree.root_node.children:
            if child.type == "import_declaration":
                imp = _java_import_name(child, source)
                if imp:
                    apis.append(ApiReference(imp, _classify_api(imp, project_packages), file_path, 0, "import"))

        for node in _walk(tree.root_node):
            if node.type in ("class_declaration", "interface_declaration", "enum_declaration"):
                cls = _java_type_node(node, source, pkg, file_path, is_test)
                if cls:
                    classes.append(cls)
                    for ext in _java_extends(node, source, pkg):
                        apis.append(ApiReference(ext, _classify_api(ext, project_packages), file_path, 0, "extends"))
                    for impl in _java_implements(node, source, pkg):
                        apis.append(ApiReference(impl, _classify_api(impl, project_packages), file_path, 0, "implements"))

        for call in re.finditer(
            r"\b((?:android|androidx|java|javax|kotlin|kotlinx|vendor|oem|com\.android)\.[\w.]+)",
            content,
        ):
            q = call.group(1)
            apis.append(ApiReference(q, _classify_api(q, project_packages), file_path, 0, "call"))

        return AstParseResult(classes=classes, apis=apis, parser="tree-sitter-java", success=True)
    except Exception as exc:
        return AstParseResult(parser="tree-sitter-java", success=False, error=str(exc))


def _parse_kotlin_ts(
    content: str,
    file_path: str,
    project_packages: set[str],
) -> AstParseResult:
    category = classify_file(file_path)
    is_test = category == FileCategory.SOURCE_TEST
    source = content.encode("utf-8")
    classes: list[ClassInfo] = []
    apis: list[ApiReference] = []

    try:
        tree = _KOTLIN_PARSER.parse(source)
        if tree.root_node.has_error:
            return AstParseResult(parser="tree-sitter-kotlin", success=False, error="syntax errors in AST")

        pkg = ""
        for child in tree.root_node.children:
            if child.type == "package_header":
                pkg = _kotlin_package(child, source)
            elif child.type == "import_header":
                for imp in _kotlin_imports(child, source):
                    apis.append(ApiReference(imp, _classify_api(imp, project_packages), file_path, 0, "import"))

        for node in _walk(tree.root_node):
            if node.type in ("class_declaration", "object_declaration", "interface_declaration"):
                cls = _kotlin_type_node(node, source, pkg, file_path, is_test)
                if cls:
                    classes.append(cls)

        top_level: list[MethodInfo] = []
        for child in tree.root_node.children:
            if child.type == "function_declaration":
                m = _kotlin_function_node(child, source, pkg or file_path, file_path, is_test)
                if m:
                    top_level.append(m)
        if top_level:
            classes.append(
                ClassInfo(
                    name="(file)",
                    qualified_name=pkg or file_path,
                    file_path=file_path,
                    language=Lang.KOTLIN,
                    kind="file",
                    methods=top_level,
                    is_test=is_test,
                )
            )

        for call in re.finditer(
            r"\b((?:android|androidx|java|javax|kotlin|kotlinx|vendor|oem)\.[\w.]+)",
            content,
        ):
            q = call.group(1)
            apis.append(ApiReference(q, _classify_api(q, project_packages), file_path, 0, "call"))

        return AstParseResult(classes=classes, apis=apis, parser="tree-sitter-kotlin", success=True)
    except Exception as exc:
        return AstParseResult(parser="tree-sitter-kotlin", success=False, error=str(exc))


def _walk(node):
    yield node
    for child in node.children:
        yield from _walk(child)


def _collect_identifiers(node, source: bytes) -> str:
    parts = []
    for child in _walk(node):
        if child.type == "identifier" and child != node:
            parts.append(_node_text(source, child))
    # package_declaration: package scoped_identifier
    if node.type == "package_declaration":
        for child in node.children:
            if child.type == "scoped_identifier":
                return ".".join(
                    _node_text(source, c) for c in child.children if c.type == "identifier"
                )
    return ".".join(parts)


def _java_import_name(node, source: bytes) -> str:
    for child in node.children:
        if child.type == "scoped_identifier":
            return ".".join(_node_text(source, c) for c in child.children if c.type == "identifier")
    return ""


def _java_type_node(node, source: bytes, pkg: str, file_path: str, is_test: bool) -> Optional[ClassInfo]:
    name = ""
    for child in node.children:
        if child.type == "identifier":
            name = _node_text(source, child)
            break
    if not name:
        return None
    qn = f"{pkg}.{name}" if pkg else name
    kind = "interface" if node.type == "interface_declaration" else "class"
    if node.type == "enum_declaration":
        kind = "enum"

    methods: list[MethodInfo] = []
    for child in _walk(node):
        if child.type == "method_declaration" and child.parent and child.parent.type in (
            "class_body",
            "interface_body",
            "enum_body",
        ):
            m = _java_method_node(child, source, qn, file_path, is_test)
            if m:
                methods.append(m)

    return ClassInfo(name, qn, file_path, Lang.JAVA, kind, methods, is_test=is_test)


def _java_method_node(node, source: bytes, class_name: str, file_path: str, is_test: bool) -> Optional[MethodInfo]:
    name = ""
    for child in node.children:
        if child.type == "identifier":
            name = _node_text(source, child)
            break
    if not name:
        return None
    sig = _node_text(source, node).split("{")[0].strip()
    body = ""
    for child in node.children:
        if child.type == "block":
            body = _node_text(source, child)
            break
    return MethodInfo(
        name=name,
        signature=sig,
        class_name=class_name,
        file_path=file_path,
        language=Lang.JAVA,
        line_start=node.start_point[0] + 1,
        line_end=node.end_point[0] + 1,
        body_hash=_body_hash(body or sig),
        is_test=is_test,
    )


def _java_extends(node, source: bytes, pkg: str) -> list[str]:
    types = []
    for child in node.children:
        if child.type == "superclass":
            for sub in child.children:
                if sub.type == "type_identifier":
                    types.append(_node_text(source, sub))
                elif sub.type == "scoped_type_identifier":
                    types.append(_scoped_type(sub, source))
    return types


def _java_implements(node, source: bytes, pkg: str) -> list[str]:
    types = []
    for child in node.children:
        if child.type == "super_interfaces":
            for sub in _walk(child):
                if sub.type in ("type_identifier", "scoped_type_identifier"):
                    types.append(_scoped_type(sub, source) if sub.type == "scoped_type_identifier" else _node_text(source, sub))
    return types


def _scoped_type(node, source: bytes) -> str:
    return ".".join(_node_text(source, c) for c in node.children if c.type == "identifier")


def _kotlin_package(node, source: bytes) -> str:
    for child in node.children:
        if child.type == "qualified_identifier":
            return ".".join(_node_text(source, c) for c in child.children if c.type == "identifier")
    return ""


def _kotlin_imports(node, source: bytes) -> list[str]:
    imports = []
    for child in _walk(node):
        if child.type == "identifier" or child.type == "qualified_identifier":
            text = _node_text(source, child)
            if text and text not in ("import",):
                imports.append(text.replace(".*", ""))
    return imports


def _kotlin_type_node(node, source: bytes, pkg: str, file_path: str, is_test: bool) -> Optional[ClassInfo]:
    name = ""
    kind = "class"
    for child in node.children:
        if child.type == "identifier":
            name = _node_text(source, child)
        elif child.type == "object":
            kind = "object"
        elif child.type == "interface":
            kind = "interface"
    if not name:
        return None
    qn = f"{pkg}.{name}" if pkg else name
    methods: list[MethodInfo] = []
    for child in node.children:
        if child.type == "class_body":
            for decl in _walk(child):
                if decl.type == "function_declaration" and decl.parent and decl.parent.type in (
                    "class_body",
                    "object_body",
                ):
                    m = _kotlin_function_node(decl, source, qn, file_path, is_test)
                    if m:
                        methods.append(m)
            break
    return ClassInfo(name, qn, file_path, Lang.KOTLIN, kind, methods, is_test=is_test)


def _kotlin_function_node(
    node, source: bytes, class_name: str, file_path: str, is_test: bool
) -> Optional[MethodInfo]:
    name = ""
    for child in node.children:
        if child.type == "identifier":
            name = _node_text(source, child)
            break
    if not name:
        return None
    sig = _node_text(source, node).split("=")[0].split("{")[0].strip()
    body = ""
    for child in node.children:
        if child.type == "function_body":
            body = _node_text(source, child)
    return MethodInfo(
        name=name,
        signature=sig,
        class_name=class_name,
        file_path=file_path,
        language=Lang.KOTLIN,
        line_start=node.start_point[0] + 1,
        line_end=node.end_point[0] + 1,
        body_hash=_body_hash(body or sig),
        is_test=is_test,
    )
