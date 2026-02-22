#!/usr/bin/env bash
set -euo pipefail

BOLD='\033[1m'
CYAN='\033[0;36m'
YELLOW='\033[0;33m'
RED='\033[0;31m'
GREEN='\033[0;32m'
NC='\033[0m'

AEM_ROOT="$PWD/target/author"
PID_FILE="$AEM_ROOT/crx-quickstart/conf/cq.pid"
PROFILING_DIR="$AEM_ROOT/profiling"

usage() {
  echo -e "${BOLD}Usage:${NC} nix run .#profile -- <command> [options]"
  echo ""
  echo -e "${BOLD}Commands:${NC}"
  echo "  start [mode] [duration]  Attach async-profiler (modes: cpu, alloc, wall, lock; default: cpu)"
  echo "  stop                     Stop profiling and save results"
  echo "  status                   Check if profiler is attached"
  echo "  jfr-dump                 Snapshot the always-on JFR recording"
  echo ""
  echo -e "${BOLD}Examples:${NC}"
  echo "  nix run .#profile -- start cpu 300    # CPU profile for 5 minutes"
  echo "  nix run .#profile -- start alloc 60   # Allocation profile for 1 minute"
  echo "  nix run .#profile -- jfr-dump         # Snapshot JFR recording"
  exit 1
}

get_pid() {
  if [[ ! -f "$PID_FILE" ]]; then
    echo -e "${RED}AEM not running — PID file not found: $PID_FILE${NC}" >&2
    exit 1
  fi
  local pid
  pid=$(cat "$PID_FILE")
  if ! kill -0 "$pid" 2>/dev/null; then
    echo -e "${RED}AEM process $pid is not running${NC}" >&2
    exit 1
  fi
  echo "$pid"
}

check_perf_paranoid() {
  if [[ -f /proc/sys/kernel/perf_event_paranoid ]]; then
    local level
    level=$(cat /proc/sys/kernel/perf_event_paranoid)
    if [[ "$level" -gt 1 ]]; then
      echo -e "${YELLOW}Warning: perf_event_paranoid=$level (>1) — async-profiler will fall back to itimer${NC}"
      echo -e "${YELLOW}For accurate CPU profiling: sudo sysctl kernel.perf_event_paranoid=1${NC}"
    fi
  fi
}

cmd_start() {
  local mode="${1:-cpu}"
  local duration="${2:-}"
  local pid
  pid=$(get_pid)

  mkdir -p "$PROFILING_DIR"

  local timestamp
  timestamp=$(date +%Y%m%d_%H%M%S)
  local output_file="$PROFILING_DIR/${mode}_${timestamp}.html"

  local event
  case "$mode" in
    cpu)   event="cpu" ;;
    alloc) event="alloc" ;;
    wall)  event="wall" ;;
    lock)  event="lock" ;;
    *)
      echo -e "${RED}Unknown mode: $mode (use cpu, alloc, wall, or lock)${NC}" >&2
      exit 1
      ;;
  esac

  check_perf_paranoid

  local duration_args=()
  if [[ -n "$duration" ]]; then
    duration_args=(-d "$duration")
  fi

  echo -e "${BOLD}${CYAN}Attaching async-profiler to AEM (PID $pid)...${NC}"
  echo -e "  Mode:     $mode"
  echo -e "  Output:   $output_file"
  if [[ -n "$duration" ]]; then
    echo -e "  Duration: ${duration}s"
  else
    echo -e "  Duration: until 'nix run .#profile -- stop'"
  fi

  if [[ -n "$duration" ]]; then
    async-profiler -e "$event" -d "$duration" -f "$output_file" "$pid"
    echo -e "${GREEN}Profiling complete: $output_file${NC}"
  else
    async-profiler start -e "$event" -f "$output_file" "$pid"
    echo -e "${GREEN}Profiler attached. Run 'nix run .#profile -- stop' to finish.${NC}"
  fi
}

cmd_stop() {
  local pid
  pid=$(get_pid)

  echo -e "${BOLD}${CYAN}Stopping async-profiler on AEM (PID $pid)...${NC}"
  async-profiler stop "$pid"
  echo -e "${GREEN}Profiling stopped. Check $PROFILING_DIR/ for results.${NC}"
}

cmd_status() {
  local pid
  pid=$(get_pid)

  echo -e "${BOLD}${CYAN}Profiler status for AEM (PID $pid):${NC}"
  async-profiler status "$pid"
}

cmd_jfr_dump() {
  local pid
  pid=$(get_pid)

  mkdir -p "$PROFILING_DIR"

  local timestamp
  timestamp=$(date +%Y%m%d_%H%M%S)
  local output_file="$PROFILING_DIR/jfr_snapshot_${timestamp}.jfr"

  echo -e "${BOLD}${CYAN}Dumping JFR recording from AEM (PID $pid)...${NC}"
  jcmd "$pid" JFR.dump name=oakgit filename="$output_file"
  echo -e "${GREEN}JFR snapshot saved: $output_file${NC}"
}

if [[ $# -lt 1 ]]; then
  usage
fi

case "$1" in
  start)    cmd_start "${2:-}" "${3:-}" ;;
  stop)     cmd_stop ;;
  status)   cmd_status ;;
  jfr-dump) cmd_jfr_dump ;;
  *)        usage ;;
esac
