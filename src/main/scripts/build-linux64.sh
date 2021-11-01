#!/bin/bash

./src/main/scripts/ci-download.sh
./src/main/scripts/ci-build.sh
cp -r ./target/* /output
