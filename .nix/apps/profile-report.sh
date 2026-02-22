#!/usr/bin/env bash
set -euo pipefail

BOLD='\033[1m'
CYAN='\033[0;36m'
YELLOW='\033[0;33m'
RED='\033[0;31m'
NC='\033[0m'

PROFILING_DIR="$PWD/target/author/profiling"

usage() {
  echo -e "${BOLD}Usage:${NC} nix run .#profile-report -- <command> [file]"
  echo ""
  echo -e "${BOLD}Commands:${NC}"
  echo "  summary [file]     Recording overview (event counts, duration)"
  echo "  hotmethods [file]  Top 50 hot methods from CPU samples"
  echo "  alloc [file]       Top 50 allocation sites"
  echo "  gc [file]          GC pause events"
  echo "  oakgit [file]      Filter CPU samples for stacks containing 'oakgit'"
  echo "  list               List all profiling artifacts"
  echo ""
  echo "All commands default to the latest .jfr file in $PROFILING_DIR"
  exit 1
}

find_latest_jfr() {
  if [[ ! -d "$PROFILING_DIR" ]]; then
    echo -e "${RED}Profiling directory not found: $PROFILING_DIR${NC}" >&2
    echo -e "${YELLOW}Run AEM first with 'nix run .#localrun'${NC}" >&2
    exit 1
  fi

  local latest
  latest=$(ls -t "$PROFILING_DIR"/*.jfr 2>/dev/null | head -1)
  if [[ -z "$latest" ]]; then
    echo -e "${RED}No .jfr files found in $PROFILING_DIR${NC}" >&2
    exit 1
  fi
  echo "$latest"
}

resolve_jfr() {
  local file="${1:-}"
  if [[ -n "$file" ]]; then
    if [[ ! -f "$file" ]]; then
      echo -e "${RED}File not found: $file${NC}" >&2
      exit 1
    fi
    echo "$file"
  else
    find_latest_jfr
  fi
}

cmd_summary() {
  local jfr
  jfr=$(resolve_jfr "${1:-}")
  echo -e "${BOLD}${CYAN}JFR Summary: $jfr${NC}"
  echo ""
  jfr summary "$jfr"
}

cmd_hotmethods() {
  local jfr
  jfr=$(resolve_jfr "${1:-}")
  echo -e "${BOLD}${CYAN}Top 50 Hot Methods: $jfr${NC}"
  echo ""
  jfr print --events jdk.ExecutionSample --stack-depth 1 "$jfr" \
    | grep -oP '(?<=at ).*(?=\()' \
    | sort | uniq -c | sort -rn | head -50
}

cmd_alloc() {
  local jfr
  jfr=$(resolve_jfr "${1:-}")
  echo -e "${BOLD}${CYAN}Top 50 Allocation Sites: $jfr${NC}"
  echo ""
  jfr print --events jdk.ObjectAllocationSample --stack-depth 1 "$jfr" \
    | grep -oP '(?<=at ).*(?=\()' \
    | sort | uniq -c | sort -rn | head -50
}

cmd_gc() {
  local jfr
  jfr=$(resolve_jfr "${1:-}")
  echo -e "${BOLD}${CYAN}GC Pause Events: $jfr${NC}"
  echo ""
  jfr print --events jdk.GCPhasePause "$jfr"
}

cmd_oakgit() {
  local jfr
  jfr=$(resolve_jfr "${1:-}")
  echo -e "${BOLD}${CYAN}oakgit CPU Samples: $jfr${NC}"
  echo ""
  jfr print --events jdk.ExecutionSample --stack-depth 64 "$jfr" \
    | grep -B5 -A5 "oakgit"
}

cmd_list() {
  if [[ ! -d "$PROFILING_DIR" ]]; then
    echo -e "${RED}Profiling directory not found: $PROFILING_DIR${NC}" >&2
    exit 1
  fi

  echo -e "${BOLD}${CYAN}Profiling artifacts in $PROFILING_DIR:${NC}"
  echo ""

  local count=0
  for f in "$PROFILING_DIR"/*; do
    if [[ -f "$f" ]]; then
      local size
      size=$(du -h "$f" | cut -f1)
      local modified
      modified=$(date -r "$f" '+%Y-%m-%d %H:%M:%S')
      printf "  %-8s  %-20s  %s\n" "$size" "$modified" "$(basename "$f")"
      count=$((count + 1))
    fi
  done

  if [[ $count -eq 0 ]]; then
    echo -e "  ${YELLOW}(no artifacts found)${NC}"
  fi
}

if [[ $# -lt 1 ]]; then
  usage
fi

case "$1" in
  summary)    cmd_summary "${2:-}" ;;
  hotmethods) cmd_hotmethods "${2:-}" ;;
  alloc)      cmd_alloc "${2:-}" ;;
  gc)         cmd_gc "${2:-}" ;;
  oakgit)     cmd_oakgit "${2:-}" ;;
  list)       cmd_list ;;
  *)          usage ;;
esac
