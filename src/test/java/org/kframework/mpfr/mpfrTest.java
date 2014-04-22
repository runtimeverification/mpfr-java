// Copyright (c) 2014 K Team. All Rights Reserved.
package org.kframework.mpfr;

import static org.junit.Assert.*;

import org.junit.Test;
import org.kframework.mpfr.mpfr.__mpfr_struct;

public class mpfrTest {
    @Test
    public void testLoadLibrary() {
        __mpfr_struct nan = new __mpfr_struct();
        mpfr.mpfr_init2(nan, 5);
        assertNotEquals(0, mpfr.mpfr_nan_p(nan));
    }
}