// Copyright (c) 2014 K Team. All Rights Reserved.
package org.kframework.mpfr;

import java.math.BigInteger;
import java.math.RoundingMode;

import org.kframework.mpfr.mpfr.__mpfr_struct;

import static org.kframework.mpfr.mpfr.*;

public class BigFloat extends Number implements Comparable<BigFloat> {
    
    public static BigFloat maxValue(int precision, long emax) {
        return positiveInfinity(precision).nextDown(-1, emax);
    }
    
    public static BigFloat minNormal(int precision, long emin) {
        return zero(precision).nextUp(emin + precision - 1, 1);
    }
    
    public static BigFloat minValue(int precision, long emin) {
        return zero(precision).nextUp(emin, 1);
    }
    
    /**
     * The value 0, with the specified precision.
     */
    public static BigFloat zero(int precision) {
        __mpfr_struct op = new __mpfr_struct(precision);
        mpfr_set_zero(op, 1);
        return new BigFloat(op);
    }
    
    /**
     * The value -0, with the specified precision.
     */
    public static BigFloat negativeZero(int precision) {
        __mpfr_struct op = new __mpfr_struct(precision);
        mpfr_set_zero(op, -1);
        return new BigFloat(op);
    }
    
    /**
     * The value NaN, with the specified precision.
     */
    public static BigFloat NaN(int precision) {
        // init2 by default initializes to NaN
        return new BigFloat(new __mpfr_struct(precision));
    }
    
    /**
     * The value +Infinity, with the specified precision.
     */
    public static BigFloat positiveInfinity(int precision) {
        __mpfr_struct op = new __mpfr_struct(precision);
        mpfr_set_inf(op, 1);
        return new BigFloat(op);
    }
    
    /**
     * The value -Infinity, with the specified precision.
     */
    public static BigFloat negativeInfinity(int precision) {
        __mpfr_struct op = new __mpfr_struct(precision);
        mpfr_set_inf(op, -1);
        return new BigFloat(op);
    }
    
    /**
     * The value Pi, with rounding according to the context settings.
     */
    public static BigFloat pi(BinaryMathContext mc) {
        return new Operation() {
            
            @Override
            public int doIt(__mpfr_struct rop, int rnd) {
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
    
    private __mpfr_struct op;

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
        op = new __mpfr_struct(mc.getPrecision());
        int ternary = mpfr_set_str(op, in, 0, convertRoundingMode(mc.getRoundingMode()));
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
     * @param val String representation of {@code BigFloat}.
     * @param mc the context to use.
     * @throws ArithmeticException if the result is inexact but the rounding mode is
     * {@code UNNECESSARY}.
     * @throws IllegalArgumentException if the specified rounding mode is not supported
     * (i.e. HALF_UP or HALF_DOWN), or if the precision is less than 2
     * @throws NumberFormatException if {@code val} is not a valid representation of a
     * {@code BigFloat}.
     */
    public BigFloat(String in, BinaryMathContext mc) {
        op = new __mpfr_struct(mc.getPrecision());
        int ternary = mpfr_set_str(op, in, 0, convertRoundingMode(mc.getRoundingMode()));
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
        op = new __mpfr_struct(mc.getPrecision());
        int ternary = mpfr_set_d(op, val, convertRoundingMode(mc.getRoundingMode()));
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
        op = new __mpfr_struct(mc.getPrecision());
        int ternary = mpfr_set_z(op, new __mpz_struct(val), convertRoundingMode(mc.getRoundingMode()));
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
    
    private BigFloat(__mpfr_struct op) {
        this.op = op;
    }
    
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
     * This conversion is similar to the <i>narrowing primitive conversion</i> from 
     * {@code double} to {@code float} as defined in section 5.1.3 of <i>The Java™
     * Language Specification</i>: if this {@code BigFloat} has too great a magnitude
     * to represent as a {@code double}, it will be converted to 
     * {@link Double.NEGATIVE_INFINITY} or {@link Double.POSITIVE_INFINITY} as appropriate.
     * Note that even when the return value is finite, this conversion can lose information
     * about the precision of the {@code BigFloat} value. However, no precision is lost if the
     * precision of this {@code BigFloat} is less than or equal to the precision of a 
     * {@code double}.
     * @return this {@code BigFloat} converted to a {@code double}.
     */
    @Override
    public double doubleValue() {
        return mpfr_get_d(op, MPFR_RNDN);
    }
    
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
     * {@link Float.NEGATIVE_INFINITY} or {@link Float.POSITIVE_INFINITY} as appropriate.
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
    

    public float floatValueExact() {
        float f = floatValue();
        if (mpfr_cmp_d(op, f) != 0) {
            throw new ArithmeticException("Rounding necessary");
        }
        return f;
    }

    /**
     * Converts this {@code BigFloat} to an {@code int}. 
     * This conversion is analogous to the <i>narrowing primitive conversion</i> from 
     * {@code double} to {@code short} as defined in section 5.1.3 of <i>The Java™
     * Language Specification</i>: any fractional part of this {@code BigFloat} will be
     * discarded, and if the resulting "{@code BigInteger}" is too big to fit in an {@code int},
     * only the low-order 32 bits are returned. Note that this conversion can lose information 
     * about the overall magnitude and precision of this {@BigFloat} value as well as return
     * a result with the opposite sign.
     * 
     * Per the section on narrowing primitive conversions, if this {@code BigFloat} is
     * either {@link BigFloat.POSITIVE_INFINITY} or {@link BigFloat.NEGATIVE_INFINITY},
     * this method returns {@link Integer.MAX_VALUE} or {@link Integer.MIN_VALUE} respectively.
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
     * This conversion is analogous to the <i>narrowing primitive conversion</i> from 
     * {@code double} to {@code long} as defined in section 5.1.3 of <i>The Java™
     * Language Specification</i>: any fractional part of this {@code BigFloat} will be
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
     * @return this {@code BigFloat} converted to a {@code BigInteger}.
     */
    public BigInteger toBigInteger() {
        if (mpfr_nan_p(op) || mpfr_inf_p(op)) {
            return BigInteger.ZERO;
        }
        return toBigIntegerInternal();
    }
    
    public BigInteger toBigIntegerExact() {
        if (mpfr_nan_p(op) || mpfr_inf_p(op) || !mpfr_integer_p(op)) {
            throw new ArithmeticException("Rounding necessary");
        }
        return toBigIntegerInternal();
    }

    private BigInteger toBigIntegerInternal() {
        __mpz_struct rop = new __mpz_struct();
        mpfr_get_z(rop, op, MPFR_RNDZ);
        return new BigInteger(mpz_get_str(10, rop));
    }
    
    /**
     * Converts this {@code BigFloat} to a {@code long}. 
     * This conversion is analogous to the <i>narrowing primitive conversion</i> from 
     * {@code double} to {@code short} as defined in section 5.1.3 of <i>The Java™
     * Language Specification</i>: any fractional part of this {@code BigFloat} will be
     * discarded, and if the resulting "{@code BigInteger}" is too big to fit in a {@code long},
     * only the low-order 64 bits are returned. Note that this conversion can lose information 
     * about the overall magnitude and precision of this {@BigFloat} value as well as return
     * a result with the opposite sign.
     * 
     * Per the section on narrowing primitive conversions, if this {@code BigFloat} is
     * either {@link BigFloat.POSITIVE_INFINITY} or {@link BigFloat.NEGATIVE_INFINITY},
     * this method returns {@link Long.MAX_VALUE} or {@link Long.MIN_VALUE} respectively.
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
    
    public long longValueExact() {
        if (mpfr_nan_p(op) || mpfr_inf_p(op) || !mpfr_integer_p(op)
                || mpfr_greater_p(op, longMaxValue.op)
                || mpfr_less_p(op, longMinValue.op)) {
            throw new ArithmeticException("Rounding necessary");
        }
        return longValue();
    }
    
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
    
    public short shortValueExact() {
        if (mpfr_nan_p(op) || mpfr_inf_p(op) || !mpfr_integer_p(op)
                || mpfr_greater_p(op, shortMaxValue.op)
                || mpfr_less_p(op, shortMinValue.op)) {
            throw new ArithmeticException("Rounding necessary");
        }
        return shortValue();
    }
    
    public int precision() {
        return op._mpfr_prec;
    }
    
    public long exponent() {
        if (isNaN() || isInfinite() || isPositiveZero() || isNegativeZero()) {
            throw new ArithmeticException("IEEE exponent would depend on exponent range");
        }
        return op._mpfr_exp - 1;
    }
    
    public boolean isNaN() {
        return mpfr_nan_p(op);
    }
    
    public boolean isInfinite() {
        return mpfr_inf_p(op);
    }
    
    public boolean isPositiveZero() {
        return mpfr_zero_p(op) && !mpfr_signbit(op);
    }
    
    public boolean isNegativeZero() {
        return mpfr_zero_p(op) && mpfr_signbit(op);
    }
    
    @Override
    public String toString() {
        return toString("%Re");
    }
    
    public String toString(String format) {
        return mpfr_asprintf(format, op);
    }
    

    public static BigFloat valueOf(long val, BinaryMathContext mc) {
        return new BigFloat(val, mc);
    }
    public static BigFloat valueOf(double val, BinaryMathContext mc) {
        return new BigFloat(val, mc);
    }
    
    public boolean lt(BigFloat val) {
        return mpfr_less_p(op, val.op);
    }
    
    public boolean gt(BigFloat val) {
        return mpfr_greater_p(op, val.op);
    }
    
    public boolean le(BigFloat val) {
        return mpfr_lessequal_p(op, val.op);
    }
    
    public boolean ge(BigFloat val) {
        return mpfr_greaterequal_p(op, val.op);
    }
    
    public boolean eq(BigFloat val) {
        return mpfr_equal_p(op, val.op);
    }
    
    public boolean ne(BigFloat val) {
        return !eq(val);
    }

    @Override
    public int compareTo(BigFloat val) {
        if (this.lt(val)) {
            return -1;
        }
        if (this.gt(val)) {
            return 1;
        }
        if (this.isNegativeZero() && val.isPositiveZero()) {
            return -1;
        }
        if (!this.isNaN() && val.isNaN()) {
            return -1;
        }
        if (this.isPositiveZero() && val.isNegativeZero()) {
            return 1;
        }
        if (this.isNaN() && !val.isNaN()) {
            return 1;
        }
        if (this.isNaN() && val.isNaN()) {
            return 0;
        }
        int res = mpfr_cmp(op, val.op);
        if (res != 0) {
            return res;
        }
        return Integer.compare(this.precision(), val.precision());
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (obj == this) return true;
        if (obj.getClass() != this.getClass()) return false;
        
        BigFloat other = (BigFloat)obj;
        return compareTo(other) == 0 && op._mpfr_prec == other.op._mpfr_prec;
    }
    
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
        if (rounded && mc.getRoundingMode() == RoundingMode.UNNECESSARY) {
            throw new ArithmeticException("rounding necessary");
        }
    }

    //TODO(dwightguth): fix with Java 8 and lambdas
    private static abstract class Operation {
        public abstract int doIt(__mpfr_struct rop, int rnd);
        
        public BigFloat execute(BinaryMathContext mc) {
            __mpfr_struct rop = new __mpfr_struct(mc.getPrecision());
            int ternary = doIt(rop, convertRoundingMode(mc.getRoundingMode()));
            boolean rounded = roundExponent(ternary, rop, mc);
            throwArithmeticException(rounded, mc);
            return new BigFloat(rop);
        }
    }
    
    static boolean roundExponent(int ternary, __mpfr_struct x, BinaryMathContext mc) {
        try {
            setExponentRange(mc.getMinExponent(), mc.getMaxExponent(), mc.getPrecision());
            ternary = mpfr_check_range(x, ternary, convertRoundingMode(mc.getRoundingMode()));
            boolean rounded = mpfr_subnormalize(x, ternary, convertRoundingMode(mc.getRoundingMode()));
            return rounded;
        } finally {
            resetExponentRange();
        }
    }

    private static void resetExponentRange() {
        boolean failed = mpfr_set_emin(MPFR_EMIN_DEFAULT);
        failed |= mpfr_set_emax(MPFR_EMAX_DEFAULT);
        assert !failed : "unexpected failure resetting exponent range";
    }

    private static void setExponentRange(long emin, long emax, int precision) {
        if (emin < MPFR_EMIN_DEFAULT || emax > MPFR_EMAX_DEFAULT) {
            throw new ArithmeticException("invalid exponent range");
        }
        emin = emin - precision + 2;
        emax = emax + 1;
        boolean failed = mpfr_set_emin(emin);
        failed |= mpfr_set_emax(emax);
        if (failed) {
            resetExponentRange();
            throw new ArithmeticException("invalid exponent range for specified precision");
        }
    }
    
    public BigFloat add(final BigFloat augend, BinaryMathContext mc) {
        return new Operation() {
            
            @Override
            public int doIt(__mpfr_struct rop, int rnd) {
                return mpfr_add(rop, op, augend.op, rnd);
            }
        }.execute(mc);
    }
    
    public BigFloat subtract(final BigFloat subtrahend, BinaryMathContext mc) {
        return new Operation() {
            
            @Override
            public int doIt(__mpfr_struct rop, int rnd) {
                return mpfr_sub(rop, op, subtrahend.op, rnd);
            }
        }.execute(mc);
    }
    
    public BigFloat multiply(final BigFloat multiplicand, BinaryMathContext mc) {
        return new Operation() {
            
            @Override
            public int doIt(__mpfr_struct rop, int rnd) {
                return mpfr_mul(rop, op, multiplicand.op, rnd);
            }
        }.execute(mc);
    }
    
    public BigFloat divide(final BigFloat divisor, BinaryMathContext mc) {
        return new Operation() {
            
            @Override
            public int doIt(__mpfr_struct rop, int rnd) {
                return mpfr_div(rop, op, divisor.op, rnd);
            }
        }.execute(mc);
    }

    public BigFloat remainder(final BigFloat divisor, BinaryMathContext mc) {
        return new Operation() {
            
            @Override
            public int doIt(__mpfr_struct rop, int rnd) {
                return mpfr_remainder(rop, op, divisor.op, rnd);
            }
        }.execute(mc);
    }

    public BigFloat pow(final BigFloat n, BinaryMathContext mc) {
        return new Operation() {
            
            @Override
            public int doIt(__mpfr_struct rop, int rnd) {
                return mpfr_pow(rop, op, n.op, rnd);
            }
        }.execute(mc);
    }
    
    public BigFloat abs() {
        return abs(new BinaryMathContext(precision(), mpfr.MPFR_EMIN_DEFAULT, 
                mpfr.MPFR_EMAX_DEFAULT, RoundingMode.UNNECESSARY));
    }

    public BigFloat abs(BinaryMathContext mc) {
        return new Operation() {
            
            @Override
            public int doIt(__mpfr_struct rop, int rnd) {
                return mpfr_abs(rop, op, rnd);
            }
        }.execute(mc);
    }
    
    public BigFloat negate() {
        return negate(new BinaryMathContext(precision(), mpfr.MPFR_EMIN_DEFAULT, 
                mpfr.MPFR_EMAX_DEFAULT, RoundingMode.UNNECESSARY));
    }

    public BigFloat negate(BinaryMathContext mc) {
        return new Operation() {
            
            @Override
            public int doIt(__mpfr_struct rop, int rnd) {
                return mpfr_neg(rop, op, rnd);
            }
        }.execute(mc);
    }
    
    public BigFloat plus() {
        return this;
    }
    
    public BigFloat plus(BinaryMathContext mc) {
        return round(mc);
    }
    
    public double signum() {
        if (mpfr_zero_p(op) || mpfr_nan_p(op)) {
            return doubleValueExact();
        }
        if (mpfr_signbit(op)) {
            return -1.0;
        }
        return 1.0;
    }

    public BigFloat round(BinaryMathContext mc) {
        return new Operation() {
            
            @Override
            public int doIt(__mpfr_struct rop, int rnd) {
                return mpfr_set(rop, op, rnd);
            }
        }.execute(mc);
    }
    
    public BigFloat sin(BinaryMathContext mc) {
        return new Operation() {
            
            @Override
            public int doIt(__mpfr_struct rop, int rnd) {
                return mpfr_sin(rop, op, rnd);
            }
        }.execute(mc);
    }
    
    public BigFloat cos(BinaryMathContext mc) {
        return new Operation() {
            
            @Override
            public int doIt(__mpfr_struct rop, int rnd) {
                return mpfr_cos(rop, op, rnd);
            }
        }.execute(mc);
    }
    
    public BigFloat sec(BinaryMathContext mc) {
        return new Operation() {
            
            @Override
            public int doIt(__mpfr_struct rop, int rnd) {
                return mpfr_sec(rop, op, rnd);
            }
        }.execute(mc);
    }
    
    public BigFloat csc(BinaryMathContext mc) {
        return new Operation() {
            
            @Override
            public int doIt(__mpfr_struct rop, int rnd) {
                return mpfr_csc(rop, op, rnd);
            }
        }.execute(mc);
    }
    
    public BigFloat cot(BinaryMathContext mc) {
        return new Operation() {
            
            @Override
            public int doIt(__mpfr_struct rop, int rnd) {
                return mpfr_cot(rop, op, rnd);
            }
        }.execute(mc);
    }
    
    public BigFloat tan(BinaryMathContext mc) {
        return new Operation() {
            
            @Override
            public int doIt(__mpfr_struct rop, int rnd) {
                return mpfr_tan(rop, op, rnd);
            }
        }.execute(mc);
    }
    
    public BigFloat asin(BinaryMathContext mc) {
        return new Operation() {
            
            @Override
            public int doIt(__mpfr_struct rop, int rnd) {
                return mpfr_asin(rop, op, rnd);
            }
        }.execute(mc);
    }
    
    public BigFloat acos(BinaryMathContext mc) {
        return new Operation() {
            
            @Override
            public int doIt(__mpfr_struct rop, int rnd) {
                return mpfr_acos(rop, op, rnd);
            }
        }.execute(mc);
    }
    
    public BigFloat atan(BinaryMathContext mc) {
        return new Operation() {
            
            @Override
            public int doIt(__mpfr_struct rop, int rnd) {
                return mpfr_atan(rop, op, rnd);
            }
        }.execute(mc);
    }
    
    public static BigFloat atan2(final BigFloat y, final BigFloat x, BinaryMathContext mc) {
        return new Operation() {
            
            @Override
            public int doIt(__mpfr_struct rop, int rnd) {
                return mpfr_atan2(rop, y.op, x.op, rnd);
            }
        }.execute(mc);
    }
    
    public BigFloat sinh(BinaryMathContext mc) {
        return new Operation() {
            
            @Override
            public int doIt(__mpfr_struct rop, int rnd) {
                return mpfr_sinh(rop, op, rnd);
            }
        }.execute(mc);
    }
    
    public BigFloat cosh(BinaryMathContext mc) {
        return new Operation() {
            
            @Override
            public int doIt(__mpfr_struct rop, int rnd) {
                return mpfr_cosh(rop, op, rnd);
            }
        }.execute(mc);
    }
    
    public BigFloat tanh(BinaryMathContext mc) {
        return new Operation() {
            
            @Override
            public int doIt(__mpfr_struct rop, int rnd) {
                return mpfr_tanh(rop, op, rnd);
            }
        }.execute(mc);
    }
    
    public BigFloat sech(BinaryMathContext mc) {
        return new Operation() {
            
            @Override
            public int doIt(__mpfr_struct rop, int rnd) {
                return mpfr_sech(rop, op, rnd);
            }
        }.execute(mc);
    }
    
    public BigFloat csch(BinaryMathContext mc) {
        return new Operation() {
            
            @Override
            public int doIt(__mpfr_struct rop, int rnd) {
                return mpfr_csch(rop, op, rnd);
            }
        }.execute(mc);
    }
    
    public BigFloat coth(BinaryMathContext mc) {
        return new Operation() {
            
            @Override
            public int doIt(__mpfr_struct rop, int rnd) {
                return mpfr_coth(rop, op, rnd);
            }
        }.execute(mc);
    }
    
    public BigFloat asinh(BinaryMathContext mc) {
        return new Operation() {
            
            @Override
            public int doIt(__mpfr_struct rop, int rnd) {
                return mpfr_asinh(rop, op, rnd);
            }
        }.execute(mc);
    }
    
    public BigFloat acosh(BinaryMathContext mc) {
        return new Operation() {
            
            @Override
            public int doIt(__mpfr_struct rop, int rnd) {
                return mpfr_acosh(rop, op, rnd);
            }
        }.execute(mc);
    }
    
    public BigFloat atanh(BinaryMathContext mc) {
        return new Operation() {
            
            @Override
            public int doIt(__mpfr_struct rop, int rnd) {
                return mpfr_atanh(rop, op, rnd);
            }
        }.execute(mc);
    }
    
    public BigFloat exp(BinaryMathContext mc) {
        return new Operation() {
            
            @Override
            public int doIt(__mpfr_struct rop, int rnd) {
                return mpfr_exp(rop, op, rnd);
            }
        }.execute(mc);
    }
    
    public BigFloat exp10(BinaryMathContext mc) {
        return new Operation() {
            
            @Override
            public int doIt(__mpfr_struct rop, int rnd) {
                return mpfr_exp10(rop, op, rnd);
            }
        }.execute(mc);
    }
    
    public BigFloat log(BinaryMathContext mc) {
        return new Operation() {
            
            @Override
            public int doIt(__mpfr_struct rop, int rnd) {
                return mpfr_log(rop, op, rnd);
            }
        }.execute(mc);
    }
    
    public BigFloat log10(BinaryMathContext mc) {
        return new Operation() {
            
            @Override
            public int doIt(__mpfr_struct rop, int rnd) {
                return mpfr_log10(rop, op, rnd);
            }
        }.execute(mc);
    }
    
    public BigFloat rint(BinaryMathContext mc) {
        return new Operation() {
            
            @Override
            public int doIt(__mpfr_struct rop, int rnd) {
                return mpfr_rint(rop, op, rnd);
            }
        }.execute(mc);
    }
    
    public BigFloat ceil() {
        return rint(new BinaryMathContext(precision(), mpfr.MPFR_EMIN_DEFAULT, 
                mpfr.MPFR_EMAX_DEFAULT, RoundingMode.CEILING));
    }
    
    public BigFloat floor() {
        return rint(new BinaryMathContext(precision(), mpfr.MPFR_EMIN_DEFAULT, 
                mpfr.MPFR_EMAX_DEFAULT, RoundingMode.FLOOR));
    }
    
    public static BigFloat max(final BigFloat a, final BigFloat b, BinaryMathContext mc) {
        return new Operation() {
            
            @Override
            public int doIt(__mpfr_struct rop, int rnd) {
                return mpfr_max(rop, a.op, b.op, rnd);
            }
        }.execute(mc);
    }
    
    public static BigFloat min(final BigFloat a, final BigFloat b, BinaryMathContext mc) {
        return new Operation() {
            
            @Override
            public int doIt(__mpfr_struct rop, int rnd) {
                return mpfr_min(rop, a.op, b.op, rnd);
            }
        }.execute(mc);
    }
    
    public BigFloat nextAfter(BigFloat direction, long emin, long emax) {
        if(eq(direction)) {
            return direction;
        }
        if (lt(direction)) {
            return nextUp(emin, emax);
        }
        if (gt(direction)) {
            return nextDown(emin, emax);
        }
        if (direction.isNaN()) {
            return direction;
        }
        assert isNaN() : "this should only be reachable if this is NaN";
        return this;
    }
    
    public BigFloat nextUp(long emin, long emax) {
        __mpfr_struct rop = new __mpfr_struct(precision());
        int ternary = mpfr_set(rop, op, MPFR_RNDN);
        try {
            setExponentRange(emin, emax, precision());
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
    
    public BigFloat nextDown(long emin, long emax) {
        __mpfr_struct rop = new __mpfr_struct(precision());
        int ternary = mpfr_set(rop, op, MPFR_RNDN);
        try {
            setExponentRange(emin, emax, precision());
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