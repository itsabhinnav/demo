# AAOS Branch Diff

Python tool to compare two git branches of an **Android Automotive (AAOS)** project and produce a detailed **HTML report** at:

- **Project level** — file counts, classes, methods, API references per branch
- **File level** — added / removed / modified / renamed, split by source, test, gradle, resources
- **Method level** — methods only in either branch, or modified between branches (Java + Kotlin)
- **API level** — imports and usages classified as AOSP, AAOS (`android.car.*`), AndroidX, vendor, project, third-party

Designed for repos where an Android 15 branch diverged long ago from Android 14 and the trees have drifted significantly.

## Requirements

- Python 3.10+
- Git repository with both branches available locally

## Install

```bash
cd scripts/aaos-branch-diff
python3 -m venv .venv
source .venv/bin/activate   # Windows: .venv\Scripts\activate
pip install -r requirements.txt
```

## Usage

Run from your AAOS project root (or pass `--repo`):

```bash
# Baseline = Android 14 branch, target = Android 15 branch
python -m aaos_branch_diff android-14 android-15

# Custom repo path and output file
python -m aaos_branch_diff android-14 android-15 \
  --repo /path/to/your/aaos-app \
  --output reports/a14-vs-a15.html

# Or use the launcher script
./run_diff.py android-14 android-15 -o report.html
```

### Arguments

| Argument | Description |
|----------|-------------|
| `branch_a` | Baseline branch (e.g. `android-14`) |
| `branch_b` | Target branch (e.g. `android-15`) |
| `--repo`, `-r` | Git repo root (default: `.`) |
| `--output`, `-o` | HTML output path (default: `aaos-branch-diff-report.html`) |
| `--include-unchanged` | Include unchanged files in file diff table |
| `--json-summary` | Print JSON summary to stdout |

**Branch order matters:** the report describes changes when moving from `branch_a` → `branch_b` (what was added/removed in B relative to A).

## What it analyzes

### File categories

| Category | Detection |
|----------|-----------|
| `source_main` | `src/main/java`, `src/main/kotlin` |
| `source_test` | `src/test`, `src/androidTest`, `*Test.java/kt` |
| `gradle` | `*.gradle`, `*.gradle.kts`, `gradle/` |
| `resource` | `res/`, `assets/`, layouts |
| `manifest` | `AndroidManifest.xml` |

### Languages

- **Java** — parsed with `javalang` (classes, interfaces, enums, methods, imports)
- **Kotlin** — regex-based extraction (classes, objects, interfaces, `fun` methods, imports)
- **Gradle** — dependency coordinates from `implementation` / `api` lines

### API classification

| Kind | Examples |
|------|----------|
| `aosp` | `android.*`, `com.android.*` |
| `aaos` | `android.car.*`, `android.hardware.automotive.*` |
| `androidx` | `androidx.*` |
| `vendor` | `vendor.*`, `oem.*`, OEM-specific packages |
| `project` | Your app’s own packages under `src/` |
| `third_party` | Other external libraries |

Vendor heuristics can be extended in `aaos_branch_diff/parsers/java_parser.py` (`_classify_api`).

### Git features used

- `git rev-parse` — resolve branch SHAs
- `git merge-base` — common ancestor
- `git diff --name-status -M` — file add/remove/rename/modify
- `git diff --numstat` — line change stats
- `git ls-tree` / `git show` — per-branch file trees and contents
- `git log A..B` — commits unique to each branch

## Output

Open the generated HTML in a browser. Sections include:

1. **Project summary** — cards and tables by category/language
2. **File diff** — filterable table with expandable per-file class/method/API details
3. **Method diff** — methods only in A, only in B, or modified
4. **API diff** — AOSP vs AAOS vs vendor breakdown
5. **Commits** — `git log` each way between branches

## Limitations

- Kotlin parsing is heuristic (not a full compiler frontend); complex DSLs may miss edge cases.
- Method “modified” detection uses a body snippet hash, not semantic diff.
- Generated / protobuf sources are not specially excluded (add paths to skip in `git_scanner.py` if needed).
- Run locally where both branches are fetched: `git fetch origin android-14 android-15`.

## Project layout

```
scripts/aaos-branch-diff/
├── requirements.txt
├── run_diff.py
├── README.md
└── aaos_branch_diff/
    ├── __main__.py          # CLI
    ├── git_scanner.py       # Git operations
    ├── diff_engine.py       # Cross-branch comparison
    ├── file_classifier.py   # source / test / gradle
    ├── models.py
    ├── report_generator.py
    ├── parsers/
    │   ├── java_parser.py
    │   └── kotlin_parser.py
    └── templates/
        └── report.html.j2
```

## License

Use and adapt freely for your AAOS projects.
