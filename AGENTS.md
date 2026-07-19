# AGENTS.md

## Project overview

Android Automotive (AAOS) design system demo built with Kotlin and Jetpack Compose.

- `app` — demo application and feature screens
- `component` — reusable OEM UI components and theme tokens
- `template` — automotive dashboard layout zones (blue / green / yellow)

## Build and test

```bash
chmod +x ./gradlew
./gradlew assembleDebug testDebugUnitTest
```

Debug APK output:

`app/build/outputs/apk/debug/app-debug.apk`

## Cursor Cloud specific instructions

This repo is configured for **Cursor Cloud Agents**, so work continues when the developer laptop is off. Use the **Cursor iOS app** or [cursor.com/agents](https://cursor.com/agents) to start and supervise agents.

### Use from Cursor iOS (laptop off)

1. Install **Cursor for iOS** and sign in with the same account.
2. Choose repository `itsabhinnav/demo` and branch `main`.
3. Select **Cloud** as the worker — not Remote Control.
4. Send tasks such as "build the app", "add a screen", or "run unit tests".
5. Review artifacts (screenshots, videos, logs) on the agent session. Agents push directly to `main` unless you request a PR.

Remote Control requires the laptop to stay awake. Cloud Agents do not.

### Run the app in the cloud

After the cloud environment boots:

```bash
./gradlew assembleDebug
.cursor/scripts/install-apk.sh   # when an emulator/device is connected
```

To interact with the UI from iOS, use **remote desktop control** on the cloud agent session after the agent starts the emulator terminal (`.cursor/scripts/start-emulator.sh`).

### Environment

Cloud agent setup is defined in:

- `.cursor/environment.json`
- `.cursor/Dockerfile`
- `.cursor/scripts/cloud-install.sh`

`ANDROID_HOME` defaults to `/opt/android-sdk` in cloud VMs.

### Build & run gotchas (non-obvious)

- The app's `compileSdk` is `release(37)`, which requires `platforms;android-37.0` (note the `.0` suffix — plain `platforms;android-37` does not exist) and `build-tools;37.0.0`. These are installed by `.cursor/Dockerfile` (the environment source of truth referenced by `.cursor/environment.json`). Gradle uses **JDK 17** (`JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64`), not the VM's default JDK 21.
- Use `./gradlew assembleDebug testDebugUnitTest` to verify. `./gradlew lintDebug` currently **fails on pre-existing lint errors** in the app code (e.g. `MissingSuperCall`), so it is not a reliable gate — do not treat those lint failures as an environment problem.
- The cloud VM has **no KVM** (it is itself a KVM/Firecracker guest with no nested virtualization and no `/dev/kvm`), so the emulator runs software-only (`-accel off -gpu swiftshader_indirect`). Cold boot takes several minutes, and the starved SystemUI process often shows recurring "System UI isn't responding" ANR dialogs — the demo app itself renders fine behind them. Run `adb shell settings put global hide_error_dialogs 1` and be patient; tap **Wait** to dismiss. Confirm the app is up via `adb shell dumpsys activity activities | grep topResumedActivity` (expect `com.test.design/.MainActivity`).
- Two AVDs are provisioned (`.cursor/Dockerfile`): **`cloud_avd`** (Android 14 / API 34) and **`android17_avd`** (Android 17 / API 37). Only `cloud_avd` boots in the cloud — the Android 17 x86_64 image requires **AVX/F16C** CPU instructions that QEMU's software (TCG) engine cannot emulate (log shows `TCG doesn't support requested feature: CPUID.01H:ECX.avx`), so `android17_avd` hangs at boot without KVM. Use `android17_avd` only on a host that exposes `/dev/kvm` (local machine or a KVM-enabled runner); the default `.cursor/scripts/start-emulator.sh` targets `cloud_avd`, override with `ANDROID_AVD_NAME=android17_avd`.

### Device safety (agents)

**Never brick the device or reboot into recovery/bootloader.** See `.cursor/rules/device-safety.mdc`.

- Only `adb reboot` (normal Android). Never `reboot recovery|bootloader|fastboot|sideload`.
- Never wipe/factory-reset or `fastboot flash/erase`.
- Overlay installs must use `scripts/lib/device-safety.*` (backup → signed push → boot wait → auto-rollback).
- On Dewd (`car.dewd.config=dynamic`) use `install-prebuilts.* --dewd` / `-Dewd` only.

### Git workflow (agents)

**Push directly to `main` by default.** Do not create feature branches or PRs unless the user explicitly asks for one.

1. `git checkout main && git pull origin main`
2. Make changes, commit, and run tests.
3. `git push origin main`

If you already worked on a branch, merge it into `main` and push before finishing — do not leave completed work on an unmerged branch.

See also `.cursor/rules/git-workflow.mdc`.

### CI APK (no laptop required)

GitHub Actions builds `app-debug.apk` on every push to `main` and on pull requests. Download the artifact from the Actions tab on your phone and sideload to an Android device or emulator.
