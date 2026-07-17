#!/usr/bin/env bash
# Install Design Adaptive Space Scalable UI + framework RROs.
set -euo pipefail

ROOT="$(cd "$(dirname "$0")/.." && pwd)"
USER_ID="${ANDROID_USER:-10}"
SU_APK="${ROOT}/prebuilt/DesignScalableUiRRO.apk"
FW_APK="${ROOT}/prebuilt/DesignFrameworkScalableUiRRO.apk"
# Fall back to gradle outputs if prebuilt missing
[[ -f "$SU_APK" ]] || SU_APK="$ROOT/scalable-ui-rro/build/outputs/apk/debug/scalable-ui-rro-debug.apk"
[[ -f "$FW_APK" ]] || FW_APK="$ROOT/framework-scalable-rro/build/outputs/apk/debug/framework-scalable-rro-debug.apk"
PKG="com.test.design.systemui.scalableui"
DEST="/system_ext/overlay/DesignScalableUiRRO.apk"
FW_DEST="/system_ext/overlay/DesignFrameworkScalableUiRRO.apk"

if [[ ! -f "$SU_APK" ]]; then
  echo "Missing Scalable UI RRO — run: ./scripts/sync-prebuilts.sh"
  exit 1
fi

adb root
adb remount
adb shell rm -f /system_ext/overlay/DesignFullscreen*.apk || true
adb shell rm -f /data/resource-cache/system_ext@overlay@Design*.apk@idmap || true
adb push "$SU_APK" "$DEST"
adb shell chmod 644 "$DEST"
if [[ -f "$FW_APK" ]]; then
  adb push "$FW_APK" "$FW_DEST"
  adb shell chmod 644 "$FW_DEST"
fi
adb reboot
adb wait-for-device
sleep 25
adb root
adb shell cmd overlay enable --user "$USER_ID" "$PKG" || true
adb shell cmd overlay set-priority --user "$USER_ID" "$PKG" highest || true
adb shell am crash com.android.systemui || true
sleep 8
adb shell cmd statusbar carsysui-dispatch-event _System_OnHomeEvent || true
adb shell cmd statusbar carsysui-dump-panelstates | grep -E "mId=|mBounds|mLayer" || true
echo "Installed Adaptive Space RRO → $DEST"
