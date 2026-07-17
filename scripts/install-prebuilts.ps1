# Install Design app + Adaptive Space Scalable UI RROs from ./prebuilt.
# Usage (from repo root):
#   .\scripts\install-prebuilts.ps1
#   .\scripts\install-prebuilts.ps1 -Dewd
#   .\scripts\install-prebuilts.ps1 -SkipReboot
#   .\scripts\install-prebuilts.ps1 -User 0

param(
    [switch]$Dewd,
    [switch]$SkipReboot,
    [switch]$NoLaunch,
    [int]$User = $(if ($env:ANDROID_USER) { [int]$env:ANDROID_USER } else { 10 })
)

$ErrorActionPreference = "Stop"

$Root = Resolve-Path (Join-Path $PSScriptRoot "..")
$Prebuilt = Join-Path $Root "prebuilt"

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

Invoke-Adb wait-for-device
Invoke-Adb install -r -t -d $AppApk
Write-Host "Installed app → $AppApk"

Invoke-Adb root
Invoke-Adb remount
Invoke-AdbSoft shell rm -f /system_ext/overlay/DesignFullscreen*.apk
Invoke-AdbSoft shell rm -f "/data/resource-cache/system_ext@overlay@Design*.apk@idmap"

if ($Dewd) {
    if (-not $DewdApk) {
        throw "Missing Dewd bridge APK — run: .\scripts\sync-prebuilts.ps1"
    }
    Invoke-Adb push $DewdApk $DewdDest
    Invoke-Adb shell chmod 644 $DewdDest
    Write-Host "Installed Dewd bridge → $DewdDest"
} else {
    if (-not $SuApk) {
        throw "Missing Scalable UI RRO — run: .\scripts\sync-prebuilts.ps1"
    }
    Invoke-Adb push $SuApk $SuDest
    Invoke-Adb shell chmod 644 $SuDest
    if ($FwApk) {
        Invoke-Adb push $FwApk $FwDest
        Invoke-Adb shell chmod 644 $FwDest
    }
    Write-Host "Installed Adaptive Space RROs → $SuDest"
}

if (-not $SkipReboot) {
    Invoke-Adb reboot
    Invoke-Adb wait-for-device
    Start-Sleep -Seconds 25
    Invoke-Adb root
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

Write-Host "Install complete (user $User)"
