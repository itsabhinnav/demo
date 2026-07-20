FROM ubuntu:24.04

ENV DEBIAN_FRONTEND=noninteractive
ENV ANDROID_HOME=/opt/android-sdk
ENV JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64
ENV PATH=${PATH}:${ANDROID_HOME}/cmdline-tools/latest/bin:${ANDROID_HOME}/platform-tools:${ANDROID_HOME}/emulator

RUN apt-get update && apt-get install -y --no-install-recommends \
    openjdk-17-jdk \
    curl \
    ca-certificates \
    unzip \
    zip \
    git \
    python3 \
    socat \
    && rm -rf /var/lib/apt/lists/*

RUN mkdir -p ${ANDROID_HOME}/cmdline-tools \
    && curl -fsSL -o /tmp/cmdline-tools.zip https://dl.google.com/android/repository/commandlinetools-linux-11076708_latest.zip \
    && unzip -q /tmp/cmdline-tools.zip -d ${ANDROID_HOME}/cmdline-tools \
    && mv ${ANDROID_HOME}/cmdline-tools/cmdline-tools ${ANDROID_HOME}/cmdline-tools/latest \
    && rm /tmp/cmdline-tools.zip \
    && yes | sdkmanager --sdk_root=${ANDROID_HOME} \
        "platform-tools" \
        "platforms;android-37.0" \
        "platforms;android-36" \
        "platforms;android-35" \
        "build-tools;37.0.0" \
        "emulator" \
        "system-images;android-34;google_apis;x86_64" \
        "system-images;android-37.0;google_apis_ps16k;x86_64" \
    # Android 17 (API 37) AVD — requires a host with KVM to boot (the Android 17
    # x86_64 image needs AVX/F16C, which QEMU software/TCG mode cannot emulate).
    && echo "no" | avdmanager create avd -n android17_avd -k "system-images;android-37.0;google_apis_ps16k;x86_64" --device "pixel_6" --force \
    # Android 14 (API 34) AVD — boots under software emulation (no KVM) in cloud VMs.
    # Prefer AOSP Car Cuttlefish (scripts/cuttlefish/) when KVM or a remote CVD is available.
    && echo "no" | avdmanager create avd -n cloud_avd -k "system-images;android-34;google_apis;x86_64" --device "pixel_6" --force
