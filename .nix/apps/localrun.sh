#!/usr/bin/env bash
set -euo pipefail

BOLD='\033[1m'
CYAN='\033[0;36m'
YELLOW='\033[0;33m'
RED='\033[0;31m'
NC='\033[0m'

AEM_ROOT="$PWD/target/author"
CQ_PID_FILE="$AEM_ROOT/crx-quickstart/conf/cq.pid"

if [[ ! -d "$AEM_ROOT/crx-quickstart" ]]; then
  echo -e "${RED}AEM not unpacked — run 'nix run .#localdeploy' first${NC}" >&2
  exit 1
fi

# --- Stop AEM gracefully ---
echo -e "${BOLD}${CYAN}Stopping AEM...${NC}"
"$AEM_ROOT/crx-quickstart/bin/stop" || true

# --- Force-kill after 10s if still running ---
PID=$(cat "$CQ_PID_FILE" 2>/dev/null || true)
if [[ -n "$PID" ]] && kill -0 "$PID" 2>/dev/null; then
  echo -e "${YELLOW}Process $PID still running, waiting 10s...${NC}"
  for i in $(seq 1 10); do
    sleep 1
    if ! kill -0 "$PID" 2>/dev/null; then
      break
    fi
  done
  if kill -0 "$PID" 2>/dev/null; then
    echo -e "${RED}Force-killing process $PID${NC}"
    kill -9 "$PID" || true
  fi
fi

# --- Ensure profiling output dir exists ---
mkdir -p "$AEM_ROOT/profiling"

# --- Start AEM ---
echo -e "${BOLD}${CYAN}Starting AEM...${NC}"
"$AEM_ROOT/crx-quickstart/bin/start"

# --- Tail error log ---
LOG_FILE="$AEM_ROOT/crx-quickstart/logs/error.log"
echo -e "${BOLD}${CYAN}Waiting for log file...${NC}"
while [[ ! -f "$LOG_FILE" ]]; do
  sleep 1
done
tail -f "$LOG_FILE"
