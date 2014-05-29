package org.kframework.mpfr;

import java.io.Serializable;
import java.math.RoundingMode;

/**
 * Immutable objects which encapsulate the context settings which describe
 * certain rules for numerical operators upon IEEE binary floating point types,
 * such as those implemented by the {@link BigFloat} class.
 * 
 * <p>The base-independent settings are:
 * <ol>
 * <li>{@code precision}:
 * the number of binary digits to be used in floats constructed
 * by an operation; results are rounded to this precision. This also has
 * an effect on the exact size of the subnormal range.
 * 
 * <li>{@code exponent}:
 * the number of bits in the exponent range of floats constructed
 * by an operation; results are rounded to the exponent range from
 * {@code minExponent} to {@code maxExponent}, where {@code maxExponent}
 * equals 2<sup>exponent-1</sup>-1 and {@code minExponent} equals
 * -2<sup>exponent-1</sup>+2.
 * 
 * <li>{@code roundingMode}:
 * A {@link RoundingMode} object which specifies the algorithm to be used for
 * rounding. Note that the rounding modes {@link RoundingMode#HALF_DOWN HALF_DOWN}
 * and {@link RoundingMode#HALF_UP HALF_UP} are not supported by this library,
 * and using them will throw an {@link IllegalArgumentException}.
 * 
 * @see BigFloat
 * @see RoundingMode
 * @author Dwight Guth
 *
 */
public final class BinaryMathContext implements Serializable {
    
    // Serialization version
    private static final long serialVersionUID = -9122817091910408578L;

    public static final int BINARY16_EXPONENT_BITS = 5;
    public static final int BINARY32_EXPONENT_BITS = 8;
    public static final int BINARY64_EXPONENT_BITS = 11;
    public static final int BINARY128_EXPONENT_BITS = 15;
    
    /**
     * A {@code BinaryMathContext} object with precision and exponent settings
     * matching the IEEE 754-2008 Binary16 format, 11 bits of precision and
     * 5 bits of exponent, and a rounding mode of {@link RoundingMode#HALF_EVEN HALF_EVEN},
     * the IEEE 754-2008 default.
     */
    public static final BinaryMathContext BINARY16 = new BinaryMathContext(11, 
            BINARY16_EXPONENT_BITS);
    
    /**
     * A {@code BinaryMathContext} object with precision and exponent settings
     * matching the IEEE 754-2008 Binary32 format, 24 bits of precision and
     * 8 bits of exponent, and a rounding mode of {@link RoundingMode#HALF_EVEN HALF_EVEN},
     * the IEEE 754-2008 default.
     */
    public static final BinaryMathContext BINARY32 = new BinaryMathContext(24, 
            BINARY32_EXPONENT_BITS);
    
    /**
     * A {@code BinaryMathContext} object with precision and exponent settings
     * matching the IEEE 754-2008 Binary64 format, 53 bits of precision and
     * 11 bits of exponent, and a rounding mode of {@link RoundingMode#HALF_EVEN HALF_EVEN},
     * the IEEE 754-2008 default.
     */
    public static final BinaryMathContext BINARY64 = new BinaryMathContext(53, 
            BINARY64_EXPONENT_BITS);
    
    /**
     * A {@code BinaryMathContext} object with precision and exponent settings
     * matching the IEEE 754-2008 Binary128 format, 113 bits of precision and
     * 15 bits of exponent, and a rounding mode of {@link RoundingMode#HALF_EVEN HALF_EVEN},
     * the IEEE 754-2008 default.
     */
    public static final BinaryMathContext BINARY128 = new BinaryMathContext(113, 
            BINARY128_EXPONENT_BITS);
    
    /**
     * The number of digits to be used for an operation. The value 0 is
     * reserved to indicate unlimited precision, however, the implementation
     * does not currently support this. Note that this count includes the single
     * digit which is implicitly placed before the decimal point, which is not 
     * included in the interchange format, as well as the number of leading
     * zeroes in a subnormal number.
     * 
     * <p>{@code precision} will always be non-negative.
     * 
     * @serial
     */
    public final int precision;
    
    /**
     * The minimum normal exponent in the exponent range to be used for an operation.
     * This is equal to -2<sup>exponent-1</sup>+2 where {@code exponent} is the number
     * of bits passed to the constructor. Note that {@link BigFloat#minNormal(int, long)}
     * returns the minimum normal floating point number within this exponent range when
     * passed this value, and {@link BigFloat#minValue(int, long)} returns the minimum
     * subnormal floating point number within this exponent range and precision
     * when passed {@link #precision} and this value.
     * 
     * <p>{@code minExponent} will always be less than {@link #maxExponent}.
     * 
     * @serial
     */
    public final long minExponent;
    
    /**
     * The maximum normal exponent in the exponent range to be used for an operation.
     * This is equal to 2<sup>exponent-1</sup>-1 where {@code exponent} is the number
     * of bits passed to the constructor. Note that {@link BigFloat#maxValue(int, long)}
     * returns the maximum normal floating point number within this exponent range and
     * precision when passed {@link #precision} and this value.
     * 
     * <p>{@code maxExponent} will always be greater than {@link #minExponent}.
     * 
     * @serial
     */
    public final long maxExponent;
    
    /**
     * The rounding algorithm to be used for an operation. Note that the rounding modes
     * {@link RoundingMode#HALF_DOWN HALF_DOWN} and {@link RoundingMode#HALF_UP HALF_UP}
     * are not supported by this library, and using them will throw an
     * {@link IllegalArgumentException}.
     * 
     * @see RoundingMode
     * @serial
     */
    public final RoundingMode roundingMode;
    
    
    /**
     * Constructs a new {@code BinaryMathContext} with the specified
     * precision and exponent and the {@link RoundingMode#HALF_EVEN HALF_EVEN}
     * rounding mode. Sets {@code minExponent} to -2<sup>setExponent-1</sup>+2
     * and {@code maxExponent} to 2<sup>setExponent-1</sup>-1.
     * 
     * @param setPrecision The non-negative {@code int} precision setting.
     * @param setExponent The non-negative {@code int} exponent setting, in
     * bits, to specify the exponent range.
     ** @throws IllegalArgumentException if the {@code setPrecision} parameter
     * is less than zero, or if the {@code setExponent} parameter is less than
     * zero or greater than 63 (because {@code minExponent} and {@code maxExponent}
     * would not fit in a {@code long}).
     */
    public BinaryMathContext(int setPrecision, int setExponent) {
        this(setPrecision, setExponent, RoundingMode.HALF_EVEN);
    }
    
    /**
     * Constructs a new {@code BinaryMathContext} with the specified
     * precision and rounding mode and 30 bits of precision. 30 bits
     * of precision is used because it is the largest exponent range
     * that is guaranteed to be supported by all MPFR implementations
     * for a significant range of possible precisions.
     * 
     * Note that because of the need to emulate subnormal arithmetic,
     * this constructor will produce an object which is outside the
     * usable range of this library if {@code setPrecision} is greater than
     * {@code Integer.MAX_VALUE / 4 + 4}. To perform operations on numbers
     * of larger precision, you must decrease the exponent range accordingly.
     * 
     * @param setPrecision The non-negative {@code int} precision setting.
     * @param setRoundingMode The rounding mode to use.
     * @throws IllegalArgumentException if the {@code setPrecision} parameter
     * is less than zero.
     * @throws NullPointerException if the rounding mode argument is {@code null}.
     */
    public BinaryMathContext(int setPrecision, RoundingMode setRoundingMode) {
        this(setPrecision, 30, setRoundingMode);
    }
    
    /**
     * Constructs a new {@code BinaryMathContext} with the specified
     * precision, exponent, and rounding mode. Sets {@code minExponent}
     * to -2<sup>setExponent-1</sup>+2 and {@code maxExponent} to 
     * 2<sup>setExponent-1</sup>-1.
     * 
     * @param setPrecision The non-negative {@code int} precision setting.
     * @param setExponent The non-negative {@code int} exponent setting, in
     * bits, to specify the exponent range.
     * @param setRoundingMode The rounding mode to use.
     * @throws IllegalArgumentException if the {@code setPrecision} parameter
     * is less than zero, or if the {@code setExponent} parameter is less than
     * zero or greater than 63 (because {@code minExponent} and {@code maxExponent}
     * would not fit in a {@code long}).
     * @throws NullPointerException if the rounding mode argument is {@code null}.
     */
    public BinaryMathContext(int setPrecision, int setExponent, RoundingMode setRoundingMode) {
        this(setPrecision, -(1L << (setExponent - 1)) + 2, (1L << (setExponent - 1)) - 1, setRoundingMode);
        if (setExponent < 0 || setExponent > 63) {
            throw new IllegalArgumentException("Exponent range not expressible as a long");
        }
    }
    
    /**
     * Used internally to construct exponent ranges which are not IEEE 754-2008 compliant. For
     * example, MPFR does not by default support subnormal arithmetic, so it does not reserve
     * the minimum possible exponent for use by subnormal and zero, which means that an MPFR
     * exponent range of 31 bits is not identical to an IEEE exponent range of 31 bits. We
     * use this constructor internally in cases where rounding is not desired, and therefore it
     * is undesirable to modify the internally-used exponent range.
     * 
     * @param setPrecision The non-negative {@code int} precision setting.
     * @param setMinExponent The minimum exponent in the exponent range to set.
     * @param setMaxExponent The maximum exponent in the exponent range to set.
     * @param setRoundingMode The rounding mode to use.
     * @throws IllegalArgumentException if the {@code setPrecision} parameter
     * is less than zero, or if the {@code setMinExponent} parameter is greater than
     * or equal to the {@code setMaxExponent} parameter.
     * @throws NullPointerException if the rounding mode argument is {@code null}.
     */
    BinaryMathContext(int setPrecision, long setMinExponent, long setMaxExponent, RoundingMode setRoundingMode) {
        if (setPrecision < 0) {
            throw new IllegalArgumentException("Precision < 0");
        }
        if (setMinExponent >= setMaxExponent) {
            throw new IllegalArgumentException("minExponent >= maxExponent");
        }
        if (setRoundingMode == null) {
            throw new NullPointerException("null RoundingMode");
        }
        this.precision = setPrecision;
        this.minExponent = setMinExponent;
        this.maxExponent = setMaxExponent;
        this.roundingMode = setRoundingMode;
    }
    
    /**
     * Return a new {@code BinaryMathContext} object which has the same precision
     * and exponent range of this object, but with the specified {@code roundingMode}.
     * @param roundingMode a {@code RoundingMode} object to use as the rounding mode
     * of the returned {@code BinaryMathContext}.
     * @return a {@code BinaryMathContext} object with precision equal to {@link #precision},
     * minExponent equal to {@link #minExponent}, maxExponent equal to {@link #maxExponent},
     * and roundingMode equal to the {@code roundingMode} parameter.
     */
    public BinaryMathContext withRoundingMode(RoundingMode roundingMode) {
        return new BinaryMathContext(precision, minExponent, maxExponent, roundingMode);
    }
    
    /**
     * Compares this {@code BinaryMathContext} with the specified {@code Object}
     * for equality.
     * 
     * @param x {@code Object} to which this {@code MathContext} is to be compared.
     * @return {@code true} if and only if the specified {@code Object} is a 
     * {@code BinaryMathContext} object which has the exact same settings as this
     * object.
     */
    @Override
    public boolean equals(Object x) {
        if (!(x instanceof BinaryMathContext)) return false;
        BinaryMathContext mc = (BinaryMathContext)x;
        return mc.precision == this.precision
                && mc.minExponent == this.minExponent
                && mc.maxExponent == this.maxExponent
                && mc.roundingMode == this.roundingMode;
    }
    
    /**
     * Returns the hash code for this {@code BinaryMathContext}
     * 
     * @return hash code for this {@code BinaryMathContext}.
     */
    public int hashCode() {
        return (int) (this.minExponent * 59 + this.maxExponent * 31 + roundingMode.hashCode() * 17 + precision);
    }
    
    /**
     * Reconstitute the {@code BinaryMathContext} instance from a stream (that is,
     * deserialize it).
     * 
     * @param s the stream being read.
     */
    private void readObject(java.io.ObjectInputStream s)
        throws java.io.IOException, ClassNotFoundException {
        s.defaultReadObject();
        // validate possibly bad fields
        if (precision < 0) {
            throw new java.io.StreamCorruptedException("Precision < 0");
        }
        if (minExponent >= maxExponent) {
            throw new java.io.StreamCorruptedException("minExponent >= maxExponent");
        }
        if (roundingMode == null) {
            throw new java.io.StreamCorruptedException("null RoundingMode");
        }
    }
}
