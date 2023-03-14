#!/usr/bin/env bash

###
# Happy path release automation script
#
# Usage:
#         GPG_PASSPHRASE=... bash .bin/central-release.sh
#         bash .bin/central-release.sh -Dgpg.passphrase=...
#         GPG_PASSPHRASE=... bash .bin/central-release.sh -DskipTests
#
# Require:
#         brew reinstall gnupg@2.2
#         export GPG_HOME=$HOME/.homebrew/opt/gnupg@2.2
#         export PATH=$GPG_HOME/bin:$PATH
#         gpg --gen-key # specify: Real name, Email address, press Okay, enter passphrase
#         see details here: https://central.sonatype.org/publish/publish-maven/
#         and here: https://central.sonatype.org/publish/requirements/gpg/#listing-keys
#         ```bash
#         cp -Rfv ~/.m2/settings.xml ~/.m2/settings.xml.backup
#         echo '
#         <?xml version="1.0" encoding="UTF-8"?>
#         <settings xmlns="http://maven.apache.org/SETTINGS/1.1.0"
#                   xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
#                   xsi:schemaLocation="http://maven.apache.org/SETTINGS/1.1.0 https://maven.apache.org/xsd/settings-1.1.0.xsd">
#           <servers>
#             <server>
#               <id>ossrh</id>
#               <username>Sonatype username...</username>
#               <password>Sonatype password...</password>
#             </server>
#           </servers>
#         </settings>
#         ' > ~/.m2/settings.xml
###

set -o pipefail
set -e

if [[ -n "$GPG_PASSPHRASE" ]] ; then
  echo "Detected GPG_PASSPHRASE environment variable"
  export GPG_PASSPHRASE
fi

ARGS=${1:-"-Dgpg.passphrase=$GPG_PASSPHRASE"}
ROOT_PROJECT_DIR=$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)

cd "${ROOT_PROJECT_DIR}" && VERSION=$(bash mvnw help:evaluate -Dexpression=project.version -q -DforceStdout)
echo "Deploying $VERSION version..."

if [[ "$VERSION" == *"-SNAPSHOT" ]] ; then
  echo "Snapshot version was detected. Update $VERSION version to released, commit and push changes first..."
  cd "${ROOT_PROJECT_DIR}" && ./mvnw build-helper:parse-version versions:set -DnewVersion=\${parsedVersion.majorVersion}.\${parsedVersion.minorVersion}.\${parsedVersion.incrementalVersion}
  cd "${ROOT_PROJECT_DIR}" && ./mvnw build-helper:parse-version versions:commit initialize
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
