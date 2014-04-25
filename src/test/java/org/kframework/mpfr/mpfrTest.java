// Copyright (c) 2014 K Team. All Rights Reserved.
package org.kframework.mpfr;

import static org.junit.Assert.*;

import java.math.BigInteger;

import org.junit.Test;
import static org.kframework.mpfr.mpfr.*;

public class mpfrTest {
    @Test
    public void testLoadLibrary() {
        __mpfr_struct nan = new __mpfr_struct(5);
        assertTrue(mpfr_nan_p(nan));
        assertEquals(5, nan._mpfr_prec);
    }
    
    @Test
    public void testSetStrValid() {
        __mpfr_struct x = new __mpfr_struct(24);
        boolean rounded = mpfr_set_str(x, "0.5", 10, MPFR_RNDN);
        assertFalse(rounded);
    }
    
    @Test(expected=NumberFormatException.class)
    public void testSetStrInvalid() {
        __mpfr_struct x = new __mpfr_struct(24);
        mpfr_set_str(x, "foo", 10, MPFR_RNDN);
    }
    
    @Test(expected=NumberFormatException.class)
    public void testNotACString() {
        __mpfr_struct x = new __mpfr_struct(24);
        mpfr_set_str(x, "\uffff", 10, MPFR_RNDN);
    }
    
    @Test
    public void testSetStrRounded() {
        __mpfr_struct x = new __mpfr_struct(24);
        boolean rounded = mpfr_set_str(x, "0.2", 10, MPFR_RNDN);
        assertTrue(rounded);
    }
    
    @Test
    public void testAsPrintf() {
        __mpfr_struct x = new __mpfr_struct(24);
        String s = mpfr_asprintf("%Rf", x);
        assertEquals("nan", s);
    }
    
    @Test
    public void testCopy() {
        __mpfr_struct x = new __mpfr_struct(24);
        __mpfr_struct y = new __mpfr_struct(x);
        mpfr_set_d(x, 1.0/0.0, MPFR_RNDN);
        assertEquals("nan", mpfr_asprintf("%Rf", y));
        assertEquals("inf", mpfr_asprintf("%Rf", x));
    }
    
    @Test
    public void testMpz() {
        __mpz_struct x = new __mpz_struct(BigInteger.valueOf(1));
        assertEquals("1", mpz_get_str(10, x));
        x = new __mpz_struct(BigInteger.valueOf(-1));
        assertEquals("-1", mpz_get_str(10, x));
        x = new __mpz_struct(new BigInteger("9223372036854775808"));
        assertEquals("9223372036854775808", mpz_get_str(10, x));
    }
}