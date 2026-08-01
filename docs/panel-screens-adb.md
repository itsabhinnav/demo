# Panel screens — adb commands

Standalone full-screen activities for Music (Media), Climate, and Vehicle info.
Car assistants can open the same screens with matching intent actions.

## Prerequisites

```bash
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

## Launch by action (preferred)

```bash
# Music / media player
adb shell am start -a com.test.design.action.OPEN_MEDIA \
  -n com.test.design/.presentation.ivi.glanceables.MediaPanelActivity

# Climate control
adb shell am start -a com.test.design.action.OPEN_CLIMATE \
  -n com.test.design/.presentation.ivi.glanceables.ClimatePanelActivity

# Vehicle info
adb shell am start -a com.test.design.action.OPEN_VEHICLE \
  -n com.test.design/.presentation.ivi.glanceables.VehiclePanelActivity
```

Action-only (resolved via intent-filter):

```bash
adb shell am start -a com.test.design.action.OPEN_MEDIA
adb shell am start -a com.test.design.action.OPEN_CLIMATE
adb shell am start -a com.test.design.action.OPEN_VEHICLE
```

## Launch by component

```bash
adb shell am start -n com.test.design/.presentation.ivi.glanceables.MediaPanelActivity
adb shell am start -n com.test.design/.presentation.ivi.glanceables.ClimatePanelActivity
adb shell am start -n com.test.design/.presentation.ivi.glanceables.VehiclePanelActivity
```

## From app / assistant code

```kotlin
context.startActivity(PanelIntents.openMedia(context))
context.startActivity(PanelIntents.openClimate(context))
context.startActivity(PanelIntents.openVehicle(context))
```

## Related

- Map: `com.test.design.action.OPEN_MAP` → `MapActivity` (see `MapIntents`)
- Assistant: [assistant-adb.md](assistant-adb.md)
