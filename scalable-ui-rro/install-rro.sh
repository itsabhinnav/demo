#!/usr/bin/env bash
# Install Design Adaptive Space Scalable UI + framework RROs.
# SAFETY: never recovery/bootloader reboot; backs up before push; waits for boot.
set -euo pipefail

ROOT="$(cd "$(dirname "$0")/.." && pwd)"
# shellcheck source=../scripts/lib/device-safety.sh
source "$ROOT/scripts/lib/device-safety.sh"

USER_ID="${ANDROID_USER:-10}"
SU_APK="${ROOT}/prebuilt/DesignScalableUiRRO.apk"
FW_APK="${ROOT}/prebuilt/DesignFrameworkScalableUiRRO.apk"
[[ -f "$SU_APK" ]] || SU_APK="$ROOT/scalable-ui-rro/build/outputs/apk/debug/scalable-ui-rro-debug.apk"
[[ -f "$FW_APK" ]] || FW_APK="$ROOT/framework-scalable-rro/build/outputs/apk/debug/framework-scalable-rro-debug.apk"
PKG="com.test.design.systemui.scalableui"
DEST="/system_ext/overlay/DesignScalableUiRRO.apk"
FW_DEST="/system_ext/overlay/DesignFrameworkScalableUiRRO.apk"

if [[ ! -f "$SU_APK" ]]; then
  echo "Missing Scalable UI RRO — run: ./scripts/sync-prebuilts.sh"
  exit 1
fi

# Dewd devices must use the signed bridge installer, not this Design RRO path.
if [[ "$(adb shell getprop car.dewd.config 2>/dev/null | tr -d '\r')" == "dynamic" ]]; then
  echo "SAFE-ABORT: Dewd device detected. Use ./scripts/install-prebuilts.sh --dewd instead." >&2
  exit 2
fi

assert_adb_device_mode
assert_not_recovery_props
adb root
adb remount
assert_adb_device_mode

adb shell rm -f /system_ext/overlay/DesignFullscreen*.apk || true
adb shell rm -f /data/resource-cache/system_ext@overlay@Design*.apk@idmap || true
assert_apk_signed "$SU_APK"
push_overlay_safe "$SU_APK" "$DEST" 1
if [[ -f "$FW_APK" ]]; then
  assert_apk_signed "$FW_APK"
  push_overlay_safe "$FW_APK" "$FW_DEST" 1
fi

safe_adb_reboot
wait_android_boot_completed 180
adb root
assert_adb_device_mode

adb shell cmd overlay enable --user "$USER_ID" "$PKG" || true
adb shell cmd overlay set-priority --user "$USER_ID" "$PKG" highest || true
adb shell am crash com.android.systemui || true
sleep 8
adb shell cmd statusbar carsysui-dispatch-event _System_OnHomeEvent || true
adb shell cmd statusbar carsysui-dump-panelstates | grep -E "mId=|mBounds|mLayer" || true
echo "Installed Adaptive Space RRO → $DEST"
