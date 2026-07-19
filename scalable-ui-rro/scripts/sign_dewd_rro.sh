#!/usr/bin/env bash
# Re-sign a patched Dewd Dynamic RRO with AOSP platform test keys.
set -euo pipefail

INPUT="${1:?usage: sign_dewd_rro.sh <input.apk> <output.apk>}"
OUTPUT="${2:?usage: sign_dewd_rro.sh <input.apk> <output.apk>}"
ROOT="$(cd "$(dirname "$0")/../.." && pwd)"
KEY_DIR="$ROOT/scalable-ui-rro/prebuilt/aosp-platform"
mkdir -p "$KEY_DIR"
PK8="$KEY_DIR/platform.pk8"
PEM="$KEY_DIR/platform.x509.pem"
BASE="https://raw.githubusercontent.com/aosp-mirror/platform_build/master/target/product/security"

if [[ ! -f "$PK8" || ! -f "$PEM" ]]; then
  curl -fsSL "$BASE/platform.pk8" -o "$PK8"
  curl -fsSL "$BASE/platform.x509.pem" -o "$PEM"
fi

APKSIGNER="${APKSIGNER:-}"
if [[ -z "$APKSIGNER" ]]; then
  SDK="${ANDROID_HOME:-${ANDROID_SDK_ROOT:-}}"
  if [[ -n "$SDK" && -d "$SDK/build-tools" ]]; then
    APKSIGNER="$(ls -1d "$SDK"/build-tools/*/apksigner 2>/dev/null | sort -V | tail -1 || true)"
  fi
fi
if [[ -z "${APKSIGNER:-}" || ! -x "$APKSIGNER" ]]; then
  echo "apksigner not found — set ANDROID_HOME or APKSIGNER" >&2
  exit 1
fi

TMP="$OUTPUT.signing-tmp.apk"
"$APKSIGNER" sign --key "$PK8" --cert "$PEM" --out "$TMP" "$INPUT"
"$APKSIGNER" verify --verbose "$TMP" >/dev/null
mv -f "$TMP" "$OUTPUT"
echo "Signed Dewd RRO → $OUTPUT"
