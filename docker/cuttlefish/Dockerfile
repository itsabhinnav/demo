# Cuttlefish host for AOSP Automotive (aosp_cf_x86_64_auto) — Android 17.
# Images are mounted from ./cuttlefish-data (populated by scripts/cuttlefish/fetch-aosp-car.sh).
# Host package under the mount provides ./bin/adb and ./bin/launch_cvd.
FROM us-docker.pkg.dev/android-cuttlefish-artifacts/cuttlefish-orchestration/cuttlefish-orchestration:stable

USER root
RUN apt-get update && apt-get install -y --no-install-recommends \
      curl \
      ca-certificates \
      python3 \
    && rm -rf /var/lib/apt/lists/*

RUN mkdir -p /home/vsoc-01/cf /cf \
    && if id vsoc-01 >/dev/null 2>&1; then chown -R vsoc-01:vsoc-01 /home/vsoc-01; fi

COPY docker/cuttlefish/entrypoint.sh /usr/local/bin/cuttlefish-entrypoint.sh
RUN chmod +x /usr/local/bin/cuttlefish-entrypoint.sh

EXPOSE 8443 6520

WORKDIR /cf
ENTRYPOINT ["/usr/local/bin/cuttlefish-entrypoint.sh"]
