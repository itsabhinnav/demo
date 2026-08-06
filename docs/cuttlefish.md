# Docker + AOSP Car Cuttlefish (Android 17)

This repo runs in Docker and targets a **Cuttlefish** virtual device built from
**AOSP Automotive** (`aosp_cf_x86_64_auto`) on the Android 17 release train
(`aosp-android-latest-release`).

| Knob | Value |
|------|--------|
| compileSdk / targetSdk | Android 17 (API 37) |
| minSdk | Android 15 (API 35) |
| Device image | `aosp_cf_x86_64_auto-userdebug` |
| Car SDK | `android.car-*.jar` from the same CI build |
| ADB port | `6520` |
| WebRTC UI | `https://<host>:8443` |

## Prerequisites

- Docker + Compose v2
- **KVM** on the machine that runs Cuttlefish (`ls /dev/kvm`)
- ~3 GB free disk for images + host package

Cursor Cloud VMs do **not** expose `/dev/kvm`. Use a remote KVM host for CVD and
point agents at it with `CUTTLEFISH_HOST` (see below). Without that, the cloud
fallback is a phone AVD for UI preview only.

## One-time fetch (images + Car SDK)

```bash
chmod +x scripts/cuttlefish/*.sh docker/cuttlefish/entrypoint.sh
./scripts/cuttlefish/fetch-aosp-car.sh
```

Downloads into:

- `cuttlefish-data/` — CVD host tools + auto images
- `prebuilt/car-sdk/` — `android.car-stubs.jar` (wired as optional `compileOnly`)

Override build with `CF_BUILD_ID=...` or branch/target via `CF_BRANCH` / `CF_TARGET`.

## Local Docker (KVM host)

```bash
docker compose --profile cuttlefish up -d --build
./scripts/cuttlefish/connect-remote.sh   # adb → 127.0.0.1:6520
./gradlew assembleDebug
.cursor/scripts/install-apk.sh
```

Or: `./scripts/cuttlefish/start.sh` / `./scripts/cuttlefish/stop.sh`

Full stack (dev container + cuttlefish):

```bash
docker compose --profile full up -d --build
docker compose exec dev ./gradlew assembleDebug
```

## Remote Cuttlefish instance

On the KVM server:

```bash
./scripts/cuttlefish/fetch-aosp-car.sh
./scripts/cuttlefish/start.sh
# expose TCP 6520 (ADB) and optionally 8443 (WebRTC)
```

On the client / Cursor Cloud agent:

```bash
export CUTTLEFISH_HOST=<kvm-server-ip>
export CUTTLEFISH_ADB_PORT=6520
./scripts/cuttlefish/connect-remote.sh
.cursor/scripts/install-apk.sh
```

`.cursor/scripts/start-device.sh` (cloud terminal) auto-connects when
`CUTTLEFISH_HOST` is set to a non-local address.

## Native launch (no Docker)

```bash
./scripts/cuttlefish/fetch-aosp-car.sh
cd cuttlefish-data
HOME=$PWD ./bin/launch_cvd --daemon --start_webrtc=true
./bin/adb connect 127.0.0.1:6520
```

## Car SDK stubs

After fetch, `app` picks up stubs automatically when present:

`prebuilt/car-sdk/android.car-stubs.jar` → `compileOnly`

Do not package `android.car.jar` into the APK — Car APIs come from the AAOS image.
