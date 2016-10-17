#!/bin/sh

git stash -q --keep-index
./gradlew test
RESULT=$?
git stash pop -q
exit $RESULT