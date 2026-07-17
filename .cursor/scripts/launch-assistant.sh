#!/usr/bin/env bash
# Launch the transparent NOMI assistant overlay over the main demo UI.
set -euo pipefail

PKG="${PACKAGE:-com.test.design}"
MAIN="${MAIN_ACTIVITY:-com.test.design/.MainActivity}"
ACTIVITY="${ACTIVITY:-com.test.design/.presentation.assistant.VirtualAssistantActivity}"
ACTION="${ACTION:-com.test.design.action.OPEN_ASSISTANT}"

SERIAL_ARGS=()
if [[ -n "${ANDROID_SERIAL:-}" ]]; then
  SERIAL_ARGS=(-s "$ANDROID_SERIAL")
fi

# Bring main content up first so the translucent assistant sits on top of it.
echo "Opening main UI (${MAIN})…"
adb "${SERIAL_ARGS[@]}" shell am start -n "$MAIN" >/dev/null || true
sleep 0.4

echo "Overlaying assistant (${ACTIVITY})…"
adb "${SERIAL_ARGS[@]}" shell am start \
  -a "$ACTION" \
  -n "$ACTIVITY" \
  --activity-single-top

echo "OK — existing content stays visible under the transparent overlay."
echo "Say “Hey assistant” or tap to summon the orb."
