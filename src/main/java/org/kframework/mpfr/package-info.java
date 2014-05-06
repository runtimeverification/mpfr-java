/**
 * Providers classes for performing arbitrary-precision binary floating point
 * arithmetic ({@code BigFloat}). {@code BigFloat} is analogous to the
 * primitive floating point types except that it provides a considerably
 * larger allowable range of precision and exponent values. In addition to
 * the standard arithmetic operations, {@code BigFloat} provides methods
 * borrowed from the functionality of Java's {@link java.lang.Math}, 
 * {@link java.lang.Double}, and {@link java.math.BigDecimal} classes,
 * but adapted for arbitrary precision binary floating point arithmetic.
 * 
 * {@code BinaryMathContext} provides functionality similar to 
 * {@link java.math.MathContext}, except it contains additional fields
 * for the exponent range of a {@code BigFloat}.
 * 
 * @see java.math.RoundingMode
 * @see java.math.MathContext
 * @see java.math.BigDecimal
 * @see java.lang.Math
 * @see java.lang.Double
 */
package org.kframework.mpfr;
