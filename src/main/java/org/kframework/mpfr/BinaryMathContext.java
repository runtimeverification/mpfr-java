package org.kframework.mpfr;

import java.io.Serializable;
import java.math.MathContext;
import java.math.RoundingMode;

public final class BinaryMathContext implements Serializable {
    public static final BinaryMathContext BINARY16 = new BinaryMathContext(11, 5);
    public static final BinaryMathContext BINARY32 = new BinaryMathContext(24, 8);
    public static final BinaryMathContext BINARY64 = new BinaryMathContext(53, 11);
    public static final BinaryMathContext BINARY128 = new BinaryMathContext(113, 15);
    
    public BinaryMathContext(int setPrecision, int setExponent) {
        this(setPrecision, setExponent, RoundingMode.HALF_EVEN);
    }
    
    private final int precision;
    private final long emin;
    private final long emax;
    private final RoundingMode roundingMode;
    private final MathContext mathContext;
    
    public BinaryMathContext(int setPrecision, RoundingMode setRoundingMode) {
        this(setPrecision, 30, setRoundingMode);
    }
    
    public BinaryMathContext(int setPrecision, int setExponent, RoundingMode setRoundingMode) {
        if (setPrecision < 0) {
            throw new IllegalArgumentException("Precision < 0");
        }
        if (setExponent < 0 || setExponent > 63) {
            throw new IllegalArgumentException("Exponent range not expressible as a long");
        }
        if (setRoundingMode == null) {
            throw new NullPointerException("null RoundingMode");
        }
        this.precision = setPrecision;
        this.emax = (1 << (setExponent - 1)) - 1;
        this.emin = -emax + 1;
        this.roundingMode = setRoundingMode;
        this.mathContext = new MathContext(setPrecision, setRoundingMode);
    }
    
    BinaryMathContext(int setPrecision, long setEmin, long setEmax, RoundingMode setRoundingMode) {
        this.precision = setPrecision;
        this.emin = setEmin;
        this.emax = setEmax;
        this.roundingMode = setRoundingMode;
        this.mathContext = new MathContext(setPrecision, setRoundingMode);
    }
    
    public int getPrecision() {
        return precision;
    }
    
    public long getMaxExponent() {
        return emax;
    }
    
    public long getMinExponent() {
        return emin;
    }
    
    public RoundingMode getRoundingMode() {
        return roundingMode;
    }
    
    public BinaryMathContext withRoundingMode(RoundingMode roundingMode) {
        return new BinaryMathContext(precision, emin, emax, roundingMode);
    }
    
    @Override
    public boolean equals(Object x) {
        if (!(x instanceof BinaryMathContext)) return false;
        BinaryMathContext mc = (BinaryMathContext)x;
        return mc.precision == this.precision
                && mc.emin == this.emin
                && mc.emax == this.emax
                && mc.roundingMode == this.roundingMode;
    }
    
    public int hashCode() {
        return (int) (this.emin * 59 + this.emax * 31 + mathContext.hashCode());
    }
}
