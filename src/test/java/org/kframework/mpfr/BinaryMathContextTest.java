package org.kframework.mpfr;

import static org.junit.Assert.*;

import java.math.RoundingMode;

import org.junit.Test;
import static org.kframework.mpfr.mpfr.*;

public class BinaryMathContextTest {

    @Test
    public void testSetThenGet() {
        BinaryMathContext mc = new BinaryMathContext(5, MPFR_EMIN_DEFAULT, MPFR_EMAX_DEFAULT, RoundingMode.UNNECESSARY);
        assertEquals(MPFR_EMIN_DEFAULT, mc.getMinExponent());
        assertEquals(MPFR_EMAX_DEFAULT, mc.getMaxExponent());
    }
    
    @Test
    public void testIEEEConformance() {
        assertEquals(Double.MAX_EXPONENT, BinaryMathContext.BINARY64.getMaxExponent());
        assertEquals(Double.MIN_EXPONENT, BinaryMathContext.BINARY64.getMinExponent());
        assertEquals(Float.MAX_EXPONENT, BinaryMathContext.BINARY32.getMaxExponent());
        assertEquals(Float.MIN_EXPONENT, BinaryMathContext.BINARY32.getMinExponent());
    }
    
    @Test(expected=IllegalArgumentException.class)
    public void testIllegalPrecision() {
        new BinaryMathContext(-1, 5);
    }
    
    @Test(expected=IllegalArgumentException.class)
    public void testIllegalExponentRange() {
        new BinaryMathContext(5, -1);
    }
    
    @Test(expected=NullPointerException.class)
    public void testNoRoundingMode() {
        new BinaryMathContext(5, 5, null);
    }
    
    @Test(expected=IllegalArgumentException.class)
    public void testIllegalExponentRange2() {
        new BinaryMathContext(5, 70);
    }
    
    @Test
    public void testEquals() {
        assertNotEquals(BinaryMathContext.BINARY32, 5);
        assertEquals(BinaryMathContext.BINARY32, new BinaryMathContext(24, 8));
        assertEquals(BinaryMathContext.BINARY32.hashCode(), new BinaryMathContext(24, 8).hashCode());
        assertEquals(BinaryMathContext.BINARY64, new BinaryMathContext(53, 11));
        assertEquals(BinaryMathContext.BINARY64.hashCode(), new BinaryMathContext(53, 11).hashCode());
        assertNotEquals(BinaryMathContext.BINARY32, BinaryMathContext.BINARY64);
        assertNotEquals(BinaryMathContext.BINARY32, new BinaryMathContext(24, 9));
        assertNotEquals(BinaryMathContext.BINARY32, new BinaryMathContext(24, Float.MIN_EXPONENT, Float.MAX_EXPONENT + 1, RoundingMode.HALF_EVEN));
        assertNotEquals(BinaryMathContext.BINARY32, BinaryMathContext.BINARY32.withRoundingMode(RoundingMode.UNNECESSARY));
    }

}
