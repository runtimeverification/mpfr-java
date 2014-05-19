package org.kframework.mpfr;

import org.junit.Test;

/**
 * Integration tests for BigFloat.java. Currently contains only one test: a test which spawns a very
 * large number of objects in order to make sure that garbage collection occurs correctly.
 * 
 * @author dwightguth
 *
 */
public class BigFloatIT {

    @Test
    public void testCreateManyObjects() {
        for (long i = 0; i < 10000000L; i++) {
            new BigFloat(1.0, BinaryMathContext.BINARY128);
        }
    }
}
