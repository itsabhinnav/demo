#!/usr/bin/env bash
# Start the floating AAOS assistant overlay (capsule / corner bug).
set -euo pipefail

PKG="${PACKAGE:-com.test.design}"
BOOTSTRAP="${BOOTSTRAP:-com.test.design/.presentation.assistant.overlay.AssistantOverlayBootstrapActivity}"
SERVICE="${SERVICE:-com.test.design/.presentation.assistant.overlay.AssistantOverlayService}"
STATE="${1:-LISTENING}"

SERIAL_ARGS=()
if [[ -n "${ANDROID_SERIAL:-}" ]]; then
  SERIAL_ARGS=(-s "$ANDROID_SERIAL")
fi

echo "Granting overlay permission (no-op if already granted)…"
adb "${SERIAL_ARGS[@]}" shell appops set "$PKG" SYSTEM_ALERT_WINDOW allow >/dev/null 2>&1 || true

echo "Bootstrapping overlay…"
adb "${SERIAL_ARGS[@]}" shell am start -n "$BOOTSTRAP" >/dev/null
sleep 0.5

echo "Setting state → $STATE"
adb "${SERIAL_ARGS[@]}" shell am startservice \
  -n "$SERVICE" \
  -a com.test.design.assistant.UPDATE_STATE \
  --es state "$STATE"

echo "OK — IDLE bug is BottomEnd; active capsule is 420×180, 32dp from bottom."
echo "States: IDLE | LISTENING | THINKING | SPEAKING | ERROR"
