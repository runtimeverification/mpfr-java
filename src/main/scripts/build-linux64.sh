#!/bin/bash

set -x

./src/main/scripts/ci-download.sh
./src/main/scripts/ci-build.sh
cp -r ./target/* /output
chown -R "$1":"$2" /output
