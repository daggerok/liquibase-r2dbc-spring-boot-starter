#!/usr/bin/env bash

###
# bash .bin/local-rollback.sh
###

ARGS=${1:-}
ROOT_PROJECT_DIR=$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)

cd "${ROOT_PROJECT_DIR}" && ./mvnw -P local-release release:rollback

cd "${ROOT_PROJECT_DIR}" && git fetch --all -p -a --tags

## FInally go to repo releases and remove last created tag, then:
#git tag -l
#git tag -d $latestTag
#git fetch --all -p -a --tags
