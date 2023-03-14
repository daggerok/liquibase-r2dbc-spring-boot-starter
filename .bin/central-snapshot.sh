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
#
#         gpg --gen-key # specify: Real name, Email address, press Okay, enter passphrase
#
#         gpg --list-signatures
#         gpg --keyserver keyserver.ubuntu.com --send-keys 0D6866D45122F4B762BBA078CA756566F2B91BC1 # <- your key
#
#         if it doesn't worked:
#         gpg --armor --output public-key.gpg --export daggerok@gmail.com # <- your email
#         cat public-key.gpg | pbcopy
#         go to https://keyserver.ubuntu.com/
#         click Submit Key
#         paste copied key with pbcopy command and submit it
#         verify: https://keyserver.ubuntu.com/pks/lookup?search=0D6866D45122F4B762BBA078CA756566F2B91BC1&fingerprint=on&op=index
#
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
#
#         see details here: https://central.sonatype.org/publish/publish-maven/
#         and here: https://central.sonatype.org/publish/requirements/gpg/#listing-keys
#         and here: https://www.linode.com/docs/guides/gpg-keys-to-send-encrypted-messages/#generate-a-revocation-certificate
#         and here: https://issues.sonatype.org/browse/OSSRH-71957
#         and here: https://maven.apache.org/repository/guide-central-repository-upload.html
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
