#!/bin/bash

set -x

JOBS=${1:-1}
ROOT_DIR="`pwd`"

VENDOR_DIR="$ROOT_DIR/vendor"
GMP_DIR="$VENDOR_DIR/gmp"
MPFR_DIR="$VENDOR_DIR/mpfr"

if [ ! -d "$GMP_DIR" ]; then
  echo "Error: no GMP source found"
  exit 1
fi

if [ ! -d "$MPFR_DIR" ]; then
  echo "Error: no GMP source found"
  exit 2
fi

BUILD_ARCH=`gcc -dumpmachine`
BUILD_ARCH=${BUILD_ARCH/arm64-apple/aarch64-apple}

if [ -z "$BUILD_ARCH" ]; then
  echo "Error: unable to determine build architecture"
  exit 3
fi

cd "$GMP_DIR"
./configure                                                           \
  --with-pic                                                          \
  --build="$BUILD_ARCH"
make clean
make -j$JOBS

cd "$MPFR_DIR"
./configure                                                           \
  --with-gmp-include="$GMP_DIR"                                       \
  --with-gmp-lib="$GMP_DIR/.libs"                                     \
  --with-pic                                                          \
  --build="$BUILD_ARCH"
make clean
make -j$JOBS

cd "$ROOT_DIR"
mvn install                                                           \
  -Dmpfr.cppflags="-I$MPFR_DIR/src -I$GMP_DIR"                        \
  -Dmpfr.libs="$MPFR_DIR/src/.libs/libmpfr.a $GMP_DIR/.libs/libgmp.a" \
  -Dmpfr.build="$BUILD_ARCH" \
  -Dforce-configure
