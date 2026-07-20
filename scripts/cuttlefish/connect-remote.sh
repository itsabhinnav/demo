#!/usr/bin/env bash
# Connect adb to a local or remote AOSP Car Cuttlefish instance.
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
# shellcheck source=common.sh
source "${SCRIPT_DIR}/common.sh"

export ANDROID_HOME="${ANDROID_HOME:-/opt/android-sdk}"
export PATH="${PATH}:${ANDROID_HOME}/platform-tools:${CF_DATA_DIR}/bin"

HOST="${CUTTLEFISH_HOST}"
PORT="${CUTTLEFISH_ADB_PORT}"
SERIAL="${HOST}:${PORT}"

if ! command -v adb >/dev/null 2>&1; then
  echo "error: adb not found (install platform-tools or fetch Cuttlefish host package)."
  exit 1
fi

echo "Connecting adb to ${SERIAL} ..."
adb disconnect "${SERIAL}" >/dev/null 2>&1 || true
adb connect "${SERIAL}"

for _ in $(seq 1 60); do
  state="$(adb -s "${SERIAL}" get-state 2>/dev/null || true)"
  if [[ "${state}" == "device" ]]; then
    echo "Connected: ${SERIAL}"
    adb -s "${SERIAL}" shell getprop ro.build.version.release 2>/dev/null || true
    adb -s "${SERIAL}" shell getprop ro.hardware.virtual_device 2>/dev/null || true
    adb -s "${SERIAL}" shell getprop ro.product.name 2>/dev/null || true
    # Prefer this device for subsequent adb commands in this shell.
    export ANDROID_SERIAL="${SERIAL}"
    echo "export ANDROID_SERIAL=${SERIAL}"
    exit 0
  fi
  sleep 2
  adb connect "${SERIAL}" >/dev/null 2>&1 || true
done

echo "error: timed out waiting for ${SERIAL}"
echo "If the instance is remote, ensure port ${PORT} is reachable and CVD is running."
exit 1
