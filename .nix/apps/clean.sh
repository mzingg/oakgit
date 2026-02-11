#!/usr/bin/env bash
set -e

BOLD='\033[1m'
CYAN='\033[0;36m'
NC='\033[0m'

echo -e "${BOLD}${CYAN}Cleaning build artifacts...${NC}"

echo "Removing Maven target directories..."
mvn clean -q 2>/dev/null || true

echo -e "${BOLD}${CYAN}Done.${NC}"
