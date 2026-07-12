FROM ubuntu:24.04

ENV DEBIAN_FRONTEND=noninteractive
ENV ANDROID_HOME=/opt/android-sdk
ENV JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64
ENV PATH=${PATH}:${ANDROID_HOME}/cmdline-tools/latest/bin:${ANDROID_HOME}/platform-tools:${ANDROID_HOME}/emulator

RUN apt-get update && apt-get install -y \
    openjdk-17-jdk \
    curl \
    unzip \
    git \
    && rm -rf /var/lib/apt/lists/*

RUN mkdir -p ${ANDROID_HOME}/cmdline-tools \
    && curl -fsSL -o /tmp/cmdline-tools.zip https://dl.google.com/android/repository/commandlinetools-linux-11076708_latest.zip \
    && unzip -q /tmp/cmdline-tools.zip -d ${ANDROID_HOME}/cmdline-tools \
    && mv ${ANDROID_HOME}/cmdline-tools/cmdline-tools ${ANDROID_HOME}/cmdline-tools/latest \
    && rm /tmp/cmdline-tools.zip \
    && yes | sdkmanager --sdk_root=${ANDROID_HOME} \
        "platform-tools" \
        "platforms;android-36" \
        "platforms;android-36.1" \
        "platforms;android-37.0" \
        "build-tools;36.0.0" \
        "build-tools;37.0.0" \
        "emulator" \
        "system-images;android-34;google_apis;x86_64" \
    && echo "no" | avdmanager create avd -n cloud_avd -k "system-images;android-34;google_apis;x86_64" --device "pixel_6" --force
