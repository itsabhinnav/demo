# In-app floating system bars — adb commands

Show/hide the Compose floating top/bottom bars hosted by `DesignAppShell`
(not the AAOS CarSystemUI Scalable UI bars).

## Prerequisites

```bash
# Install (or reinstall) debug APK
adb install -r app/build/outputs/apk/debug/app-debug.apk

# Open the demo home (bars are hidden by default)
adb shell am start -n com.test.design/.MainActivity
```

The demo app process must be running. Bars only appear on hosts that use
`DesignAppShell` with `showFloatingSystemBars = true` (e.g. `MainActivity`)
**and** after an adb show/toggle (hidden on cold start).

## Commands

```bash
# Hide
adb shell am broadcast -a com.test.design.action.HIDE_SYSTEM_BARS \
  -n com.test.design/.presentation.ivi.dashboard.FloatingSystemBarsReceiver

# Show
adb shell am broadcast -a com.test.design.action.SHOW_SYSTEM_BARS \
  -n com.test.design/.presentation.ivi.dashboard.FloatingSystemBarsReceiver

# Toggle
adb shell am broadcast -a com.test.design.action.TOGGLE_SYSTEM_BARS \
  -n com.test.design/.presentation.ivi.dashboard.FloatingSystemBarsReceiver

# Set explicitly
adb shell am broadcast -a com.test.design.action.SET_SYSTEM_BARS \
  -n com.test.design/.presentation.ivi.dashboard.FloatingSystemBarsReceiver \
  --ez visible false

adb shell am broadcast -a com.test.design.action.SET_SYSTEM_BARS \
  -n com.test.design/.presentation.ivi.dashboard.FloatingSystemBarsReceiver \
  --ez visible true
```

## Helper script

```bash
.cursor/scripts/toggle-system-bars.sh hide
.cursor/scripts/toggle-system-bars.sh show
.cursor/scripts/toggle-system-bars.sh toggle
.cursor/scripts/toggle-system-bars.sh set false
.cursor/scripts/toggle-system-bars.sh set true
```

Optional: `ANDROID_SERIAL=<device>` to target a specific device/emulator.

## Notes

- Visibility is process-wide; bars start **hidden** and restarting the app restores **hidden**.
- When hidden, chrome padding on driving home / dashboard collapses so content can use the full panel.
- Map / assistant / glanceable activities already force bars off via `showFloatingSystemBars = false`; these broadcasts do not override that.
