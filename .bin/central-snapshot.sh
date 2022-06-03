#!/usr/bin/env bash

###
# Happy path release automation script
#
# Usage:
#         GPG_PASSPHRASE=... bash .bin/central-snapshot.sh
#         bash .bin/central-snapshot.sh -Dgpg.passphrase=...
#         GPG_PASSPHRASE=... bash .bin/central-snapshot.sh -DskipTests
###

set -o pipefail
set -e

if [[ -n "$GPG_PASSPHRASE" ]] ; then
  echo "Detected $GPG_PASSPHRASE environment variable"
fi

ARGS=${1:-"-Dgpg.passphrase=$GPG_PASSPHRASE"}
ROOT_PROJECT_DIR=$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)

cd "${ROOT_PROJECT_DIR}" && VERSION=$(bash mvnw help:evaluate -Dexpression=project.version -q -DforceStdout)
echo "Deploying $VERSION version..."

if [[ "$VERSION" != *"-SNAPSHOT" ]] ; then
  echo "Snapshot version was not found. Exiting..."
  exit 1
fi

echo "Deploying $VERSION..."
cd "${ROOT_PROJECT_DIR}" && bash mvnw -P central-release clean deploy ${ARGS}

echo "Snapshot released."
