#!/usr/bin/env bash
# Build Design app + Scalable UI RROs and copy APKs into ./prebuilt.
set -euo pipefail

ROOT="$(cd "$(dirname "$0")/.." && pwd)"
PREBUILT="$ROOT/prebuilt"
mkdir -p "$PREBUILT"

cd "$ROOT"
chmod +x ./gradlew
./gradlew :app:assembleDebug :scalable-ui-rro:assembleDebug :framework-scalable-rro:assembleDebug

cp -f "$ROOT/app/build/outputs/apk/debug/app-debug.apk" \
  "$PREBUILT/app-debug.apk"
cp -f "$ROOT/scalable-ui-rro/build/outputs/apk/debug/scalable-ui-rro-debug.apk" \
  "$PREBUILT/DesignScalableUiRRO.apk"
cp -f "$ROOT/framework-scalable-rro/build/outputs/apk/debug/framework-scalable-rro-debug.apk" \
  "$PREBUILT/DesignFrameworkScalableUiRRO.apk"

# Keep Dewd bridge APKs alongside product RROs when present
if [[ -f "$ROOT/scalable-ui-rro/prebuilt/DewdDynamicAospRRO-design.apk" ]]; then
  cp -f "$ROOT/scalable-ui-rro/prebuilt/DewdDynamicAospRRO-design.apk" \
    "$PREBUILT/DewdDynamicAospRRO-design.apk"
fi
if [[ -f "$ROOT/scalable-ui-rro/prebuilt/DewdDynamicAospRRO.orig.apk" ]]; then
  cp -f "$ROOT/scalable-ui-rro/prebuilt/DewdDynamicAospRRO.orig.apk" \
    "$PREBUILT/DewdDynamicAospRRO.orig.apk"
fi

cat > "$PREBUILT/README.md" <<'EOF'
# Prebuilt APKs — Adaptive Space

| File | Package / role |
|------|----------------|
| `app-debug.apk` | `com.test.design` — Design demo + Adaptive Space dashboard |
| `DesignScalableUiRRO.apk` | `com.test.design.systemui.scalableui` — Map-Under-Apps panels |
| `DesignFrameworkScalableUiRRO.apk` | `com.test.design.framework.scalableui` — remote insets handshake |
| `DewdDynamicAospRRO-design.apk` | Dewd interim bridge (patched) |
| `DewdDynamicAospRRO.orig.apk` | Dewd stock Dynamic RRO (patch input) |

Rebuild: `./scripts/sync-prebuilts.sh`
EOF

ls -lh "$PREBUILT"
echo "Synced prebuilts → $PREBUILT"
