# Install Design app + Adaptive Space Scalable UI RROs from ./prebuilt.
# Usage (from repo root):
#   .\scripts\install-prebuilts.ps1
#   .\scripts\install-prebuilts.ps1 -Dewd
#   .\scripts\install-prebuilts.ps1 -SkipReboot
#   .\scripts\install-prebuilts.ps1 -User 0
#
# SAFETY: never reboots to recovery/bootloader, never wipes, backs up overlays
# before replace, verifies APK signatures for Dewd bridge, rolls back on boot fail.

param(
    [switch]$Dewd,
    [switch]$SkipReboot,
    [switch]$NoLaunch,
    [int]$User = $(if ($env:ANDROID_USER) { [int]$env:ANDROID_USER } else { 10 })
)

$ErrorActionPreference = "Stop"

$Root = Resolve-Path (Join-Path $PSScriptRoot "..")
$Prebuilt = Join-Path $Root "prebuilt"
. (Join-Path $PSScriptRoot "lib\device-safety.ps1")

function Resolve-Apk {
    param([string[]]$Candidates)
    foreach ($path in $Candidates) {
        if (Test-Path $path) { return $path }
    }
    return $null
}

$AppApk = Resolve-Apk @(
    (Join-Path $Prebuilt "app-debug.apk"),
    (Join-Path $Root "app\build\outputs\apk\debug\app-debug.apk")
)
$SuApk = Resolve-Apk @(
    (Join-Path $Prebuilt "DesignScalableUiRRO.apk"),
    (Join-Path $Root "scalable-ui-rro\build\outputs\apk\debug\scalable-ui-rro-debug.apk")
)
$FwApk = Resolve-Apk @(
    (Join-Path $Prebuilt "DesignFrameworkScalableUiRRO.apk"),
    (Join-Path $Root "framework-scalable-rro\build\outputs\apk\debug\framework-scalable-rro-debug.apk")
)
$DewdApk = Resolve-Apk @(
    (Join-Path $Prebuilt "DewdDynamicAospRRO-design.apk"),
    (Join-Path $Root "scalable-ui-rro\prebuilt\DewdDynamicAospRRO-design.apk")
)

$Pkg = "com.test.design.systemui.scalableui"
$Activity = "com.test.design/.MainActivity"
$SuDest = "/system_ext/overlay/DesignScalableUiRRO.apk"
$FwDest = "/system_ext/overlay/DesignFrameworkScalableUiRRO.apk"
$DewdDest = "/system_ext/overlay/DewdDynamicAospRRO.apk"
$DewdBak = "$DewdDest.stock-bak"
$DewdOrig = "$DewdDest.orig"

if (-not $AppApk) {
    throw "Missing app APK — run: .\scripts\sync-prebuilts.ps1"
}

function Invoke-Adb {
    param([Parameter(ValueFromRemainingArguments = $true)][string[]]$AdbArgs)
    & adb @AdbArgs
    if ($LASTEXITCODE -ne 0) {
        throw "adb $($AdbArgs -join ' ') failed (exit $LASTEXITCODE)"
    }
}

function Invoke-AdbSoft {
    param([Parameter(ValueFromRemainingArguments = $true)][string[]]$AdbArgs)
    & adb @AdbArgs | Out-Null
}

Assert-AdbDeviceMode
Assert-NotRecoveryProps
Invoke-Adb wait-for-device

# App install is data-partition only (cannot brick / enter recovery).
try {
    Invoke-Adb install -r -t -d $AppApk
} catch {
    Write-Host "App install failed (signature mismatch?) — uninstalling and retrying…" -ForegroundColor Yellow
    Invoke-AdbSoft uninstall com.test.design
    Invoke-Adb install -r -t -d $AppApk
}
Write-Host "Installed app → $AppApk"

Invoke-Adb root
Invoke-Adb remount
Assert-AdbDeviceMode

Invoke-AdbSoft shell rm -f /system_ext/overlay/DesignFullscreen*.apk
Invoke-AdbSoft shell rm -f "/data/resource-cache/system_ext@overlay@Design*.apk@idmap"

$dewdProp = (& adb shell getprop car.dewd.config).Trim()
if (-not $Dewd -and $dewdProp -eq "dynamic") {
    Write-Host "WARNING: device is Dewd (car.dewd.config=dynamic). Use -Dewd — Design SystemUI RRO idmap fails and leaves a black map + rail." -ForegroundColor Yellow
}

$rollbackOverlay = $null
$rollbackBackup = $null

if ($Dewd) {
    if (-not $DewdApk) {
        throw "Missing Dewd bridge APK — run: .\scripts\sync-prebuilts.ps1"
    }
    # Never delete stock .orig / .stock-bak
    Invoke-AdbSoft shell rm -f `
        /system_ext/overlay/DesignScalableUiRRO.apk `
        /system_ext/overlay/DesignFrameworkScalableUiRRO.apk `
        /system_ext/overlay/DesignScalableFrameworkRRO.apk `
        /system_ext/overlay/DewdDynamicAospRRO-design.apk
    Invoke-AdbSoft shell "rm -f /data/resource-cache/system_ext@overlay@Design*.apk@idmap /data/resource-cache/system_ext@overlay@DewdDynamic*.apk@idmap"

    # Prefer preserving device .orig if present, else create .stock-bak
    $origExists = (adb shell "if [ -f '$DewdOrig' ]; then echo yes; fi" | Out-String).Trim()
    if ($origExists -ne "yes") {
        $null = Backup-RemoteOverlay -RemotePath $DewdDest -BackupPath $DewdBak
        $rollbackBackup = $DewdBak
    } else {
        $rollbackBackup = $DewdOrig
        Write-Host "Using device stock backup → $DewdOrig"
    }
    Push-OverlaySafe -LocalApk $DewdApk -RemotePath $DewdDest -RequireSignature
    $rollbackOverlay = $DewdDest
    Write-Host "Installed Dewd bridge → $DewdDest (rollback: $rollbackBackup)"
} else {
    if (-not $SuApk) {
        throw "Missing Scalable UI RRO — run: .\scripts\sync-prebuilts.ps1"
    }
    Assert-ApkSigned -ApkPath $SuApk
    Push-OverlaySafe -LocalApk $SuApk -RemotePath $SuDest
    if ($FwApk) {
        Assert-ApkSigned -ApkPath $FwApk
        Push-OverlaySafe -LocalApk $FwApk -RemotePath $FwDest
    }
    Write-Host "Installed Adaptive Space RROs → $SuDest"
}

if (-not $SkipReboot) {
    Invoke-SafeAdbReboot
    Wait-AndroidBootCompleted -TimeoutSec 180 `
        -RollbackOverlay $rollbackOverlay `
        -RollbackBackup $rollbackBackup
    Invoke-Adb root
    Assert-AdbDeviceMode
}

# Post-boot health: Dewd overlay must still be present/enabled
if ($Dewd) {
    $path = (adb shell pm path com.android.systemui.rro.dewd.aosp.dynamic 2>$null | Out-String).Trim()
    if (-not $path) {
        Write-Host "Dewd Dynamic overlay missing after boot — restoring stock…" -ForegroundColor Yellow
        Restore-OverlayFromBackup -Dest $DewdDest -Backup $rollbackBackup
        Invoke-SafeAdbReboot
        Wait-AndroidBootCompleted -TimeoutSec 180
        throw "Install aborted: Dewd bridge rejected; stock overlay restored."
    }
}

if (-not $Dewd) {
    Invoke-AdbSoft shell cmd overlay enable --user $User $Pkg
    Invoke-AdbSoft shell cmd overlay set-priority --user $User $Pkg highest
    Invoke-AdbSoft shell am crash com.android.systemui
    Start-Sleep -Seconds 8
    Invoke-AdbSoft shell cmd statusbar carsysui-dispatch-event _System_OnHomeEvent
}

if (-not $NoLaunch) {
    & adb shell am start --user $User -n $Activity
    if ($LASTEXITCODE -ne 0) {
        Invoke-AdbSoft shell am start -n $Activity
    }
}

Write-Host "Install complete (user $User) — device remained in Android mode"
