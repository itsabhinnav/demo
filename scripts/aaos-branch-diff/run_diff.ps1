# AAOS branch diff — Windows PowerShell launcher
# Usage: .\run_diff.ps1 android-14 android-15 [-Repo C:\path\to\repo] [-Output report.html]

param(
    [Parameter(ValueFromRemainingArguments = $true)]
    [string[]]$Args
)

$ErrorActionPreference = "Stop"
Set-Location $PSScriptRoot

$venvPython = Join-Path $PSScriptRoot ".venv\Scripts\python.exe"

if (-not (Test-Path $venvPython)) {
    Write-Host "Creating virtual environment..."
    python -m venv .venv
    if ($LASTEXITCODE -ne 0) {
        Write-Error "Failed to create venv. Install Python 3.10+ from https://www.python.org/downloads/"
    }
    & .\.venv\Scripts\Activate.ps1
    python -m pip install --upgrade pip
    pip install -r requirements.txt
} else {
    & .\.venv\Scripts\Activate.ps1
}

python -m aaos_branch_diff @Args
exit $LASTEXITCODE
