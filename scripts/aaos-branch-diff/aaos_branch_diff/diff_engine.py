from __future__ import annotations

from collections import defaultdict

from .file_classifier import classify_file, detect_language
from .git_scanner import GitScanner, summarize_branch
from .models import (
    ApiReference,
    BranchSnapshot,
    ChangeStatus,
    DiffReport,
    FileCategory,
    FileDiff,
    MethodInfo,
)


def _method_key(m: MethodInfo) -> str:
    return f"{m.class_name}#{m.signature}"


def _api_key(a: ApiReference) -> str:
    return f"{a.qualified_name}@{a.usage_context}"


def build_diff_report(
    scanner: GitScanner,
    branch_a: str,
    branch_b: str,
    include_unchanged: bool = False,
) -> DiffReport:
    commit_a = scanner.resolve_ref(branch_a)[:12]
    commit_b = scanner.resolve_ref(branch_b)[:12]
    merge_base = scanner.merge_base(branch_a, branch_b)[:12]

    files_a = scanner.list_files(branch_a)
    files_b = scanner.list_files(branch_b)
    paths_a = set(files_a.keys())
    paths_b = set(files_b.keys())
    all_paths = paths_a | paths_b

    name_status = scanner.diff_name_status(branch_a, branch_b)
    diff_stats = scanner.diff_stat(branch_a, branch_b)

    status_map: dict[str, tuple[ChangeStatus, str | None]] = {}
    for code, path, old_path in name_status:
        if code == "A":
            status_map[path] = (ChangeStatus.ADDED, None)
        elif code == "D":
            status_map[path] = (ChangeStatus.REMOVED, None)
        elif code == "R":
            status_map[path] = (ChangeStatus.RENAMED, old_path)
        elif code == "M":
            status_map[path] = (ChangeStatus.MODIFIED, None)
        else:
            status_map[path] = (ChangeStatus.MODIFIED, old_path)

    for p in paths_b - paths_a:
        if p not in status_map:
            status_map[p] = (ChangeStatus.ADDED, None)
    for p in paths_a - paths_b:
        if p not in status_map:
            status_map[p] = (ChangeStatus.REMOVED, None)

    relevant = {p for p, (st, _) in status_map.items() if st != ChangeStatus.UNCHANGED}
    relevant |= set(status_map.keys())

    def progress(done: int, total: int, path: str) -> None:
        if scanner.verbose and done % 50 == 0:
            print(f"  Snapshot progress {done}/{total}")

    print(f"Building snapshot for {branch_a} …")
    snapshot_a = scanner.build_snapshot(branch_a, relevant | paths_a, progress=progress if scanner.verbose else None)
    print(f"Building snapshot for {branch_b} …")
    snapshot_b = scanner.build_snapshot(branch_b, relevant | paths_b, progress=progress if scanner.verbose else None)

    file_diffs: list[FileDiff] = []

    for path in sorted(all_paths):
        in_a = path in paths_a
        in_b = path in paths_b

        if in_a and in_b:
            sha_a = files_a.get(path)
            sha_b = files_b.get(path)
            if sha_a == sha_b and not include_unchanged:
                continue
            status = status_map.get(path, (ChangeStatus.UNCHANGED, None))[0]
            if status == ChangeStatus.UNCHANGED and sha_a != sha_b:
                status = ChangeStatus.MODIFIED
        elif in_b:
            status = ChangeStatus.ADDED
        else:
            status = ChangeStatus.REMOVED

        old_path = status_map.get(path, (status, None))[1]
        category = classify_file(path)
        language = detect_language(path, category)

        snap_a = snapshot_a.files.get(path)
        snap_b = snapshot_b.files.get(path)

        fd = _diff_file(path, status, category, language, old_path, snap_a, snap_b)
        ins, dels = diff_stats.get(path, (0, 0))
        fd.similarity = _similarity(ins, dels, snap_a, snap_b)
        file_diffs.append(fd)

    methods_a = _collect_methods(snapshot_a)
    methods_b = _collect_methods(snapshot_b)
    apis_a = _collect_apis(snapshot_a)
    apis_b = _collect_apis(snapshot_b)

    keys_a = set(methods_a.keys())
    keys_b = set(methods_b.keys())
    methods_only_a = [methods_a[k] for k in sorted(keys_a - keys_b)]
    methods_only_b = [methods_b[k] for k in sorted(keys_b - keys_a)]
    methods_both = []
    methods_modified_source: list[str] = []
    methods_modified_test: list[str] = []
    for k in sorted(keys_a & keys_b):
        ma, mb = methods_a[k], methods_b[k]
        if ma.body_hash != mb.body_hash:
            methods_both.append((k, "modified"))
            if ma.is_test:
                methods_modified_test.append(k)
            else:
                methods_modified_source.append(k)
        else:
            methods_both.append((k, "unchanged"))

    api_keys_a = set(apis_a.keys())
    api_keys_b = set(apis_b.keys())
    apis_only_a = [apis_a[k] for k in sorted(api_keys_a - api_keys_b)]
    apis_only_b = [apis_b[k] for k in sorted(api_keys_b - api_keys_a)]
    apis_in_both = sorted(api_keys_a & api_keys_b)

    def _is_test_api(api: ApiReference) -> bool:
        return classify_file(api.file_path) == FileCategory.SOURCE_TEST

    methods_only_a_source = [m for m in methods_only_a if not m.is_test]
    methods_only_a_test = [m for m in methods_only_a if m.is_test]
    methods_only_b_source = [m for m in methods_only_b if not m.is_test]
    methods_only_b_test = [m for m in methods_only_b if m.is_test]
    apis_only_a_source = [a for a in apis_only_a if not _is_test_api(a)]
    apis_only_a_test = [a for a in apis_only_a if _is_test_api(a)]
    apis_only_b_source = [a for a in apis_only_b if not _is_test_api(a)]
    apis_only_b_test = [a for a in apis_only_b if _is_test_api(a)]

    production_file_diffs = [f for f in file_diffs if f.category != FileCategory.SOURCE_TEST]
    test_file_diffs = [f for f in file_diffs if f.category == FileCategory.SOURCE_TEST]

    def _file_counts(diffs: list[FileDiff]) -> dict:
        return {
            "added": sum(1 for f in diffs if f.status == ChangeStatus.ADDED),
            "removed": sum(1 for f in diffs if f.status == ChangeStatus.REMOVED),
            "modified": sum(1 for f in diffs if f.status == ChangeStatus.MODIFIED),
            "renamed": sum(1 for f in diffs if f.status == ChangeStatus.RENAMED),
            "total_changed": len(diffs),
        }

    api_by_kind: dict[str, dict[str, int]] = defaultdict(lambda: defaultdict(int))
    api_by_kind_source: dict[str, dict[str, int]] = defaultdict(lambda: defaultdict(int))
    for a in apis_only_a:
        api_by_kind[a.kind.value]["removed_in_b"] += 1
        if not _is_test_api(a):
            api_by_kind_source[a.kind.value]["removed_in_b"] += 1
    for a in apis_only_b:
        api_by_kind[a.kind.value]["added_in_b"] += 1
        if not _is_test_api(a):
            api_by_kind_source[a.kind.value]["added_in_b"] += 1

    summary_a = summarize_branch(snapshot_a)
    summary_b = summarize_branch(snapshot_b)

    by_category: dict[str, dict[str, int]] = defaultdict(lambda: defaultdict(int))
    by_language: dict[str, dict[str, int]] = defaultdict(lambda: defaultdict(int))
    for fd in file_diffs:
        by_category[fd.category.value][fd.status.value] += 1
        by_language[fd.language.value][fd.status.value] += 1

    report = DiffReport(
        repo_path=str(scanner.repo_path),
        branch_a=branch_a,
        branch_b=branch_b,
        commit_a=commit_a,
        commit_b=commit_b,
        merge_base=merge_base,
        summary={
            "branch_a": _summary_dict(summary_a),
            "branch_b": _summary_dict(summary_b),
            "files": {
                "added": sum(1 for f in file_diffs if f.status == ChangeStatus.ADDED),
                "removed": sum(1 for f in file_diffs if f.status == ChangeStatus.REMOVED),
                "modified": sum(1 for f in file_diffs if f.status == ChangeStatus.MODIFIED),
                "renamed": sum(1 for f in file_diffs if f.status == ChangeStatus.RENAMED),
                "total_changed": len(file_diffs),
            },
            "methods": {
                "only_in_a": len(methods_only_a),
                "only_in_b": len(methods_only_b),
                "in_both": len(methods_both),
                "modified": len(methods_modified_source) + len(methods_modified_test),
            },
            "apis": {
                "only_in_a": len(apis_only_a),
                "only_in_b": len(apis_only_b),
                "in_both": len(apis_in_both),
                "by_kind": dict(api_by_kind),
            },
            "source_focus": {
                "files": _file_counts(production_file_diffs),
                "test_files": _file_counts(test_file_diffs),
                "methods": {
                    "only_in_a": len(methods_only_a_source),
                    "only_in_b": len(methods_only_b_source),
                    "modified": len(methods_modified_source),
                    "test_only_in_a": len(methods_only_a_test),
                    "test_only_in_b": len(methods_only_b_test),
                    "test_modified": len(methods_modified_test),
                },
                "apis": {
                    "only_in_a": len(apis_only_a_source),
                    "only_in_b": len(apis_only_b_source),
                    "test_only_in_a": len(apis_only_a_test),
                    "test_only_in_b": len(apis_only_b_test),
                    "by_kind": dict(api_by_kind_source),
                },
                "branch_sizes": {
                    "source_files_a": summary_a.by_category.get("source_main", 0),
                    "source_files_b": summary_b.by_category.get("source_main", 0),
                    "test_files_a": summary_a.by_category.get("source_test", 0),
                    "test_files_b": summary_b.by_category.get("source_test", 0),
                    "source_methods_a": _count_source_methods(snapshot_a),
                    "source_methods_b": _count_source_methods(snapshot_b),
                },
            },
            "by_category": {k: dict(v) for k, v in by_category.items()},
            "by_language": {k: dict(v) for k, v in by_language.items()},
            "commits_b_ahead": scanner.log_oneline(branch_a, branch_b),
            "commits_a_ahead": scanner.log_oneline(branch_b, branch_a),
            "warnings": list(scanner.warnings),
        },
        file_diffs=file_diffs,
        methods_only_in_a=methods_only_a,
        methods_only_in_b=methods_only_b,
        methods_in_both=methods_both,
        apis_only_in_a=apis_only_a,
        apis_only_in_b=apis_only_b,
        apis_in_both=apis_in_both,
        snapshot_a=snapshot_a,
        snapshot_b=snapshot_b,
    )
    report.summary["methods_modified_source"] = methods_modified_source
    report.summary["methods_modified_test"] = methods_modified_test
    return report


def _count_source_methods(snapshot: BranchSnapshot) -> int:
    total = 0
    for f in snapshot.files.values():
        if f.category != FileCategory.SOURCE_MAIN:
            continue
        for c in f.classes:
            total += len(c.methods)
    return total


def _summary_dict(s) -> dict:
    return {
        "total_files": s.total_files,
        "by_category": s.by_category,
        "by_language": s.by_language,
        "class_count": s.class_count,
        "method_count": s.method_count,
        "api_count": s.api_count,
    }


def _similarity(ins, dels, snap_a, snap_b) -> float | None:
    if not snap_a or not snap_b:
        return None
    total = snap_a.line_count + snap_b.line_count
    if total == 0:
        return 100.0
    changed = ins + dels
    return max(0.0, 100.0 - (changed / max(total, 1)) * 100)


def _collect_methods(snapshot: BranchSnapshot) -> dict[str, MethodInfo]:
    out: dict[str, MethodInfo] = {}
    for f in snapshot.files.values():
        for c in f.classes:
            for m in c.methods:
                out[_method_key(m)] = m
    return out


def _collect_apis(snapshot: BranchSnapshot) -> dict[str, ApiReference]:
    out: dict[str, ApiReference] = {}
    for f in snapshot.files.values():
        for a in f.apis:
            out[_api_key(a)] = a
    return out


def _diff_file(path, status, category, language, old_path, snap_a, snap_b) -> FileDiff:
    classes_a = {c.qualified_name for c in (snap_a.classes if snap_a else [])}
    classes_b = {c.qualified_name for c in (snap_b.classes if snap_b else [])}
    methods_a = {_method_key(m) for c in (snap_a.classes if snap_a else []) for m in c.methods}
    methods_b = {_method_key(m) for c in (snap_b.classes if snap_b else []) for m in c.methods}
    apis_a = {_api_key(a) for a in (snap_a.apis if snap_a else [])}
    apis_b = {_api_key(a) for a in (snap_b.apis if snap_b else [])}

    mod_methods = []
    for k in methods_a & methods_b:
        if not (snap_a and snap_b):
            continue
        try:
            ma = next(m for c in snap_a.classes for m in c.methods if _method_key(m) == k)
            mb = next(m for c in snap_b.classes for m in c.methods if _method_key(m) == k)
            if ma.body_hash != mb.body_hash:
                mod_methods.append(k)
        except StopIteration:
            continue

    return FileDiff(
        path=path,
        status=status,
        category=category,
        language=language,
        old_path=old_path,
        methods_added=sorted(methods_b - methods_a),
        methods_removed=sorted(methods_a - methods_b),
        methods_modified=sorted(mod_methods),
        methods_unchanged=sorted((methods_a & methods_b) - set(mod_methods)),
        classes_added=sorted(classes_b - classes_a),
        classes_removed=sorted(classes_a - classes_b),
        apis_added=sorted(apis_b - apis_a),
        apis_removed=sorted(apis_a - apis_b),
    )
