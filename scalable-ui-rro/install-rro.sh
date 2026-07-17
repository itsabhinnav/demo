#!/usr/bin/env bash
# Install Design Scalable UI RRO (sealed panel layout).
set -euo pipefail

ROOT="$(cd "$(dirname "$0")/.." && pwd)"
USER_ID="${ANDROID_USER:-10}"
APK="$ROOT/scalable-ui-rro/build/outputs/apk/debug/scalable-ui-rro-debug.apk"
PKG="com.test.design.systemui.scalableui"
DEST="/system_ext/overlay/DesignScalableUiRRO.apk"

if [[ ! -f "$APK" ]]; then
  echo "Missing $APK — run: ./gradlew :scalable-ui-rro:assembleDebug"
  exit 1
fi

adb root
adb remount
adb shell rm -f /system_ext/overlay/DesignFullscreen*.apk || true
adb shell rm -f /data/resource-cache/system_ext@overlay@Design*.apk@idmap || true
adb push "$APK" "$DEST"
adb shell chmod 644 "$DEST"
adb reboot
adb wait-for-device
sleep 25
adb root
adb shell cmd overlay enable --user "$USER_ID" "$PKG"
adb shell cmd overlay set-priority --user "$USER_ID" "$PKG" highest
adb shell am crash com.android.systemui || true
sleep 8
adb shell cmd statusbar carsysui-dispatch-event _System_OnHomeEvent || true
adb shell cmd statusbar carsysui-dump-panelstates | grep -E "mId=|mBounds|mLayer" || true
echo "Installed $PKG → $DEST"
