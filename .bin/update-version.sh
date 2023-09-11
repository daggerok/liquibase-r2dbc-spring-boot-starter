#!/usr/bin/env bash

###
# Update maven project version in all pom.xml files up to specified snapshot.
#
# Usage:
#         bash .bin/update-version.sh 3.0.6-SNAPSHOT
###

set -o pipefail
set -e

GIT_BRANCH=$(git branch --show-current)
ROOT_PROJECT_DIR=$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)

if [[ -n "${VERSION}" ]] ; then
  echo "Detected VERSION environment variable: ${VERSION}"
elif [[ $# -ne 1 ]] || [[ -z "${1}" ]] ; then
  echo ""
  echo "Usage:"
  echo "  bash .bin/update-version.sh <VERSION>"
  echo "or:"
  echo "  VERSION=<VERSION> bash .bin/update-version.sh"
  echo ""
  exit 1
fi

VERSION="${1:-${VERSION}}"

if [[ "${VERSION}" == *"-SNAPSHOT" ]] ; then
  echo "Specified version is already a SNAPSHOT..."
else
  echo "Setting a SNAPSHOT for specified version..."
  VERSION="${VERSION}-SNAPSHOT"
fi

cd "${ROOT_PROJECT_DIR}" && ./mvnw build-helper:parse-version versions:set -DnewVersion="${VERSION}"
cd "${ROOT_PROJECT_DIR}" && ./mvnw build-helper:parse-version versions:commit
cd "${ROOT_PROJECT_DIR}" && UPDATED_VERSION=$(./mvnw help:evaluate -Dexpression=project.version -q -DforceStdout)
cd "${ROOT_PROJECT_DIR}" && git add . ; git commit -am "Update project version to: ${UPDATED_VERSION}" ; git push origin "${GIT_BRANCH}"

echo "Maven project snapshot version updated."
