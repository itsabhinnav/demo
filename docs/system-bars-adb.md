# In-app floating system bars — adb commands

Show/hide the Compose floating top/bottom bars hosted by `DesignAppShell`
(not the AAOS CarSystemUI Scalable UI bars).

Requires the demo app process to be running (`MainActivity` or another host
that uses `DesignAppShell` with `showFloatingSystemBars = true`).

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
```

Helper:

```bash
.cursor/scripts/toggle-system-bars.sh hide
.cursor/scripts/toggle-system-bars.sh show
.cursor/scripts/toggle-system-bars.sh toggle
```
