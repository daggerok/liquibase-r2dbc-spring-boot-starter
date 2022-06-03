#!/usr/bin/env bash

###
# Happy path release automation script
#
# Usage:
#         GPG_PASSPHRASE=... bash .bin/central-release.sh
#         bash .bin/central-release.sh -Dgpg.passphrase=...
#         GPG_PASSPHRASE=... bash .bin/central-release.sh -DskipTests
###

set -o pipefail
set -e

if [[ -n "$GPG_PASSPHRASE" ]] ; then
  echo "Detected $GPG_PASSPHRASE environment variable"
  export GPG_PASSPHRASE
fi

ARGS=${1:-"-Dgpg.passphrase=$GPG_PASSPHRASE"}
ROOT_PROJECT_DIR=$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)

cd "${ROOT_PROJECT_DIR}" && VERSION=$(bash mvnw help:evaluate -Dexpression=project.version -q -DforceStdout)
echo "Deploying $VERSION version..."

if [[ "$VERSION" == *"-SNAPSHOT" ]] ; then
  echo "Snapshot version was detected. Update $VERSION version to released, commit and push changes first..."
  cd "${ROOT_PROJECT_DIR}" && ./mvnw build-helper:parse-version versions:set -DnewVersion=\${parsedVersion.majorVersion}.\${parsedVersion.minorVersion}.\${parsedVersion.incrementalVersion}
  cd "${ROOT_PROJECT_DIR}" && ./mvnw build-helper:parse-version versions:commit
  cd "${ROOT_PROJECT_DIR}" && VERSION=$(mvn help:evaluate -Dexpression=project.version -q -DforceStdout)
  cd "${ROOT_PROJECT_DIR}" && git add . ; git commit -am "Release $VERSION version." ; git push origin master
fi

echo "Deploying $VERSION release..."
cd "${ROOT_PROJECT_DIR}" && ./mvnw -P central-release clean deploy ${ARGS}

echo "Creating $VERSION GitHub tag..."
cd "${ROOT_PROJECT_DIR}" && git tag "$VERSION" ; git push origin "$VERSION"

echo "Create new snapshot version for next development iteration..."
cd "${ROOT_PROJECT_DIR}" && ./mvnw build-helper:parse-version versions:set -DnewVersion=\${parsedVersion.majorVersion}.\${parsedVersion.minorVersion}.\${parsedVersion.nextIncrementalVersion}-SNAPSHOT
cd "${ROOT_PROJECT_DIR}" && ./mvnw build-helper:parse-version versions:commit
cd "${ROOT_PROJECT_DIR}" && VERSION=$(mvn help:evaluate -Dexpression=project.version -q -DforceStdout)
cd "${ROOT_PROJECT_DIR}" && git add . ; git commit -am "Start next $VERSION development iteration." ; git push origin master

echo "Released."
