#!/usr/bin/env bash
# Shared device safety helpers for RRO / system_ext installs.
# Hard rules: never reboot to recovery/bootloader, never wipe, always keep rollback.
set -euo pipefail

DEVICE_SAFETY_ORIG_SUFFIX="${DEVICE_SAFETY_ORIG_SUFFIX:-.stock-bak}"

assert_adb_device_mode() {
  local states
  states="$(adb devices 2>/dev/null | awk '/\t/{print $2}' | tr -d '\r')"
  if echo "$states" | grep -qx recovery || echo "$states" | grep -qx sideload; then
    echo "SAFE-ABORT: device is in recovery/sideload. Refusing all changes. Boot to Android first." >&2
    exit 2
  fi
  if echo "$states" | grep -qx bootloader || echo "$states" | grep -qx fastboot; then
    echo "SAFE-ABORT: device is in bootloader/fastboot. Refusing all changes. Boot to Android first." >&2
    exit 2
  fi
  if ! echo "$states" | grep -qx device; then
    echo "SAFE-ABORT: no adb device in 'device' mode." >&2
    exit 2
  fi
}

assert_not_recovery_props() {
  local bootmode
  bootmode="$(adb shell getprop ro.bootmode 2>/dev/null | tr -d '\r')"
  if [[ "$bootmode" == *recovery* ]]; then
    echo "SAFE-ABORT: ro.bootmode=recovery. Refusing all changes." >&2
    exit 2
  fi
}

# Only normal Android reboot — never recovery/bootloader/fastboot/sideload.
safe_adb_reboot() {
  assert_adb_device_mode
  echo "Safe reboot → Android (not recovery/bootloader)…"
  adb reboot
}

wait_android_boot_completed() {
  local timeout_sec="${1:-180}"
  local rollback_overlay="${2:-}"
  local rollback_backup="${3:-}"
  local deadline=$((SECONDS + timeout_sec))
  echo "Waiting for Android boot (timeout ${timeout_sec}s)…"
  while (( SECONDS < deadline )); do
    local state
    state="$(adb devices 2>/dev/null | awk '/\t/{print $2; exit}' | tr -d '\r')"
    case "$state" in
      recovery|sideload)
        echo "SAFE-ABORT: device entered recovery after reboot. Manual recover required; stock bak at ${rollback_backup:-unknown}" >&2
        exit 2
        ;;
      bootloader|fastboot)
        echo "SAFE-ABORT: device entered bootloader after reboot. Manual recover required; stock bak at ${rollback_backup:-unknown}" >&2
        exit 2
        ;;
      device)
        if [[ "$(adb shell getprop sys.boot_completed 2>/dev/null | tr -d '\r')" == "1" ]]; then
          echo "Boot completed."
          return 0
        fi
        ;;
    esac
    sleep 3
  done

  if [[ -n "$rollback_overlay" && -n "$rollback_backup" ]]; then
    echo "Boot timeout — attempting stock overlay rollback…" >&2
    restore_overlay_from_backup "$rollback_overlay" "$rollback_backup"
    safe_adb_reboot
    wait_android_boot_completed "$timeout_sec"
    echo "Install aborted: boot did not complete; stock overlay restored and device rebooted." >&2
    exit 3
  fi
  echo "SAFE-ABORT: boot_completed not set within ${timeout_sec}s." >&2
  exit 2
}

assert_apk_signed() {
  local apk="$1"
  local sdk="${ANDROID_HOME:-${ANDROID_SDK_ROOT:-}}"
  local apksigner=""
  if [[ -n "$sdk" && -d "$sdk/build-tools" ]]; then
    apksigner="$(ls -1d "$sdk"/build-tools/*/apksigner 2>/dev/null | sort -V | tail -1 || true)"
  fi
  if [[ -z "$apksigner" || ! -x "$apksigner" ]]; then
    echo "WARN: apksigner not found — skipping signature check for $apk" >&2
    return 0
  fi
  if ! "$apksigner" verify "$apk" >/dev/null 2>&1; then
    echo "SAFE-ABORT: refusing to push unsigned/invalid APK: $apk" >&2
    exit 2
  fi
}

backup_remote_overlay() {
  local remote_path="$1"
  local backup_path="${2:-${remote_path}${DEVICE_SAFETY_ORIG_SUFFIX}}"
  assert_adb_device_mode
  if adb shell "[ -f '$remote_path' ]" >/dev/null 2>&1; then
    if adb shell "[ -f '$backup_path' ]" >/dev/null 2>&1; then
      echo "Keeping existing backup → $backup_path"
    else
      adb shell "cp -f '$remote_path' '$backup_path' && chmod 644 '$backup_path'"
      echo "Backed up overlay → $backup_path"
    fi
  fi
  printf '%s' "$backup_path"
}

restore_overlay_from_backup() {
  local dest="$1"
  local backup="$2"
  assert_adb_device_mode
  adb root >/dev/null 2>&1 || true
  adb remount >/dev/null 2>&1 || true
  if ! adb shell "[ -f '$backup' ]" >/dev/null 2>&1; then
    if adb shell "[ -f '${dest}.orig' ]" >/dev/null 2>&1; then
      backup="${dest}.orig"
    else
      echo "No backup available to restore ($backup)" >&2
      exit 2
    fi
  fi
  adb shell "cp -f '$backup' '$dest' && chmod 644 '$dest'"
  adb shell "rm -f /data/resource-cache/system_ext@overlay@DewdDynamic*.apk@idmap /data/resource-cache/system_ext@overlay@Design*.apk@idmap" >/dev/null 2>&1 || true
  echo "Restored overlay from $backup → $dest"
}

push_overlay_safe() {
  local local_apk="$1"
  local remote_path="$2"
  local require_sig="${3:-1}"
  assert_adb_device_mode
  assert_not_recovery_props
  if [[ "$require_sig" == "1" ]]; then
    assert_apk_signed "$local_apk"
  fi
  backup_remote_overlay "$remote_path" >/dev/null
  adb push "$local_apk" "$remote_path"
  adb shell "chmod 644 '$remote_path'"
}
