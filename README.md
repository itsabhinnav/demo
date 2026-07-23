# Assist Bot

Android Automotive immersive assistant + UI gallery (Kotlin / Jetpack Compose).

## Modules

| Module | Role |
|--------|------|
| `:assistant-api` | Pure contracts: backend, host, session events/models |
| `:assistant` | Face UI, overlay services, demo backend, STT/TTS, gallery |
| `:app` | Host APK — installs runtime, launcher, manifest entry points |

## Build

```bash
chmod +x ./gradlew
./gradlew assembleDebug testDebugUnitTest
```

Debug APK: `app/build/outputs/apk/debug/app-debug.apk`

## Run

```bash
adb install -r app/build/outputs/apk/debug/app-debug.apk
adb shell appops set com.test.design SYSTEM_ALERT_WINDOW allow
adb shell pm grant com.test.design android.permission.RECORD_AUDIO
```

Or: `.cursor/scripts/install-apk.sh`

### Launch

```bash
# Immersive assistant
adb shell am start -a com.test.design.action.OPEN_ASSISTANT \
  -n com.test.design/.presentation.assistant.VirtualAssistantActivity

# UI gallery
adb shell am start -a com.test.design.action.OPEN_ASSISTANT_GALLERY \
  -n com.test.design/.presentation.assistant.gallery.AssistantUiGalleryActivity
```

Helpers: `.cursor/scripts/launch-assistant.sh`, `launch-assistant-gallery.sh`, `launch-assistant-overlay.sh`

Full adb cheat sheet: [`docs/assistant-adb.md`](docs/assistant-adb.md)

Module boundary notes: [`docs/assistant-standalone.md`](docs/assistant-standalone.md)
