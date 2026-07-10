#!/usr/bin/env python3
"""CLI entry point for AAOS branch diff analyzer."""

from __future__ import annotations

import argparse
import sys
from pathlib import Path

from .diff_engine import build_diff_report
from .git_scanner import GitScanner
from .report_generator import generate_html_report


def main(argv: list[str] | None = None) -> int:
    parser = argparse.ArgumentParser(
        description=(
            "Compare two git branches of an Android Automotive (AAOS) project and "
            "generate a detailed HTML report at project, file, method, and API levels."
        ),
        formatter_class=argparse.RawDescriptionHelpFormatter,
        epilog="""
Examples:
  %(prog)s android-14 android-15
  %(prog)s android-14 android-15 --repo /path/to/aaos-app -o report.html
  %(prog)s main feature/aaos-15 --include-unchanged

Branch order:
  First branch is the baseline (e.g. Android 14). Second is the target (e.g. Android 15).
  The report shows what was added, removed, or modified when moving from baseline to target.
        """,
    )
    parser.add_argument(
        "branch_a",
        help="Baseline branch (e.g. android-14)",
    )
    parser.add_argument(
        "branch_b",
        help="Target branch to compare against baseline (e.g. android-15)",
    )
    parser.add_argument(
        "--repo",
        "-r",
        default=".",
        help="Path to the git repository root (default: current directory)",
    )
    parser.add_argument(
        "--output",
        "-o",
        default="aaos-branch-diff-report.html",
        help="Output HTML report path (default: aaos-branch-diff-report.html)",
    )
    parser.add_argument(
        "--include-unchanged",
        action="store_true",
        help="Include unchanged files in the file-level diff section",
    )
    parser.add_argument(
        "--json-summary",
        action="store_true",
        help="Also print a JSON summary to stdout",
    )

    args = parser.parse_args(argv)
    repo_path = Path(args.repo).resolve()

    if not (repo_path / ".git").exists():
        print(f"Error: not a git repository: {repo_path}", file=sys.stderr)
        return 1

    try:
        scanner = GitScanner(repo_path)
        scanner.resolve_ref(args.branch_a)
        scanner.resolve_ref(args.branch_b)
    except Exception as exc:
        print(f"Error resolving branches: {exc}", file=sys.stderr)
        return 1

    print(f"Analyzing {args.branch_a} → {args.branch_b} in {repo_path} ...")
    report = build_diff_report(
        scanner,
        args.branch_a,
        args.branch_b,
        include_unchanged=args.include_unchanged,
    )

    out = generate_html_report(report, args.output)
    print(f"Report written to {out}")
    print(
        f"  Files changed: {report.summary['files']['total_changed']} "
        f"(+{report.summary['files']['added']} "
        f"-{report.summary['files']['removed']} "
        f"~{report.summary['files']['modified']})"
    )
    print(
        f"  Methods: {report.summary['methods']['only_in_a']} only in {args.branch_a}, "
        f"{report.summary['methods']['only_in_b']} only in {args.branch_b}"
    )
    print(
        f"  APIs: {report.summary['apis']['only_in_a']} only in {args.branch_a}, "
        f"{report.summary['apis']['only_in_b']} only in {args.branch_b}"
    )

    if args.json_summary:
        import json

        print(json.dumps(report.summary, indent=2, default=str))

    return 0


if __name__ == "__main__":
    raise SystemExit(main())
