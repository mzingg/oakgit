#!/usr/bin/env bash
set -euo pipefail

BOLD='\033[1m'
CYAN='\033[0;36m'
RED='\033[0;31m'
YELLOW='\033[0;33m'
NC='\033[0m'

PROJECT_ROOT="$PWD"
AEM_ROOT="$PROJECT_ROOT/target/author"
AEM_JAR="aem-quickstart.jar"

ensure_wknd_package() {
  local deps_dir="$PROJECT_ROOT/ops/deps"
  local cached
  cached=$(find "$deps_dir" -maxdepth 1 -name 'aem-guides-wknd.all-*.zip' -print -quit)

  # If cached file exists and is less than 10 days old, use it directly
  if [[ -n "$cached" ]]; then
    local stale
    stale=$(find "$cached" -maxdepth 0 -mtime +10 -print -quit)
    if [[ -z "$stale" ]]; then
      echo "Using cached WKND package: $(basename "$cached")"
      WKND_ZIP="$cached"
      return
    fi
  fi

  # Query GitHub API for latest release
  local api_response
  if ! api_response=$(curl -sfL "https://api.github.com/repos/adobe/aem-guides-wknd/releases/latest" 2>/dev/null); then
    if [[ -n "$cached" ]]; then
      echo -e "${YELLOW}Network unavailable — using stale cached WKND package: $(basename "$cached")${NC}"
      WKND_ZIP="$cached"
    else
      echo -e "${YELLOW}Network unavailable — skipping WKND content package${NC}"
    fi
    return
  fi

  local tag_name download_url filename
  tag_name=$(echo "$api_response" | jq -r '.tag_name')
  download_url=$(echo "$api_response" | jq -r '.assets[] | select(.name | test("^aem-guides-wknd\\.all-.*\\.zip$")) | select(.name | test("classic") | not) | .browser_download_url')

  if [[ -z "$download_url" || "$download_url" == "null" ]]; then
    echo -e "${YELLOW}Could not find WKND .all zip in latest release — skipping${NC}"
    [[ -n "$cached" ]] && WKND_ZIP="$cached"
    return
  fi

  filename=$(basename "$download_url")

  # If cached file matches latest version, touch it and use it
  if [[ -n "$cached" && "$(basename "$cached")" == "$filename" ]]; then
    echo "Cached WKND package is current ($tag_name) — refreshing timestamp"
    touch "$cached"
    WKND_ZIP="$cached"
    return
  fi

  # Download new version (delete old cached file if any)
  [[ -n "$cached" ]] && rm -f "$cached"
  echo "Downloading WKND $tag_name: $filename"
  if curl -sfL -o "$deps_dir/$filename" "$download_url"; then
    echo "Downloaded: $filename"
    WKND_ZIP="$deps_dir/$filename"
  else
    echo -e "${YELLOW}Download failed — skipping WKND content package${NC}"
  fi
}

# --- Build the project ---
echo -e "${BOLD}${CYAN}Building project...${NC}"
mvn -f "$PROJECT_ROOT/pom.xml" package -DskipTests -q

# --- Stop running AEM instance ---
CQ_PID_FILE="$AEM_ROOT/crx-quickstart/conf/cq.pid"
if [[ -f "$AEM_ROOT/crx-quickstart/bin/stop" ]]; then
  echo -e "${BOLD}${CYAN}Stopping AEM...${NC}"
  "$AEM_ROOT/crx-quickstart/bin/stop" || true
  PID=$(cat "$CQ_PID_FILE" 2>/dev/null || true)
  if [[ -n "$PID" ]] && kill -0 "$PID" 2>/dev/null; then
    echo "Waiting 10s for process $PID to exit..."
    for i in $(seq 1 10); do
      sleep 1
      if ! kill -0 "$PID" 2>/dev/null; then
        break
      fi
    done
    if kill -0 "$PID" 2>/dev/null; then
      echo "Force-killing process $PID"
      kill -9 "$PID" || true
    fi
  fi
fi

# --- Clean previous installation ---
echo -e "${BOLD}${CYAN}Cleaning previous AEM installation...${NC}"
rm -rf "$AEM_ROOT"
mkdir -p "$AEM_ROOT"

# --- Find SDK zip ---
SDK_ZIP=$(find "$PROJECT_ROOT/ops/deps" -maxdepth 1 -name 'aem-sdk-*.zip' -print -quit)
if [[ -z "$SDK_ZIP" ]]; then
  echo -e "${RED}No aem-sdk-*.zip found in ops/deps/ — download the AEM SDK first${NC}" >&2
  exit 1
fi
echo "Using SDK: $(basename "$SDK_ZIP")"

# --- Ensure WKND content package ---
echo -e "${BOLD}${CYAN}Checking WKND content package...${NC}"
WKND_ZIP=""
ensure_wknd_package

# --- Extract quickstart JAR from SDK zip ---
echo -e "${BOLD}${CYAN}Extracting quickstart JAR...${NC}"
unzip -jo "$SDK_ZIP" 'aem-sdk-quickstart-*.jar' -d "$AEM_ROOT"
# Rename the versioned JAR to a stable name
QS_JAR=$(find "$AEM_ROOT" -maxdepth 1 -name 'aem-sdk-quickstart-*.jar' -print -quit)
if [[ -n "$QS_JAR" ]]; then
  mv "$QS_JAR" "$AEM_ROOT/$AEM_JAR"
fi

# --- Unpack AEM ---
echo -e "${BOLD}${CYAN}Unpacking AEM quickstart...${NC}"
(cd "$AEM_ROOT" && java -jar "$AEM_JAR" -unpack)

# --- Copy OSGi configs ---
echo -e "${BOLD}${CYAN}Copying OSGi configs...${NC}"
cp -rv "$PROJECT_ROOT/ops/localdev/crx-quickstart/." "$AEM_ROOT/crx-quickstart/"

# --- Copy oakgit bundle JAR ---
echo -e "${BOLD}${CYAN}Copying oakgit bundle...${NC}"
BUNDLE_JAR=$(find "$PROJECT_ROOT/target" -maxdepth 1 -name 'oakgit-persistence-*.jar' ! -name '*-sources.jar' -print -quit)
if [[ -z "$BUNDLE_JAR" ]]; then
  echo -e "${RED}oakgit bundle JAR not found in target/ — build may have failed${NC}" >&2
  exit 1
fi
mkdir -p "$AEM_ROOT/crx-quickstart/install/9"
cp -v "$BUNDLE_JAR" "$AEM_ROOT/crx-quickstart/install/9/"

# --- Copy WKND content package ---
if [[ -n "$WKND_ZIP" ]]; then
  echo -e "${BOLD}${CYAN}Copying WKND content package...${NC}"
  cp -v "$WKND_ZIP" "$AEM_ROOT/crx-quickstart/install/"
fi

echo ""
echo -e "${BOLD}${CYAN}Local AEM deployment ready at: $AEM_ROOT${NC}"
echo "Start with: $AEM_ROOT/crx-quickstart/bin/start"
