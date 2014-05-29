// Copyright (c) 2014 K Team. All Rights Reserved.
package org.kframework.mpfr;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;

import org.kframework.mpfr.mpfr.mpfr_t;

import static org.kframework.mpfr.mpfr.*;

/**
 * Immutable, arbitrary-precision signed IEEE binary floating point numbers.
 * A {@code BigFloat} consists of an arbitrary precision <i>significand</i> of
 * up to 2<sup>31</sup>-1 bits, a <i>sign</i>, and a 64-bit integer <i>exponent</i>.
 * The significand is represented internally as a sequence of bytes, however,
 * it is accessible to the user as a scaled integer corresponding to the value
 * of the mantissa left shifted by the number of bits of precision minus one.
 * Thus the mantissa can be computed by the calculation
 * <tt>significand &div; 2<sup>precision-1</sup></tt>.
 * As expected for a number in base 2 scientific notation, the value of the
 * number represented by the {@code BigFloat} is therefore 
 * <tt>signum() &times; 2<sup>exponent-(precision-1)</sup> &times; significand</tt>.
 * 
 * <p>The {@code BigFloat} class provides operations for arithmetic, rounding,
 * comparison, hashing, conversion, and trigonometric and transcendental
 * computation. The {@link #toString} method provides a canonical representation
 * of a {@code BigFloat} which can be used to reconstruct an equal {@code BigFloat}
 * if the same precision is used.
 * 
 * <p>The {@code BigFloat} class gives its user complete control over rounding
 * behavior. If the user specifies that no rounding should be performed, and
 * the exact result cannot be represented, an exception is thrown;
 * otherwise, calculations can be carried out specifying the
 * precision of the resulting {@code BigFloat}, as well as the exponent range
 * and rounding mode to be used for rounding, by supplying an appropriate
 * {@link BinaryMathContext} object to the operation. In either case, six
 * <em>rounding modes</em> are provided for the control of rounding
 * (The {@code BigFloat} class does not support the Java rounding modes
 * {@link RoundingMode#HALF_UP HALF_UP} and {@link RoundingMode#HALF_DOWN HALF_DOWN}).
 * 
 * <p>When using one of the constant {@link BinaryMathContext} objects like
 * {@link BinaryMathContext#BINARY64 BINARY64}, the rules of {@link BigFloat}
 * arithmetic are broadly compatible with the IEEE 754-2008 standard for the
 * corresponding binary floating point format. Effort has also been made to make
 * the class compatible in design to the {@link BigDecimal}, {@link Double}, and 
 * {@link Math} classes which share these arithmetic operations and values.
 * Any conflicts between the IEEE 754-2008 standard and the corresponding
 * Java specifications are resolved in favor of the IEEE 754-2008 standard.
 * 
 * <p>Since the same numerical value can have different representations (with
 * different precisions and exponent ranges), the {@link BinaryMathContext}
 * parameter is used to specify the precision and exponent range of the resulting
 * {@code BigFloat} of an operation. Some unary operators also allow the
 * {@link BinaryMathContext} object to be omitted, in which case the resulting
 * object specifies the exact (i.e. not rounded) numerical result of the operation
 * with the same precision as the original {@code BigFloat}.
 * 
 * <p>In general the rounding mode, precision, and exponent range settings
 * determine how operations return results with a limited number of digits
 * when the exact result has more digits (perhaps infinitely many in the
 * case of irrational numbers or repeating fractions) than the number of
 * digits returned.
 * 
 * First, the total number of binary digits to return is specified by the
 * {@code BinaryMathContext}'s {@code precision} setting; this determines the
 * result's <i>precision</i>. The digit count starts from the leftmost nonzero
 * digit of the exact result.  Next, the rounding mode determines how any 
 * discarded trailing digits affect the returned result. Finally, the 
 * <i>exponent range</i> setting determines whether underflow or overflow occurs.
 * In the case of overflow, the result is rounded to the corresponding infinity;
 * in the case of underflow, the result is rounded according to the standards
 * of subnormal arithmetic specified in the IEEE standard, with results too
 * small to be represented by a subnormal number rounded to the corresponding
 * signed zero.
 * 
 * <p>For all arithmetic operators, the operation is carried out as though an
 * exact intermediate result were first calculated and then rounded to the
 * specified precision and exponent range (if necessary), using the selected
 * rounding miode. If the exact result is not returned, some digit positions
 * of the exact result are discarded. It is possible for rounding to change
 * the number of digits in the significand, in which case the result is
 * automatically scaled to the appropriate new exponent value.
 * 
 * <p>One operation in particular is provided for manipulating the precision
 * and exponent range of a {@code BigFloat}: the rounding operation
 * {@link #round(BinaryMathContext) round}. {@link #round(BinaryMathContext) round}
 * returns a {@code BigFloat} whose value is approximately (or exactly) equal
 * to that of the operand, but which has been rounded according to the specified
 * rounding mode to a value representable using the specified precision and
 * exponent range.
 * 
 * <p>For the sake of brevity and clarity, pseudo-code is used throughout
 * the descriptions of {@code BigFloat} methods. The pseudo-code expression
 * {@code (i + j)} is shorthand for "a {@code BigFloat} whose value is that
 * of the {@code BigFloat} {@code i} added to that of the {@code BigFloat}
 * {@code j} with the specified {@code BinaryMathContext}." The pseudo-code
 * expression {@code (i == j)} is shorthand for "{@code true} if and only
 * if the {@code BigFloat} {@code i} tests true on IEEE equality against
 * the {@code BigFloat} {@code j}." Other pseudo-code expressions are
 * interpreted similarly. Square brackets are used to represent the particular
 * floating point value and precision defining a {@code BigFloat} value;
 * for example, [0.0, 24] is the {@code BigFloat} numerically equal to 0.0 
 * with a precision of 24.
 * 
 * <p>All methods and constructors for this class throw
 * {@code NullPointerException} when passed a {@code null} object
 * reference for any input parameter.
 * 
 * @see BigInteger
 * @see BigDecimal
 * @see BinaryMathContext
 * @see Double
 * @see Math
 * @see RoundingMode
 * @see java.util.SortedMap
 * @see java.util.SortedSet
 * @author Dwight Guth
 */
public class BigFloat extends Number implements Comparable<BigFloat> {

    // Serialization version
    private static final long serialVersionUID = 1051598651735015348L;
    /**
     * The internal MPFR representation of this {@code BigFloat}.
     */
    private final mpfr_t op;


    /**
     * The largest positive finite value of a particular precision and
     * exponent range. This is equal to
     * (2-2<sup>-(precision-1)</sup>)&middot;2<sup>maxExponent</sup>.
     */
    public static BigFloat maxValue(int precision, long maxExponent) {
        return positiveInfinity(precision).nextDown(-1, maxExponent);
    }
    
    /**
     * The smallest positive normal value of a particular precision and
     * exponent range. This is equal to 2<sup>minExponent</sup>.
     */
    public static BigFloat minNormal(int precision, long minExponent) {
        return zero(precision).nextUp(minExponent + precision - 1, 1);
    }
    
    /**
     * The smallest positive nonzero value of a particular precision
     * and exponent range. This is equal to 
     * 2<sup>minExponent-(precision-1)</sup>.
     */
    public static BigFloat minValue(int precision, long minExponent) {
        return zero(precision).nextUp(minExponent, 1);
    }
    
    /**
     * The value 0.0, with the specified precision.
     */
    public static BigFloat zero(int precision) {
        mpfr_t op = new mpfr_t(precision);
        mpfr_set_zero(op, 1);
        return new BigFloat(op);
    }
    
    /**
     * The value -0.0, with the specified precision.
     */
    public static BigFloat negativeZero(int precision) {
        mpfr_t op = new mpfr_t(precision);
        mpfr_set_zero(op, -1);
        return new BigFloat(op);
    }
    
    /**
     * The value NaN, with the specified precision.
     */
    public static BigFloat NaN(int precision) {
        // init2 by default initializes to NaN
        return new BigFloat(new mpfr_t(precision));
    }
    
    /**
     * The value +Infinity, with the specified precision.
     */
    public static BigFloat positiveInfinity(int precision) {
        mpfr_t op = new mpfr_t(precision);
        mpfr_set_inf(op, 1);
        return new BigFloat(op);
    }
    
    /**
     * The value -Infinity, with the specified precision.
     */
    public static BigFloat negativeInfinity(int precision) {
        mpfr_t op = new mpfr_t(precision);
        mpfr_set_inf(op, -1);
        return new BigFloat(op);
    }
    
    /**
     * The value Pi, with rounding according to the context settings.
     */
    public static BigFloat pi(BinaryMathContext mc) {
        return new Operation() {
            
            @Override
            public int doIt(mpfr_t rop, int rnd) {
                return mpfr_const_pi(rop, rnd);
            }
        }.execute(mc);
    }

    /**
     * The value Pi, with rounding according to the context settings.
     */
    public static BigFloat e(BinaryMathContext mc) {
        return new BigFloat(1, mc).exp(mc);
    }
    
    /**
     * Translates a C character array representation of a {@code BigFloat} into a
     * {@code BigFloat}, accepting the same sequence of characters as the 
     * {@link #BigFloat(String, BinaryMathContext)} constructor and with rounding according
     * to the context settings.
     * 
     * Note that if the sequence of characters is already available as a byte array,
     * using this constructor is faster than converting the {@code byte} array to
     * string and using the {@link #BigFloat(String, BinaryMathContext)} constructor.
     * @param in {@code byte} array that is the source of characters.
     * @param mc the context to use.
     * @throws ArithmeticException if the result is inexact but the rounding mode is
     * {@code UNNECESSARY}.
     * @throws IllegalArgumentException if the specified rounding mode is not supported
     * (i.e. HALF_UP or HALF_DOWN), or if the precision is less than 2
     * @throws NumberFormatException if {@code in} is not a valid representation of a
     * {@code BigFloat}.
     */
    public BigFloat(byte[] in, BinaryMathContext mc) {
        op = new mpfr_t(mc.precision);
        int ternary = mpfr_set_str(op, in, 0, convertRoundingMode(mc.roundingMode));
        boolean rounded = roundExponent(ternary, op, mc);
        throwArithmeticException(rounded, mc);
    }
    
    /**
     * Translates the string representation of a {@code BigFloat} into a {@code BigFloat}.
     * The string representation has a one-to-one mapping with the input to the MPFR
     * function {@code mpfr_set_str} when instructed to auto-detect the base, and the
     * result of this constructor is precisely the result of that function when called
     * on an {@code mpfr_t} initialized with the specified precision and set with
     * the specified rounding mode.
     * 
     * @param in String representation of {@code BigFloat}.
     * @param mc the context to use.
     * @throws ArithmeticException if the result is inexact but the rounding mode is
     * {@code UNNECESSARY}.
     * @throws IllegalArgumentException if the specified rounding mode is not supported
     * (i.e. HALF_UP or HALF_DOWN), or if the precision is less than 2
     * @throws NumberFormatException if {@code val} is not a valid representation of a
     * {@code BigFloat}.
     */
    public BigFloat(String in, BinaryMathContext mc) {
        op = new mpfr_t(mc.precision);
        int ternary = mpfr_set_str(op, in, 0, convertRoundingMode(mc.roundingMode));
        boolean rounded = roundExponent(ternary, op, mc);
        throwArithmeticException(rounded, mc);
    }
    
    /**
     * Translates a {@code double} into a {@code BigFloat}, with rounding according to
     * the context settings. This method does not cause any loss of precision or magnitude
     * unless {@code mc.getPrecision()} is less than 53.
     * @param val {@code double} value to be converted to {@code BigFloat}.
     * @param mc the context to use.
     * @throws ArithmeticException if the result is inexact but the rounding mode is
     * {@code UNNECESSARY}.
     * @throws IllegalArgumentException if the specified rounding mode is not supported
     * (i.e. HALF_UP or HALF_DOWN), or if the precision is less than 2
     */
    public BigFloat(double val, BinaryMathContext mc) {
        op = new mpfr_t(mc.precision);
        int ternary = mpfr_set_d(op, val, convertRoundingMode(mc.roundingMode));
        boolean rounded = roundExponent(ternary, op, mc);
        throwArithmeticException(rounded, mc);
    }
    
    /**
     * Translates a {@code BigInteger} into a {@code BigFloat}, with rounding
     * according to the context settings. This method does not cause any loss of
     * precision or magnitude if {@code mc.getPrecision()} is high enough to
     * represent the integer input exactly.
     * @param val {@code BigInteger} value to be converted to {@code BigFloat}.
     * @param mc the context to use.
     * @throws ArithmeticException if the result is inexact but the rounding mode is
     * {@code UNNECESSARY}.
     * @throws IllegalArgumentException if the specified rounding mode is not supported
     * (i.e. HALF_UP or HALF_DOWN), or if the precision is less than 2
     */
    public BigFloat(BigInteger val, BinaryMathContext mc) {
        op = new mpfr_t(mc.precision);
        int ternary = mpfr_set_z(op, new mpz_t(val), convertRoundingMode(mc.roundingMode));
        boolean rounded = roundExponent(ternary, op, mc);
        throwArithmeticException(rounded, mc);
    }
    
    /**
     * Translates a {@code long} into a {@code BigFloat}, with rounding
     * according to the context settings. This method does not cause any loss of
     * precision or magnitude if {@code mc.getPrecision()} is high enough to
     * represent the integer input exactly.
     * @param val {@code long} value to be converted to {@code BigFloat}.
     * @param mc the context to use.
     * @throws ArithmeticException if the result is inexact but the rounding mode is
     * {@code UNNECESSARY}.
     * @throws IllegalArgumentException if the specified rounding mode is not supported
     * (i.e. HALF_UP or HALF_DOWN), or if the precision is less than 2
     */
    public BigFloat(long val, BinaryMathContext mc) {
        // mpfr doesn't have a mpfr_set_ll (for long long), so we need to convert to BigInteger first.
        this(BigInteger.valueOf(val), mc);
    }
    
    /**
     * Translates a {@code boolean} sign, a {@code BigInteger} significand
     * and a {@code long} exponent into a {@code BigFloat}, with rounding
     * according to the context settings. The value of the {@code BigFloat} is 
     * <tt>significand &times; 2<sup>exponent-(precision-1)</sup></tt>,
     * rounded according to the specified precision, exponent range, and
     * rounding mode, and with the specified value of the sign bit.
     * 
     * Special cases:
     * <ul><li>If {@code exponent} is equal to {@code mc.maxExponent + 1} and
     * {@code significand} is nonzero, then the result is a NaN with the sign
     * bit equal to {@code sign}.
     * <li>If {@code exponent} is equal to {@code mc.maxExponent + 1} and
     * {@code significand} is zero, then the result is positive or negative
     * infinity, depending on whether {@code sign} is {@code false} or
     * {@code true}, respectively.
     * <li>If {@code exponent} is equal to {@code mc.minExponent - 1}, 
     * this function behaves as if {@code exponent} is equal to {@code mc.minExponent},
     * for consistency with IEEE standards as regards positive and negative zero,
     * and subnormal numbers.</ul>
     * 
     * @param sign {@code true} if the result should be negative; false
     * otherwise.
     * @param significand The {@code BigInteger} significand of the {@code BigFloat}.
     * @param exponent The integer exponent of the {@code BigFloat}.
     * @param mc the context to use.
     * @throws ArithmeticException if the result is inexact but the rounding mode is
     * {@code UNNECESSARY}.
     * @throws IllegalArgumentException if the specified rounding mode is not supported
     * (i.e. HALF_UP or HALF_DOWN), if the precision is less than 2, if the specified
     * exponent is not in the specified exponent range, or if the significand is not
     * between 0 and 2<sup>precision</sup>-1.
     */
    public BigFloat(boolean sign, BigInteger significand, long exponent, BinaryMathContext mc) {
        if (exponent < mc.minExponent - 1 || exponent > mc.maxExponent + 1) {
            throw new IllegalArgumentException("exponent not in exponent range");
        }
        if (significand.compareTo(BigInteger.ZERO) < 0 || significand.compareTo(
                BigInteger.ONE.shiftLeft(mc.precision).subtract(BigInteger.ONE)) > 0) {
            throw new IllegalArgumentException("significand not in precision range");
        }
        if (exponent == mc.minExponent - 1) {
            //zeroes and subnormals don't need a special case
            exponent = mc.minExponent;
        }
        if (exponent == mc.maxExponent + 1) {
            if (significand.compareTo(BigInteger.ZERO) != 0) {
                op = new mpfr_t(mc.precision);
                // init2 by default initializes to NaN
            } else {
                op = new mpfr_t(mc.precision);
                mpfr_set_inf(op, (sign ? -1 : 1));
            }
        } else {
            op = new mpfr_t(mc.precision);
            int ternary = mpfr_set_z_2exp(op, new mpz_t(significand), exponent-(mc.precision-1), convertRoundingMode(mc.roundingMode));
            boolean rounded = roundExponent(ternary, op, mc);
            throwArithmeticException(rounded, mc);
        }
        boolean rounded = mpfr_setsign(op, op, sign, convertRoundingMode(mc.roundingMode));
        assert !rounded;
    }
    
    private BigFloat(mpfr_t op) {
        this.op = op;
    }
    
    /**
     * Converts this {@code BigFloat} to a {@code byte}. This conversion is
     * analogous to the <a
     * href="http://docs.oracle.com/javase/specs/jls/se7/html/jls-5.html#jls-5.1.3"><i>narrowing
     * primitive conversion</i></a> from {@code double} to {@code short} as
     * defined in the <a
     * href="http://docs.oracle.com/javase/specs/jls/se7/html/index.html">Java
     * Language Specification</a>: any fractional part of this
     * {@code BigFloat} will be discarded, and if the resulting
     * "{@code BigInteger}" is too big to fit in a {@code byte}, only the
     * low-order 8 bits are returned. Note that this conversion can lose
     * information about the overall magnitude and precision of this
     * {@code BigFloat} value as well as return a result with the opposite sign.
     * 
     * Per the section on narrowing primitive conversions, if this
     * {@code BigFloat} is either {@link BigFloat#positiveInfinity(int)} or
     * {@link BigFloat#negativeInfinity(int)}, this method returns
     * {@link Byte#MAX_VALUE} or {@link Byte#MIN_VALUE} respectively.
     * 
     * @return this {@code BigFloat} converted to a {@code byte}.
     */
    @Override
    public byte byteValue() {
        if (mpfr_inf_p(op)) {
            if (mpfr_signbit(op)) {
                //negative
                return Byte.MIN_VALUE;
            } else {
                //positive
                return Byte.MAX_VALUE;
            }
            
        }
        return toBigInteger().byteValue();
    }
    
    private static final BigFloat byteMaxValue = new BigFloat(Byte.MAX_VALUE, BinaryMathContext.BINARY64);
    private static final BigFloat byteMinValue = new BigFloat(Byte.MIN_VALUE, BinaryMathContext.BINARY64);
    
    /**
     * Converts this {@code BigFloat} to a {@code byte}, checking for lost
     * information. If this {@code BigFloat} has a nonzero fractional part,
     * is NaN or infinite, or is out of the possible range for a {@code byte}
     * result, then an {@code ArithmeticException} is thrown.
     * 
     * @return this {@code BigDecimal} converted to a {@code byte}.
     * @throws ArithmeticException if {@code this} has a nonzero fractional
     * part, is NaN or infinite, or will not fit in a {@code byte}.
     */
    public byte byteValueExact() {
        if (mpfr_nan_p(op) || mpfr_inf_p(op) || !mpfr_integer_p(op)
                || mpfr_greater_p(op, byteMaxValue.op)
                || mpfr_less_p(op, byteMinValue.op)) {
            throw new ArithmeticException("Rounding necessary");
        }
        return byteValue();
    }
    
    /** 
     * Converts this {@code BigFloat} to a {@code double}.
     * This conversion is similar to the <a
     * href="http://docs.oracle.com/javase/specs/jls/se7/html/jls-5.html#jls-5.1.3"><i>narrowing
     * primitive conversion</i></a> from {@code double} to {@code float} as
     * defined in the <a
     * href="http://docs.oracle.com/javase/specs/jls/se7/html/index.html">Java
     * Language Specification</a>: if this {@code BigFloat} has too great a
     * magnitude to represent as a {@code double}, it will be converted to 
     * {@link Double#NEGATIVE_INFINITY} or {@link Double#POSITIVE_INFINITY} as
     * appropriate. Note that even when the return value is finite, this
     * conversion can lose information about the precision of the
     * {@code BigFloat} value. However, no precision is lost if the precision
     * of this {@code BigFloat} is less than or equal to the precision of a 
     * {@code double}.
     * 
     * @return this {@code BigFloat} converted to a {@code double}.
     */
    @Override
    public double doubleValue() {
        return mpfr_get_d(op, MPFR_RNDN);
    }
    
    /**
     * Converts this {@code BigFloat} to a {@code double}, checking for lost
     * information. If this {@code BigFloat} is not expressible in the
     * precision and exponent range for a {@code double}
     * result, then an {@code ArithmeticException} is thrown.
     * 
     * @return this {@code BigDecimal} converted to a {@code double}.
     * @throws ArithmeticException if {@code this} is not expressible in the
     * precision and exponent range for a {@code double}.
     */
    public double doubleValueExact() {
        double d = doubleValue();
        if (mpfr_cmp_d(op, d) != 0) {
            throw new ArithmeticException("Rounding necessary");
        }
        return d;
    }

    /** 
     * Converts this {@code BigFloat} to a {@code float}.
     * This conversion is similar to the <i>narrowing primitive conversion</i> from 
     * {@code double} to {@code float} as defined in section 5.1.3 of <i>The Java™
     * Language Specification</i>: if this {@code BigFloat} has too great a magnitude
     * to represent as a {@code float}, it will be converted to 
     * {@link Float#NEGATIVE_INFINITY} or {@link Float#POSITIVE_INFINITY} as appropriate.
     * Note that even when the return value is finite, this conversion can lose information
     * about the precision of the {@code BigFloat} value. However, no precision is lost if the
     * precision of this {@code BigFloat} is less than or equal to the precision of a 
     * {@code float}.
     * @return this {@code BigFloat} converted to a {@code float}.
     */
    @Override
    public float floatValue() {
        return mpfr_get_flt(op, MPFR_RNDN);
    }
    
    /**
     * Converts this {@code BigFloat} to a {@code float}, checking for lost
     * information. If this {@code BigFloat} is not expressible in the
     * precision and exponent range for a {@code float}
     * result, then an {@code ArithmeticException} is thrown.
     * 
     * @return this {@code BigDecimal} converted to a {@code float}.
     * @throws ArithmeticException if {@code this} is not expressible in the
     * precision and exponent range for a {@code float}.
     */
    public float floatValueExact() {
        float f = floatValue();
        if (mpfr_cmp_d(op, f) != 0) {
            throw new ArithmeticException("Rounding necessary");
        }
        return f;
    }

    /**
     * Converts this {@code BigFloat} to an {@code int}. This conversion is
     * analogous to a <a
     * href="http://docs.oracle.com/javase/specs/jls/se7/html/jls-5.html#jls-5.1.3"><i>narrowing
     * primitive conversion</i></a> from {@code double} to {@code short} as
     * defined in the <a
     * href="http://docs.oracle.com/javase/specs/jls/se7/html/index.html">Java
     * Language Specification</a>: any fractional part of this
     * {@code BigFloat} will be discarded, and if the resulting
     * "{@code BigInteger}" is too big to fit in an {@code int}, only the
     * low-order 32 bits are returned. Note that this conversion can lose
     * information about the overall magnitude and precision of this
     * {@code BigFloat} value as well as return a result with the opposite sign.
     * 
     * Per the section on narrowing primitive conversions, if this
     * {@code BigFloat} is either {@link BigFloat#positiveInfinity(int)} or
     * {@link BigFloat#negativeInfinity(int)}, this method returns
     * {@link Integer#MAX_VALUE} or {@link Integer#MIN_VALUE} respectively.
     * 
     * @return this {@code BigFloat} converted to an {@code int}.
     */
    @Override
    public int intValue() {
        if (mpfr_inf_p(op)) {
            if (mpfr_signbit(op)) {
                //negative
                return Integer.MIN_VALUE;
            } else {
                //positive
                return Integer.MAX_VALUE;
            }
            
        }
        return toBigInteger().intValue();
    }
    
    private static final BigFloat intMaxValue = new BigFloat(Integer.MAX_VALUE, BinaryMathContext.BINARY64);
    private static final BigFloat intMinValue = new BigFloat(Integer.MIN_VALUE, BinaryMathContext.BINARY64);
    
    /**
     * Converts this {@code BigFloat} to an {@code int}, checking for lost
     * information. If this {@code BigFloat} has a nonzero fractional part,
     * is NaN or infinite, or is out of the possible range for an {@code int}
     * result, then an {@code ArithmeticException} is thrown.
     * 
     * @return this {@code BigDecimal} converted to an {@code int}.
     * @throws ArithmeticException if {@code this} has a nonzero fractional
     * part, is NaN or infinite, or will not fit in an {@code int}.
     */
    public int intValueExact() {
        if (mpfr_nan_p(op) || mpfr_inf_p(op) || !mpfr_integer_p(op)
                || mpfr_greater_p(op, intMaxValue.op)
                || mpfr_less_p(op, intMinValue.op)) {
            throw new ArithmeticException("Rounding necessary");
        }
        return intValue();
    }
    
    /**
     * Converts this {@code BigFloat} to a {@code BigInteger}. 
     * This conversion is analogous to a <a
     * href="http://docs.oracle.com/javase/specs/jls/se7/html/jls-5.html#jls-5.1.3"><i>narrowing
     * primitive conversion</i></a> from {@code double} to {@code long} as
     * defined in the <a
     * href="http://docs.oracle.com/javase/specs/jls/se7/html/index.html">Java
     * Language Specification</a>: any fractional part of this {@code BigFloat} will be
     * discarded. Note that this conversion can lose information about the precision of 
     * the {@code BigFloat} value.
     * 
     * One difference between this method and the narrowing primitive conversion is
     * that {@code long} has a maximum value and {@code BigInteger} does not. Therefore,
     * in keeping with the convention that casting NaN from a {@code double} to a {@code long}
     * results in the number zero, this method returns zero if this {@code BigFloat} is
     * infinite. A side effect of this fact is that {@code toBigInteger().intValue()} and
     * {@code intValue()} are not identical on {@code BigFloat}s which do not fit
     * in the range of an {@code int}, and the same is true for {@code long}, {@code short},
     * and {@code byte} conversions: the former in each case will return zero, whereas the
     * latter will return the maximum or minimum value for the narrower type.
     * 
     * To have an exception thrown if the conversion is inexact (in other words if a
     * nonzero fractional part is discarded, or this {@code BigFloat} is NaN or infinite),
     * use the {@link #toBigIntegerExact()} method.
     * 
     * @return this {@code BigFloat} converted to a {@code BigInteger}.
     */
    public BigInteger toBigInteger() {
        if (mpfr_nan_p(op) || mpfr_inf_p(op)) {
            return BigInteger.ZERO;
        }
        return toBigIntegerInternal();
    }
    
    /**
     * Converts this {@code BigFloat} to a {@code BigInteger}, checking for
     * lost information. An exception is thrown if this {@code BigFloat}
     * has a nonzero fractional part, or is NaN or infinite.
     * 
     * @return this {@code BigFloat} converted to a {@code BigInteger}.
     * @throws ArithmeticException if {@code this} has a nonzero
     * fractional part.
     */
    public BigInteger toBigIntegerExact() {
        if (mpfr_nan_p(op) || mpfr_inf_p(op) || !mpfr_integer_p(op)) {
            throw new ArithmeticException("Rounding necessary");
        }
        return toBigIntegerInternal();
    }

    private BigInteger toBigIntegerInternal() {
        mpz_t rop = new mpz_t();
        mpfr_get_z(rop, op, MPFR_RNDZ);
        return new BigInteger(mpz_get_str(10, rop));
    }
    
    /**
     * Converts this {@code BigFloat} to a {@code long}. This conversion is
     * analogous to a <a
     * href="http://docs.oracle.com/javase/specs/jls/se7/html/jls-5.html#jls-5.1.3"><i>narrowing
     * primitive conversion</i></a> from {@code double} to {@code short} as
     * defined in the <a
     * href="http://docs.oracle.com/javase/specs/jls/se7/html/index.html">Java
     * Language Specification</a>: any fractional part of this
     * {@code BigFloat} will be discarded, and if the resulting
     * "{@code BigInteger}" is too big to fit in a {@code byte}, only the
     * low-order 64 bits are returned. Note that this conversion can lose
     * information about the overall magnitude and precision of this
     * {@code BigFloat} value as well as return a result with the opposite sign.
     * 
     * Per the section on narrowing primitive conversions, if this
     * {@code BigFloat} is either {@link BigFloat#positiveInfinity(int)} or
     * {@link BigFloat#negativeInfinity(int)}, this method returns
     * {@link Long#MAX_VALUE} or {@link Long#MIN_VALUE} respectively.
     * 
     * @return this {@code BigFloat} converted to a {@code long}.
     */
    @Override
    public long longValue() {
        if (mpfr_inf_p(op)) {
            if (mpfr_signbit(op)) {
                //negative
                return Long.MIN_VALUE;
            } else {
                //positive
                return Long.MAX_VALUE;
            }
            
        }
        return toBigInteger().longValue();
    }

    private static final BigFloat longMaxValue = new BigFloat(Long.MAX_VALUE, BinaryMathContext.BINARY128);
    private static final BigFloat longMinValue = new BigFloat(Long.MIN_VALUE, BinaryMathContext.BINARY128);
    
    /**
     * Converts this {@code BigFloat} to a {@code long}, checking for lost
     * information. If this {@code BigFloat} has a nonzero fractional part,
     * is NaN or infinite, or is out of the possible range for a {@code long}
     * result, then an {@code ArithmeticException} is thrown.
     * 
     * @return this {@code BigDecimal} converted to a {@code long}.
     * @throws ArithmeticException if {@code this} has a nonzero fractional
     * part, is NaN or infinite, or will not fit in a {@code long}.
     */
    public long longValueExact() {
        if (mpfr_nan_p(op) || mpfr_inf_p(op) || !mpfr_integer_p(op)
                || mpfr_greater_p(op, longMaxValue.op)
                || mpfr_less_p(op, longMinValue.op)) {
            throw new ArithmeticException("Rounding necessary");
        }
        return longValue();
    }
    
    /**
     * Converts this {@code BigFloat} to a {@code short}. This conversion is
     * analogous to a <a
     * href="http://docs.oracle.com/javase/specs/jls/se7/html/jls-5.html#jls-5.1.3"><i>narrowing
     * primitive conversion</i></a> from {@code double} to {@code short} as
     * defined in the <a
     * href="http://docs.oracle.com/javase/specs/jls/se7/html/index.html">Java
     * Language Specification</a>: any fractional part of this
     * {@code BigFloat} will be discarded, and if the resulting
     * "{@code BigInteger}" is too big to fit in a {@code short}, only the
     * low-order 16 bits are returned. Note that this conversion can lose
     * information about the overall magnitude and precision of this
     * {@code BigFloat} value as well as return a result with the opposite sign.
     * 
     * Per the section on narrowing primitive conversions, if this
     * {@code BigFloat} is either {@link BigFloat#positiveInfinity(int)} or
     * {@link BigFloat#negativeInfinity(int)}, this method returns
     * {@link Short#MAX_VALUE} or {@link Short#MIN_VALUE} respectively.
     * 
     * @return this {@code BigFloat} converted to a {@code short}.
     */
    @Override
    public short shortValue() {
        if (mpfr_inf_p(op)) {
            if (mpfr_signbit(op)) {
                //negative
                return Short.MIN_VALUE;
            } else {
                //positive
                return Short.MAX_VALUE;
            }
            
        }
        return toBigInteger().shortValue();
    }

    private static final BigFloat shortMaxValue = new BigFloat(Short.MAX_VALUE, BinaryMathContext.BINARY64);
    private static final BigFloat shortMinValue = new BigFloat(Short.MIN_VALUE, BinaryMathContext.BINARY64);
    
    /**
     * Converts this {@code BigFloat} to a {@code short}, checking for lost
     * information. If this {@code BigFloat} has a nonzero fractional part,
     * is NaN or infinite, or is out of the possible range for a {@code short}
     * result, then an {@code ArithmeticException} is thrown.
     * 
     * @return this {@code BigDecimal} converted to a {@code short}.
     * @throws ArithmeticException if {@code this} has a nonzero fractional
     * part, is NaN or infinite, or will not fit in a {@code short}.
     */
    public short shortValueExact() {
        if (mpfr_nan_p(op) || mpfr_inf_p(op) || !mpfr_integer_p(op)
                || mpfr_greater_p(op, shortMaxValue.op)
                || mpfr_less_p(op, shortMinValue.op)) {
            throw new ArithmeticException("Rounding necessary");
        }
        return shortValue();
    }
    
    /**
     * Returns the <i>precision</i> of this {@code BigFloat}. (The precision
     * is the number of bits in the significand.)
     * 
     * @return the precision of this {@code BigFloat}.
     */
    public int precision() {
        return op._mpfr_prec;
    }
    
    /**
     * Returns the <i>sign bit</i> of this {@code BigFloat}. (the sign bit
     * is true if the number is negative, negative zero, or a NaN with the
     * sign bit set, and false if the the number is positive, positive zero,
     * or a NaN bit without the sign bit set.
     * 
     * Note that NaNs with and without the sign bit set both test true against
     * each other according to {@link #equals(Object)}. Of course, 
     * {@code NaN == NaN} is always false.
     * @return The sign bit of this floating point number.
     */
    public boolean sign() {
        return mpfr_signbit(op);
    }
    
    /**
     * Returns the <i>exponent</i> of this {@code BigFloat}. (The exponent
     * is the power of two to which the significand is multiplied.) Special cases:
     * <ul><li>The exponent of NaN, +Infinity, and -Infinity is
     * {@code maxExponent + 1}.
     * <li>The exponent of +0 and -0 is {@code minExponent - 1}.
     * <li>The exponent of subnormal numbers is {@code minExponent - 1}.</ul>
     * 
     * @param minExponent the minimum exponent in the exponent range
     * @param maxExponent the maximum exponent in the exponent range
     * @return the exponent of this {@code BigFloat}.
     * @throws ArithmeticException if the number cannot be represented in the specified
     * exponent range.
     */
    public long exponent(long minExponent, long maxExponent) {
        if (isNaN() || isInfinite()) {
            return maxExponent + 1;
        }
        if (isPositiveZero() || isNegativeZero() || isSubnormal(minExponent)) {
            return minExponent - 1;
        }
        if (op._mpfr_exp - 1 > maxExponent || op._mpfr_exp - 1 < minExponent) {
            throw new ArithmeticException("exponent is not in the specified exponent range");
        }
        return op._mpfr_exp - 1;
    }
    
    /**
     * Return a {@code BigInteger} whose value is the <i>scaled significand</i>
     * of this {@code BigFloat}. The scaled significand corresponds to the
     * significand of this IEEE floating point number (including the implicit
     * bit at the beginning), multiplied by 2<sup>precision-1</sup>, in order
     * to construct an integer type. Note that the return value of this function, when
     * the most significant bit is cleared, is equal to an unsigned integer corresponding
     * to the value of the bit vector that IEEE uses in its interchange format to
     * express the significand of this {@code BigFloat}.
     * 
     * @param minExponent the minimum exponent in the exponent range
     * @param maxExponent the maximum exponent in the exponent range
     * @return the mantissa of this floating point number multiplied by 
     * 2<sup>precision-1</sup>.
     * @throws UnsupportedOperationException if {@code this.isNan()}. The current
     * release does not support NaN payloads.
     */
    public BigInteger significand(long minExponent, long maxExponent) {
        if (isNaN()) {
            throw new UnsupportedOperationException("NaN payload is undefined");
        }
        if (isInfinite() || isPositiveZero() || isNegativeZero()) {
            return BigInteger.ZERO;
        }
        mpz_t rop = new mpz_t();
        mpfr_get_z_2exp(rop, op);
        BigInteger scaledSignificand = new BigInteger(mpz_get_str(10, rop)).abs();
        if (isSubnormal(minExponent)) {
            return scaledSignificand.shiftRight((int)(minExponent - (op._mpfr_exp - 1)));
        }
        return scaledSignificand;
    }
    
    /**
     * Returns {@code true} if the specified number is a Not-a-Number (NaN)
     * value, {@code false} otherwise.
     * 
     * @return {@code} true if {@code this} is NaN; {@code false} otherwise.
     */
    public boolean isNaN() {
        return mpfr_nan_p(op);
    }
    
    /**
     * Returns {@code true} if the specified number is infinitely large
     * in magnitude, {@code false} otherwise.
     * 
     * @return {@code} true if {@code this} is positive infinity or negative
     * infinity; {@code false} otherwise.
     */
    public boolean isInfinite() {
        return mpfr_inf_p(op);
    }

    /**
     * Returns {@code true} if the specified number is positive zero, 
     * {@code false} otherwise.
     * 
     * @return {@code} true if {@code this} is +0.0; {@code false} otherwise.
     */
    public boolean isPositiveZero() {
        return mpfr_zero_p(op) && !mpfr_signbit(op);
    }
    
    /**
     * Returns {@code true} if the specified number is negative zero, 
     * {@code false} otherwise.
     * 
     * @return {@code} true if {@code this} is -0.0; {@code false} otherwise.
     */
    public boolean isNegativeZero() {
        return mpfr_zero_p(op) && mpfr_signbit(op);
    }
    
    /**
     * Returns {@code true} if the specified number is a subnormal number
     * within the specified exponent range; {@code false} otherwise.
     * 
     * @param minExponent the minimum normal exponent in the exponent range.
     * @return {@code true} if {@code this} is subnormal; {@code false}
     * otherwise.
     * @throws ArithmeticException if {@code this} is less than the
     * smallest subnormal number in the specified exponent range.
     */
    public boolean isSubnormal(long minExponent) {
        if (isNaN() || isInfinite() || isPositiveZero() || isNegativeZero()) {
            return false;
        }
        if (minExponent - (op._mpfr_exp - 1) > precision() - 1) {
            throw new ArithmeticException("the specified BigFloat is not in the provided exponent range");
        }
        return (op._mpfr_exp - 1) < minExponent;
    }
    
    /**
     * Returns the string representation of this {@code BigFloat}, using
     * scientific notation.
     * 
     * While for simplicity's sake, this function always uses scientific
     * notation, even for {@code BigFloats} of small magnitude, the format
     * it returns is guaranteed to be consistent with
     * {@link Double#parseDouble(String)}, {@link Float#parseFloat(String)}.
     * It is also consistent with {@link java.math.BigDecimal#BigDecimal(String)}
     * on regular numbers, although both positive and negative zero are parsed
     * by that method as {@link java.math.BigDecimal#ZERO}.
     */
    @Override
    public String toString() {
        if (isInfinite() && sign()) {
            return "-Infinity";
        } else if (isInfinite()) {
            return "Infinity";
        } else if (isNaN()) {
            return "NaN";
        }
        return toString("%Re");
    }
    
    /**
     * Exposes the underlying MPFR floating point formatting code. This code
     * is compatible with {@link #BigFloat(String, BinaryMathContext)}, 
     * but is not gauranteed to be compatible with {@link Double#parseDouble(String)}
     * or {@link Float#parseFloat(String)}. It is provided for the sake of
     * flexibility in case users want to choose precisely how to represent their
     * values as a String.
     * @param format
     * @return A String representation of the {@code BigFloat}, formatted according
     * to {@code format}.
     */
    public String toString(String format) {
        return mpfr_asprintf(format, op);
    }
    
    /**
     * Compares two {@code BigFloat} objects numerically according to IEEE
     * comparison for floating point numbers, to determine whether this
     * {@code BigFloat} is less than the provided one. This method is
     * consistent with the behavior of the {@code <} Java language numerical
     * comparison operator for {@code float} and {@code double}. In
     * particular:
     * <ul><li>
     * If {@code this} or {@code anotherBigFloat} are NaN, this method returns
     * {@code false}.
     * <li>
     * If {@code this} and {@code anotherBigFloat} are both signed zeroes, 
     * this method returns {@code false}.
     * <li>
     * If {@code this} and {@code anotherBigFloat} contain the same numerical
     * value, but differ in precision, this method returns {@code false}.
     * </ul>
     * 
     * @param anotherBigFloat the {@code BigFloat} to be compared.
     * @return {@code true} if {@code this} is less than {@code anotherBigFloat};
     * {@code false} otherwise.
     */
    public boolean lessThan(BigFloat anotherBigFloat) {
        return mpfr_less_p(op, anotherBigFloat.op);
    }
    
    /**
     * Compares two {@code BigFloat} objects numerically according to IEEE
     * comparison for floating point numbers, to determine whether this
     * {@code BigFloat} is greater than the provided one. This method is
     * consistent with the behavior of the {@code >} Java language numerical
     * comparison operator for {@code float} and {@code double}. In
     * particular:
     * <ul><li>
     * If {@code this} or {@code anotherBigFloat} are NaN, this method returns
     * {@code false}.
     * <li>
     * If {@code this} and {@code anotherBigFloat} are both signed zeroes,
     * this method returns {@code false}.
     * <li>
     * If {@code this} and {@code anotherBigFloat} contain the same numerical
     * value, but differ in precision, this method returns {@code false}.
     * </ul>
     * 
     * @param anotherBigFloat the {@code BigFloat} to be compared.
     * @return {@code true} if {@code this} is greater than
     * {@code anotherBigFloat}; {@code false} otherwise.
     */
    public boolean greaterThan(BigFloat anotherBigFloat) {
        return mpfr_greater_p(op, anotherBigFloat.op);
    }
    
    /**
     * Compares two {@code BigFloat} objects numerically according to IEEE
     * comparison for floating point numbers, to determine whether this
     * {@code BigFloat} is less than or equal to the provided one. This method
     * is consistent with the behavior of the {@code <=} Java language
     * numerical comparison operator for {@code float} and {@code double}. In
     * particular:
     * <ul><li>
     * If {@code this} or {@code anotherBigFloat} are NaN, this method returns
     * {@code false}.
     * <li>
     * If {@code this} and {@code anotherBigFloat} are both signed zeroes,
     * this method returns {@code true}.
     * <li>
     * If {@code this} and {@code anotherBigFloat} contain the same numerical
     * value, but differ in precision, this method returns {@code true}.
     * </ul>
     * 
     * @param anotherBigFloat the {@code BigFloat} to be compared.
     * @return {@code true} if {@code this} is less than or equal to
     * {@code anotherBigFloat}; {@code false} otherwise.
     */
    public boolean lessThanOrEqualTo(BigFloat anotherBigFloat) {
        return mpfr_lessequal_p(op, anotherBigFloat.op);
    }
    
    /**
     * Compares two {@code BigFloat} objects numerically according to IEEE
     * comparison for floating point numbers, to determine whether this
     * {@code BigFloat} is greater than or equal to the provided one. This
     * method is consistent with the behavior of the {@code >=} Java language
     * numerical comparison operator for {@code float} and {@code double}. In
     * particular:
     * <ul><li>
     * If {@code this} or {@code anotherBigFloat} are NaN, this method returns
     * {@code false}.
     * <li>
     * If {@code this} and {@code anotherBigFloat} are both signed zeroes,
     * this method returns {@code true}.
     * <li>
     * If {@code this} and {@code anotherBigFloat} contain the same numerical
     * value, but differ in precision, this method returns {@code true}.
     * </ul>
     * 
     * @param anotherBigFloat the {@code BigFloat} to be compared.
     * @return {@code true} if {@code this} is greater than or equal to
     * {@code anotherBigFloat}; {@code false} otherwise.
     */
    public boolean greaterThanOrEqualTo(BigFloat anotherBigFloat) {
        return mpfr_greaterequal_p(op, anotherBigFloat.op);
    }
    
    /**
     * Compares two {@code BigFloat} objects numerically according to IEEE
     * comparison for floating point numbers, to determine whether this
     * {@code BigFloat} is equal to the provided one. This method is
     * consistent with the behavior of the {@code ==} Java language
     * numerical comparison operator for {@code float} and {@code double}. In
     * particular:
     * <ul><li>
     * If {@code this} or {@code anotherBigFloat} are NaN, this method returns
     * {@code false}.
     * <li>
     * If {@code this} and {@code anotherBigFloat} are both signed zeroes,
     * this method returns {@code true}.
     * <li>
     * If {@code this} and {@code anotherBigFloat} contain the same numerical
     * value, but differ in precision, this method returns {@code true}.
     * </ul>
     * 
     * @param anotherBigFloat the {@code BigFloat} to be compared.
     * @return {@code true} if {@code this} is equal to
     * {@code anotherBigFloat}; {@code false} otherwise.
     */
    public boolean equalTo(BigFloat anotherBigFloat) {
        return mpfr_equal_p(op, anotherBigFloat.op);
    }
    
    /**
     * Compares two {@code BigFloat} objects numerically according to IEEE
     * comparison for floating point numbers, to determine whether this
     * {@code BigFloat} is not equal to the provided one. This method is
     * consistent with the behavior of the {@code !=} Java language
     * numerical comparison operator for {@code float} and {@code double}. In
     * particular:
     * <ul><li>
     * If {@code this} or {@code anotherBigFloat} are NaN, this method returns
     * {@code true}.
     * <li>
     * If {@code this} and {@code anotherBigFloat} are both signed zeroes,
     * this method returns {@code false}.
     * <li>
     * If {@code this} and {@code anotherBigFloat} contain the same numerical
     * value, but differ in precision, this method returns {@code false}.
     * </ul>
     * 
     * @param anotherBigFloat the {@code BigFloat} to be compared.
     * @return {@code true} if {@code this} is not equal to
     * {@code anotherBigFloat}; {@code false} otherwise.
     */
    public boolean notEqualTo(BigFloat anotherBigFloat) {
        return !equalTo(anotherBigFloat);
    }

    /**
     * Compares two {@code BigFloat} objects numerically to create a
     * total ordering according to the specifications of the
     * {@link Comparable} interface. There are three ways in which comparisons
     * performed by this method differ from those performed by the IEEE
     * comparison methods {@link #lessThan}, {@link #greaterThan},
     * {@link #lessThanOrEqualTo}, {@link #greaterThanOrEqualTo},
     * {@link #equalTo}, and {@link #notEqualTo} when applied to {@code BigFloat} values:
     * <ul><li>
     * NaN is considered by this method to be equal to itself and
     * greater than all other {@code BigFloat} values (including +Infinity).
     * <li>
     * {@code 0.0} is considered by this method to be greater than
     * {@code -0.0}.
     * <li>
     * Two {@code BigFloat} objects that are equal in value but have a
     * different precision (like 2.0f and 2.0d) are considered to be unequal,
     * with the greater being the one with the greater precision.
     * </ul>
     * This ensures that the <i>natural ordering</i> of {@code BigFloat}
     * objects imposed by this method is <i>consistent with equals</i>.
     * 
     * @param anotherBigFloat the {@code BigFloat} to be compared.
     * @return the value {@code 0} if {@code anotherBigFloat} is equal to
     * this {@code BigFloat}; a value less than {@code 0} if this
     * {@code BigFloat} is numerically less than {@code anotherBigFloat} or
     * meets one of the special cases listed above; and a value greater than
     * {@code 0} if this {@code BigFloat} is numerically greater than 
     * {@code anotherBigFloat} or meets one of the special cases listed above.
     */
    @Override
    public int compareTo(BigFloat anotherBigFloat) {
        if (this.lessThan(anotherBigFloat)) {
            return -1;
        }
        if (this.greaterThan(anotherBigFloat)) {
            return 1;
        }
        if (this.isNegativeZero() && anotherBigFloat.isPositiveZero()) {
            return -1;
        }
        if (!this.isNaN() && anotherBigFloat.isNaN()) {
            return -1;
        }
        if (this.isPositiveZero() && anotherBigFloat.isNegativeZero()) {
            return 1;
        }
        if (this.isNaN() && !anotherBigFloat.isNaN()) {
            return 1;
        }
        if (this.isNaN() && anotherBigFloat.isNaN()) {
            return Integer.valueOf(this.precision()).compareTo(anotherBigFloat.precision());
        }
        int res = mpfr_cmp(op, anotherBigFloat.op);
        if (res != 0) {
            return res;
        }
        return Integer.valueOf(this.precision()).compareTo(anotherBigFloat.precision());
    }
    
    /**
     * Compares this {@code BigFloat} with the specified {@code Object} for
     * equality. Unlike {@link #equalTo}, this method considers two
     * {@code BigFloat} objects equal only if they are equal in value and
     * precision; it also considers -0.0 and 0.0 to be distinct, and NaN
     * to be equal to itself.
     * 
     * This method returns {@code true} if and only if
     * {@code this.compareTo((BigFloat)obj) == 0}.
     * 
     * @param x {@code Object} to which this {@code BigFloat} is to be
     * compared.
     * @return {@code true} if and only if the sepcified {@code Object}
     * is a {@code BigFloat} whose value and precision are exactly equal
     * to this {@code BigFloat}'s according to logical equality.
     * @see #compareTo(BigFloat)
     * @see #equalTo(BigFloat)
     * @see #hashCode()
     */
    @Override
    public boolean equals(Object x) {
        if (x == null) return false;
        if (x == this) return true;
        if (x.getClass() != this.getClass()) return false;
        
        BigFloat other = (BigFloat)x;
        return compareTo(other) == 0;
    }
    
    /**
     * Returns the hash code for this {@code BigFloat}. Note that
     * two {@code BigFloat} objects that are numerically equal but differ in
     * precision (like 2.0f and 2.0d) will generally <i>not</i> have the
     * same hash code; however, two {@code BigFloat} objects with the same
     * precision and exponent will usually have the same hashCode if they 
     * differ only very slightly in their significand. This improves the
     * speed of the hashing algorithm by allowing it to complete without
     * needing to read the entire significand, which may be very large.
     * 
     * @return hash code for this {@code BigFloat}.
     * @see #equals(Object)
     */
    @Override
    public int hashCode() {
        // two BigFloats which have the same numeric value should convert
        // to the same double
        Double d = doubleValue();
        return d.hashCode() * 31 + op._mpfr_prec;
    }
    
    private static int convertRoundingMode(RoundingMode mode) {
        switch (mode) {
            case HALF_EVEN:
            case UNNECESSARY: //unnecessary is handled separately
                return MPFR_RNDN;
            case DOWN:
                return MPFR_RNDZ;
            case CEILING:
                return MPFR_RNDU;
            case FLOOR:
                return MPFR_RNDD;
            case UP:
                return MPFR_RNDA;
            default:
                throw new IllegalArgumentException("Unsupported rounding mode");
        }
    }
    
    private static void throwArithmeticException(boolean rounded, BinaryMathContext mc) {
        if (rounded && mc.roundingMode == RoundingMode.UNNECESSARY) {
            throw new ArithmeticException("rounding necessary");
        }
    }
    
    // would be private, but we want to unit test it
    static boolean roundExponent(int ternary, mpfr_t x, BinaryMathContext mc) {
        try {
            setExponentRange(mc.minExponent, mc.maxExponent, mc.precision);
            ternary = mpfr_check_range(x, ternary, convertRoundingMode(mc.roundingMode));
            boolean rounded = mpfr_subnormalize(x, ternary, convertRoundingMode(mc.roundingMode));
            return rounded;
        } finally {
            resetExponentRange();
        }
    }

    static void resetExponentRange() {
        boolean failed = mpfr_set_emin(MPFR_EMIN_DEFAULT);
        failed |= mpfr_set_emax(MPFR_EMAX_DEFAULT);
        assert !failed : "unexpected failure resetting exponent range";
    }
    
    static long eminMin(int precision) {
        return MPFR_EMIN_DEFAULT + precision - 2;
    }
    
    static long EMAX_MAX = MPFR_EMAX_DEFAULT - 1;

    static void setExponentRange(long minExponent, long maxExponent, int precision) {
        minExponent = minExponent - precision + 2;
        maxExponent = maxExponent + 1;
        if (minExponent < MPFR_EMIN_DEFAULT || maxExponent > MPFR_EMAX_DEFAULT) {
            throw new ArithmeticException("invalid exponent range for specified precision: "
                + "maximum allowed exponent range for this precision is [" + eminMin(precision)
                + "," + EMAX_MAX + "]");
        }
        boolean failed = mpfr_set_emin(minExponent);
        failed |= mpfr_set_emax(maxExponent);
        if (failed) {
            resetExponentRange();
            assert false : "should never fail to set exponent range successfully";
        }
    }

    //TODO(dwightguth): fix with Java 8 and lambdas
    private static abstract class Operation {
        public abstract int doIt(mpfr_t rop, int rnd);
        
        public BigFloat execute(BinaryMathContext mc) {
            mpfr_t rop = new mpfr_t(mc.precision);
            int ternary = doIt(rop, convertRoundingMode(mc.roundingMode));
            boolean rounded = roundExponent(ternary, rop, mc);
            throwArithmeticException(rounded, mc);
            return new BigFloat(rop);
        }
    }
    
    /**
     * Returns a {@code BigFloat} whose value is {@code (this + augend)},
     * whose precision is equal to
     * {@link BinaryMathContext#precision mc.precision},
     * and with rounding according to the context settings. Special cases:
     * 
     * <ul><li>If one argument is finite and the other is positive infinity,
     * then the result is positive infinity.
     * <li>If one argument is finite and the other is negative infinity,
     * then the result is negative infinity.
     * <li>If one argument is negative infinity and the other is positive
     * infinity, then the result is NaN.
     * <li>If both arguments are negative zero, then the result is negative
     * zero.
     * <li>If both arguments are positive zero, then the result is positive
     * zero.
     * <li>If one argument is equal to the other but with the opposite sign,
     * and {@link BinaryMathContext#roundingMode mc.roundingMode} is not
     * {@link RoundingMode#FLOOR FLOOR}, then the result is positive zero.
     * <li>If one argument is equal to the other but with the opposite sign,
     * and {@link BinaryMathContext#roundingMode mc.roundingMode} is
     * {@link RoundingMode#FLOOR FLOOR}, then the result is negative zero.</ul>
     * 
     * @param augend value to be added to this {@code BigFloat}.
     * @param mc the context to use.
     * @return {@code this + augend} in the specified precision, rounded as
     * necessary.
     * @throws ArithmeticException if the result is inexact but the rounding
     * mode is {@code UNNECESSARY}; if the specified exponent range to round
     * to is not allowed by the underlying MPFR library; or if the precision
     * is too high to be able to emulate subnormal arithmetic for the specified
     * exponent range.
     * @throws IllegalArgumentException if the specified rounding mode is not supported
     * (i.e. HALF_UP or HALF_DOWN), or if the precision is less than 2
     */
    public BigFloat add(final BigFloat augend, BinaryMathContext mc) {
        return new Operation() {
            
            @Override
            public int doIt(mpfr_t rop, int rnd) {
                return mpfr_add(rop, op, augend.op, rnd);
            }
        }.execute(mc);
    }
    
    /**
     * Returns a {@code BigFloat} whose value is {@code (this - subtrahend)},
     * whose precision is equal to
     * {@link BinaryMathContext#precision mc.precision},
     * and with rounding according to the context settings. Special cases:
     * 
     * <ul><li>
     * If<ul><li>the first argument is finite and the second argument is
     * positive infinity, or <li>the first argument is negative infinity
     * and the second argument is finite,</ul> then the result is negative
     * infinity.
     * <li>
     * If<ul><li>the first argument is finite and the second argument is
     * negative infinity, or <li>the first argument is positive infinity
     * and the second argument is finite,</ul> then the result is positive
     * infinity.
     * <li>
     * If the first argument is infinite and the second argument is equal to
     * the first argument, then the result is NaN.
     * <li>
     * If<ul><li>the first argument is positive zero and the second argument
     * is negative zero, or <li>the first argument is negative zero and the
     * second argument is positive zero,</ul> then the result is the same as
     * the first argument.
     * <li>
     * If the first argument is equal to the second argument, and
     * {@link BinaryMathContext#roundingMode mc.roundingMode} is not
     * {@link RoundingMode#FLOOR FLOOR}, then the result is positive zero.
     * <li>
     * If the first argument is equal to the second argument, and
     * {@link BinaryMathContext#roundingMode mc.roundingMode} is
     * {@link RoundingMode#FLOOR FLOOR}, then the result is negative zero.</ul>
     * 
     * @param subtrahend value to be subtracted from this {@code BigFloat}.
     * @param mc the context to use.
     * @return {@code this - subtrahend} in the specified precision, rounded
     * as necessary.
     * @throws ArithmeticException if the result is inexact but the rounding
     * mode is {@code UNNECESSARY}; if the specified exponent range to round
     * to is not allowed by the underlying MPFR library; or if the precision
     * is too high to be able to emulate subnormal arithmetic for the specified
     * exponent range.
     * @throws IllegalArgumentException if the specified rounding mode is not supported
     * (i.e. HALF_UP or HALF_DOWN), or if the precision is less than 2
     */
    public BigFloat subtract(final BigFloat subtrahend, BinaryMathContext mc) {
        return new Operation() {
            
            @Override
            public int doIt(mpfr_t rop, int rnd) {
                return mpfr_sub(rop, op, subtrahend.op, rnd);
            }
        }.execute(mc);
    }
    
    /**
     * Returns a {@code BigFloat} whose value is <tt>(this &times;
     * multiplicand)</tt>, whose precision is equal to
     * {@link BinaryMathContext#precision mc.precision},
     * and with rounding according to the context settings. Special cases:
     * 
     * <ul><li>
     * If<ul><li>one argument is positive and finite and the other is
     * positive infinity, or <li>one argument is negative and finite and the
     * other is negative infinity,</ul> then the result is positive infinity.
     * <li>
     * If<ul><li>one argument is negative and finite and the other is positive
     * infinity, or <li>one argument is positive and finite and the other is
     * negative infinity,</ul> then the result is negative infinity.
     * <li>
     * If one argument is infinite and the other is zero, then the result is
     * NaN.
     * <li>
     * If the result is zero, and the signs of the two arguments are the
     * same, then sign of the result is positive.
     * <li>
     * If the result is zero, and the signs of the two arguments are
     * different, then sign of the result is negative.
     * 
     * @param multiplicand value to multiplied by this {@code BigFloat}.
     * @param mc the context to use.
     * @return {@code this * multiplicand} in the specified precision, rounded
     * as necessary.
     * @throws ArithmeticException if the result is inexact but the rounding
     * mode is {@code UNNECESSARY}; if the specified exponent range to round
     * to is not allowed by the underlying MPFR library; or if the precision
     * is too high to be able to emulate subnormal arithmetic for the specified
     * exponent range.
     * @throws IllegalArgumentException if the specified rounding mode is not supported
     * (i.e. HALF_UP or HALF_DOWN), or if the precision is less than 2
     */
    public BigFloat multiply(final BigFloat multiplicand, BinaryMathContext mc) {
        return new Operation() {
            
            @Override
            public int doIt(mpfr_t rop, int rnd) {
                return mpfr_mul(rop, op, multiplicand.op, rnd);
            }
        }.execute(mc);
    }

    /**
     * Returns a {@code BigFloat} whose value is {@code (this / divisor)},
     * whose precision is equal to
     * {@link BinaryMathContext#precision mc.precision},
     * and with rounding according to the context settings. Special cases:
     * 
     * <ul><li>
     * If both arguments are infinite, then the result is NaN.
     * <li>
     * If<ul><li>the first argument is positive infinity and the second
     * argument is positive and finite, or <li>the first argument is negative
     * infinity and the second argument is negative and finite,</ul> then the
     * result is positive infinity.
     * <li>
     * If<ul><li>the first argument is negative infinity and the second
     * argument is positive and finite, or <li>the first argument is positive
     * infinity and the second argument is negative and finite,</ul> then the
     * result is negative infinity.
     * <li>
     * If<ul><li>the first argument is positive and the second argument is
     * positive zero, or <li>the first argument is negative and the second
     * argument is negative zero,</ul> then the result is positive infinity.
     * <li>
     * If<ul><li>the first argument is negative and the second argument is
     * negative zero, or <li>the first argument is positive and the second
     * argument is positive zero,</ul> then the result is negative infinity.
     * <li>
     * If both arguments are zero, then the result is NaN.
     * <li>
     * If the first argument is finite and the second argument is infinite,
     * then the result is a signed zero.
     * <li>
     * If the result is zero, and the signs of the two arguments are the
     * same, then the sign of the result is positive.
     * <li>
     * If the result is zero, and the signs of the two arguments are
     * different, then the sign of the result is negative.</ul>
     * 
     * @param divisor value by which this {@code BigFloat} is to be divided.
     * @param mc the context to use.
     * @return {@code this / divisor} in the specified precision, rounded
     * as necessary.
     * @throws ArithmeticException if the result is inexact but the rounding
     * mode is {@code UNNECESSARY}; if the specified exponent range to round
     * to is not allowed by the underlying MPFR library; or if the precision
     * is too high to be able to emulate subnormal arithmetic for the specified
     * exponent range.
     * @throws IllegalArgumentException if the specified rounding mode is not supported
     * (i.e. HALF_UP or HALF_DOWN), or if the precision is less than 2
     */
    public BigFloat divide(final BigFloat divisor, BinaryMathContext mc) {
        return new Operation() {
            
            @Override
            public int doIt(mpfr_t rop, int rnd) {
                return mpfr_div(rop, op, divisor.op, rnd);
            }
        }.execute(mc);
    }

    /**
     * Returns a {@code BigFloat} whose value is {@code (this % divisor)},
     * whose precision is equal to
     * {@link BinaryMathContext#precision mc.precision},
     * and with rounding according to the context settings. The
     * {@code BinaryMathContext} settings affect the subtraction and
     * multiplication operations implicit in computing the remainder; the
     * division operation itself automatically uses
     * {@link RoundingMode#HALF_EVEN}. The remainder value is mathematically
     * equal to {@code this - divisor &times; <i>n</i>},
     * where <i>n</i> is the mathematical integer closest to the exact
     * mathematical value of the quotient {@code this/divisor}, and if two
     * mathematical integers are equally close to {@code this/divisor},
     * then <i>n</i> is the integer that is even. If the remainder is
     * zero, its sign is the same as the sign of the first argument. Special
     * cases:
     * 
     * <ul><li>If either argument is NaN, or the first argument is infinite,
     * or the second argument is zero, then the result is NaN.
     * <li>If the second argument is infinite and the first argument is
     * finite, then the result is the same as the first argument rounded to
     * the specified precision.</ul>
     * 
     * @param divisor value by which this {@code BigFloat} is to be divided.
     * @param mc the context to use.
     * @return {@code this % divisor} in the specified precision, rounded
     * as necessary.
     * @throws ArithmeticException if the result is inexact but the rounding
     * mode is {@code UNNECESSARY}; if the specified exponent range to round
     * to is not allowed by the underlying MPFR library; or if the precision
     * is too high to be able to emulate subnormal arithmetic for the specified
     * exponent range.
     * @throws IllegalArgumentException if the specified rounding mode is not supported
     * (i.e. HALF_UP or HALF_DOWN), or if the precision is less than 2
     */
    public BigFloat remainder(final BigFloat divisor, BinaryMathContext mc) {
        return new Operation() {
            
            @Override
            public int doIt(mpfr_t rop, int rnd) {
                return mpfr_remainder(rop, op, divisor.op, rnd);
            }
        }.execute(mc);
    }

    /**
     * Return a {@code BigFloat} whose value is <tt>(this<sup>n</sup>)</tt>,
     * whose precision is equal to
     * {@link BinaryMathContext#precision mc.precision},
     * and with rounding according to the context settings. Special cases:
     * 
     * <ul><li>If the second argument is positive or negative zero, then the
     * result is 1.0.
     * <li>If the second argument is 1.0, then the result is the same as the
     * first argument.
     * <li>If the second argument is NaN, then the result is NaN.
     * <li>If the first argument is NaN and the second argument is nonzero,
     * then the result is NaN.
     *
     * <li>If
     * <ul>
     * <li>the absolute value of the first argument is greater than 1
     * and the second argument is positive infinity, or
     * <li>the absolute value of the first argument is less than 1 and
     * the second argument is negative infinity,
     * </ul>
     * then the result is positive infinity.
     *
     * <li>If
     * <ul>
     * <li>the absolute value of the first argument is greater than 1 and
     * the second argument is negative infinity, or
     * <li>the absolute value of the
     * first argument is less than 1 and the second argument is positive
     * infinity,
     * </ul>
     * then the result is positive zero.
     *
     * <li>If the absolute value of the first argument equals 1 and the
     * second argument is infinite, then the result is 1.0. Note that this
     * differs from {@link Math#pow(double, double)} for which the
     * equivalent result is NaN.
     *
     * <li>If
     * <ul>
     * <li>the first argument is positive zero and the second argument
     * is greater than zero, or
     * <li>the first argument is positive infinity and the second
     * argument is less than zero,
     * </ul>
     * then the result is positive zero.
     *
     * <li>If
     * <ul>
     * <li>the first argument is positive zero and the second argument
     * is less than zero, or
     * <li>the first argument is positive infinity and the second
     * argument is greater than zero,
     * </ul>
     * then the result is positive infinity.
     *
     * <li>If
     * <ul>
     * <li>the first argument is negative zero and the second argument
     * is greater than zero but not a finite odd integer, or
     * <li>the first argument is negative infinity and the second
     * argument is less than zero but not a finite odd integer,
     * </ul>
     * then the result is positive zero.
     *
     * <li>If
     * <ul>
     * <li>the first argument is negative zero and the second argument
     * is a positive finite odd integer, or
     * <li>the first argument is negative infinity and the second
     * argument is a negative finite odd integer,
     * </ul>
     * then the result is negative zero.
     *
     * <li>If
     * <ul>
     * <li>the first argument is negative zero and the second argument
     * is less than zero but not a finite odd integer, or
     * <li>the first argument is negative infinity and the second
     * argument is greater than zero but not a finite odd integer,
     * </ul>
     * then the result is positive infinity.
     *
     * <li>If
     * <ul>
     * <li>the first argument is negative zero and the second argument
     * is a negative finite odd integer, or
     * <li>the first argument is negative infinity and the second
     * argument is a positive finite odd integer,
     * </ul>
     * then the result is negative infinity.
     *
     * <li>If the first argument is finite and less than zero
     * <ul>
     * <li> if the second argument is a finite even integer, the
     * result is equal to the result of raising the absolute value of
     * the first argument to the power of the second argument
     *
     * <li>if the second argument is a finite odd integer, the result
     * is equal to the negative of the result of raising the absolute
     * value of the first argument to the power of the second
     * argument
     *
     * <li>if the second argument is finite and not an integer, then
     * the result is NaN.
     * </ul>
     *
     * <li>If both arguments are integers, then the result is exactly equal
     * to the mathematical result of raising the first argument to the power
     * of the second argument if that result can in fact be represented
     * exactly as a {@code BigFloat} value with the specified precision.</ul>
     * 
     * @param n value to which power this {@code BigFloat} is to be raised.
     * @param mc the context to use.
     * @return {@code pow(this, divisor)} in the specified precision, rounded
     * as necessary.
     * @throws ArithmeticException if the result is inexact but the rounding
     * mode is {@code UNNECESSARY}; if the specified exponent range to round
     * to is not allowed by the underlying MPFR library; or if the precision
     * is too high to be able to emulate subnormal arithmetic for the specified
     * exponent range.
     * @throws IllegalArgumentException if the specified rounding mode is not supported
     * (i.e. HALF_UP or HALF_DOWN), or if the precision is less than 2
     */
    public BigFloat pow(final BigFloat n, BinaryMathContext mc) {
        return new Operation() {
            
            @Override
            public int doIt(mpfr_t rop, int rnd) {
                return mpfr_pow(rop, op, n.op, rnd);
            }
        }.execute(mc);
    }
    
    /**
     * Return a {@code BigFloat} whose value is <tt>(this<sup>1/n</sup>)</tt>,
     * whose precision is equal to
     * {@link BinaryMathContext#precision mc.precision},
     * and with rounding according to the context settings. Special cases:
     * 
     * <ul><li>If k is equal to zero, then the result is NaN.
     * <li>If k is even and the argument is negative, then the result is NaN.
     * <li>If the argument is positive infinity, or k is odd and the argument
     * is negative infinity, then the result is the same as the argument.
     * <li>If the argument is positive zero or negative zero, then the
     * result is the same as the argument.</ul>
     * Otherwise, the result is the {@code BigFloat} value closest to the
     * true mathematical k'th root of the argument value.
     * 
     * @param k the integral root of {@code this} to compute.
     * @param mc the context to use.
     * @return {@code root(this, k)} in the specified precision, rounded
     * as necessary.
     * @throws ArithmeticException if the result is inexact but the rounding
     * mode is {@code UNNECESSARY}; if the specified exponent range to round
     * to is not allowed by the underlying MPFR library; or if the precision
     * is too high to be able to emulate subnormal arithmetic for the specified
     * exponent range.
     * @throws IllegalArgumentException if the specified rounding mode is not supported
     * (i.e. HALF_UP or HALF_DOWN), or if the precision is less than 2
     */
    public BigFloat root(final int k, BinaryMathContext mc) {
        if (k == 0) {
            return BigFloat.NaN(mc.precision);
        } else if (k < 0) {
            return new BigFloat(1, mc).divide(root(-k, mc), mc);
        } else {
            return new Operation() {
    
                @Override
                public int doIt(mpfr_t rop, int rnd) {
                    return mpfr_root(rop, op, k, rnd);
                }
            }.execute(mc);
        }
    }
    
    /**
     * Returns the correctly rounded positive square root of a
     * {@code BigFloat} value. This is equivalent to calling
     * {@code root(2, mc)}.
     * 
     * @param mc the context to use.
     * @return {@code root(this, k)} in the specified precision, rounded
     * as necessary.
     * @throws ArithmeticException if the result is inexact but the rounding
     * mode is {@code UNNECESSARY}; if the specified exponent range to round
     * to is not allowed by the underlying MPFR library; or if the precision
     * is too high to be able to emulate subnormal arithmetic for the specified
     * exponent range.
     * @throws IllegalArgumentException if the specified rounding mode is not supported
     * (i.e. HALF_UP or HALF_DOWN), or if the precision is less than 2
     */
    public BigFloat sqrt(BinaryMathContext mc) {
        return root(2, mc);
    }
    
    /**
     * Returns the cube root of a
     * {@code BigFloat} value. This is equivalent to calling
     * {@code root(3, mc)}.
     * 
     * @param mc the context to use.
     * @return {@code root(this, k)} in the specified precision, rounded
     * as necessary.
     * @throws ArithmeticException if the result is inexact but the rounding
     * mode is {@code UNNECESSARY}; if the specified exponent range to round
     * to is not allowed by the underlying MPFR library; or if the precision
     * is too high to be able to emulate subnormal arithmetic for the specified
     * exponent range.
     * @throws IllegalArgumentException if the specified rounding mode is not supported
     * (i.e. HALF_UP or HALF_DOWN), or if the precision is less than 2
     */
    public BigFloat cbrt(BinaryMathContext mc) {
        return root(3, mc);
    }
    
    /**
     * Returns a {@code BigFloat} whose value is the absolute value of this
     * {@code BigFloat}, and whose precision is {@code this.precision()}.
     * 
     * @return {@code abs(this)}.
     */
    public BigFloat abs() {
        return abs(new BinaryMathContext(precision(), eminMin(precision()), 
                EMAX_MAX, RoundingMode.UNNECESSARY));
    }

    /**
     * Returns a {@code BigFloat} whose value is the absolute value of this
     * {@code BigFloat}, with rounding according to the context settings.
     * 
     * @param mc the context to use.
     * @return {@code abs(this)}, rounded as necessary.
     * @throws ArithmeticException if the result is inexact but the rounding
     * mode is {@code UNNECESSARY}; if the specified exponent range to round
     * to is not allowed by the underlying MPFR library; or if the precision
     * is too high to be able to emulate subnormal arithmetic for the specified
     * exponent range.
     * @throws IllegalArgumentException if the specified rounding mode is not supported
     * (i.e. HALF_UP or HALF_DOWN), or if the precision is less than 2
     */
    public BigFloat abs(BinaryMathContext mc) {
        return new Operation() {
            
            @Override
            public int doIt(mpfr_t rop, int rnd) {
                return mpfr_abs(rop, op, rnd);
            }
        }.execute(mc);
    }
    
    /**
     * Returns a {@code BigFloat} whose value is {@code -this}, and whose
     * precision is {@code this.precision()}.
     * 
     * @return {@code -this}.
     */
    public BigFloat negate() {
        return negate(new BinaryMathContext(precision(), eminMin(precision()), 
                EMAX_MAX, RoundingMode.UNNECESSARY));
    }

    /**
     * Returns a {@code BigFloat} whose value is {@code -this}, with rounding
     * according to the context settings.
     * 
     * @param mc the context to use.
     * @return {@code -this}, rounded as necessary.
     * @throws ArithmeticException if the result is inexact but the rounding
     * mode is {@code UNNECESSARY}; if the specified exponent range to round
     * to is not allowed by the underlying MPFR library; or if the precision
     * is too high to be able to emulate subnormal arithmetic for the specified
     * exponent range.
     * @throws IllegalArgumentException if the specified rounding mode is not supported
     * (i.e. HALF_UP or HALF_DOWN), or if the precision is less than 2
     */
    public BigFloat negate(BinaryMathContext mc) {
        return new Operation() {
            
            @Override
            public int doIt(mpfr_t rop, int rnd) {
                return mpfr_neg(rop, op, rnd);
            }
        }.execute(mc);
    }
    
    /**
     * Returns a {@code BigFloat} whose value is {@code +this}, and whose
     * precision is {@code this.precision()}.
     * 
     * <p>This method, which simply returns this {@code BigFloat} is included
     * for symmetry with the unary minus method {@link #negate()}.
     * 
     * @return {@code this}.
     * @see #negate()
     */
    public BigFloat plus() {
        return this;
    }
    
    /**
     * Returns a {@code BigFloat} whose value is {@code +this}, with rounding
     * according to the context settings.
     * 
     * <p>The effect of this method is identical to that of the
     * {@link #round(BinaryMathContext)} method.
     * 
     * @param mc the context to use.
     * @return {@code this}, rounded as necessary.
     * @throws ArithmeticException if the result is inexact but the rounding
     * mode is {@code UNNECESSARY}; if the specified exponent range to round
     * to is not allowed by the underlying MPFR library; or if the precision
     * is too high to be able to emulate subnormal arithmetic for the specified
     * exponent range.
     * @throws IllegalArgumentException if the specified rounding mode is not supported
     * (i.e. HALF_UP or HALF_DOWN), or if the precision is less than 2
     * @see #round(BinaryMathContext)
     */
    public BigFloat plus(BinaryMathContext mc) {
        return round(mc);
    }
    
    /**
     * Returns the signum function of this {@code BigFloat}; zero if the
     * argument is zero, 1.0 if the argument is greater than zero, -1.0 if
     * the argument is less than zero. Special cases:
     * 
     * <ul><li>If the argument is NaN, then the result is NaN.
     * <li>If the argument is positive zero or negative zero, then the result
     * is the same as the argument.</ul>
     * 
     * @return the signum function of this {@code BigFloat}.
     */
    public double signum() {
        if (mpfr_zero_p(op) || mpfr_nan_p(op)) {
            return doubleValueExact();
        }
        if (mpfr_signbit(op)) {
            return -1.0;
        }
        return 1.0;
    }

    /**
     * Returns a {@code BigFloat} rounded according to the
     * {@code BinaryMathContext} settings.
     * 
     * <p>The effect of this method is identical to that of the
     * {@link #plus(BinaryMathContext)} method.
     * 
     * @param mc the context to use.
     * @return {@code this}, rounded as necessary.
     * @throws ArithmeticException if the result is inexact but the rounding
     * mode is {@code UNNECESSARY}; if the specified exponent range to round
     * to is not allowed by the underlying MPFR library; or if the precision
     * is too high to be able to emulate subnormal arithmetic for the specified
     * exponent range.
     * @throws IllegalArgumentException if the specified rounding mode is not supported
     * (i.e. HALF_UP or HALF_DOWN), or if the precision is less than 2
     * @see #plus(BinaryMathContext)
     */
    public BigFloat round(BinaryMathContext mc) {
        return new Operation() {
            
            @Override
            public int doIt(mpfr_t rop, int rnd) {
                return mpfr_set(rop, op, rnd);
            }
        }.execute(mc);
    }
    
    /**
     * Returns the trigonometric sine of an angle, with rounding
     * according to the context settings. Special cases:
     * <ul><li>If the argument is NaN or an infinity, then the result is
     * NaN.
     * <li>If the argument is zero, then the result is a zero with the same
     * sign as the argument.</ul>
     * 
     * @param mc the context to use.
     * @return {@code sin(this)}, rounded as necessary.
     * @throws ArithmeticException if the result is inexact but the rounding
     * mode is {@code UNNECESSARY}; if the specified exponent range to round
     * to is not allowed by the underlying MPFR library; or if the precision
     * is too high to be able to emulate subnormal arithmetic for the specified
     * exponent range.
     * @throws IllegalArgumentException if the specified rounding mode is not supported
     * (i.e. HALF_UP or HALF_DOWN), or if the precision is less than 2
     */
    public BigFloat sin(BinaryMathContext mc) {
        return new Operation() {
            
            @Override
            public int doIt(mpfr_t rop, int rnd) {
                return mpfr_sin(rop, op, rnd);
            }
        }.execute(mc);
    }
    
    /**
     * Returns the trigonometric cosine of an angle, with rounding
     * according to the context settings. Special cases:
     * <ul><li>If the argument is NaN or an infinity, then the result is
     * NaN.</ul>
     * 
     * @param mc the context to use.
     * @return {@code cos(this)}, rounded as necessary.
     * @throws ArithmeticException if the result is inexact but the rounding
     * mode is {@code UNNECESSARY}; if the specified exponent range to round
     * to is not allowed by the underlying MPFR library; or if the precision
     * is too high to be able to emulate subnormal arithmetic for the specified
     * exponent range.
     * @throws IllegalArgumentException if the specified rounding mode is not supported
     * (i.e. HALF_UP or HALF_DOWN), or if the precision is less than 2
     */
    public BigFloat cos(BinaryMathContext mc) {
        return new Operation() {
            
            @Override
            public int doIt(mpfr_t rop, int rnd) {
                return mpfr_cos(rop, op, rnd);
            }
        }.execute(mc);
    }
    
    /**
     * Returns the trigonometric secant of an angle, with rounding
     * according to the context settings. Special cases:
     * <ul><li>If the argument is NaN or an infinity, then the result is
     * NaN.</ul>
     * 
     * @param mc the context to use.
     * @return {@code sec(this)}, rounded as necessary.
     * @throws ArithmeticException if the result is inexact but the rounding
     * mode is {@code UNNECESSARY}; if the specified exponent range to round
     * to is not allowed by the underlying MPFR library; or if the precision
     * is too high to be able to emulate subnormal arithmetic for the specified
     * exponent range.
     * @throws IllegalArgumentException if the specified rounding mode is not supported
     * (i.e. HALF_UP or HALF_DOWN), or if the precision is less than 2
     */
    public BigFloat sec(BinaryMathContext mc) {
        return new Operation() {
            
            @Override
            public int doIt(mpfr_t rop, int rnd) {
                return mpfr_sec(rop, op, rnd);
            }
        }.execute(mc);
    }
    
    /**
     * Returns the trigonometric cosecant of an angle, with rounding
     * according to the context settings. Special cases:
     * <ul><li>If the argument is NaN or an infinity, then the result is
     * NaN.</ul>
     * <li>If the argument is zero, then the result is an infinity with the same
     * sign as the argument.</ul>
     *  
     * @param mc the context to use.
     * @return {@code csc(this)}, rounded as necessary.
     * @throws ArithmeticException if the result is inexact but the rounding
     * mode is {@code UNNECESSARY}; if the specified exponent range to round
     * to is not allowed by the underlying MPFR library; or if the precision
     * is too high to be able to emulate subnormal arithmetic for the specified
     * exponent range.
     * @throws IllegalArgumentException if the specified rounding mode is not supported
     * (i.e. HALF_UP or HALF_DOWN), or if the precision is less than 2
     */
    public BigFloat csc(BinaryMathContext mc) {
        return new Operation() {
            
            @Override
            public int doIt(mpfr_t rop, int rnd) {
                return mpfr_csc(rop, op, rnd);
            }
        }.execute(mc);
    }
    
    /**
     * Returns the trigonometric cotangent of an angle, with rounding
     * according to the context settings. Special cases:
     * <ul><li>If the argument is NaN or an infinity, then the result is
     * NaN.</ul>
     * <li>If the argument is zero, then the result is an infinity with the same
     * sign as the argument.</ul>
     *  
     * @param mc the context to use.
     * @return {@code cot(this)}, rounded as necessary.
     * @throws ArithmeticException if the result is inexact but the rounding
     * mode is {@code UNNECESSARY}; if the specified exponent range to round
     * to is not allowed by the underlying MPFR library; or if the precision
     * is too high to be able to emulate subnormal arithmetic for the specified
     * exponent range.
     * @throws IllegalArgumentException if the specified rounding mode is not supported
     * (i.e. HALF_UP or HALF_DOWN), or if the precision is less than 2
     */
    public BigFloat cot(BinaryMathContext mc) {
        return new Operation() {
            
            @Override
            public int doIt(mpfr_t rop, int rnd) {
                return mpfr_cot(rop, op, rnd);
            }
        }.execute(mc);
    }
    
    /**
     * Returns the trigonometric tangent of an angle, with rounding
     * according to the context settings. Special cases:
     * <ul><li>If the argument is NaN or an infinity, then the result is
     * NaN.
     * <li>If the argument is zero, then the result is a zero with the same
     * sign as the argument.</ul>
     * 
     * @param mc the context to use.
     * @return {@code tan(this)}, rounded as necessary.
     * @throws ArithmeticException if the result is inexact but the rounding
     * mode is {@code UNNECESSARY}; if the specified exponent range to round
     * to is not allowed by the underlying MPFR library; or if the precision
     * is too high to be able to emulate subnormal arithmetic for the specified
     * exponent range.
     * @throws IllegalArgumentException if the specified rounding mode is not supported
     * (i.e. HALF_UP or HALF_DOWN), or if the precision is less than 2
     */
    public BigFloat tan(BinaryMathContext mc) {
        return new Operation() {
            
            @Override
            public int doIt(mpfr_t rop, int rnd) {
                return mpfr_tan(rop, op, rnd);
            }
        }.execute(mc);
    }
    
    /**
     * Returns the arc sine of a value, with rounding
     * according to the context settings; the returned angle is in the range
     * -<i>pi</i>/2 through <i>pi</i>/2. Special cases:
     * <ul><li>If the argument is NaN or an infinity, then the result is
     * NaN.
     * <li>If the argument is zero, then the result is a zero with the same
     * sign as the argument.</ul>
     * 
     * @param mc the context to use.
     * @return {@code asin(this)}, rounded as necessary.
     * @throws ArithmeticException if the result is inexact but the rounding
     * mode is {@code UNNECESSARY}; if the specified exponent range to round
     * to is not allowed by the underlying MPFR library; or if the precision
     * is too high to be able to emulate subnormal arithmetic for the specified
     * exponent range.
     * @throws IllegalArgumentException if the specified rounding mode is not supported
     * (i.e. HALF_UP or HALF_DOWN), or if the precision is less than 2
     */
    public BigFloat asin(BinaryMathContext mc) {
        return new Operation() {
            
            @Override
            public int doIt(mpfr_t rop, int rnd) {
                return mpfr_asin(rop, op, rnd);
            }
        }.execute(mc);
    }
    
    /**
     * Returns the arc cosine of a value, with rounding
     * according to the context settings; the returned angle is in the range
     * 0.0 through <i>pi</i>. Special cases:
     * <ul><li>If the argument is NaN or an infinity, then the result is
     * NaN.</ul>
     * 
     * @param mc the context to use.
     * @return {@code acos(this)}, rounded as necessary.
     * @throws ArithmeticException if the result is inexact but the rounding
     * mode is {@code UNNECESSARY}; if the specified exponent range to round
     * to is not allowed by the underlying MPFR library; or if the precision
     * is too high to be able to emulate subnormal arithmetic for the specified
     * exponent range.
     * @throws IllegalArgumentException if the specified rounding mode is not supported
     * (i.e. HALF_UP or HALF_DOWN), or if the precision is less than 2
     */
    public BigFloat acos(BinaryMathContext mc) {
        return new Operation() {
            
            @Override
            public int doIt(mpfr_t rop, int rnd) {
                return mpfr_acos(rop, op, rnd);
            }
        }.execute(mc);
    }
    
    /**
     * Returns the arc tangent of a value, with rounding
     * according to the context settings; the returned angle is in the range
     * -<i>pi</i>/2 through <i>pi</i>/2. Special cases:
     * <ul><li>If the argument is NaN or an infinity, then the result is
     * NaN.
     * <li>If the argument is zero, then the result is a zero with the same
     * sign as the argument.</ul>
     * 
     * @param mc the context to use.
     * @return {@code atan(this)}, rounded as necessary.
     * @throws ArithmeticException if the result is inexact but the rounding
     * mode is {@code UNNECESSARY}; if the specified exponent range to round
     * to is not allowed by the underlying MPFR library; or if the precision
     * is too high to be able to emulate subnormal arithmetic for the specified
     * exponent range.
     * @throws IllegalArgumentException if the specified rounding mode is not supported
     * (i.e. HALF_UP or HALF_DOWN), or if the precision is less than 2
     */
    public BigFloat atan(BinaryMathContext mc) {
        return new Operation() {
            
            @Override
            public int doIt(mpfr_t rop, int rnd) {
                return mpfr_atan(rop, op, rnd);
            }
        }.execute(mc);
    }
    
    /**
     * Returns the angle <i>theta</i> from the conversion of rectangular
     * coordinates ({@code x},&nbsp;{@code y}) to polar
     * coordinates (r,&nbsp;<i>theta</i>), with rounding
     * according to the context settings.
     * This method computes the phase <i>theta</i> by computing an arc tangent
     * of {@code y/x} in the range of -<i>pi</i> to <i>pi</i>. Special
     * cases:
     * <ul><li>If either argument is NaN, then the result is NaN.
     * <li>If the first argument is positive zero and the second argument
     * is positive, or the first argument is positive and finite and the
     * second argument is positive infinity, then the result is positive
     * zero.
     * <li>If the first argument is negative zero and the second argument
     * is positive, or the first argument is negative and finite and the
     * second argument is positive infinity, then the result is negative zero.
     * <li>If the first argument is positive zero and the second argument
     * is negative, or the first argument is positive and finite and the
     * second argument is negative infinity, then the result is the
     * {@code double} value closest to <i>pi</i>.
     * <li>If the first argument is negative zero and the second argument
     * is negative, or the first argument is negative and finite and the
     * second argument is negative infinity, then the result is the
     * {@code double} value closest to -<i>pi</i>.
     * <li>If the first argument is positive and the second argument is
     * positive zero or negative zero, or the first argument is positive
     * infinity and the second argument is finite, then the result is the
     * {@code double} value closest to <i>pi</i>/2.
     * <li>If the first argument is negative and the second argument is
     * positive zero or negative zero, or the first argument is negative
     * infinity and the second argument is finite, then the result is the
     * {@code double} value closest to -<i>pi</i>/2.
     * <li>If both arguments are positive infinity, then the result is the
     * {@code double} value closest to <i>pi</i>/4.
     * <li>If the first argument is positive infinity and the second argument
     * is negative infinity, then the result is the {@code double}
     * value closest to 3*<i>pi</i>/4.
     * <li>If the first argument is negative infinity and the second argument
     * is positive infinity, then the result is the {@code double} value
     * closest to -<i>pi</i>/4.
     * <li>If both arguments are negative infinity, then the result is the
     * {@code double} value closest to -3*<i>pi</i>/4.</ul>
     *
     * <p>The computed result must be within 2 ulps of the exact result.
     * Results must be semi-monotonic.
     *
     * @param   y   the ordinate coordinate
     * @param   x   the abscissa coordinate
     * @param   mc  the context to use.
     * @return  the <i>theta</i> component of the point
     *          (<i>r</i>,&nbsp;<i>theta</i>)
     *          in polar coordinates that corresponds to the point
     *          (<i>x</i>,&nbsp;<i>y</i>) in Cartesian coordinates.
     * @throws ArithmeticException if the result is inexact but the rounding
     * mode is {@code UNNECESSARY}; if the specified exponent range to round
     * to is not allowed by the underlying MPFR library; or if the precision
     * is too high to be able to emulate subnormal arithmetic for the specified
     * exponent range.
     * @throws IllegalArgumentException if the specified rounding mode is not supported
     * (i.e. HALF_UP or HALF_DOWN), or if the precision is less than 2
     */
    public static BigFloat atan2(final BigFloat y, final BigFloat x, BinaryMathContext mc) {
        return new Operation() {
            
            @Override
            public int doIt(mpfr_t rop, int rnd) {
                return mpfr_atan2(rop, y.op, x.op, rnd);
            }
        }.execute(mc);
    }
    
    /**
     * Returns the hyperbolic sine of a {@code BigFloat} value, with rounding
     * according to the context settings. The hyperbolic
     * sine of <i>x</i> is defined to be 
     * (<i>e<sup>x</sup>&nbsp;-&nbsp;e<sup>-x</sup></i>)/2
     * where <i>e</i> is {@linkplain #e(BinaryMathContext) Euler's number}.
     * Special cases:
     * <ul>
     * <li>If the argument is NaN, then the result is NaN.
     *
     * <li>If the argument is infinite, then the result is an infinity
     * with the same sign as the argument.
     *
     * <li>If the argument is zero, then the result is a zero with the
     * same sign as the argument.
     *
     * </ul>
     * 
     * @param mc the context to use.
     * @return {@code sinh(this)}, rounded as necessary.
     * @throws ArithmeticException if the result is inexact but the rounding
     * mode is {@code UNNECESSARY}; if the specified exponent range to round
     * to is not allowed by the underlying MPFR library; or if the precision
     * is too high to be able to emulate subnormal arithmetic for the specified
     * exponent range.
     * @throws IllegalArgumentException if the specified rounding mode is not supported
     * (i.e. HALF_UP or HALF_DOWN), or if the precision is less than 2
     */
    public BigFloat sinh(BinaryMathContext mc) {
        return new Operation() {
            
            @Override
            public int doIt(mpfr_t rop, int rnd) {
                return mpfr_sinh(rop, op, rnd);
            }
        }.execute(mc);
    }
    
    /**
     * Returns the hyperbolic cosine of a {@code BigFloat} value, with rounding
     * according to the context settings. The hyperbolic
     * cosine of <i>x</i> is defined to be 
     * (<i>e<sup>x</sup>&nbsp;+&nbsp;e<sup>-x</sup></i>)/2
     * where <i>e</i> is {@linkplain #e(BinaryMathContext) Euler's number}.
     * Special cases:
     * <ul>
     * <li>If the argument is NaN, then the result is NaN.
     *
     * <li>If the argument is infinite, then the result is positive infinity.
     *
     * <li>If the argument is zero, then the result is {@code 1.0}.
     *
     * </ul>
     * 
     * @param mc the context to use.
     * @return {@code cosh(this)}, rounded as necessary.
     * @throws ArithmeticException if the result is inexact but the rounding
     * mode is {@code UNNECESSARY}; if the specified exponent range to round
     * to is not allowed by the underlying MPFR library; or if the precision
     * is too high to be able to emulate subnormal arithmetic for the specified
     * exponent range.
     * @throws IllegalArgumentException if the specified rounding mode is not supported
     * (i.e. HALF_UP or HALF_DOWN), or if the precision is less than 2
     */
    public BigFloat cosh(BinaryMathContext mc) {
        return new Operation() {
            
            @Override
            public int doIt(mpfr_t rop, int rnd) {
                return mpfr_cosh(rop, op, rnd);
            }
        }.execute(mc);
    }
    
    /**
     * Returns the hyperbolic tangent of a {@code BigFloat} value, with rounding
     * according to the context settings. The hyperbolic
     * tangent of <i>x</i> is defined to be 
     * (<i>e<sup>x</sup>&nbsp;-&nbsp;e<sup>-x</sup></i>)/(<i>e<sup>x</sup>&nbsp;+&nbsp;e<sup>-x</sup></i>),
     * in other words, {@linkplain #sinh sinh(<i>x</i>)}/{@linkplain #cosh cosh(<i>x</i>)}.
     * Note that the absolute value of the exact tanh is always less than 1.
     * Special cases:
     * <ul>
     *
     * <li>If the argument is NaN, then the result is NaN.
     *
     * <li>If the argument is zero, then the result is a zero with the
     * same sign as the argument.
     *
     * <li>If the argument is positive infinity, then the result is
     * {@code +1.0}.
     *
     * <li>If the argument is negative infinity, then the result is
     * {@code -1.0}.
     *
     * </ul>
     * 
     * @param mc the context to use.
     * @return {@code tanh(this)}, rounded as necessary.
     * @throws ArithmeticException if the result is inexact but the rounding
     * mode is {@code UNNECESSARY}; if the specified exponent range to round
     * to is not allowed by the underlying MPFR library; or if the precision
     * is too high to be able to emulate subnormal arithmetic for the specified
     * exponent range.
     * @throws IllegalArgumentException if the specified rounding mode is not supported
     * (i.e. HALF_UP or HALF_DOWN), or if the precision is less than 2
     */
    public BigFloat tanh(BinaryMathContext mc) {
        return new Operation() {
            
            @Override
            public int doIt(mpfr_t rop, int rnd) {
                return mpfr_tanh(rop, op, rnd);
            }
        }.execute(mc);
    }
    
    /**
     * Returns the hyperbolic secant of a {@code BigFloat} value, with rounding
     * according to the context settings. The hyperbolic
     * secant of <i>x</i> is defined to be 
     * 2/(<i>e<sup>x</sup>&nbsp;+&nbsp;e<sup>-x</sup></i>),
     * in other words, 1/{@linkplain #cosh cosh(<i>x</i>)}.
     * Special cases:
     * <ul>
     * <li>If the argument is NaN, then the result is NaN.
     *
     * <li>If the argument is infinite, then the result is positive zero.
     *
     * <li>If the argument is zero, then the result is {@code 1.0}.
     *
     * </ul>
     * 
     * @param mc the context to use.
     * @return {@code sech(this)}, rounded as necessary.
     * @throws ArithmeticException if the result is inexact but the rounding
     * mode is {@code UNNECESSARY}; if the specified exponent range to round
     * to is not allowed by the underlying MPFR library; or if the precision
     * is too high to be able to emulate subnormal arithmetic for the specified
     * exponent range.
     * @throws IllegalArgumentException if the specified rounding mode is not supported
     * (i.e. HALF_UP or HALF_DOWN), or if the precision is less than 2
     */
    public BigFloat sech(BinaryMathContext mc) {
        return new Operation() {
            
            @Override
            public int doIt(mpfr_t rop, int rnd) {
                return mpfr_sech(rop, op, rnd);
            }
        }.execute(mc);
    }
    
    /**
     * Returns the hyperbolic cosecant of a {@code BigFloat} value, with rounding
     * according to the context settings. The hyperbolic
     * cosecant of <i>x</i> is defined to be 
     * 2/(<i>e<sup>x</sup>&nbsp;-&nbsp;e<sup>-x</sup></i>),
     * in other words, 1/{@linkplain #sinh sinh(<i>x</i>)}.
     * Special cases:
     * <ul>
     * <li>If the argument is NaN, then the result is NaN.
     *
     * <li>If the argument is infinite, then the result is a zero
     * with the same sign as the argument.
     *
     * <li>If the argument is zero, then the result is an infinity with the
     * same sign as the argument.
     *
     * </ul>
     * 
     * @param mc the context to use.
     * @return {@code csch(this)}, rounded as necessary.
     * @throws ArithmeticException if the result is inexact but the rounding
     * mode is {@code UNNECESSARY}; if the specified exponent range to round
     * to is not allowed by the underlying MPFR library; or if the precision
     * is too high to be able to emulate subnormal arithmetic for the specified
     * exponent range.
     * @throws IllegalArgumentException if the specified rounding mode is not supported
     * (i.e. HALF_UP or HALF_DOWN), or if the precision is less than 2
     */
    public BigFloat csch(BinaryMathContext mc) {
        return new Operation() {
            
            @Override
            public int doIt(mpfr_t rop, int rnd) {
                return mpfr_csch(rop, op, rnd);
            }
        }.execute(mc);
    }
    
    /**
     * Returns the hyperbolic cotangent of a {@code BigFloat} value, with rounding
     * according to the context settings. The hyperbolic
     * cotangent of <i>x</i> is defined to be 
     * (<i>e<sup>x</sup>&nbsp;+&nbsp;e<sup>-x</sup></i>)/(<i>e<sup>x</sup>&nbsp;-&nbsp;e<sup>-x</sup></i>),
     * in other words, {@linkplain #cosh cosh(<i>x</i>)}/{@linkplain #sinh sinh(<i>x</i>)}.
     * Note that the absolute value of the exact tanh is always greater than 1.
     * Special cases:
     * <ul>
     *
     * <li>If the argument is NaN, then the result is NaN.
     *
     * <li>If the argument is zero, then the result is an infinity with the
     * same sign as the argument.
     *
     * <li>If the argument is positive infinity, then the result is
     * {@code +1.0}.
     *
     * <li>If the argument is negative infinity, then the result is
     * {@code -1.0}.
     *
     * </ul>
     * 
     * @param mc the context to use.
     * @return {@code coth(this)}, rounded as necessary.
     * @throws ArithmeticException if the result is inexact but the rounding
     * mode is {@code UNNECESSARY}; if the specified exponent range to round
     * to is not allowed by the underlying MPFR library; or if the precision
     * is too high to be able to emulate subnormal arithmetic for the specified
     * exponent range.
     * @throws IllegalArgumentException if the specified rounding mode is not supported
     * (i.e. HALF_UP or HALF_DOWN), or if the precision is less than 2
     */
    public BigFloat coth(BinaryMathContext mc) {
        return new Operation() {
            
            @Override
            public int doIt(mpfr_t rop, int rnd) {
                return mpfr_coth(rop, op, rnd);
            }
        }.execute(mc);
    }
    
    /**
     * Returns the hyperbolic arc sine of a {@code BigFloat} value, with rounding
     * according to the context settings. The hyperbolic
     * arc sine of <x> is defined to be 
     * ln(x+sqrt(x<sup>2</sup>+1), where ln is the natural logarithm.
     * Special cases:
     * <ul>
     * <li>If the argument is NaN, then the result is NaN.
     *
     * <li>If the argument is infinite, then the result is an infinity
     * with the same sign as the argument.
     *
     * <li>If the argument is zero, then the result is a zero with the
     * same sign as the argument.
     *
     * </ul>
     * 
     * @param mc the context to use.
     * @return {@code asinh(this)}, rounded as necessary.
     * @throws ArithmeticException if the result is inexact but the rounding
     * mode is {@code UNNECESSARY}; if the specified exponent range to round
     * to is not allowed by the underlying MPFR library; or if the precision
     * is too high to be able to emulate subnormal arithmetic for the specified
     * exponent range.
     * @throws IllegalArgumentException if the specified rounding mode is not supported
     * (i.e. HALF_UP or HALF_DOWN), or if the precision is less than 2
     */
    public BigFloat asinh(BinaryMathContext mc) {
        return new Operation() {
            
            @Override
            public int doIt(mpfr_t rop, int rnd) {
                return mpfr_asinh(rop, op, rnd);
            }
        }.execute(mc);
    }
    
    /**
     * Returns the hyperbolic arc cosine of a {@code BigFloat} value, with rounding
     * according to the context settings. The hyperbolic
     * arc cosine of <x> is defined to be 
     * ln(x+sqrt(x<sup>2</sup>-1), where ln is the natural logarithm. The hyperbolic
     * arc cosine is not defined for x < 1.
     * Special cases:
     * <ul>
     * <li>If the argument is NaN, or the argument is less than 1.0, then the result is NaN.
     *
     * <li>If the argument is positive infinity, then the result is positive infinity.
     *
     * <li>If the argument is 1.0, then the result is positive zero.
     *
     * </ul>
     * 
     * @param mc the context to use.
     * @return {@code acosh(this)}, rounded as necessary.
     * @throws ArithmeticException if the result is inexact but the rounding
     * mode is {@code UNNECESSARY}; if the specified exponent range to round
     * to is not allowed by the underlying MPFR library; or if the precision
     * is too high to be able to emulate subnormal arithmetic for the specified
     * exponent range.
     * @throws IllegalArgumentException if the specified rounding mode is not supported
     * (i.e. HALF_UP or HALF_DOWN), or if the precision is less than 2
     */
    public BigFloat acosh(BinaryMathContext mc) {
        return new Operation() {
            
            @Override
            public int doIt(mpfr_t rop, int rnd) {
                return mpfr_acosh(rop, op, rnd);
            }
        }.execute(mc);
    }
    
    /**
     * Returns the hyperbolic arc tangent of a {@code BigFloat} value, with rounding
     * according to the context settings. The hyperbolic
     * arc tangent of <i>x</i> is defined to be 
     * ln((1+x)/(1-x))/2, where ln is the natural logarithm. The hyperbolic
     * arc tangent is not defined for |x| > 1.
     * Special cases:
     * <ul>
     *
     * <li>If the argument is NaN or the absolute value of the argument is
     * greater than one, then the result is NaN.
     *
     * <li>If the argument is zero, then the result is a zero with the
     * same sign as the argument.
     *
     * <li>If the argument is 1.0, then the result is positive infinity.
     *
     * <li>If the argument is -1.0, then the result is negative infinity.
     *
     * </ul>
     * 
     * @param mc the context to use.
     * @return {@code atanh(this)}, rounded as necessary.
     * @throws ArithmeticException if the result is inexact but the rounding
     * mode is {@code UNNECESSARY}; if the specified exponent range to round
     * to is not allowed by the underlying MPFR library; or if the precision
     * is too high to be able to emulate subnormal arithmetic for the specified
     * exponent range.
     * @throws IllegalArgumentException if the specified rounding mode is not supported
     * (i.e. HALF_UP or HALF_DOWN), or if the precision is less than 2
     */
    public BigFloat atanh(BinaryMathContext mc) {
        return new Operation() {
            
            @Override
            public int doIt(mpfr_t rop, int rnd) {
                return mpfr_atanh(rop, op, rnd);
            }
        }.execute(mc);
    }
    
    /**
     * Returns Euler's number <i>e</i> raised to the power of a
     * {@code BigFloat} value, with rounding
     * according to the context settings. Special cases:
     * <ul><li>If the argument is NaN, then the result is NaN.
     * <li>If the argument is positive infinity, then the result is
     * positive infinity.
     * <li>If the argument is negative infinity, then the result is
     * positive zero.</ul>
     * 
     * @param mc the context to use.
     * @return e<sup>{@code this}</sup>, rounded as necessary.
     * @throws ArithmeticException if the result is inexact but the rounding
     * mode is {@code UNNECESSARY}; if the specified exponent range to round
     * to is not allowed by the underlying MPFR library; or if the precision
     * is too high to be able to emulate subnormal arithmetic for the specified
     * exponent range.
     * @throws IllegalArgumentException if the specified rounding mode is not supported
     * (i.e. HALF_UP or HALF_DOWN), or if the precision is less than 2
     */
    public BigFloat exp(BinaryMathContext mc) {
        return new Operation() {
            
            @Override
            public int doIt(mpfr_t rop, int rnd) {
                return mpfr_exp(rop, op, rnd);
            }
        }.execute(mc);
    }
    
    /**
     * Returns the natural logarithm (base <i>e</i>) of a
     * {@code BigFloat} value, with rounding
     * according to the context settings. Special cases:
     * <ul><li>If the argument is NaN or less than zero, then the result is
     * NaN.
     * <li>If the argument is positive infinity, then the result is
     * positive infinity.
     * <li>If the argument is positive zero or negative zero, then the result
     * is negative infinity.</ul>
     * 
     * @param mc the context to use.
     * @return ln({@code this}), rounded as necessary.
     * @throws ArithmeticException if the result is inexact but the rounding
     * mode is {@code UNNECESSARY}; if the specified exponent range to round
     * to is not allowed by the underlying MPFR library; or if the precision
     * is too high to be able to emulate subnormal arithmetic for the specified
     * exponent range.
     * @throws IllegalArgumentException if the specified rounding mode is not supported
     * (i.e. HALF_UP or HALF_DOWN), or if the precision is less than 2
     */
    public BigFloat log(BinaryMathContext mc) {
        return new Operation() {
            
            @Override
            public int doIt(mpfr_t rop, int rnd) {
                return mpfr_log(rop, op, rnd);
            }
        }.execute(mc);
    }
       
    /**
     * Returns the {@code BigFloat} value which represents {@code this}
     * rounded to an integer according to context settings. Special cases:
     * <ul><li>If the argument value is already equal to a mathematical
     * integer, then the result is the same as the argument.
     * <li>If the argument is NaN or an infinity or positive zero or negative
     * zero, then the result is the same as the argument.</ul>
     * 
     * @param mc the context to use.
     * @return the closest floating-point value to {@code this} that is
     * equal to a mathematical integer, as determined by the specified
     * precision, exponent range, and rounding mode.
     * @throws ArithmeticException if the result is inexact but the rounding
     * mode is {@code UNNECESSARY}; if the specified exponent range to round
     * to is not allowed by the underlying MPFR library; or if the precision
     * is too high to be able to emulate subnormal arithmetic for the specified
     * exponent range.
     * @throws IllegalArgumentException if the specified rounding mode is not supported
     * (i.e. HALF_UP or HALF_DOWN), or if the precision is less than 2
     */
    public BigFloat rint(BinaryMathContext mc) {
        return new Operation() {
            
            @Override
            public int doIt(mpfr_t rop, int rnd) {
                return mpfr_rint(rop, op, rnd);
            }
        }.execute(mc);
    }
    
    /**
     * Returns the greater of two {@code BigFloat} values, in the precision
     * specified by context settings. That is, the result is the argument
     * closer to positive infinity. If the arguments have the same value,
     * the result is that same value rounded to the specified precision. If one
     * argument is NaN but the other is numerical, the result is the numerical
     * value. If both arguments are NaN, then the result is NaN. If one
     * argument is positive zero and the other is negative zero, the result is positive
     * zero.
     * @param a an argument.
     * @param b another argument.
     * @param mc the context to use.
     * @return the larger of {@code a} and {@code b}, rounded using the
     * specified precision, exponent range, and rounding mode.
     * @throws ArithmeticException if the result is inexact but the rounding
     * mode is {@code UNNECESSARY}; if the specified exponent range to round
     * to is not allowed by the underlying MPFR library; or if the precision
     * is too high to be able to emulate subnormal arithmetic for the specified
     * exponent range.
     * @throws IllegalArgumentException if the specified rounding mode is not supported
     * (i.e. HALF_UP or HALF_DOWN), or if the precision is less than 2
     */
    public static BigFloat max(final BigFloat a, final BigFloat b, BinaryMathContext mc) {
        return new Operation() {
            
            @Override
            public int doIt(mpfr_t rop, int rnd) {
                return mpfr_max(rop, a.op, b.op, rnd);
            }
        }.execute(mc);
    }
    
    /**
     * Returns the smaller of two {@code BigFloat} values, in the precision
     * specified by context settings. That is, the result is the argument
     * closer to negative infinity. If the arguments have the same value,
     * the result is that same value rounded to the specified precision. If one
     * argument is NaN but the other is numerical, the result is the numerical
     * value. If both arguments are NaN, then the result is NaN. If one
     * argument is positive zero and the other is negative zero, the result is negative
     * zero.
     * @param a an argument.
     * @param b another argument.
     * @param mc the context to use.
     * @return the smaller of {@code a} and {@code b}, rounded using the
     * specified precision, exponent range, and rounding mode.
     * @throws ArithmeticException if the result is inexact but the rounding
     * mode is {@code UNNECESSARY}; if the specified exponent range to round
     * to is not allowed by the underlying MPFR library; or if the precision
     * is too high to be able to emulate subnormal arithmetic for the specified
     * exponent range.
     * @throws IllegalArgumentException if the specified rounding mode is not supported
     * (i.e. HALF_UP or HALF_DOWN), or if the precision is less than 2
     */
    public static BigFloat min(final BigFloat a, final BigFloat b, BinaryMathContext mc) {
        return new Operation() {
            
            @Override
            public int doIt(mpfr_t rop, int rnd) {
                return mpfr_min(rop, a.op, b.op, rnd);
            }
        }.execute(mc);
    }
    
    /**
     * Returns the {@code BigFloat} number adjacent to the first
     * argument in the direction of the second argument, in the
     * specified precision and exponent range.  If both
     * arguments compare as equal the second argument is returned.
     *
     * <p>
     * Special cases:
     * <ul>
     * <li> If either argument is a NaN, then NaN is returned.
     *
     * <li> If both arguments are signed zeros, {@code direction}
     * is returned unchanged (as implied by the requirement of
     * returning the second argument if the arguments compare as
     * equal).
     *
     * <li> If {@code this} is
     * &plusmn;{@link #minValue(int, long)} and {@code direction}
     * has a value such that the result should have a smaller
     * magnitude, then a zero with the same sign as {@code this}
     * is returned.
     *
     * <li> If {@code this} is infinite and
     * {@code direction} has a value such that the result should
     * have a smaller magnitude, {@link #maxValue(int, long)} with the
     * same sign as {@code this} is returned.
     *
     * <li> If {@code this} is equal to &plusmn;
     * {@link #maxValue(int, long)} and {@code direction} has a
     * value such that the result should have a larger magnitude, an
     * infinity with same sign as {@code this} is returned.
     * </ul>
     *
     * @param direction value indicating which of
     * {@code this}'s neighbors or {@code this} should
     * be returned
     * @param minExponent the minimum exponent in the exponent range
     * to compute the adjacent number in.
     * @param maxExponent the maximum exponent in the exponent range
     * to compute the adjacent number in.
     * @return The floating-point number adjacent to {@code this} in the
     * direction of {@code direction}.
     */
    public BigFloat nextAfter(BigFloat direction, long minExponent, long maxExponent) {
        if(equalTo(direction)) {
            return direction;
        }
        if (lessThan(direction)) {
            return nextUp(minExponent, maxExponent);
        }
        if (greaterThan(direction)) {
            return nextDown(minExponent, maxExponent);
        }
        if (direction.isNaN()) {
            return direction;
        }
        assert isNaN() : "this should only be reachable if this is NaN";
        return this;
    }
    
    /**
     * Returns the {@code BigFloat} number adjacent to {@code this} in the
     * direction of positive infinity, in the specified precision and 
     * exponent range. This method is semantically equivalent to 
     * {@code nextAfter(positiveInfinity(precision()), minExponent, maxExponent)};
     * however, a {@code nextUp} implementation may run faster than its
     * equivalent {@code nextAfter} call.
     *
     * <p>
     * Special cases:
     * <ul>
     * <li> If the argument is NaN, then NaN is returned.
     *
     * <li> If the argument is positive infinity, the result is positive
     * infinity.
     * 
     * <li> If the argument is zero, the result is {@link #minValue(int, long)}.
     * 
     * </ul>
     *
     * @param minExponent the minimum exponent in the exponent range
     * to compute the adjacent number in.
     * @param maxExponent the maximum exponent in the exponent range
     * to compute the adjacent number in.
     * @return The adjacent floating-point value closer to positive infinity.
     */
    public BigFloat nextUp(long minExponent, long maxExponent) {
        mpfr_t rop = new mpfr_t(precision());
        int ternary = mpfr_set(rop, op, MPFR_RNDN);
        try {
            setExponentRange(minExponent, maxExponent, precision());
            ternary = mpfr_check_range(rop, ternary, MPFR_RNDN);
            if (ternary != 0) {
                //cannot represent this BigFloat in the provided exponent range
                throw new ArithmeticException("the specified BigFloat is not in the provided exponent range");
            }
            mpfr_nextabove(rop);
            mpfr_subnormalize(rop, 0, MPFR_RNDN);
            return new BigFloat(rop);
        } finally {
            resetExponentRange();
        }
    }
    
    /**positive
     * Returns the {@code BigFloat} number adjacent to {@code this} in the
     * direction of negative infinity, in the specified precision and 
     * exponent range. This method is semantically equivalent to 
     * {@code nextAfter(negativeInfinity(precision()), minExponent, maxExponent)};
     * however, a {@code nextDown} implementation may run faster than its
     * equivalent {@code nextAfter} call.
     *
     * <p>
     * Special cases:
     * <ul>
     * <li> If the argument is NaN, then NaN is returned.
     *
     * <li> If the argument is negative infinity, the result is negative
     * infinity.
     * 
     * <li> If the argument is zero, the result is -{@link #minValue(int, long)}.
     * 
     * </ul>
     *
     * @param minExponent the minimum exponent in the exponent range
     * to compute the adjacent number in.
     * @param maxExponent the maximum exponent in the exponent range
     * to compute the adjacent number in.
     * @return The adjacent floating-point value closer to negative infinity.
     */
    public BigFloat nextDown(long minExponent, long maxExponent) {
        mpfr_t rop = new mpfr_t(precision());
        int ternary = mpfr_set(rop, op, MPFR_RNDN);
        try {
            setExponentRange(minExponent, maxExponent, precision());
            ternary = mpfr_check_range(rop, ternary, MPFR_RNDN);
            if (ternary != 0) {
                //cannot represent this BigFloat in the provided exponent range
                throw new ArithmeticException("the specified BigFloat is not in the provided exponent range");
            }
            mpfr_nextbelow(rop);
            mpfr_subnormalize(rop, 0, MPFR_RNDN);
            return new BigFloat(rop);
        } finally {
            resetExponentRange();
        }
    }
}
