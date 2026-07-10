@echo off
setlocal EnableExtensions
cd /d "%~dp0"

REM AAOS branch diff — Windows launcher
REM Usage: run_diff.bat android-14 android-15 [--repo C:\path\to\repo] [-o report.html]

if not exist ".venv\Scripts\python.exe" (
  echo Creating virtual environment...
  python -m venv .venv
  if errorlevel 1 (
    echo Failed to create venv. Install Python 3.10+ from https://www.python.org/downloads/
    exit /b 1
  )
  call .venv\Scripts\activate.bat
  python -m pip install --upgrade pip
  pip install -r requirements.txt
) else (
  call .venv\Scripts\activate.bat
)

python -m aaos_branch_diff %*
set EXITCODE=%ERRORLEVEL%
endlocal & exit /b %EXITCODE%
