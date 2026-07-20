#!/usr/bin/env bash
# Install Design app + Adaptive Space Scalable UI RROs from ./prebuilt.
# Usage:
#   ./scripts/install-prebuilts.sh
#   ./scripts/install-prebuilts.sh --dewd          # Dewd interim bridge
#   ./scripts/install-prebuilts.sh --skip-reboot
#   ANDROID_USER=0 ./scripts/install-prebuilts.sh
#
# SAFETY: never reboots to recovery/bootloader, never wipes, backs up overlays
# before replace, verifies APK signatures for Dewd bridge, rolls back on boot fail.
set -euo pipefail

ROOT="$(cd "$(dirname "$0")/.." && pwd)"
# shellcheck source=lib/device-safety.sh
source "$ROOT/scripts/lib/device-safety.sh"

PREBUILT="$ROOT/prebuilt"
USER_ID="${ANDROID_USER:-10}"
DEWD=0
SKIP_REBOOT=0
LAUNCH=1

for arg in "$@"; do
  case "$arg" in
    --dewd) DEWD=1 ;;
    --skip-reboot) SKIP_REBOOT=1 ;;
    --no-launch) LAUNCH=0 ;;
    -h|--help)
      sed -n '2,10p' "$0"
      exit 0
      ;;
    *)
      echo "Unknown option: $arg" >&2
      exit 1
      ;;
  esac
done

APP_APK="$PREBUILT/app-debug.apk"
SU_APK="$PREBUILT/DesignScalableUiRRO.apk"
FW_APK="$PREBUILT/DesignFrameworkScalableUiRRO.apk"
DEWD_APK="$PREBUILT/DewdDynamicAospRRO-design.apk"

[[ -f "$APP_APK" ]] || APP_APK="$ROOT/app/build/outputs/apk/debug/app-debug.apk"
[[ -f "$SU_APK" ]] || SU_APK="$ROOT/scalable-ui-rro/build/outputs/apk/debug/scalable-ui-rro-debug.apk"
[[ -f "$FW_APK" ]] || FW_APK="$ROOT/framework-scalable-rro/build/outputs/apk/debug/framework-scalable-rro-debug.apk"
[[ -f "$DEWD_APK" ]] || DEWD_APK="$ROOT/scalable-ui-rro/prebuilt/DewdDynamicAospRRO-design.apk"

PKG="com.test.design.systemui.scalableui"
ACTIVITY="com.test.design/.MainActivity"
SU_DEST="/system_ext/overlay/DesignScalableUiRRO.apk"
FW_DEST="/system_ext/overlay/DesignFrameworkScalableUiRRO.apk"
DEWD_DEST="/system_ext/overlay/DewdDynamicAospRRO.apk"
DEWD_BAK="${DEWD_DEST}.stock-bak"
DEWD_ORIG="${DEWD_DEST}.orig"

if [[ ! -f "$APP_APK" ]]; then
  echo "Missing app APK — run: ./scripts/sync-prebuilts.sh" >&2
  exit 1
fi

assert_adb_device_mode
assert_not_recovery_props
adb wait-for-device

# App install is data-partition only (cannot brick / enter recovery).
if ! adb install -r -t -d "$APP_APK"; then
  echo "App install failed (signature mismatch?) — uninstalling and retrying…" >&2
  adb uninstall com.test.design || true
  adb install -r -t -d "$APP_APK"
fi
# Overlay cannot be runtime-granted by the app; allow by default for demos.
adb shell appops set com.test.design SYSTEM_ALERT_WINDOW allow || true
adb shell pm grant com.test.design android.permission.RECORD_AUDIO || true
echo "Installed app → $APP_APK"

adb root
adb remount
assert_adb_device_mode

adb shell rm -f /system_ext/overlay/DesignFullscreen*.apk || true
adb shell rm -f /data/resource-cache/system_ext@overlay@Design*.apk@idmap || true

DEWD_PROP="$(adb shell getprop car.dewd.config | tr -d '\r')"
if [[ "$DEWD" -eq 0 && "$DEWD_PROP" == "dynamic" ]]; then
  echo "WARNING: device is Dewd (car.dewd.config=dynamic). Use --dewd — Design SystemUI RRO idmap fails and leaves a black map + rail." >&2
fi

ROLLBACK_OVERLAY=""
ROLLBACK_BACKUP=""

if [[ "$DEWD" -eq 1 ]]; then
  if [[ ! -f "$DEWD_APK" ]]; then
    echo "Missing Dewd bridge APK — run: ./scripts/sync-prebuilts.sh" >&2
    exit 1
  fi
  # Never delete stock .orig / .stock-bak
  adb shell rm -f \
    /system_ext/overlay/DesignScalableUiRRO.apk \
    /system_ext/overlay/DesignFrameworkScalableUiRRO.apk \
    /system_ext/overlay/DesignScalableFrameworkRRO.apk \
    /system_ext/overlay/DewdDynamicAospRRO-design.apk || true
  adb shell rm -f /data/resource-cache/system_ext@overlay@Design*.apk@idmap \
    /data/resource-cache/system_ext@overlay@DewdDynamic*.apk@idmap || true

  if adb shell "[ -f '$DEWD_ORIG' ]" >/dev/null 2>&1; then
    ROLLBACK_BACKUP="$DEWD_ORIG"
    echo "Using device stock backup → $DEWD_ORIG"
  else
    ROLLBACK_BACKUP="$(backup_remote_overlay "$DEWD_DEST" "$DEWD_BAK")"
  fi
  push_overlay_safe "$DEWD_APK" "$DEWD_DEST" 1
  ROLLBACK_OVERLAY="$DEWD_DEST"
  echo "Installed Dewd bridge → $DEWD_DEST (rollback: $ROLLBACK_BACKUP)"
else
  if [[ ! -f "$SU_APK" ]]; then
    echo "Missing Scalable UI RRO — run: ./scripts/sync-prebuilts.sh" >&2
    exit 1
  fi
  assert_apk_signed "$SU_APK"
  push_overlay_safe "$SU_APK" "$SU_DEST" 1
  if [[ -f "$FW_APK" ]]; then
    assert_apk_signed "$FW_APK"
    push_overlay_safe "$FW_APK" "$FW_DEST" 1
  fi
  echo "Installed Adaptive Space RROs → $SU_DEST"
fi

if [[ "$SKIP_REBOOT" -eq 0 ]]; then
  safe_adb_reboot
  wait_android_boot_completed 180 "${ROLLBACK_OVERLAY:-}" "${ROLLBACK_BACKUP:-}"
  adb root
  assert_adb_device_mode
fi

if [[ "$DEWD" -eq 1 ]]; then
  if ! adb shell pm path com.android.systemui.rro.dewd.aosp.dynamic >/dev/null 2>&1; then
    echo "Dewd Dynamic overlay missing after boot — restoring stock…" >&2
    restore_overlay_from_backup "$DEWD_DEST" "$ROLLBACK_BACKUP"
    safe_adb_reboot
    wait_android_boot_completed 180
    echo "Install aborted: Dewd bridge rejected; stock overlay restored." >&2
    exit 3
  fi
fi

if [[ "$DEWD" -eq 0 ]]; then
  adb shell cmd overlay enable --user "$USER_ID" "$PKG" || true
  adb shell cmd overlay set-priority --user "$USER_ID" "$PKG" highest || true
  adb shell am crash com.android.systemui || true
  sleep 8
  adb shell cmd statusbar carsysui-dispatch-event _System_OnHomeEvent || true
fi

if [[ "$LAUNCH" -eq 1 ]]; then
  adb shell am start --user "$USER_ID" -n "$ACTIVITY" \
    || adb shell am start -n "$ACTIVITY" \
    || true
fi

echo "Install complete (user $USER_ID) — device remained in Android mode"
