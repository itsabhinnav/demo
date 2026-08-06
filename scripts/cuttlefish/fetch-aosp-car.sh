#!/usr/bin/env bash
# Fetch AOSP Car Cuttlefish images (Android 17) + host package + Car SDK jars from Android CI.
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
# shellcheck source=common.sh
source "${SCRIPT_DIR}/common.sh"

mkdir -p "${CF_DATA_DIR}" "${CF_SDK_DIR}"

BUILD_ID="${CF_BUILD_ID:-$(cf_latest_build_id)}"
echo "Using ${CF_BRANCH} / ${CF_TARGET} build ${BUILD_ID}"

IMG_ZIP="${CF_DATA_DIR}/${CF_PRODUCT}-img-${BUILD_ID}.zip"
HOST_TGZ="${CF_DATA_DIR}/cvd-host_package.tar.gz"
MARKER="${CF_DATA_DIR}/.fetched-build-id"

if [[ -f "${MARKER}" ]] && [[ "$(cat "${MARKER}")" == "${BUILD_ID}" ]] \
  && [[ -x "${CF_DATA_DIR}/bin/launch_cvd" ]] \
  && [[ -f "${CF_DATA_DIR}/android-info.txt" || -f "${CF_DATA_DIR}/super.img" ]]; then
  echo "Already fetched build ${BUILD_ID} in ${CF_DATA_DIR}"
else
  cf_download_artifact "${BUILD_ID}" "${CF_PRODUCT}-img-${BUILD_ID}.zip" "${IMG_ZIP}"
  cf_download_artifact "${BUILD_ID}" "cvd-host_package.tar.gz" "${HOST_TGZ}"

  echo "Extracting host package..."
  tar -xzf "${HOST_TGZ}" -C "${CF_DATA_DIR}"

  echo "Extracting auto images..."
  unzip -o "${IMG_ZIP}" -d "${CF_DATA_DIR}"

  # Keep archives optional; remove to save disk unless CF_KEEP_ARCHIVES=1.
  if [[ "${CF_KEEP_ARCHIVES:-0}" != "1" ]]; then
    rm -f "${IMG_ZIP}" "${HOST_TGZ}"
  fi

  echo "${BUILD_ID}" > "${MARKER}"
fi

# Car SDK stubs/jars from the same auto build (compile against platform Car APIs).
for art in \
  android.car-stubs.jar \
  android.car-system-stubs.jar \
  android.car.jar
do
  dest="${CF_SDK_DIR}/${art}"
  if [[ -f "${dest}" ]] && [[ -f "${CF_SDK_DIR}/.build-id" ]] \
    && [[ "$(cat "${CF_SDK_DIR}/.build-id")" == "${BUILD_ID}" ]]; then
    continue
  fi
  cf_download_artifact "${BUILD_ID}" "${art}" "${dest}" || true
done
echo "${BUILD_ID}" > "${CF_SDK_DIR}/.build-id"

cat > "${CF_SDK_DIR}/BUILD_INFO.txt" <<EOF
build_id=${BUILD_ID}
branch=${CF_BRANCH}
target=${CF_TARGET}
EOF

echo "Ready:"
echo "  Images:  ${CF_DATA_DIR}"
echo "  Car SDK: ${CF_SDK_DIR}"
echo "  Next:    docker compose --profile cuttlefish up -d --build"
echo "           or ./scripts/cuttlefish/start.sh"
