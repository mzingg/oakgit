#!/usr/bin/env bash
set -e

# Colors
BOLD='\033[1m'
GREEN='\033[0;32m'
CYAN='\033[0;36m'
NC='\033[0m'

print_header() {
  echo ""
  echo -e "${BOLD}${CYAN}$1${NC}"
  echo "─────────────────────────────────────────────────────────────────"
}

print_table_header() {
  printf "${BOLD}%-40s %12s  →  %-12s${NC}\n" "Package" "Current" "Latest"
  echo "─────────────────────────────────────────────────────────────────"
}

# Maven dependency updates
print_header "Maven Dependency Updates"
maven_deps=$(mvn versions:display-dependency-updates -q 2>/dev/null | \
  grep -E "\->" | \
  grep -v "^\[INFO\] *$" | \
  sed 's/\[INFO\] *//' || true)

if [ -n "$maven_deps" ]; then
  print_table_header
  echo "$maven_deps" | while IFS= read -r line; do
    # Parse: "  groupId:artifactId .......................... current -> latest"
    clean=$(echo "$line" | sed 's/\.\.\.*/  /' | tr -s ' ')
    pkg=$(echo "$clean" | awk -F' -> ' '{print $1}' | awk '{print $1}')
    current=$(echo "$clean" | awk -F' -> ' '{print $1}' | awk '{print $NF}')
    latest=$(echo "$clean" | awk -F' -> ' '{print $2}' | awk '{print $1}')
    if [ -n "$pkg" ] && [ -n "$latest" ]; then
      printf "%-40s %12s  →  ${GREEN}%-12s${NC}\n" "$pkg" "$current" "$latest"
    fi
  done
else
  echo "All dependencies are up to date."
fi

# Maven plugin updates
print_header "Maven Plugin Updates"
maven_plugins=$(mvn versions:display-plugin-updates -q 2>/dev/null | \
  grep -E "\->" | \
  grep -v "^\[INFO\] *$" | \
  sed 's/\[INFO\] *//' || true)

if [ -n "$maven_plugins" ]; then
  print_table_header
  echo "$maven_plugins" | while IFS= read -r line; do
    clean=$(echo "$line" | sed 's/\.\.\.*/  /' | tr -s ' ')
    pkg=$(echo "$clean" | awk -F' -> ' '{print $1}' | awk '{print $1}')
    current=$(echo "$clean" | awk -F' -> ' '{print $1}' | awk '{print $NF}')
    latest=$(echo "$clean" | awk -F' -> ' '{print $2}' | awk '{print $1}')
    if [ -n "$pkg" ] && [ -n "$latest" ]; then
      printf "%-40s %12s  →  ${GREEN}%-12s${NC}\n" "$pkg" "$current" "$latest"
    fi
  done
else
  echo "All plugins are up to date."
fi

echo ""
