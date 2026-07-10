#!/usr/bin/env python3
"""CLI entry point for AAOS branch diff analyzer."""

from __future__ import annotations

import argparse
import json
import logging
import sys
from pathlib import Path

from .diff_engine import build_diff_report
from .git_scanner import GitScanner, GitScannerError
from .platform_utils import configure_stdio, resolve_output_path, resolve_repo_path, safe_display_path
from .report_generator import generate_html_report


def main(argv: list[str] | None = None) -> int:
    configure_stdio()

    parser = argparse.ArgumentParser(
        prog="aaos-branch-diff",
        description=(
            "Compare two git branches of an Android Automotive (AAOS) project and "
            "generate a detailed HTML report at project, file, method, and API levels."
        ),
        formatter_class=argparse.RawDescriptionHelpFormatter,
        epilog="""
Examples:
  %(prog)s android-14 android-15
  %(prog)s android-14 android-15 --repo C:\\work\\aaos-app -o report.html
  %(prog)s android-14 android-15 --repo /path/to/aaos-app -o reports/a14-vs-a15.html
  %(prog)s main feature/aaos-15 --verbose --json-summary

Windows:
  .venv\\Scripts\\activate
  python -m aaos_branch_diff android-14 android-15 --repo C:\\src\\ivi

  Or double-click / run: run_diff.bat android-14 android-15

Branch order:
  First branch is the baseline (e.g. Android 14). Second is the target (e.g. Android 15).
  The report shows what was added, removed, or modified when moving from baseline to target.
        """,
    )
    parser.add_argument("branch_a", help="Baseline branch (e.g. android-14)")
    parser.add_argument("branch_b", help="Target branch (e.g. android-15)")
    parser.add_argument(
        "--repo", "-r", default=".",
        help="Path to the git repository root (default: current directory)",
    )
    parser.add_argument(
        "--output", "-o", default="aaos-branch-diff-report.html",
        help="Output HTML report path (default: aaos-branch-diff-report.html)",
    )
    parser.add_argument(
        "--include-unchanged", action="store_true",
        help="Include unchanged files in the file-level diff section",
    )
    parser.add_argument(
        "--json-summary", action="store_true",
        help="Also print a JSON summary to stdout",
    )
    parser.add_argument(
        "--verbose", "-v", action="store_true",
        help="Print progress and warnings to stderr",
    )
    parser.add_argument(
        "--max-file-size-mb", type=float, default=2.0,
        help="Skip source files larger than this many MiB (default: 2)",
    )
    parser.add_argument(
        "--log-file",
        help="Optional log file path for warnings and diagnostics",
    )

    args = parser.parse_args(argv)

    if args.verbose or args.log_file:
        handlers: list[logging.Handler] = []
        if args.verbose:
            handlers.append(logging.StreamHandler(sys.stderr))
        if args.log_file:
            handlers.append(logging.FileHandler(args.log_file, encoding="utf-8"))
        logging.basicConfig(
            level=logging.INFO,
            format="%(levelname)s: %(message)s",
            handlers=handlers or None,
        )

    try:
        repo_path = resolve_repo_path(args.repo)
    except ValueError as exc:
        print(f"Error: {exc}", file=sys.stderr)
        return 1

    max_bytes = int(args.max_file_size_mb * 1024 * 1024)

    try:
        scanner = GitScanner(
            repo_path,
            max_file_bytes=max_bytes,
            verbose=args.verbose,
        )
        scanner.resolve_ref(args.branch_a)
        scanner.resolve_ref(args.branch_b)
    except GitScannerError as exc:
        print(f"Error: {exc}", file=sys.stderr)
        return 1

    print(f"Analyzing {args.branch_a} -> {args.branch_b} in {safe_display_path(repo_path)} ...")

    try:
        report = build_diff_report(
            scanner,
            args.branch_a,
            args.branch_b,
            include_unchanged=args.include_unchanged,
        )
    except GitScannerError as exc:
        print(f"Error: {exc}", file=sys.stderr)
        return 1
    except KeyboardInterrupt:
        print("\nInterrupted.", file=sys.stderr)
        return 130

    try:
        out = generate_html_report(report, resolve_output_path(args.output))
    except OSError as exc:
        print(f"Error writing report: {exc}", file=sys.stderr)
        return 1

    print(f"Report written to {safe_display_path(out)}")
    sf = report.summary.get("source_focus", {})
    sf_files = sf.get("files", report.summary["files"])
    sf_methods = sf.get("methods", report.summary["methods"])
    sf_apis = sf.get("apis", report.summary["apis"])
    print(
        f"  Source files changed: {sf_files.get('total_changed', 0)} "
        f"(+{sf_files.get('added', 0)} "
        f"-{sf_files.get('removed', 0)} "
        f"~{sf_files.get('modified', 0)})"
    )
    print(
        f"  Source methods: {sf_methods.get('only_in_a', 0)} only in {args.branch_a}, "
        f"{sf_methods.get('only_in_b', 0)} only in {args.branch_b}"
    )
    print(
        f"  Source APIs: {sf_apis.get('only_in_a', 0)} only in {args.branch_a}, "
        f"{sf_apis.get('only_in_b', 0)} only in {args.branch_b}"
    )
    test_files = sf.get("test_files", {})
    if test_files.get("total_changed", 0):
        print(f"  Test files changed: {test_files['total_changed']} (see collapsed section in report)")

    warnings = report.summary.get("warnings", [])
    if warnings:
        print(f"  Warnings: {len(warnings)} (see report or re-run with --verbose)", file=sys.stderr)

    if args.json_summary:
        print(json.dumps(report.summary, indent=2, default=str))

    return 0


if __name__ == "__main__":
    raise SystemExit(main())
