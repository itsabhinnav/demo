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

# Rebuild Dewd interim bridge (floating status/nav + map/widget patches)
DEWD_ORIG="$ROOT/scalable-ui-rro/prebuilt/DewdDynamicAospRRO.orig.apk"
DEWD_DESIGN="$ROOT/scalable-ui-rro/prebuilt/DewdDynamicAospRRO-design.apk"
if [[ -f "$DEWD_ORIG" ]]; then
  # Unsigned rebuild is rejected by PackageManager — always platform-sign.
  python3 "$ROOT/scalable-ui-rro/scripts/patch_dewd_fullpower.py" \
    --input "$DEWD_ORIG" \
    --output "$DEWD_DESIGN"
  chmod +x "$ROOT/scalable-ui-rro/scripts/sign_dewd_rro.sh"
  "$ROOT/scalable-ui-rro/scripts/sign_dewd_rro.sh" "$DEWD_DESIGN" "$DEWD_DESIGN"
  cp -f "$DEWD_ORIG" "$PREBUILT/DewdDynamicAospRRO.orig.apk"
  cp -f "$DEWD_DESIGN" "$PREBUILT/DewdDynamicAospRRO-design.apk"
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

Rebuild: `./scripts/sync-prebuilts.sh` or `.\scripts\sync-prebuilts.ps1`

Install: `./scripts/install-prebuilts.sh` or `.\scripts\install-prebuilts.ps1`
EOF

ls -lh "$PREBUILT"
echo "Synced prebuilts → $PREBUILT"
