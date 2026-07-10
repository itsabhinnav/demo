from __future__ import annotations

from datetime import datetime, timezone
from pathlib import Path

from jinja2 import Environment, FileSystemLoader, select_autoescape

from . import __version__
from .models import DiffReport


def generate_html_report(report: DiffReport, output_path: str | Path) -> Path:
    template_dir = Path(__file__).parent / "templates"
    env = Environment(
        loader=FileSystemLoader(str(template_dir)),
        autoescape=select_autoescape(["html", "xml"]),
    )
    template = env.get_template("report.html.j2")

    file_diffs = []
    for fd in report.file_diffs:
        file_diffs.append(
            {
                "path": fd.path,
                "status": fd.status.value,
                "category": fd.category.value,
                "language": fd.language.value,
                "old_path": fd.old_path,
                "classes_added": fd.classes_added,
                "classes_removed": fd.classes_removed,
                "methods_added": fd.methods_added,
                "methods_removed": fd.methods_removed,
                "methods_modified": fd.methods_modified,
                "apis_added": fd.apis_added,
                "apis_removed": fd.apis_removed,
            }
        )

    html = template.render(
        version=__version__,
        repo_path=report.repo_path,
        branch_a=report.branch_a,
        branch_b=report.branch_b,
        commit_a=report.commit_a,
        commit_b=report.commit_b,
        merge_base=report.merge_base,
        generated_at=datetime.now(timezone.utc).strftime("%Y-%m-%d %H:%M UTC"),
        summary=report.summary,
        file_diffs=file_diffs,
        methods_only_in_a=report.methods_only_in_a,
        methods_only_in_b=report.methods_only_in_b,
        methods_in_both=report.methods_in_both,
        apis_only_in_a=report.apis_only_in_a,
        apis_only_in_b=report.apis_only_in_b,
        apis_in_both=report.apis_in_both,
    )

    out = Path(output_path).resolve()
    out.parent.mkdir(parents=True, exist_ok=True)
    out.write_text(html, encoding="utf-8")
    return out
