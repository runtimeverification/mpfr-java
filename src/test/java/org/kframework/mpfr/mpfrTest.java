// Copyright (c) 2014 K Team. All Rights Reserved.
package org.kframework.mpfr;

import static org.junit.Assert.*;

import java.math.BigInteger;
import java.math.RoundingMode;

import org.junit.Test;
import static org.kframework.mpfr.mpfr.*;

public class mpfrTest {
    @Test
    public void testLoadLibrary() {
        mpfr_t nan = new mpfr_t(5);
        assertTrue(mpfr_nan_p(nan));
        assertEquals(5, nan._mpfr_prec);
    }
    
    @Test
    public void testSetStrValid() {
        mpfr_t x = new mpfr_t(24);
        int rounded = mpfr_set_str(x, "0.5", 10, MPFR_RNDN);
        assertEquals(0, rounded);
        rounded = mpfr_set_str(x, "5.0", 10, MPFR_RNDN);
        assertEquals(0, rounded);
    }
    
    @Test(expected=NumberFormatException.class)
    public void testSetStrInvalid() {
        mpfr_t x = new mpfr_t(24);
        mpfr_set_str(x, "foo", 10, MPFR_RNDN);
    }
    
    @Test(expected=NumberFormatException.class)
    public void testNotACString() {
        mpfr_t x = new mpfr_t(24);
        mpfr_set_str(x, "\uffff", 10, MPFR_RNDN);
    }
    
    @Test
    public void testSetStrRounded() {
        mpfr_t x = new mpfr_t(24);
        int rounded = mpfr_set_str(x, "0.2", 10, MPFR_RNDN);
        assertNotEquals(0, rounded);
    }
    
    @Test
    public void testAsPrintf() {
        mpfr_t x = new mpfr_t(24);
        String s = mpfr_asprintf("%Rf", x);
        assertEquals("nan", s);
    }
    
    @Test
    public void testCopy() {
        mpfr_t x = new mpfr_t(24);
        mpfr_t y = new mpfr_t(x);
        mpfr_set_d(x, 1.0/0.0, MPFR_RNDN);
        assertEquals("nan", mpfr_asprintf("%Rf", y));
        assertEquals("inf", mpfr_asprintf("%Rf", x));
    }
    
    @Test
    public void testMpz() {
        mpz_t x = new mpz_t(BigInteger.valueOf(1));
        assertEquals("1", mpz_get_str(10, x));
        x = new mpz_t(BigInteger.valueOf(-1));
        assertEquals("-1", mpz_get_str(10, x));
        x = new mpz_t(new BigInteger("9223372036854775808"));
        assertEquals("9223372036854775808", mpz_get_str(10, x));
    }
    
    @Test
    public void testExponentRounding() {
        mpfr_t x = new mpfr_t(24);
        int ternary = mpfr_set_d(x, 1.0, MPFR_RNDN);
        assertEquals(0, ternary);
        ternary = mpfr_abs(x, x, MPFR_RNDN);
        assertEquals(0, ternary);
        boolean rounded = BigFloat.roundExponent(ternary, x, 
                new BinaryMathContext(24, BigFloat.eminMin(24), BigFloat.EMAX_MAX, RoundingMode.UNNECESSARY));
        assertFalse(rounded);
    }
    
    @Test
    public void testMpfrTFull() {
        mpfr_t x = new mpfr_t(24);
        mpfr_set_d(x, 1.0, MPFR_RNDN);
        mpfr_t_full full = new mpfr_t_full(x);
        mpfr_t y = new mpfr_t(full);
        assertTrue(mpfr_equal_p(x, y));
    }
}