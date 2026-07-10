from __future__ import annotations

import os
import shutil
import sys
from pathlib import Path


def is_windows() -> bool:
    return os.name == "nt" or sys.platform.startswith("win")


def find_git_executable() -> str | None:
    """Locate git binary; on Windows also checks Program Files."""
    git = shutil.which("git")
    if git:
        return git
    if is_windows():
        for candidate in (
            r"C:\Program Files\Git\cmd\git.exe",
            r"C:\Program Files (x86)\Git\cmd\git.exe",
        ):
            if Path(candidate).is_file():
                return candidate
    return None


def configure_stdio() -> None:
    """Use UTF-8 for stdout/stderr when the console supports it (notably Windows)."""
    for stream in (sys.stdout, sys.stderr):
        reconfigure = getattr(stream, "reconfigure", None)
        if callable(reconfigure):
            try:
                reconfigure(encoding="utf-8", errors="replace")
            except (OSError, ValueError):
                pass


def resolve_repo_path(path: str | Path) -> Path:
    """Resolve repo root; supports `.git` file (worktrees) and Windows paths."""
    resolved = Path(path).expanduser()
    if not resolved.is_absolute():
        resolved = (Path.cwd() / resolved).resolve()
    else:
        resolved = resolved.resolve()

    if is_git_repo(resolved):
        return resolved

    raise ValueError(f"Not a git repository: {resolved}")


def is_git_repo(path: Path) -> bool:
    git_dir = path / ".git"
    if git_dir.is_dir():
        return True
    if git_dir.is_file():
        # Worktree: .git is a pointer file
        try:
            content = git_dir.read_text(encoding="utf-8", errors="replace").strip()
            return content.startswith("gitdir:")
        except OSError:
            return False
    return False


def resolve_output_path(path: str | Path) -> Path:
    """Resolve output path; create parent dirs; handle Windows long paths."""
    out = Path(path).expanduser()
    if not out.is_absolute():
        out = (Path.cwd() / out).resolve()
    else:
        out = out.resolve()

    if is_windows() and len(str(out)) > 240 and not str(out).startswith("\\\\?\\"):
        # Enable long-path prefix when approaching legacy MAX_PATH
        out = Path("\\\\?\\" + str(out))
    return out


def normalize_line_endings(text: str) -> str:
    return text.replace("\r\n", "\n").replace("\r", "\n")


def safe_display_path(path: str | Path) -> str:
    """Human-readable path for logs and HTML (forward slashes)."""
    return str(path).replace("\\", "/")


def is_probably_binary(data: bytes) -> bool:
    if not data:
        return False
    if b"\x00" in data[:8192]:
        return True
    # High ratio of non-text bytes
    sample = data[:4096]
    non_text = sum(1 for b in sample if b < 9 or (13 < b < 32 and b != 27))
    return non_text / max(len(sample), 1) > 0.30


def decode_git_blob(data: bytes) -> str | None:
    """Decode git blob bytes with UTF-8 / BOM / Latin-1 fallbacks."""
    if is_probably_binary(data):
        return None
    for encoding in ("utf-8-sig", "utf-8", "latin-1"):
        try:
            text = normalize_line_endings(data.decode(encoding))
            return text.lstrip("\ufeff")
        except UnicodeDecodeError:
            continue
    return normalize_line_endings(data.decode("utf-8", errors="replace")).lstrip("\ufeff")
