#!/usr/bin/env bash
set -euo pipefail

export ANDROID_HOME="${ANDROID_HOME:-/opt/android-sdk}"
export PATH="${PATH}:${ANDROID_HOME}/emulator:${ANDROID_HOME}/platform-tools"

AVD_NAME="${ANDROID_AVD_NAME:-cloud_avd}"

if ! command -v emulator >/dev/null 2>&1; then
  echo "Android emulator not installed in this environment."
  exit 0
fi

if ! emulator -list-avds | grep -qx "${AVD_NAME}"; then
  echo "AVD '${AVD_NAME}' not found. Skipping emulator startup."
  exit 0
fi

echo "Starting Android emulator (${AVD_NAME}) for remote desktop preview..."
exec emulator \
  -avd "${AVD_NAME}" \
  -no-audio \
  -no-boot-anim \
  -gpu swiftshader_indirect \
  -accel off
