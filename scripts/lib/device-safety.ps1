# Shared device safety helpers for RRO / system_ext installs.
# Hard rules: never reboot to recovery/bootloader, never wipe, always keep rollback.

$script:DeviceSafetyOrigSuffix = ".stock-bak"

function Assert-AdbDeviceMode {
    $lines = @(adb devices 2>$null | Where-Object { $_ -match "\tdevice$" })
    if ($lines.Count -lt 1) {
        $all = @(adb devices 2>$null)
        if ($all -match "\trecovery$" -or $all -match "\tsideload$") {
            throw "SAFE-ABORT: device is in recovery/sideload. Refusing all changes. Boot to Android first."
        }
        if ($all -match "\tbootloader$" -or $all -match "\tfastboot$") {
            throw "SAFE-ABORT: device is in bootloader/fastboot. Refusing all changes. Boot to Android first."
        }
        throw "SAFE-ABORT: no adb device in 'device' mode."
    }
}

function Assert-NotRecoveryProps {
    $bootmode = (adb shell getprop ro.bootmode 2>$null | Out-String).Trim()
    if ($bootmode -match "recovery") {
        throw "SAFE-ABORT: ro.bootmode=recovery. Refusing all changes."
    }
}

function Invoke-SafeAdbReboot {
    <#
      Only `adb reboot` (normal Android boot). Never recovery/bootloader/fastboot/sideload.
    #>
    Assert-AdbDeviceMode
    Write-Host "Safe reboot → Android (not recovery/bootloader)…"
    adb reboot
    if ($LASTEXITCODE -ne 0) { throw "adb reboot failed" }
}

function Wait-AndroidBootCompleted {
    param(
        [int]$TimeoutSec = 180,
        [string]$RollbackOverlay = $null,
        [string]$RollbackBackup = $null
    )
    $deadline = (Get-Date).AddSeconds($TimeoutSec)
    Write-Host "Waiting for Android boot (timeout ${TimeoutSec}s)…"
    while ((Get-Date) -lt $deadline) {
        $stateLine = adb devices 2>$null | Where-Object { $_ -match "`t" } | Select-Object -First 1
        if ($stateLine -match "`trecovery$" -or $stateLine -match "`tsideload$") {
            throw "SAFE-ABORT: device entered recovery after reboot. Manual recover required; stock bak at $RollbackBackup"
        }
        if ($stateLine -match "`tbootloader$" -or $stateLine -match "`tfastboot$") {
            throw "SAFE-ABORT: device entered bootloader after reboot. Manual recover required; stock bak at $RollbackBackup"
        }
        if ($stateLine -match "`tdevice$") {
            $done = (adb shell getprop sys.boot_completed 2>$null | Out-String).Trim()
            if ($done -eq "1") {
                Write-Host "Boot completed."
                return
            }
        }
        Start-Sleep -Seconds 3
    }

    if ($RollbackOverlay -and $RollbackBackup) {
        Write-Host "Boot timeout — attempting stock overlay rollback…" -ForegroundColor Yellow
        Restore-OverlayFromBackup -Dest $RollbackOverlay -Backup $RollbackBackup
        Invoke-SafeAdbReboot
        Wait-AndroidBootCompleted -TimeoutSec $TimeoutSec
        throw "Install aborted: boot did not complete; stock overlay restored and device rebooted."
    }
    throw "SAFE-ABORT: boot_completed not set within ${TimeoutSec}s."
}

function Assert-ApkSigned {
    param([string]$ApkPath)
    $sdk = $env:ANDROID_HOME
    if (-not $sdk) { $sdk = Join-Path $env:LOCALAPPDATA "Android\Sdk" }
    $apksigner = Get-ChildItem "$sdk\build-tools" -Recurse -Filter apksigner.bat -ErrorAction SilentlyContinue |
        Sort-Object FullName -Descending | Select-Object -First 1
    if (-not $apksigner) {
        Write-Host "WARN: apksigner not found — skipping signature check for $ApkPath" -ForegroundColor Yellow
        return
    }
    $out = & $apksigner.FullName verify $ApkPath 2>&1 | Out-String
    if ($LASTEXITCODE -ne 0 -or $out -match "DOES NOT VERIFY") {
        throw "SAFE-ABORT: refusing to push unsigned/invalid APK: $ApkPath"
    }
}

function Backup-RemoteOverlay {
    param(
        [string]$RemotePath,
        [string]$BackupPath = "$RemotePath$script:DeviceSafetyOrigSuffix"
    )
    Assert-AdbDeviceMode
    $exists = (adb shell "if [ -f '$RemotePath' ]; then echo yes; fi" | Out-String).Trim()
    if ($exists -ne "yes") { return $BackupPath }

    $bakExists = (adb shell "if [ -f '$BackupPath' ]; then echo yes; fi" | Out-String).Trim()
    if ($bakExists -eq "yes") {
        Write-Host "Keeping existing backup → $BackupPath"
        return $BackupPath
    }
    adb shell "cp -f '$RemotePath' '$BackupPath' && chmod 644 '$BackupPath'"
    if ($LASTEXITCODE -ne 0) { throw "Failed to backup $RemotePath" }
    Write-Host "Backed up overlay → $BackupPath"
    return $BackupPath
}

function Restore-OverlayFromBackup {
    param(
        [string]$Dest,
        [string]$Backup
    )
    Assert-AdbDeviceMode
    adb root 2>$null | Out-Null
    adb remount 2>$null | Out-Null
    $bakExists = (adb shell "if [ -f '$Backup' ]; then echo yes; fi" | Out-String).Trim()
    if ($bakExists -ne "yes") {
        # Prefer device .orig if present
        $orig = "$Dest.orig"
        $origExists = (adb shell "if [ -f '$orig' ]; then echo yes; fi" | Out-String).Trim()
        if ($origExists -eq "yes") { $Backup = $orig } else {
            throw "No backup available to restore ($Backup)"
        }
    }
    adb shell "cp -f '$Backup' '$Dest' && chmod 644 '$Dest'"
    adb shell "rm -f /data/resource-cache/system_ext@overlay@DewdDynamic*.apk@idmap /data/resource-cache/system_ext@overlay@Design*.apk@idmap" 2>$null | Out-Null
    Write-Host "Restored overlay from $Backup → $Dest"
}

function Push-OverlaySafe {
    param(
        [string]$LocalApk,
        [string]$RemotePath,
        [switch]$RequireSignature
    )
    Assert-AdbDeviceMode
    Assert-NotRecoveryProps
    if ($RequireSignature) { Assert-ApkSigned -ApkPath $LocalApk }
    $null = Backup-RemoteOverlay -RemotePath $RemotePath
    adb push $LocalApk $RemotePath
    if ($LASTEXITCODE -ne 0) { throw "adb push failed: $LocalApk → $RemotePath" }
    adb shell "chmod 644 '$RemotePath'"
    if ($LASTEXITCODE -ne 0) { throw "chmod failed: $RemotePath" }
}
