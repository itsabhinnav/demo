#!/usr/bin/env bash
# Install Design app + Adaptive Space Scalable UI RROs from ./prebuilt.
# Usage:
#   ./scripts/install-prebuilts.sh
#   ./scripts/install-prebuilts.sh --dewd          # Dewd interim bridge
#   ./scripts/install-prebuilts.sh --skip-reboot
#   ANDROID_USER=0 ./scripts/install-prebuilts.sh
set -euo pipefail

ROOT="$(cd "$(dirname "$0")/.." && pwd)"
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
      sed -n '2,7p' "$0"
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

if [[ ! -f "$APP_APK" ]]; then
  echo "Missing app APK — run: ./scripts/sync-prebuilts.sh" >&2
  exit 1
fi

adb wait-for-device
adb install -r -t -d "$APP_APK"
echo "Installed app → $APP_APK"

adb root
adb remount
adb shell rm -f /system_ext/overlay/DesignFullscreen*.apk || true
adb shell rm -f /data/resource-cache/system_ext@overlay@Design*.apk@idmap || true

if [[ "$DEWD" -eq 1 ]]; then
  if [[ ! -f "$DEWD_APK" ]]; then
    echo "Missing Dewd bridge APK — run: ./scripts/sync-prebuilts.sh" >&2
    exit 1
  fi
  adb push "$DEWD_APK" "$DEWD_DEST"
  adb shell chmod 644 "$DEWD_DEST"
  echo "Installed Dewd bridge → $DEWD_DEST"
else
  if [[ ! -f "$SU_APK" ]]; then
    echo "Missing Scalable UI RRO — run: ./scripts/sync-prebuilts.sh" >&2
    exit 1
  fi
  adb push "$SU_APK" "$SU_DEST"
  adb shell chmod 644 "$SU_DEST"
  if [[ -f "$FW_APK" ]]; then
    adb push "$FW_APK" "$FW_DEST"
    adb shell chmod 644 "$FW_DEST"
  fi
  echo "Installed Adaptive Space RROs → $SU_DEST"
fi

if [[ "$SKIP_REBOOT" -eq 0 ]]; then
  adb reboot
  adb wait-for-device
  sleep 25
  adb root
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

echo "Install complete (user $USER_ID)"
