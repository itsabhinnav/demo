from __future__ import annotations

import tempfile
from datetime import datetime, timezone
from pathlib import Path

from jinja2 import Environment, FileSystemLoader, select_autoescape

from . import __version__
from .models import DiffReport
from .platform_utils import safe_display_path

_CATEGORY_LABELS = {
    "source_main": "Source",
    "source_test": "Tests",
    "gradle": "Gradle",
    "resource": "Resources",
    "manifest": "Manifest",
    "other": "Other",
}

_STATUS_LABELS = {
    "added": "Added",
    "removed": "Removed",
    "modified": "Modified",
    "renamed": "Renamed",
}


def _short_path(path: str, max_len: int = 72) -> str:
    p = path.replace("\\", "/")
    return p if len(p) <= max_len else "…" + p[-(max_len - 1) :]


def _prepare_view_model(report: DiffReport) -> dict:
    files_by_status: dict[str, list[dict]] = {
        "added": [],
        "removed": [],
        "modified": [],
        "renamed": [],
    }

    for fd in report.file_diffs:
        entry = {
            "path": fd.path,
            "short_path": _short_path(fd.path),
            "status": fd.status.value,
            "category": _CATEGORY_LABELS.get(fd.category.value, fd.category.value),
            "language": fd.language.value,
            "old_path": fd.old_path,
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
        key = fd.status.value if fd.status.value in files_by_status else "modified"
        files_by_status[key].append(entry)

    methods_a = [
        {
            "class_name": m.class_name,
            "signature": m.signature,
            "file": _short_path(m.file_path),
            "test": m.is_test,
        }
        for m in report.methods_only_in_a
    ]
    methods_b = [
        {
            "class_name": m.class_name,
            "signature": m.signature,
            "file": _short_path(m.file_path),
            "test": m.is_test,
        }
        for m in report.methods_only_in_b
    ]
    methods_modified = [key for key, state in report.methods_in_both if state == "modified"]

    apis_removed = [
        {
            "name": a.qualified_name,
            "kind": a.kind.value,
            "file": _short_path(a.file_path),
        }
        for a in report.apis_only_in_a
    ]
    apis_added = [
        {
            "name": a.qualified_name,
            "kind": a.kind.value,
            "file": _short_path(a.file_path),
        }
        for a in report.apis_only_in_b
    ]

    api_kinds = []
    for kind, counts in sorted(report.summary.get("apis", {}).get("by_kind", {}).items()):
        removed = counts.get("removed_in_b", 0)
        added = counts.get("added_in_b", 0)
        if removed or added:
            api_kinds.append({"kind": kind, "removed": removed, "added": added})

    category_rows = []
    for cat, counts in sorted(report.summary.get("by_category", {}).items()):
        label = _CATEGORY_LABELS.get(cat, cat)
        total = sum(counts.values())
        if total:
            category_rows.append(
                {
                    "label": label,
                    "added": counts.get("added", 0),
                    "removed": counts.get("removed", 0),
                    "modified": counts.get("modified", 0),
                }
            )

    return {
        "files_by_status": files_by_status,
        "methods_a": methods_a,
        "methods_b": methods_b,
        "methods_modified": methods_modified,
        "apis_removed": apis_removed,
        "apis_added": apis_added,
        "api_kinds": api_kinds,
        "category_rows": category_rows,
        "apis_in_both_count": len(report.apis_in_both),
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
