# Design Scalable UI RRO

Sealed home layout matching the Design app driving home — via **Scalable UI
panels only** (no legacy `CarSystemBarPanel`).

```
┌─────────────────────────────────────────────┐
│  status  (floating SystemBar, layer Z 200)  │
├──────┬──────────────────────────────────────┤
│      │                                      │
│ rail │     map_panel (full-bleed, L2)       │
│ L20  │     MapActivity / Maps placeholder   │
│      │                                      │
├──────┴──────────────────────────────────────┤
│  nav  (floating SystemBar, layer Z 210)     │
└─────────────────────────────────────────────┘
```

| Panel | Type | Layer | Content |
|-------|------|-------|---------|
| `map_panel` | TaskPanel | 2 | Full-screen map (`MapActivity`) |
| `widget_panel` | TaskPanel | 20 | Floating left rail (`DrivingRailActivity`) |
| `app_panel` | TaskPanel | 100 | Transient apps |
| `status` | SystemBar | Z 200 | Floating top bar (geometry in RRO; views in SystemUI) |
| `nav` | SystemBar | Z 210 | Floating bottom bar + HVAC dock (geometry in RRO; views in SystemUI) |

Legacy bars are **off**: `config_enableTop/Bottom/Left/RightSystemBar=false`.

## Build

```bash
./gradlew :scalable-ui-rro:assembleDebug
# APK: scalable-ui-rro/build/outputs/apk/debug/scalable-ui-rro-debug.apk
```

Optional framework handshake:

```bash
./gradlew :framework-scalable-rro:assembleDebug
```

## Install (products that already declare these resource names)

```bash
adb root && adb remount
adb push scalable-ui-rro/build/outputs/apk/debug/scalable-ui-rro-debug.apk \
  /system_ext/overlay/DesignScalableUiRRO.apk
adb shell chmod 644 /system_ext/overlay/DesignScalableUiRRO.apk
adb reboot
# after boot:
adb shell cmd overlay enable --user 10 com.test.design.systemui.scalableui
adb shell cmd overlay set-priority --user 10 com.test.design.systemui.scalableui highest
```

## Dewd `aosp_tangorpro_car` (interim)

Do **not** rely on installing this RRO alone on stock Dewd (idmap cannot add
Dewd-only XML names; replacing `window_states` without product support drops
bars). Use the in-place patch bridge:

```bash
python scalable-ui-rro/scripts/patch_dewd_fullpower.py \
  --input scalable-ui-rro/prebuilt/DewdDynamicAospRRO.orig.apk \
  --output scalable-ui-rro/prebuilt/DewdDynamicAospRRO-design.apk
adb push scalable-ui-rro/prebuilt/DewdDynamicAospRRO-design.apk \
  /system_ext/overlay/DewdDynamicAospRRO.apk
adb install -r app/build/outputs/apk/debug/app-debug.apk
adb reboot
```

## Swap map host

| Goal | Change |
|------|--------|
| Design OSM map (default) | `string/default_map_activity` → Design `MapActivity` |
| AAOS green placeholder | → `com.android.car.mapsplaceholder/.MapsPlaceholderActivity` |
| Google Maps | → Maps package component + update controller filter |

## CarSystemUI work (not RRO)

See **[CARSYSTEMUI.md](CARSYSTEMUI.md)** — glass bar layouts, Dagger suppliers,
clipping, product `window_states` ownership.
