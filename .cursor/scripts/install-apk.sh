#!/usr/bin/env bash
set -euo pipefail

export ANDROID_HOME="${ANDROID_HOME:-/opt/android-sdk}"
export PATH="${PATH}:${ANDROID_HOME}/platform-tools"

APK_PATH="app/build/outputs/apk/debug/app-debug.apk"

if [[ ! -f "${APK_PATH}" ]]; then
  chmod +x ./gradlew
  ./gradlew assembleDebug
fi

adb wait-for-device
adb install -r "${APK_PATH}"
# SYSTEM_ALERT_WINDOW cannot be runtime-granted by the app; allow by default for demo installs.
adb shell appops set com.test.design SYSTEM_ALERT_WINDOW allow || true
adb shell pm grant com.test.design android.permission.RECORD_AUDIO || true
echo "Installed ${APK_PATH} (overlay + mic pre-granted)"
