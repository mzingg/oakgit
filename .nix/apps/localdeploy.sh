#!/usr/bin/env bash
set -euo pipefail

BOLD='\033[1m'
CYAN='\033[0;36m'
RED='\033[0;31m'
NC='\033[0m'

PROJECT_ROOT="$PWD"
AEM_ROOT="$PROJECT_ROOT/target/author"
AEM_JAR="aem-quickstart.jar"

# --- Build the project ---
echo -e "${BOLD}${CYAN}Building project...${NC}"
mvn -f "$PROJECT_ROOT/pom.xml" package -DskipTests -q

# --- Clean previous installation ---
echo -e "${BOLD}${CYAN}Cleaning previous AEM installation...${NC}"
rm -rf "$AEM_ROOT/crx-quickstart"
rm -f "$AEM_ROOT/$AEM_JAR"
mkdir -p "$AEM_ROOT"

# --- Find SDK zip ---
SDK_ZIP=$(find "$PROJECT_ROOT/ops/deps" -maxdepth 1 -name 'aem-sdk-*.zip' -print -quit)
if [[ -z "$SDK_ZIP" ]]; then
  echo -e "${RED}No aem-sdk-*.zip found in ops/deps/ — download the AEM SDK first${NC}" >&2
  exit 1
fi
echo "Using SDK: $(basename "$SDK_ZIP")"

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

echo ""
echo -e "${BOLD}${CYAN}Local AEM deployment ready at: $AEM_ROOT${NC}"
echo "Start with: $AEM_ROOT/crx-quickstart/bin/start"
