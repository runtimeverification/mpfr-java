package org.kframework.mpfr;

import org.junit.Test;

public class BigFloatIT {

    @Test
    public void testCreateManyObjects() {
        for (long i = 0; i < 10000000L; i++) {
            new BigFloat(1.0, BinaryMathContext.BINARY128);
        }
    }
}
