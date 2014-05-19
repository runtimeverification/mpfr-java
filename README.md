# MPFR Java Bindings

[GNU MPFR](http://www.mpfr.org/) is a C library for performing mathematical computations on extended precision IEEE 754 binary floating point numbers. Currently, however, MPFR has no Java interface, third-party or otherwise, and all the existing arbitrary precision floating point libraries for Java are not suitable for the task of providing conformance with the IEEE 754-2008 standard for binary floating point numbers. To address this gap, the MPFR Java project exists, with the intent of providing a managed code interface between MPFR and Java analogous to the behavior of Java's existing [BigDecimal](http://docs.oracle.com/javase/7/docs/api/java/math/BigDecimal.html) type, but prioritized to conform to the IEEE standard.

## Getting started

To compile for your platform, download the project and run `mvn install`. The resulting Java code will be placed in the file `target/mpfr_java-1.0.jar`, and the resulting native code will be placed in the file `target/mpfr_java-1.0-<platform>.jar`. For example, for 64 bit linux the native code will be found in `target/mpfr_java-1.0-linux64.jar`. All you need to do in order to use the library is place both of these JARs in the classpath of your project, and then reference the BigFloat class. If you wish to view javadocs for the project, you can run `mvn site`, which will generate the javadoc html in the directory `target/site/apidocs`.

Github releases for Windows, Linux, and Mac OS X will come soon.

## Feedback

If you have issues or questions regarding the project, please create an issue in our issue tracker. Pull requests are also welcome as long as they conform to the design principles of the project.
