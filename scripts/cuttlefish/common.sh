#!/usr/bin/env bash
# Shared defaults for AOSP Car Cuttlefish helpers.
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"
export ROOT_DIR

# Android CI branch/target for AOSP Automotive Cuttlefish (Android 17 family).
export CF_BRANCH="${CF_BRANCH:-aosp-android-latest-release}"
export CF_TARGET="${CF_TARGET:-aosp_cf_x86_64_auto-userdebug}"
export CF_PRODUCT="${CF_PRODUCT:-aosp_cf_x86_64_auto}"

export CF_DATA_DIR="${CF_DATA_DIR:-${ROOT_DIR}/cuttlefish-data}"
export CF_SDK_DIR="${CF_SDK_DIR:-${ROOT_DIR}/prebuilt/car-sdk}"

export CUTTLEFISH_HOST="${CUTTLEFISH_HOST:-127.0.0.1}"
export CUTTLEFISH_ADB_PORT="${CUTTLEFISH_ADB_PORT:-6520}"
export CUTTLEFISH_WEBRTC_PORT="${CUTTLEFISH_WEBRTC_PORT:-8443}"

BUILD_API="https://www.googleapis.com/android/internal/build/v3"

cf_latest_build_id() {
  python3 - "${CF_BRANCH}" "${CF_TARGET}" "${BUILD_API}" <<'PY'
import json, sys, urllib.request
branch, target, api = sys.argv[1], sys.argv[2], sys.argv[3]
url = (
    f"{api}/builds?branch={branch}"
    "&buildAttemptStatus=complete&buildType=submitted"
    f"&maxResults=1&successful=true&target={target}"
)
with urllib.request.urlopen(url) as resp:
    data = json.load(resp)
builds = data.get("builds") or []
if not builds:
    sys.exit(f"No successful builds for {branch} / {target}")
print(builds[0]["buildId"])
PY
}

# Download a CI artifact via the /url endpoint (follows redirect to signed GCS URL).
cf_download_artifact() {
  local build_id="$1"
  local artifact="$2"
  local dest="$3"
  local url="${BUILD_API}/builds/${build_id}/${CF_TARGET}/attempts/latest/artifacts/${artifact}/url"
  mkdir -p "$(dirname "${dest}")"
  echo "Downloading ${artifact} → ${dest}"
  curl -fL --retry 3 --retry-delay 2 -o "${dest}" "${url}"
}
