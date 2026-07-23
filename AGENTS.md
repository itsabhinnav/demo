# AGENTS.md

## Project overview

Android Automotive immersive assistant (Assist Bot) built with Kotlin and Jetpack Compose.

- `app` — host APK, launcher, manifest entry points
- `assistant` — face UI, overlays, demo backend, gallery
- `assistant-api` — host/backend contracts (no Compose)

## Build and test

```bash
chmod +x ./gradlew
./gradlew assembleDebug testDebugUnitTest
```

Debug APK output:

`app/build/outputs/apk/debug/app-debug.apk`

## Cursor Cloud

Cloud agent setup: `.cursor/environment.json`, `.cursor/Dockerfile`, `.cursor/scripts/cloud-install.sh`

`ANDROID_HOME` defaults to `/opt/android-sdk`. Use JDK 17 (`JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64`).

`compileSdk` is `release(37)` → needs `platforms;android-37.0` and `build-tools;37.0.0`.

After build:

```bash
./gradlew assembleDebug
.cursor/scripts/install-apk.sh
.cursor/scripts/launch-assistant.sh
```

Emulator: `.cursor/scripts/start-emulator.sh` (default AVD `cloud_avd`). Cloud VMs have no KVM — cold boot is slow; dismiss System UI ANRs with Wait or `adb shell settings put global hide_error_dialogs 1`.

## Git

Push directly to `main` on this repository. Do not open PRs unless explicitly requested.
