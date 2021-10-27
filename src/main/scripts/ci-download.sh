#!/bin/bash

VENDOR_DIR="`pwd`/vendor"

mkdir -p "$VENDOR_DIR"
cd "$VENDOR_DIR"

curl -OL https://gmplib.org/download/gmp/gmp-6.2.1.tar.xz
mkdir gmp
tar xf gmp-6.2.1.tar.xz -C gmp --strip-components 1
rm gmp-6.2.1.tar.xz

curl -OL https://www.mpfr.org/mpfr-3.1.2/mpfr-3.1.2.tar.xz
mkdir mpfr
tar xf mpfr-3.1.2.tar.xz -C mpfr --strip-components 1
rm mpfr-3.1.2.tar.xz
