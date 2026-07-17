# Build Design app + Scalable UI RROs and copy APKs into ./prebuilt.
# Usage (from repo root):
#   .\scripts\sync-prebuilts.ps1

$ErrorActionPreference = "Stop"

$Root = Resolve-Path (Join-Path $PSScriptRoot "..")
$Prebuilt = Join-Path $Root "prebuilt"
New-Item -ItemType Directory -Force -Path $Prebuilt | Out-Null

Push-Location $Root
try {
    & .\gradlew.bat :app:assembleDebug :scalable-ui-rro:assembleDebug :framework-scalable-rro:assembleDebug
    if ($LASTEXITCODE -ne 0) {
        throw "Gradle build failed (exit $LASTEXITCODE)"
    }

    Copy-Item -Force `
        (Join-Path $Root "app\build\outputs\apk\debug\app-debug.apk") `
        (Join-Path $Prebuilt "app-debug.apk")
    Copy-Item -Force `
        (Join-Path $Root "scalable-ui-rro\build\outputs\apk\debug\scalable-ui-rro-debug.apk") `
        (Join-Path $Prebuilt "DesignScalableUiRRO.apk")
    Copy-Item -Force `
        (Join-Path $Root "framework-scalable-rro\build\outputs\apk\debug\framework-scalable-rro-debug.apk") `
        (Join-Path $Prebuilt "DesignFrameworkScalableUiRRO.apk")

    # Keep Dewd bridge APKs alongside product RROs when present
    $DewdDesign = Join-Path $Root "scalable-ui-rro\prebuilt\DewdDynamicAospRRO-design.apk"
    if (Test-Path $DewdDesign) {
        Copy-Item -Force $DewdDesign (Join-Path $Prebuilt "DewdDynamicAospRRO-design.apk")
    }
    $DewdOrig = Join-Path $Root "scalable-ui-rro\prebuilt\DewdDynamicAospRRO.orig.apk"
    if (Test-Path $DewdOrig) {
        Copy-Item -Force $DewdOrig (Join-Path $Prebuilt "DewdDynamicAospRRO.orig.apk")
    }

    @"
# Prebuilt APKs — Adaptive Space

| File | Package / role |
|------|----------------|
| ``app-debug.apk`` | ``com.test.design`` — Design demo + Adaptive Space dashboard |
| ``DesignScalableUiRRO.apk`` | ``com.test.design.systemui.scalableui`` — Map-Under-Apps panels |
| ``DesignFrameworkScalableUiRRO.apk`` | ``com.test.design.framework.scalableui`` — remote insets handshake |
| ``DewdDynamicAospRRO-design.apk`` | Dewd interim bridge (patched) |
| ``DewdDynamicAospRRO.orig.apk`` | Dewd stock Dynamic RRO (patch input) |

Rebuild: ``./scripts/sync-prebuilts.sh`` or ``.\scripts\sync-prebuilts.ps1``

Install: ``./scripts/install-prebuilts.sh`` or ``.\scripts\install-prebuilts.ps1``
"@ | Set-Content -Path (Join-Path $Prebuilt "README.md") -Encoding utf8

    Get-ChildItem $Prebuilt | Format-Table Name, Length, LastWriteTime
    Write-Host "Synced prebuilts → $Prebuilt"
}
finally {
    Pop-Location
}
