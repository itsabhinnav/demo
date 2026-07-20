# Build, install, and launch the Design debug APK.
# Usage (from repo root):
#   .\build.ps1
#   . .\build.ps1; build          # define `build` in the current session

function build {
    param(
        [string]$Activity = "com.test.design/.MainActivity",
        [switch]$SkipLaunch
    )

    .\gradlew.bat assembleDebug --refresh-dependencies
    if ($LASTEXITCODE -ne 0) {
        Write-Host "Build failed (exit $LASTEXITCODE) - skip install"
        return
    }

    $apk = "app\build\outputs\apk\debug\app-debug.apk"
    if (-not (Test-Path $apk)) {
        Write-Host "APK missing: $apk"
        return
    }

    adb install -r -t -d $apk
    if ($LASTEXITCODE -ne 0) {
        Write-Host "Install failed (exit $LASTEXITCODE) - skip launch"
        return
    }

    # Overlay cannot be runtime-granted by the app; allow by default for demos.
    adb shell appops set com.test.design SYSTEM_ALERT_WINDOW allow | Out-Null
    adb shell pm grant com.test.design android.permission.RECORD_AUDIO | Out-Null

    if ($SkipLaunch) {
        return
    }

    # Bring MainActivity to the foreground (works for user 10 AAOS as well).
    adb shell am start -n $Activity
    if ($LASTEXITCODE -ne 0) {
        Write-Host "Launch failed - try: adb shell am start --user 10 -n $Activity"
    }
}

# Run immediately when executed as a script (not when dot-sourced only for the function).
if ($MyInvocation.InvocationName -ne '.') {
    build @args
}
