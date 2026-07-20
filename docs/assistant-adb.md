# Assistant — adb commands

Translucent immersive assistant over home / any app (when overlay is allowed).

## Prerequisites

```bash
# Install (or reinstall) debug APK
adb install -r app/build/outputs/apk/debug/app-debug.apk

# Required for translucent overlay + voice
adb shell appops set com.test.design SYSTEM_ALERT_WINDOW allow
adb shell pm grant com.test.design android.permission.RECORD_AUDIO
```

Or use the install helper (grants both):

```bash
.cursor/scripts/install-apk.sh
```

## Launch assistant

Brings `MainActivity` under the glass, then shows the translucent overlay (corner bubble → immersive morph):

```bash
adb shell am start -a com.test.design.action.OPEN_ASSISTANT \
  -n com.test.design/.presentation.assistant.VirtualAssistantActivity
```

Helper script (opens main first, then assistant):

```bash
.cursor/scripts/launch-assistant.sh
```

### Summon again (overlay already running)

```bash
adb shell am startservice \
  -n com.test.design/.presentation.assistant.ImmersiveAssistantOverlayService \
  -a com.test.design.assistant.IMMERSIVE_SUMMON
```

### Stop overlay

```bash
adb shell am startservice \
  -n com.test.design/.presentation.assistant.ImmersiveAssistantOverlayService \
  -a com.test.design.assistant.IMMERSIVE_STOP
```

## Other assistant surfaces

```bash
# UI style gallery
adb shell am start -a com.test.design.action.OPEN_ASSISTANT_GALLERY \
  -n com.test.design/.presentation.assistant.gallery.AssistantUiGalleryActivity

# AAOS capsule overlay bootstrap
adb shell am start -a com.test.design.action.START_ASSISTANT_OVERLAY \
  -n com.test.design/.presentation.assistant.overlay.AssistantOverlayBootstrapActivity
```

## Voice (hotword)

Mic listening runs **after** the assistant overlay/activity is open. Say:

- “Hey assistant”
- “Hi assistant”
- “Okay assistant”
- “Assistant”

There is no always-on background hotword yet — open the assistant first (dock sparkles icon, or the launch command above).

## Emulator note

Window blur-behind (`FLAG_BLUR_BEHIND`) is **disabled** on the immersive overlay — enabling it when the session goes fullscreen often crashes SurfaceFlinger on emulators. The Compose scrim still dims the backdrop.

## In-app

From `MainActivity`, the floating dock **AutoAwesome** (purple sparkles) / mic icons call `ImmersiveAssistantOverlayService.show()`.
