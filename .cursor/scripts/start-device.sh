#!/usr/bin/env bash
# Cloud/local device entrypoint: prefer remote/local AOSP Car Cuttlefish, else phone AVD.
set -euo pipefail

export ANDROID_HOME="${ANDROID_HOME:-/opt/android-sdk}"
export PATH="${PATH}:${ANDROID_HOME}/emulator:${ANDROID_HOME}/platform-tools"

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"

# 1) Explicit remote Cuttlefish (KVM host elsewhere).
if [[ -n "${CUTTLEFISH_HOST:-}" && "${CUTTLEFISH_HOST}" != "127.0.0.1" && "${CUTTLEFISH_HOST}" != "localhost" ]]; then
  echo "Connecting to remote AOSP Car Cuttlefish at ${CUTTLEFISH_HOST}:${CUTTLEFISH_ADB_PORT:-6520}..."
  exec "${ROOT_DIR}/scripts/cuttlefish/connect-remote.sh"
fi

# 2) Local KVM + fetched images → Docker Compose Cuttlefish.
if [[ -e /dev/kvm ]] && command -v docker >/dev/null 2>&1 \
  && [[ -x "${ROOT_DIR}/cuttlefish-data/bin/launch_cvd" ]]; then
  echo "Starting local AOSP Car Cuttlefish (Docker)..."
  exec "${ROOT_DIR}/scripts/cuttlefish/start.sh"
fi

# 3) Fallback: software phone emulator (cloud VMs without KVM).
echo "No Cuttlefish/KVM available — falling back to phone AVD (not AAOS)."
echo "To use AOSP Car: fetch images on a KVM host, then set CUTTLEFISH_HOST."
exec "${ROOT_DIR}/.cursor/scripts/start-emulator.sh"
