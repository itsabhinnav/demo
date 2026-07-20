#!/usr/bin/env bash
# Launch AOSP Car Cuttlefish from mounted cf/ directory (host package + auto images).
set -euo pipefail

CF_HOME="${CF_HOME:-/home/vsoc-01/cf}"
# Compose mounts cuttlefish-data at /home/vsoc-01/cf; also accept /cf.
if [[ ! -x "${CF_HOME}/bin/launch_cvd" && -x /cf/bin/launch_cvd ]]; then
  CF_HOME=/cf
fi

cd "${CF_HOME}"

if [[ ! -x ./bin/launch_cvd ]]; then
  echo "cuttlefish: missing ./bin/launch_cvd under ${CF_HOME}"
  echo "Run ./scripts/cuttlefish/fetch-aosp-car.sh on the host first."
  exit 1
fi

if [[ ! -f ./super.img && ! -f ./android-info.txt && ! -f ./vendor_boot.img ]]; then
  echo "cuttlefish: no AOSP auto images found in ${CF_HOME}"
  echo "Run ./scripts/cuttlefish/fetch-aosp-car.sh on the host first."
  exit 1
fi

if [[ ! -e /dev/kvm ]]; then
  echo "cuttlefish: /dev/kvm is required (host must expose KVM)."
  exit 1
fi

export HOME="${CF_HOME}"
export PATH="${CF_HOME}/bin:${PATH}"

if [[ -x ./bin/stop_cvd ]]; then
  ./bin/stop_cvd >/dev/null 2>&1 || true
fi

LAUNCH_ARGS=(--daemon)
if [[ "${CF_START_WEBRTC:-true}" == "true" ]]; then
  LAUNCH_ARGS+=(--start_webrtc=true)
fi

echo "cuttlefish: launching AOSP Car CVD (Android 17 auto)..."
./bin/launch_cvd "${LAUNCH_ARGS[@]}"

echo "cuttlefish: waiting for adb on 127.0.0.1:6520..."
ADB_BIN=./bin/adb
command -v adb >/dev/null 2>&1 && ADB_BIN=adb
for _ in $(seq 1 120); do
  if "${ADB_BIN}" connect 127.0.0.1:6520 >/dev/null 2>&1; then
    state="$("${ADB_BIN}" -s 127.0.0.1:6520 get-state 2>/dev/null || true)"
    if [[ "${state}" == "device" ]]; then
      echo "cuttlefish: device ready (WebRTC https://0.0.0.0:8443 , adb 6520)"
      break
    fi
  fi
  sleep 5
done

# Keep the container alive while CVD runs as daemon.
touch "${CF_HOME}/.cvd-running"
exec tail -F "${CF_HOME}/.cvd-running"
