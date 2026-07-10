# AAOS Branch Diff

Python tool to compare two git branches of an **Android Automotive (AAOS)** project and produce a detailed **HTML report** at:

- **Project level** — file counts, classes, methods, API references per branch
- **File level** — added / removed / modified / renamed, split by source, test, gradle, resources
- **Method level** — methods only in either branch, or modified between branches (Java + Kotlin)
- **API level** — imports and usages classified as AOSP, AAOS (`android.car.*`), AndroidX, vendor, project, third-party

Designed for repos where an Android 15 branch diverged long ago from Android 14 and the trees have drifted significantly.

Works on **Linux, macOS, and Windows**.

## Requirements

- Python 3.10+
- [Git](https://git-scm.com/) on `PATH` (Windows: Git for Windows)
- Git repository with both branches available locally

## Install

### Linux / macOS

```bash
cd scripts/aaos-branch-diff
python3 -m venv .venv
source .venv/bin/activate
pip install -r requirements.txt
```

### Windows (Command Prompt)

```bat
cd scripts\aaos-branch-diff
python -m venv .venv
.venv\Scripts\activate
pip install -r requirements.txt
```

### Windows (PowerShell)

```powershell
cd scripts\aaos-branch-diff
python -m venv .venv
.\.venv\Scripts\Activate.ps1
pip install -r requirements.txt
```

## Analysis pipeline (v1.4)

The tool runs in **three phases** for a robust comparison:

1. **AST index — branch A** — tree-sitter parses every `.java` / `.kt` file on the baseline branch (full branch, not just changed files). Falls back to javalang (Java) or regex (Kotlin) if needed.
2. **AST index — branch B** — same full parse on the target branch.
3. **Git + semantic diff** — `git diff` for file add/remove/rename, then compares indexed classes, methods, and APIs between the two AST snapshots.

The HTML report includes an **AST index** table showing parse coverage per branch.

## Usage

Run from your AAOS project root (or pass `--repo`):

```bash
# Baseline = Android 14 branch, target = Android 15 branch
python -m aaos_branch_diff android-14 android-15

# Windows path example
python -m aaos_branch_diff android-14 android-15 --repo C:\work\aaos-app -o report.html

# Linux / macOS
python -m aaos_branch_diff android-14 android-15 \
  --repo /path/to/your/aaos-app \
  --output reports/a14-vs-a15.html
```

### Windows launchers (auto-create venv)

```bat
run_diff.bat android-14 android-15 --repo C:\work\aaos-app -o report.html
```

```powershell
.\run_diff.ps1 android-14 android-15 -Repo C:\work\aaos-app -Output report.html
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
| `--verbose`, `-v` | Progress and warnings on stderr |
| `--max-file-size-mb` | Skip files larger than N MiB (default: 2) |
| `--log-file` | Write diagnostics to a log file |

**Branch order matters:** the report describes changes when moving from `branch_a` → `branch_b` (what was added/removed in B relative to A).

## Robustness

- **Windows paths** — backslashes normalized; long output paths supported
- **CRLF / UTF-8** — line endings and encodings normalized before parsing
- **Binary / large files** — skipped with warnings (not crashed)
- **Parse failures** — per-file try/except; fallback regex parser for Java
- **Git worktrees** — `.git` pointer files supported
- **Unrelated histories** — merge-base failure handled gracefully
- **Atomic report write** — temp file + rename (safe on Windows)
- **Skipped paths** — `build/`, `.gradle/`, `generated/` excluded by default

Warnings appear in the HTML report and with `--verbose`.

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

- **Java** — tree-sitter AST (primary), javalang fallback
- **Kotlin** — tree-sitter AST (primary), regex fallback
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
5. **Warnings** — skipped files, parse issues (if any)
6. **Commits** — `git log` each way between branches

## Tests

```bash
cd scripts/aaos-branch-diff
source .venv/bin/activate   # or .venv\Scripts\activate on Windows
python -m unittest discover -s tests -v
```

## Limitations

- Kotlin parsing is heuristic (not a full compiler frontend); complex DSLs may miss edge cases.
- Method “modified” detection uses a body snippet hash, not semantic diff.
- Generated / protobuf sources under `generated/` are skipped by default.
- Run locally where both branches are fetched: `git fetch origin android-14 android-15`.

## Project layout

```
scripts/aaos-branch-diff/
├── requirements.txt
├── run_diff.py
├── run_diff.bat          # Windows CMD launcher
├── run_diff.ps1          # Windows PowerShell launcher
├── README.md
├── tests/
│   └── test_platform.py
└── aaos_branch_diff/
    ├── __main__.py          # CLI
    ├── platform_utils.py    # Windows / encoding helpers
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
