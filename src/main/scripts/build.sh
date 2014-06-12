set -e
cd gmp-6.0.0
GMPDIR=`pwd`
./configure  CFLAGS='-mmacosx-version-min=10.6 -arch i386' CPPFLAGS='-mmacosx-version-min=10.6 -arch i386' CXXFLAGS='-mmacosx-version-min=10.6 -arch i386 ' LDFLAGS='-arch i386 -mmacosx-version-min=10.6 ' ABI=32 --prefix=$GMPDIR/x86 --with-pic
make clean
make install
make distclean
./configure  CFLAGS='-mmacosx-version-min=10.6 -arch x86_64' CPPFLAGS='-mmacosx-version-min=10.6 -arch x86_64' CXXFLAGS='-mmacosx-version-min=10.6 -arch x86_64 ' LDFLAGS='-mmacosx-version-min=10.6 -arch x86_64' ABI=64 --prefix=$GMPDIR/x86_64 --with-pic
make install
make distclean
rm -rf universal/lib
mkdir universal/lib
lipo -create x86/lib/libgmp.a -create x86_64/lib/libgmp.a -output universal/lib/libgmp.a
cd ../mpfr-3.1.2
MPFRDIR=`pwd`
./configure --with-gmp-include=$GMPDIR/x86/include --with-gmp-lib=$GMPDIR/x86/lib/ --with-pic CFLAGS='-mmacosx-version-min=10.6 -arch i386' CPPFLAGS='-mmacosx-version-min=10.6 -arch i386' CXXFLAGS='-mmacosx-version-min=10.6 -arch i386 ' LDFLAGS='-arch i386 -mmacosx-version-min=10.6 ' ABI=32 --prefix=$MPFRDIR/x86 --with-pic
make clean
make install
make distclean
./configure --with-gmp-include=$GMPDIR/x86_64/include --with-gmp-lib=$GMPDIR/x86_64/lib CFLAGS='-mmacosx-version-min=10.6 -arch x86_64' CPPFLAGS='-mmacosx-version-min=10.6 -arch x86_64' CXXFLAGS='-mmacosx-version-min=10.6 -arch x86_64 ' LDFLAGS='-mmacosx-version-min=10.6 -arch x86_64' ABI=64 --prefix=$MPFRDIR/x86_64 --with-pic
make install
rm -rf universal/lib
mkdir universal/lib
lipo -create x86/lib/libmpfr.a -create x86_64/lib/libmpfr.a -output universal/lib/libmpfr.a
cd ../mpfr-java
mvn test -Dmpfr.cppflags=-I$GMPDIR/universal/include\ -I$MPFRDIR/universal/include/ -Dmpfr.libs=$MPFRDIR/universal/lib/libmpfr.a\ $GMPDIR/universal/lib/libgmp.a
#mvn hawtjni:package-jar
cd ..
