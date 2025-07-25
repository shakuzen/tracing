#!/bin/bash -e
# This script deploys the project.

SWITCHES="-s --console=plain -x test"

if [ -z "$GHA_REF_TYPE" ]; then
  echo -e "WARN: Should not be here => Reference Type ['$GHA_REF_TYPE'], Reference Name ['$GHA_REF_NAME'], Full Reference ['$GHA_REF'], Commit ['$GHA_COMMIT']"
  echo -e "Not attempting to publish"
elif [ "$GHA_REF_TYPE" = 'branch' ]; then
  echo -e "Publishing Snapshot => Branch ['$GHA_REF_NAME'], Full Reference ['$GHA_REF'], Commit ['$GHA_COMMIT']"
  ./gradlew -Prelease.stage=SNAPSHOT snapshot publishNebulaPublicationToCommercialSnapshotRepository $SWITCHES
elif [ "$GHA_REF_TYPE" = 'tag' ]; then
  echo -e "Publishing Release => Tag ['$GHA_REF_NAME'], Full Reference ['$GHA_REF'], Commit ['$GHA_COMMIT']"
  case "$GHA_REF_NAME" in
  *-M*)
    echo -e "WARN: Milestone releases are disabled"
    echo -e "Not attempting to publish"
    ;;
  *-RC*)
    echo -e "WARN: RC releases are disabled"
    echo -e "Not attempting to publish"
    ;;
  *)
    ./gradlew -Prelease.disableGitChecks=true -Prelease.useLastTag=true -Prelease.stage=final final publishNebulaPublicationToCommercialReleaseRepository $SWITCHES
    ;;
  esac
else
  echo -e "WARN: Should not be here => Reference Type ['$GHA_REF_TYPE'], Reference Name ['$GHA_REF_NAME'], Full Reference ['$GHA_REF'], Commit ['$GHA_COMMIT']"
  echo -e "Not attempting to publish"
fi

EXIT=$?

exit $EXIT
