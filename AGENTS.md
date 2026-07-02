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
<<<<<<< HEAD
2. Choose repository `itsabhinnav/demo` and branch `main`.
3. Select **Cloud** as the worker — not Remote Control.
4. Send tasks such as "build the app", "add a screen", or "run unit tests".
5. Review artifacts (screenshots, videos, logs) on the agent session. Agents push directly to `main` unless you request a PR.
=======
2. Choose repository `itsabhinnav/demo` and branch `main` (or a feature branch).
3. Select **Cloud** as the worker — not Remote Control.
4. Send tasks such as "build the app", "add a screen", or "run unit tests".
5. Review artifacts (screenshots, videos, logs) and merge PRs from the phone.
>>>>>>> origin/cursor/cloud-ios-remote-setup-e4a4

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

<<<<<<< HEAD
### Git workflow (agents)

**Push directly to `main` by default.** Do not create feature branches or PRs unless the user explicitly asks for one.

1. `git checkout main && git pull origin main`
2. Make changes, commit, and run tests.
3. `git push origin main`

If you already worked on a branch, merge it into `main` and push before finishing — do not leave completed work on an unmerged branch.

See also `.cursor/rules/git-workflow.mdc`.

=======
>>>>>>> origin/cursor/cloud-ios-remote-setup-e4a4
### CI APK (no laptop required)

GitHub Actions builds `app-debug.apk` on every push to `main` and on pull requests. Download the artifact from the Actions tab on your phone and sideload to an Android device or emulator.
