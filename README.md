# Design — AAOS demo

Android Automotive (AAOS) design system demo built with Kotlin and Jetpack Compose.

## Build

```bash
chmod +x ./gradlew
./gradlew assembleDebug testDebugUnitTest
```

Debug APK: `app/build/outputs/apk/debug/app-debug.apk`

## Architecture — Scalable UI

Sealed home layout lives in **`:scalable-ui-rro`** (not Compose fake chrome):

| Panel | Role |
|-------|------|
| `map_panel` | Full-bleed map (`MapActivity` / Maps placeholder) |
| `widget_panel` | Floating left rail (`DrivingRailActivity`) |
| `status` / `nav` | Floating Scalable UI `<SystemBar>` (not legacy CarSystemBarPanel) |
| `app_panel` | Transient apps |

- RRO + install: [`scalable-ui-rro/README.md`](scalable-ui-rro/README.md)
- CarSystemUI follow-ups (glass layouts, Dagger): [`scalable-ui-rro/CARSYSTEMUI.md`](scalable-ui-rro/CARSYSTEMUI.md)
- Dewd interim bridge: `scalable-ui-rro/scripts/patch_dewd_fullpower.py`

`MainActivity` remains an in-process Compose demo of the same home.

`MainActivity` remains an in-process Compose demo.

### Intent filters

`MapActivity` accepts:

| Action | Purpose |
|--------|---------|
| `android.intent.category.APP_MAPS` | AAOS map app registration |
| `com.test.design.action.OPEN_MAP` | Direct launch — opens **Navigation** (same as Search maps) |
| `android.intent.action.VIEW` + `geo:` | Display map at coordinates |
| `androidx.car.app.action.NAVIGATE` + `geo:` | Navigation entry (shows demo route) |

### Launch from code

```kotlin
import com.test.design.presentation.ivi.map.MapIntents

startActivity(MapIntents.openMap(context))
startActivity(MapIntents.openMap(context, showRoute = true))
```

Optional intent extras:

- `com.test.design.extra.SHOW_ROUTE` (boolean) — overlay the demo route
- `com.test.design.extra.ZOOM` (double) — initial zoom level (default `14.5`)

### Launch via adb

```bash
adb shell am start -a com.test.design.action.OPEN_MAP \
  -n com.test.design/.presentation.ivi.map.MapActivity

adb shell am start -a android.intent.action.VIEW \
  -d "geo:37.7749,-122.4194" \
  -n com.test.design/.presentation.ivi.map.MapActivity

adb shell am start -a androidx.car.app.action.NAVIGATE \
  -d "geo:37.7749,-122.4194" \
  -n com.test.design/.presentation.ivi.map.MapActivity
```

### Return to main screen

From `MapActivity`:

- Tap the **Home** icon at the top of the right-side map controls
- Press the system **Back** button when no widget is expanded

Both open `MainActivity` (driving home). The app grid in the sidebar opens the full widget dashboard on `MainActivity`.

```kotlin
startActivity(MapIntents.openMain(context))
startActivity(MapIntents.openMain(context, openDashboard = true))
```

### Scalable UI action (system intent)

In `scalable_ui_actions.xml`, dispatch the map activity when a panel event fires:

```xml
<Action intent="intent:#Intent;action=com.test.design.action.OPEN_MAP;component=com.test.design/.presentation.ivi.map.MapActivity;end">
    <Event id="_System_TaskOpenEvent" panelId="@id/map_panel" />
</Action>
```

## Cloud agents

See [AGENTS.md](AGENTS.md) for Cursor Cloud build, emulator, and CI instructions.
