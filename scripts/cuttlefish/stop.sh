#!/usr/bin/env bash
# Stop local Docker Compose Cuttlefish instance.
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
# shellcheck source=common.sh
source "${SCRIPT_DIR}/common.sh"

cd "${ROOT_DIR}"
if command -v docker >/dev/null 2>&1; then
  docker compose --profile cuttlefish stop cuttlefish 2>/dev/null || true
  docker compose --profile cuttlefish rm -f cuttlefish 2>/dev/null || true
fi

if [[ -x "${CF_DATA_DIR}/bin/stop_cvd" ]]; then
  (cd "${CF_DATA_DIR}" && HOME="${CF_DATA_DIR}" ./bin/stop_cvd) >/dev/null 2>&1 || true
fi

echo "Cuttlefish stopped."
