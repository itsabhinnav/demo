#!/usr/bin/env bash
# Show / hide / toggle in-app floating system bars via adb broadcast.
set -euo pipefail

PKG="${PKG:-com.test.design}"
RECEIVER="${PKG}/.presentation.ivi.dashboard.FloatingSystemBarsReceiver"
ACTION_PREFIX="com.test.design.action"

SERIAL_ARGS=()
if [[ -n "${ANDROID_SERIAL:-}" ]]; then
  SERIAL_ARGS=(-s "$ANDROID_SERIAL")
fi

usage() {
  echo "Usage: $0 {show|hide|toggle|set <true|false>}" >&2
  exit 1
}

mode="${1:-}"
case "$mode" in
  show)
    adb "${SERIAL_ARGS[@]}" shell am broadcast \
      -a "${ACTION_PREFIX}.SHOW_SYSTEM_BARS" -n "$RECEIVER"
    ;;
  hide)
    adb "${SERIAL_ARGS[@]}" shell am broadcast \
      -a "${ACTION_PREFIX}.HIDE_SYSTEM_BARS" -n "$RECEIVER"
    ;;
  toggle)
    adb "${SERIAL_ARGS[@]}" shell am broadcast \
      -a "${ACTION_PREFIX}.TOGGLE_SYSTEM_BARS" -n "$RECEIVER"
    ;;
  set)
    visible="${2:-}"
    [[ "$visible" == "true" || "$visible" == "false" ]] || usage
    adb "${SERIAL_ARGS[@]}" shell am broadcast \
      -a "${ACTION_PREFIX}.SET_SYSTEM_BARS" -n "$RECEIVER" \
      --ez visible "$visible"
    ;;
  *)
    usage
    ;;
esac
