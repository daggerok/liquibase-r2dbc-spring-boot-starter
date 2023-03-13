#!/usr/bin/env bash

###
# Happy path snapshot automation script
#
# Usage:
#         GPG_PASSPHRASE=... bash .bin/central-snapshot.sh
#         bash .bin/central-snapshot.sh -Dgpg.passphrase=...
#         GPG_PASSPHRASE=... bash .bin/central-snapshot.sh -DskipTests
#
# Require:
#         brew reinstall gnupg@2.2
#         export GPG_HOME=$HOME/.homebrew/opt/gnupg@2.2
#         export PATH=$GPG_HOME/bin:$PATH
#         gpg --gen-key # specify: Real name, Email address, press Okay, enter passphrase
#         see details here: https://central.sonatype.org/publish/publish-maven/
#         and here: https://central.sonatype.org/publish/requirements/gpg/#listing-keys
###

set -o pipefail
set -e

if [[ -n "$GPG_PASSPHRASE" ]] ; then
  echo "Detected GPG_PASSPHRASE environment variable"
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
