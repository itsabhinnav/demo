# Design — AAOS demo

Android Automotive (AAOS) design system demo built with Kotlin and Jetpack Compose.

## Build

```bash
chmod +x ./gradlew
./gradlew assembleDebug testDebugUnitTest
```

Debug APK: `app/build/outputs/apk/debug/app-debug.apk`

## Activities

| Activity | Role |
|----------|------|
| `MainActivity` | App launcher — map-first driving home and widget dashboard |
| `MapActivity` | Full-bleed map for AAOS Scalable UI map panels |

`MainActivity` shows the map with a sidebar and widget chrome. `MapActivity` hosts the same driving-home map UI (search bar, sidebar, HVAC, controls) for AAOS Scalable UI map panels, with a **Home** button to return to the main app screen.

## MapActivity — Scalable UI map panel

On Android Automotive OS 17+, Scalable UI can host apps in dedicated panels. Register `MapActivity` as the default map panel activity in a CarSystemUI Runtime Resource Overlay (RRO):

```xml
<string-array name="config_default_activities">
    <item>map_panel;com.test.design/.presentation.ivi.map.MapActivity</item>
</string-array>
```

Replace `map_panel` with the panel ID from your Scalable UI panel XML if it differs.

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
