#!/usr/bin/env bash
set -euo pipefail

# Colors
BOLD='\033[1m'
GREEN='\033[0;32m'
RED='\033[0;31m'
CYAN='\033[0;36m'
YELLOW='\033[0;33m'
NC='\033[0m'

# Find project root by searching upward from $PWD for pom.xml.
# This is necessary because `nix run` copies the script into the Nix store,
# so BASH_SOURCE[0] cannot be used to locate the project.
PROJECT_ROOT="$PWD"
while [ "$PROJECT_ROOT" != "/" ] && [ ! -f "$PROJECT_ROOT/pom.xml" ]; do
  PROJECT_ROOT="$(dirname "$PROJECT_ROOT")"
done
if [ ! -f "$PROJECT_ROOT/pom.xml" ]; then
  echo -e "${RED}ERROR: Cannot find pom.xml – run this command from the project directory${NC}"
  exit 1
fi
OPS_DIR="$PROJECT_ROOT/ops"
DEPS_DIR="$OPS_DIR/deps"
POM_FILE="$PROJECT_ROOT/pom.xml"

# --- Parse flags ---
SET_DEFAULT=false
for arg in "$@"; do
  case "$arg" in
    --set-default) SET_DEFAULT=true ;;
    --help|-h)
      echo "Usage: sync-aem-sdk [--set-default]"
      echo ""
      echo "Extracts dependency versions from the AEM SDK zip in ops/deps/,"
      echo "installs proprietary JARs to the local Maven repo, and generates"
      echo "a Maven profile in pom.xml for building against this SDK version."
      echo ""
      echo "Options:"
      echo "  --set-default  Also update the top-level POM <properties> to this SDK"
      exit 0
      ;;
    *)
      echo -e "${RED}ERROR: Unknown argument: $arg${NC}"
      echo "Usage: sync-aem-sdk [--set-default]"
      exit 1
      ;;
  esac
done

# --- Artifact mapping table ---
# Format: BUNDLE_PATTERN|GROUP_ID|ARTIFACT_ID|PRIVATE
# PRIVATE=1 means install to local repo; PRIVATE=0 means public (version only)
ARTIFACTS=(
  "oak-core|org.apache.jackrabbit|oak-core|0"
  "oak-jcr|org.apache.jackrabbit|oak-jcr|0"
  "oak-store-document|org.apache.jackrabbit|oak-store-document|0"
  "oak-lucene|org.apache.jackrabbit|oak-lucene|0"
  "org.apache.sling.jcr.base|org.apache.sling|org.apache.sling.jcr.base|0"
  "org.apache.sling.discovery.api|org.apache.sling|org.apache.sling.discovery.api|0"
  "com.adobe.granite.repository|com.adobe.granite|com.adobe.granite.repository|1"
  "com.adobe.granite.repository.indexdefs|com.adobe.granite|com.adobe.granite.repository.indexdefs|1"
  "com.adobe.granite.toggle.api|com.adobe.granite|com.adobe.granite.toggle.api|1"
  "com.adobe.granite.license|com.adobe.granite|com.adobe.granite.license|1"
  "crx-api|com.adobe|crx-api|1"
)

# --- Additional bundles to report (BOM-managed, no override needed) ---
REPORT_ONLY=(
  "org.apache.sling.commons.threads|org.apache.sling|org.apache.sling.commons.threads"
  "org.apache.sling.jcr.api|org.apache.sling|org.apache.sling.jcr.api"
  "org.apache.commons.lang3|org.apache.commons|commons-lang3"
  "commons-io|commons-io|commons-io"
  "slf4j.api|org.slf4j|slf4j-api"
)

# --- Find SDK zip ---
SDK_ZIP=$(find "$DEPS_DIR" -maxdepth 1 -name "aem-sdk-*.zip" 2>/dev/null | sort | tail -1)
if [ -z "$SDK_ZIP" ]; then
  echo -e "${RED}ERROR: No aem-sdk-*.zip found in $DEPS_DIR${NC}"
  echo "Download the AEM SDK zip and place it in ops/deps/"
  exit 1
fi

SDK_FILENAME=$(basename "$SDK_ZIP")
echo -e "${BOLD}${CYAN}AEM SDK Sync${NC}"
echo -e "SDK: ${SDK_FILENAME}"
echo ""

# Parse SDK version from filename: aem-sdk-2026.2.24288.20260204T121510Z-260100.zip
SDK_FULL_VERSION=$(echo "$SDK_FILENAME" | sed 's/^aem-sdk-//;s/\.zip$//')
SDK_SHORT_VERSION=$(echo "$SDK_FULL_VERSION" | sed -E 's/^([0-9]+\.[0-9]+\.[0-9]+).*/\1/')

if [ -z "$SDK_SHORT_VERSION" ]; then
  echo -e "${RED}ERROR: Cannot parse SDK version from filename: $SDK_FILENAME${NC}"
  exit 1
fi

echo -e "Full version:  ${SDK_FULL_VERSION}"
echo -e "Short version: ${SDK_SHORT_VERSION}"
echo ""

# --- Extract to temp directory ---
TMPDIR=$(mktemp -d)
trap 'rm -rf "$TMPDIR"' EXIT

echo -e "${CYAN}Extracting SDK...${NC}"

# Step 1: Extract quickstart JAR from SDK zip
unzip -q -o "$SDK_ZIP" "aem-sdk-quickstart-*.jar" -d "$TMPDIR"
QS_JAR=$(find "$TMPDIR" -maxdepth 1 -name "aem-sdk-quickstart-*.jar" | head -1)
if [ -z "$QS_JAR" ]; then
  echo -e "${RED}ERROR: No quickstart JAR found in SDK zip${NC}"
  exit 1
fi

# Step 2: Extract standalone quickstart from nested JAR
cd "$TMPDIR"
jar xf "$QS_JAR" "static/app/"
STANDALONE_JAR=$(find "$TMPDIR/static/app" -name "*standalone-quickstart.jar" | head -1)
if [ -z "$STANDALONE_JAR" ]; then
  echo -e "${RED}ERROR: No standalone quickstart JAR found${NC}"
  exit 1
fi

# Step 3: Extract bundles from standalone quickstart
jar xf "$STANDALONE_JAR" "resources/"
BUNDLES_DIR="$TMPDIR/resources"

echo -e "${GREEN}Extraction complete.${NC}"
echo ""

# --- Scan bundles and collect versions ---
declare -A FOUND_VERSIONS
declare -A FOUND_PATHS

for entry in "${ARTIFACTS[@]}"; do
  IFS='|' read -r pattern group artifact private <<< "$entry"

  # Find the bundle JAR matching the pattern
  bundle_jar=$(find "$BUNDLES_DIR" -name "${pattern}-[0-9]*.jar" \
    ! -name "*-spi-*" \
    ! -name "*.hc.*" \
    ! -name "*.checker*" \
    ! -name "*.http*" \
    2>/dev/null | head -1)

  if [ -n "$bundle_jar" ]; then
    jar_name=$(basename "$bundle_jar")
    # Strip pattern prefix and .jar suffix to get version
    version=$(echo "$jar_name" | sed "s/^${pattern}-//;s/\.jar$//")
    FOUND_VERSIONS["$artifact"]="$version"
    FOUND_PATHS["$artifact"]="$bundle_jar"
  else
    echo -e "${YELLOW}WARNING: Bundle not found for pattern: ${pattern}${NC}"
  fi
done

# Collect report-only versions
declare -A REPORT_VERSIONS
for entry in "${REPORT_ONLY[@]}"; do
  IFS='|' read -r pattern group artifact <<< "$entry"
  bundle_jar=$(find "$BUNDLES_DIR" -name "${pattern}-[0-9]*.jar" 2>/dev/null | head -1)
  if [ -n "$bundle_jar" ]; then
    jar_name=$(basename "$bundle_jar")
    version=$(echo "$jar_name" | sed "s/^${pattern}-//;s/\.jar$//")
    REPORT_VERSIONS["$artifact"]="$version"
  fi
done

# --- Write properties file ---
PROPS_FILE="$DEPS_DIR/aem-versions-${SDK_SHORT_VERSION}.properties"

cat > "$PROPS_FILE" <<EOF
# AEM SDK dependency versions
# Generated by: nix run .#sync-aem-sdk
# SDK: ${SDK_FILENAME}

# WCM.io BOM version for this SDK
aem.sdk.version=${SDK_FULL_VERSION}.0000

# Apache Jackrabbit OAK (BOM has incorrect version - SDK overrides)
oak.version=${FOUND_VERSIONS["oak-core"]}

# Apache Sling (not managed by BOM)
sling.base.version=${FOUND_VERSIONS["org.apache.sling.jcr.base"]}
sling.discovery.version=${FOUND_VERSIONS["org.apache.sling.discovery.api"]}

# Vendor/proprietary artifacts (installed to local Maven repo by this script)
vendor.granite.repository.version=${FOUND_VERSIONS["com.adobe.granite.repository"]}
vendor.granite.repository.indexdefs.version=${FOUND_VERSIONS["com.adobe.granite.repository.indexdefs"]}
vendor.granite.toggle.api.version=${FOUND_VERSIONS["com.adobe.granite.toggle.api"]}
vendor.granite.license.version=${FOUND_VERSIONS["com.adobe.granite.license"]}
vendor.crx.api.version=${FOUND_VERSIONS["crx-api"]}
EOF

echo -e "${GREEN}Properties written to: ${PROPS_FILE}${NC}"
echo ""

# --- Warn if OAK version changed from any existing properties file ---
NEW_OAK_VERSION="${FOUND_VERSIONS["oak-core"]}"
for existing_props in "$DEPS_DIR"/aem-versions-*.properties; do
  [ -f "$existing_props" ] || continue
  [ "$existing_props" = "$PROPS_FILE" ] && continue
  existing_oak=$(grep '^oak.version=' "$existing_props" 2>/dev/null | cut -d= -f2)
  if [ -n "$existing_oak" ] && [ "$existing_oak" != "$NEW_OAK_VERSION" ]; then
    echo -e "${YELLOW}${BOLD}WARNING: OAK version changed: ${existing_oak} -> ${NEW_OAK_VERSION}${NC}"
    echo -e "${YELLOW}  A new SQL pattern catalog may be needed for OAK ${NEW_OAK_VERSION}.${NC}"
    echo -e "${YELLOW}  Check: src/test/resources/oak-sql-catalog/oak-${NEW_OAK_VERSION}.json${NC}"
    echo ""
    break
  fi
done

# --- Install proprietary JARs to local Maven repo ---
echo -e "${BOLD}${CYAN}Installing proprietary JARs to local Maven repo...${NC}"

for entry in "${ARTIFACTS[@]}"; do
  IFS='|' read -r pattern group artifact private <<< "$entry"

  if [ "$private" = "1" ] && [ -n "${FOUND_PATHS[$artifact]+x}" ]; then
    version="${FOUND_VERSIONS[$artifact]}"
    jar_path="${FOUND_PATHS[$artifact]}"
    echo -e "  ${artifact} ${version}"
    mvn -q install:install-file \
      -Dfile="$jar_path" \
      -DgroupId="$group" \
      -DartifactId="$artifact" \
      -Dversion="$version" \
      -Dpackaging=jar \
      -DgeneratePom=true 2>/dev/null
  fi
done

echo -e "${GREEN}Done.${NC}"
echo ""

# --- Print comparison table ---
echo -e "${BOLD}${CYAN}SDK Bundle Versions${NC}"
echo "─────────────────────────────────────────────────────────────────"
printf "${BOLD}%-45s %-15s %-8s${NC}\n" "Artifact" "SDK Version" "Scope"
echo "─────────────────────────────────────────────────────────────────"

for entry in "${ARTIFACTS[@]}"; do
  IFS='|' read -r pattern group artifact private <<< "$entry"
  version="${FOUND_VERSIONS[$artifact]:-NOT FOUND}"
  scope="override"
  [ "$private" = "1" ] && scope="private"
  printf "%-45s %-15s %-8s\n" "$group:$artifact" "$version" "$scope"
done

echo ""
echo -e "${BOLD}BOM-managed (no override needed):${NC}"
for entry in "${REPORT_ONLY[@]}"; do
  IFS='|' read -r pattern group artifact <<< "$entry"
  version="${REPORT_VERSIONS[$artifact]:-NOT FOUND}"
  printf "%-45s %-15s %-8s\n" "$group:$artifact" "$version" "bom"
done

# --- Collect version values for POM manipulation ---
V_AEM_SDK="${SDK_FULL_VERSION}.0000"
V_OAK="${FOUND_VERSIONS["oak-core"]}"
V_SLING_BASE="${FOUND_VERSIONS["org.apache.sling.jcr.base"]}"
V_SLING_DISCOVERY="${FOUND_VERSIONS["org.apache.sling.discovery.api"]}"
V_GRANITE_REPO="${FOUND_VERSIONS["com.adobe.granite.repository"]}"
V_GRANITE_INDEXDEFS="${FOUND_VERSIONS["com.adobe.granite.repository.indexdefs"]}"
V_GRANITE_TOGGLE="${FOUND_VERSIONS["com.adobe.granite.toggle.api"]}"
V_GRANITE_LICENSE="${FOUND_VERSIONS["com.adobe.granite.license"]}"
V_CRX_API="${FOUND_VERSIONS["crx-api"]}"

PROFILE_ID="aem-${SDK_SHORT_VERSION}"

# --- Generate Maven profile XML block ---
PROFILE_BLOCK="    <!-- AEM SDK ${SDK_SHORT_VERSION} (generated by: nix run .#sync-aem-sdk) -->
    <profile>
      <id>${PROFILE_ID}</id>
      <properties>
        <aem.sdk.version>${V_AEM_SDK}</aem.sdk.version>
        <oak.version>${V_OAK}</oak.version>
        <sling.base.version>${V_SLING_BASE}</sling.base.version>
        <sling.discovery.version>${V_SLING_DISCOVERY}</sling.discovery.version>
        <vendor.granite.repository.version>${V_GRANITE_REPO}</vendor.granite.repository.version>
        <vendor.granite.repository.indexdefs.version>${V_GRANITE_INDEXDEFS}</vendor.granite.repository.indexdefs.version>
        <vendor.granite.toggle.api.version>${V_GRANITE_TOGGLE}</vendor.granite.toggle.api.version>
        <vendor.granite.license.version>${V_GRANITE_LICENSE}</vendor.granite.license.version>
        <vendor.crx.api.version>${V_CRX_API}</vendor.crx.api.version>
      </properties>
    </profile>"

# --- Insert or update profile in pom.xml ---
echo ""
echo -e "${BOLD}${CYAN}Updating pom.xml...${NC}"

if grep -q "<id>${PROFILE_ID}</id>" "$POM_FILE"; then
  echo -e "  Updating existing profile: ${PROFILE_ID}"
  # Remove the old profile block (comment line + <profile>...</profile>) and replace
  # Use awk for multi-line replacement: find the comment before the profile, then the profile block
  awk -v profile_id="${PROFILE_ID}" -v new_block="${PROFILE_BLOCK}" '
    # Match the generated comment line preceding our profile
    /<!-- AEM SDK .* \(generated by: nix run/ {
      skip_comment = 1
      buf_len = 0
      buf[buf_len++] = $0
      next
    }
    skip_comment == 1 {
      if ($0 ~ "<profile>") {
        in_profile = 1
        depth = 1
        skip_comment = 0
        buf[buf_len++] = $0
        next
      } else {
        # False alarm — flush buffer and continue
        for (i = 0; i < buf_len; i++) print buf[i]
        skip_comment = 0
      }
    }
    in_profile == 1 {
      buf[buf_len++] = $0
      if ($0 ~ "<id>" profile_id "</id>") {
        confirmed = 1
      }
      if ($0 ~ /<profile>/) depth++
      if ($0 ~ /<\/profile>/) {
        depth--
        if (depth == 0) {
          if (confirmed) {
            # Replace this profile with the new block
            print new_block
          } else {
            # Wrong profile — replay all buffered lines
            for (i = 0; i < buf_len; i++) print buf[i]
          }
          in_profile = 0
          confirmed = 0
          buf_len = 0
          next
        }
      }
      next
    }
    { print }
  ' "$POM_FILE" > "${POM_FILE}.tmp" && mv "${POM_FILE}.tmp" "$POM_FILE"
else
  echo -e "  Inserting new profile: ${PROFILE_ID}"
  # Insert before the closing </profiles> tag
  awk -v new_block="${PROFILE_BLOCK}" '
    /<\/profiles>/ {
      print ""
      print new_block
    }
    { print }
  ' "$POM_FILE" > "${POM_FILE}.tmp" && mv "${POM_FILE}.tmp" "$POM_FILE"
fi

echo -e "  ${GREEN}Profile ${PROFILE_ID} written to pom.xml${NC}"

# --- Update top-level properties if --set-default ---
if [ "$SET_DEFAULT" = true ]; then
  echo -e "  Updating top-level <properties> (--set-default)"
  # Only replace properties in the FIRST <properties> block (top-level),
  # not inside profile <properties> blocks
  awk -v aem="${V_AEM_SDK}" -v oak="${V_OAK}" \
      -v sbase="${V_SLING_BASE}" -v sdisc="${V_SLING_DISCOVERY}" \
      -v grepo="${V_GRANITE_REPO}" -v gidx="${V_GRANITE_INDEXDEFS}" \
      -v gtog="${V_GRANITE_TOGGLE}" -v glic="${V_GRANITE_LICENSE}" \
      -v crx="${V_CRX_API}" '
    BEGIN { in_first_props = 0; done = 0 }
    !done && /<properties>/ { in_first_props = 1 }
    in_first_props && /<\/properties>/ { in_first_props = 0; done = 1 }
    in_first_props {
      sub(/<aem.sdk.version>[^<]*</, "<aem.sdk.version>" aem "<")
      sub(/<oak.version>[^<]*</, "<oak.version>" oak "<")
      sub(/<sling.base.version>[^<]*</, "<sling.base.version>" sbase "<")
      sub(/<sling.discovery.version>[^<]*</, "<sling.discovery.version>" sdisc "<")
      sub(/<vendor.granite.repository.version>[^<]*</, "<vendor.granite.repository.version>" grepo "<")
      sub(/<vendor.granite.repository.indexdefs.version>[^<]*</, "<vendor.granite.repository.indexdefs.version>" gidx "<")
      sub(/<vendor.granite.toggle.api.version>[^<]*</, "<vendor.granite.toggle.api.version>" gtog "<")
      sub(/<vendor.granite.license.version>[^<]*</, "<vendor.granite.license.version>" glic "<")
      sub(/<vendor.crx.api.version>[^<]*</, "<vendor.crx.api.version>" crx "<")
    }
    { print }
  ' "$POM_FILE" > "${POM_FILE}.tmp" && mv "${POM_FILE}.tmp" "$POM_FILE"
  echo -e "  ${GREEN}Top-level properties updated${NC}"
fi

echo ""
echo -e "${GREEN}Sync complete.${NC}"
echo ""
echo -e "${BOLD}Usage:${NC}"
echo "  mvn test                       # Build with default SDK (top-level properties)"
echo "  mvn test -P${PROFILE_ID}  # Build with this SDK profile"
if [ "$SET_DEFAULT" = false ]; then
  echo ""
  echo -e "${YELLOW}Tip: Run with --set-default to also update the top-level POM properties.${NC}"
fi
