#!/usr/bin/env bash
# Open the semi-transparent assistant UI gallery.
set -euo pipefail

PKG="${PACKAGE:-com.test.design}"
ACTIVITY="${ACTIVITY:-com.test.design/.presentation.assistant.gallery.AssistantUiGalleryActivity}"
STYLE="${1:-}"

SERIAL_ARGS=()
if [[ -n "${ANDROID_SERIAL:-}" ]]; then
  SERIAL_ARGS=(-s "$ANDROID_SERIAL")
fi

ARGS=(-n "$ACTIVITY" -a com.test.design.action.OPEN_ASSISTANT_GALLERY)
if [[ -n "$STYLE" ]]; then
  ARGS+=(--es style "$STYLE")
fi

echo "Opening assistant UI gallery${STYLE:+ ($STYLE)}…"
adb "${SERIAL_ARGS[@]}" shell am start "${ARGS[@]}"
echo "Styles: VoicePlate FaceOnly WaveformCenter OrbGlow CapsuleFace StatusBar SideRail EqualizerBars ListeningRings CornerBubble WaveFaceCombo AmbientPill"
