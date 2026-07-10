from __future__ import annotations

import tempfile
from datetime import datetime, timezone
from pathlib import Path

from jinja2 import Environment, FileSystemLoader, select_autoescape

from . import __version__
from .file_classifier import classify_file
from .models import DiffReport, FileCategory
from .platform_utils import safe_display_path

_CATEGORY_LABELS = {
    "source_main": "Source",
    "source_test": "Tests",
    "gradle": "Gradle",
    "resource": "Resources",
    "manifest": "Manifest",
    "other": "Other",
}

# Categories shown in the main (non-test) report flow
_PRIMARY_CATEGORIES = {
    FileCategory.SOURCE_MAIN,
    FileCategory.GRADLE,
    FileCategory.MANIFEST,
    FileCategory.RESOURCE,
    FileCategory.OTHER,
}


def _short_path(path: str, max_len: int = 72) -> str:
    p = path.replace("\\", "/")
    return p if len(p) <= max_len else "…" + p[-(max_len - 1) :]


def _file_entry(fd) -> dict:
    return {
        "path": fd.path,
        "short_path": _short_path(fd.path),
        "status": fd.status.value,
        "category": _CATEGORY_LABELS.get(fd.category.value, fd.category.value),
        "language": fd.language.value,
        "old_path": fd.old_path,
        "is_test": fd.category == FileCategory.SOURCE_TEST,
        "has_detail": bool(
            fd.classes_added
            or fd.classes_removed
            or fd.methods_added
            or fd.methods_removed
            or fd.methods_modified
            or fd.apis_added
            or fd.apis_removed
        ),
        "classes_added": fd.classes_added,
        "classes_removed": fd.classes_removed,
        "methods_added": fd.methods_added,
        "methods_removed": fd.methods_removed,
        "methods_modified": fd.methods_modified,
        "apis_added": fd.apis_added,
        "apis_removed": fd.apis_removed,
    }


def _split_files_by_status(file_diffs, *, test_only: bool) -> dict[str, list[dict]]:
    buckets: dict[str, list[dict]] = {
        "added": [],
        "removed": [],
        "modified": [],
        "renamed": [],
    }
    for fd in file_diffs:
        is_test = fd.category == FileCategory.SOURCE_TEST
        if is_test != test_only:
            continue
        if not test_only and fd.category not in _PRIMARY_CATEGORIES:
            continue
        entry = _file_entry(fd)
        key = fd.status.value if fd.status.value in buckets else "modified"
        buckets[key].append(entry)
    return buckets


def _method_rows(methods) -> list[dict]:
    return [
        {
            "class_name": m.class_name,
            "signature": m.signature,
            "file": _short_path(m.file_path),
        }
        for m in methods
        if not m.is_test
    ]


def _test_method_rows(methods) -> list[dict]:
    return [
        {
            "class_name": m.class_name,
            "signature": m.signature,
            "file": _short_path(m.file_path),
        }
        for m in methods
        if m.is_test
    ]


def _api_rows(apis) -> list[dict]:
    return [
        {
            "name": a.qualified_name,
            "kind": a.kind.value,
            "file": _short_path(a.file_path),
        }
        for a in apis
        if classify_file(a.file_path) != FileCategory.SOURCE_TEST
    ]


def _test_api_rows(apis) -> list[dict]:
    return [
        {
            "name": a.qualified_name,
            "kind": a.kind.value,
            "file": _short_path(a.file_path),
        }
        for a in apis
        if classify_file(a.file_path) == FileCategory.SOURCE_TEST
    ]


def _prepare_view_model(report: DiffReport) -> dict:
    source_focus = report.summary.get("source_focus", {})
    source_files = _split_files_by_status(report.file_diffs, test_only=False)
    test_files = _split_files_by_status(report.file_diffs, test_only=True)

    methods_a = _method_rows(report.methods_only_in_a)
    methods_b = _method_rows(report.methods_only_in_b)
    methods_a_test = _test_method_rows(report.methods_only_in_a)
    methods_b_test = _test_method_rows(report.methods_only_in_b)
    methods_modified = report.summary.get("methods_modified_source", [])
    methods_modified_test = report.summary.get("methods_modified_test", [])

    apis_removed = _api_rows(report.apis_only_in_a)
    apis_added = _api_rows(report.apis_only_in_b)
    apis_removed_test = _test_api_rows(report.apis_only_in_a)
    apis_added_test = _test_api_rows(report.apis_only_in_b)

    api_kinds = []
    for kind, counts in sorted(source_focus.get("apis", {}).get("by_kind", {}).items()):
        removed = counts.get("removed_in_b", 0)
        added = counts.get("added_in_b", 0)
        if removed or added:
            api_kinds.append({"kind": kind, "removed": removed, "added": added})

    category_rows = []
    category_order = ("source_main", "gradle", "manifest", "resource", "other")
    by_cat = report.summary.get("by_category", {})
    for cat in category_order:
        counts = by_cat.get(cat)
        if not counts or cat == "source_test":
            continue
        total = sum(counts.values())
        if total:
            category_rows.append(
                {
                    "label": _CATEGORY_LABELS.get(cat, cat),
                    "added": counts.get("added", 0),
                    "removed": counts.get("removed", 0),
                    "modified": counts.get("modified", 0),
                }
            )

    test_file_counts = source_focus.get("test_files", {})
    test_method_counts = source_focus.get("methods", {})
    test_api_counts = source_focus.get("apis", {})
    has_test_data = (
        test_file_counts.get("total_changed", 0) > 0
        or test_method_counts.get("test_only_in_a", 0)
        or test_method_counts.get("test_only_in_b", 0)
        or test_api_counts.get("test_only_in_a", 0)
        or test_api_counts.get("test_only_in_b", 0)
    )

    return {
        "source_focus": source_focus,
        "source_files": source_files,
        "test_files": test_files,
        "methods_a": methods_a,
        "methods_b": methods_b,
        "methods_a_test": methods_a_test,
        "methods_b_test": methods_b_test,
        "methods_modified": methods_modified,
        "methods_modified_test": methods_modified_test,
        "apis_removed": apis_removed,
        "apis_added": apis_added,
        "apis_removed_test": apis_removed_test,
        "apis_added_test": apis_added_test,
        "api_kinds": api_kinds,
        "category_rows": category_rows,
        "apis_in_both_count": len(report.apis_in_both),
        "has_test_data": has_test_data,
        "test_file_counts": test_file_counts,
        "test_method_counts": test_method_counts,
        "test_api_counts": test_api_counts,
    }


def generate_html_report(report: DiffReport, output_path: str | Path) -> Path:
    template_dir = Path(__file__).parent / "templates"
    env = Environment(
        loader=FileSystemLoader(str(template_dir)),
        autoescape=select_autoescape(["html", "xml"]),
    )
    template = env.get_template("report.html.j2")
    view = _prepare_view_model(report)

    html = template.render(
        version=__version__,
        repo_path=safe_display_path(report.repo_path),
        branch_a=report.branch_a,
        branch_b=report.branch_b,
        commit_a=report.commit_a,
        commit_b=report.commit_b,
        merge_base=report.merge_base,
        generated_at=datetime.now(timezone.utc).strftime("%Y-%m-%d %H:%M UTC"),
        summary=report.summary,
        warnings=report.summary.get("warnings", []),
        **view,
    )

    out = Path(output_path)
    out.parent.mkdir(parents=True, exist_ok=True)

    fd, tmp_name = tempfile.mkstemp(
        suffix=".html",
        prefix=".aaos-diff-",
        dir=str(out.parent),
    )
    tmp_path = Path(tmp_name)
    try:
        with open(fd, "w", encoding="utf-8", newline="\n") as f:
            f.write(html)
        tmp_path.replace(out)
    except Exception:
        tmp_path.unlink(missing_ok=True)
        raise

    return out.resolve()
