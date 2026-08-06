#!/usr/bin/env bash
# Start local AOSP Car Cuttlefish via Docker Compose (requires KVM + fetched images).
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
# shellcheck source=common.sh
source "${SCRIPT_DIR}/common.sh"

if [[ ! -e /dev/kvm ]]; then
  echo "error: /dev/kvm not found. Cuttlefish needs KVM."
  echo "On hosts without KVM, point at a remote instance:"
  echo "  export CUTTLEFISH_HOST=<remote-ip>"
  echo "  ./scripts/cuttlefish/connect-remote.sh"
  exit 1
fi

if [[ ! -x "${CF_DATA_DIR}/bin/launch_cvd" ]]; then
  echo "Images missing — fetching from Android CI..."
  "${SCRIPT_DIR}/fetch-aosp-car.sh"
fi

if ! command -v docker >/dev/null 2>&1; then
  echo "error: docker is required for containerized Cuttlefish."
  echo "Native fallback: cd ${CF_DATA_DIR} && HOME=\$PWD ./bin/launch_cvd --daemon --start_webrtc=true"
  exit 1
fi

cd "${ROOT_DIR}"
docker compose --profile cuttlefish up -d --build cuttlefish
echo "WebRTC: https://localhost:${CUTTLEFISH_WEBRTC_PORT}"
echo "ADB:    ${CUTTLEFISH_HOST}:${CUTTLEFISH_ADB_PORT}"
"${SCRIPT_DIR}/connect-remote.sh"
