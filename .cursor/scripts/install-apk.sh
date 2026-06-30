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
echo "Installed ${APK_PATH}"
