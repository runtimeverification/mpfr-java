package org.kframework.mpfr;

import static org.junit.Assert.*;

import java.math.BigInteger;
import java.math.MathContext;
import java.math.RoundingMode;

import org.junit.Test;

public class BigFloatTest {
    
    private BigFloat zero = new BigFloat(0, BigFloat.BINARY32);
    private BigFloat negzero = new BigFloat(-0.0, BigFloat.BINARY32);
    private BigFloat inf = new BigFloat(1.0/0.0, BigFloat.BINARY32);
    private BigFloat neginf = new BigFloat(-1.0/0.0, BigFloat.BINARY32);
    private BigFloat nan = new BigFloat(0.0/0.0, BigFloat.BINARY32);
    private BigFloat one = new BigFloat(1, BigFloat.BINARY32);
    private BigFloat pi = BigFloat.pi(BigFloat.BINARY32);

    @Test
    public void testMaxValue() {
        BigFloat f = new BigFloat(Long.MAX_VALUE, BigFloat.BINARY128);
        assertEquals(Long.MAX_VALUE, f.longValueExact());
        f = new BigFloat(Long.MIN_VALUE, BigFloat.BINARY128);
        assertEquals(Long.MIN_VALUE, f.longValueExact());
    }
    
    public void assertDoubleEquals(double expected, double result) {
        assertEquals(Double.valueOf(expected), Double.valueOf(result));
    }
    
    public void assertFloatEquals(float expected, float result) {
        assertEquals(Float.valueOf(expected), Float.valueOf(result));
    }
    
    public void assertDoubleNotEquals(double expected, double result) {
        assertNotEquals(Double.valueOf(expected), Double.valueOf(result));
    }
    
    @Test
    public void testConstants() {
        BigFloat f = BigFloat.zero(5);
        assertEquals(0L, f.longValueExact());
        assertTrue(f.isPositiveZero());
        assertDoubleEquals(0.0, f.signum());
        f = BigFloat.NaN(5);
        assertTrue(Double.isNaN(f.doubleValueExact()));
        assertTrue(f.isNaN());
        assertDoubleEquals(f.doubleValueExact(), 0.0/0.0);
        assertEquals(0, f.byteValue());
        assertEquals(0, f.shortValue());
        assertEquals(0, f.intValue());
        assertEquals(0, f.longValue());
        assertEquals(BigInteger.ZERO, f.toBigInteger());
        try {
            f.byteValueExact();
            fail();
        } catch (ArithmeticException e) {}
        try {
            f.shortValueExact();
            fail();
        } catch (ArithmeticException e) {}
        try {
            f.intValueExact();
            fail();
        } catch (ArithmeticException e) {}
        try {
            f.longValueExact();
            fail();
        } catch (ArithmeticException e) {}
        try {
            f.toBigIntegerExact();
            fail();
        } catch (ArithmeticException e) {}
        f = BigFloat.negativeInfinity(5);
        assertDoubleEquals(-1.0/0.0, f.doubleValueExact());
        assertTrue(f.isInfinite());
        assertDoubleEquals(-1.0, f.signum());
        assertEquals(Byte.MIN_VALUE, f.byteValue());
        assertEquals(Short.MIN_VALUE, f.shortValue());
        assertEquals(Integer.MIN_VALUE, f.intValue());
        assertEquals(Long.MIN_VALUE, f.longValue());
        assertEquals(BigInteger.ZERO, f.toBigInteger());
        try {
            f.byteValueExact();
            fail();
        } catch (ArithmeticException e) {}
        try {
            f.shortValueExact();
            fail();
        } catch (ArithmeticException e) {}
        try {
            f.intValueExact();
            fail();
        } catch (ArithmeticException e) {}
        try {
            f.longValueExact();
            fail();
        } catch (ArithmeticException e) {}
        try {
            f.toBigIntegerExact();
            fail();
        } catch (ArithmeticException e) {}
        f = BigFloat.negativeZero(5);
        assertDoubleEquals(-0.0, f.doubleValueExact());
        assertTrue(f.isNegativeZero());
        assertDoubleEquals(-0.0, f.signum());
        f = BigFloat.positiveInfinity(5);
        assertDoubleEquals(1.0/0.0, f.doubleValueExact());
        assertTrue(f.isInfinite());
        assertDoubleEquals(1.0, f.signum());
        assertEquals(Byte.MAX_VALUE, f.byteValue());
        assertEquals(Short.MAX_VALUE, f.shortValue());
        assertEquals(Integer.MAX_VALUE, f.intValue());
        assertEquals(Long.MAX_VALUE, f.longValue());
        assertEquals(BigInteger.ZERO, f.toBigInteger());
        try {
            f.byteValueExact();
            fail();
        } catch (ArithmeticException e) {}
        try {
            f.shortValueExact();
            fail();
        } catch (ArithmeticException e) {}
        try {
            f.intValueExact();
            fail();
        } catch (ArithmeticException e) {}
        try {
            f.longValueExact();
            fail();
        } catch (ArithmeticException e) {}
        try {
            f.toBigIntegerExact();
            fail();
        } catch (ArithmeticException e) {}
        f = BigFloat.valueOf(0.5, BigFloat.BINARY32);
        assertFloatEquals(0.5F, f.floatValueExact());
        assertEquals(0, f.byteValue());
        assertEquals(0, f.shortValue());
        assertEquals(0, f.intValue());
        assertEquals(0, f.longValue());
        assertEquals(BigInteger.ZERO, f.toBigInteger());
        try {
            f.byteValueExact();
            fail();
        } catch (ArithmeticException e) {}
        try {
            f.shortValueExact();
            fail();
        } catch (ArithmeticException e) {}
        try {
            f.intValueExact();
            fail();
        } catch (ArithmeticException e) {}
        try {
            f.longValueExact();
            fail();
        } catch (ArithmeticException e) {}
        try {
            f.toBigIntegerExact();
            fail();
        } catch (ArithmeticException e) {}
        f = BigFloat.pi(BigFloat.BINARY64);
        assertDoubleEquals(f.doubleValueExact(), Math.PI);
        f = BigFloat.e(BigFloat.BINARY64);
        assertDoubleEquals(f.doubleValueExact(), Math.E);
        try {
            BigFloat.e(new MathContext(BigFloat.BINARY64.getPrecision(), RoundingMode.UNNECESSARY));
        } catch (ArithmeticException e) {}
        try {
            BigFloat.pi(new MathContext(BigFloat.BINARY64.getPrecision(), RoundingMode.UNNECESSARY));
        } catch (ArithmeticException e) {}
    }
    
    @Test
    public void testRounding() {
        BigFloat q = new BigFloat("0.1", BigFloat.BINARY128);
        assertDoubleEquals(q.doubleValue(), 0.1);
        assertDoubleEquals(q.floatValue(), 0.1F);
        try {
            q.doubleValueExact();
            fail();
        } catch (ArithmeticException e) {}
        BigFloat f = new BigFloat("0.1", BigFloat.BINARY32);
        assertFloatEquals(f.floatValueExact(), 0.1F);
        BigFloat d = new BigFloat("0.1", BigFloat.BINARY64);
        assertDoubleEquals(d.doubleValueExact(), 0.1);
        assertEquals(q.round(BigFloat.BINARY64), d);
        assertEquals(q.round(BigFloat.BINARY64).hashCode(), d.hashCode());
        assertEquals(q.round(BigFloat.BINARY32), f);
        assertEquals(q.round(BigFloat.BINARY32).hashCode(), f.hashCode());
        assertEquals(d.round(BigFloat.BINARY32), f);
        assertEquals(d.round(BigFloat.BINARY32).hashCode(), f.hashCode());
        f = new BigFloat(100, BigFloat.BINARY32);
        MathContext insufficient = new MathContext(2, RoundingMode.UNNECESSARY);
        try {
            f.round(insufficient);
            fail();
        } catch (ArithmeticException e) {}
        try {
            f = new BigFloat(100, insufficient);
            fail();
        } catch (ArithmeticException e) {}
        try {
            f = new BigFloat(100.0, insufficient);
            fail();
        } catch (ArithmeticException e) {}
        try {
            f = new BigFloat(BigInteger.valueOf(100), insufficient);
            fail();
        } catch (ArithmeticException e) {}
        try {
            f = new BigFloat("100", insufficient);
            fail();
        } catch (ArithmeticException e) {}
    }
    
    @Test
    public void testConstructors() {
        BigFloat f = new BigFloat("0.5".getBytes(), BigFloat.BINARY32);
        assertDoubleEquals(0.5, f.doubleValueExact());
        assertEquals(24, f.precision());
        f = new BigFloat("0.5", BigFloat.BINARY32);
        assertDoubleEquals(0.5, f.doubleValueExact());
        assertEquals(24, f.precision());
        f = new BigFloat(0.5, BigFloat.BINARY32);
        assertDoubleEquals(0.5, f.doubleValueExact());
        assertEquals(24, f.precision());
        f = new BigFloat(BigInteger.valueOf(1), BigFloat.BINARY32);
        assertEquals(BigInteger.valueOf(1), f.toBigIntegerExact());
        assertEquals(24, f.precision());
        f = new BigFloat(1, BigFloat.BINARY32);
        assertEquals(1L, f.longValueExact());
        assertEquals(24, f.precision());
        try {
            new BigFloat("0.5 ", BigFloat.BINARY32);
            fail();
        } catch (NumberFormatException e) {}
        try {
            new BigFloat("foo", BigFloat.BINARY32);
            fail();
        } catch (NumberFormatException e) {}
        try {
            new BigFloat("0.5", new MathContext(5));
            fail();
        } catch (IllegalArgumentException e) {}
        try {
            new BigFloat("0.5", new MathContext(1, RoundingMode.HALF_EVEN));
            fail();
        } catch (IllegalArgumentException e) {}
    }
    
    @Test
    public void testOverflowConversion() {
        BigFloat f = new BigFloat(Byte.MAX_VALUE + 1, BigFloat.BINARY128);
        assertEquals(Byte.MIN_VALUE, f.byteValue());
        try {
            f.byteValueExact();
            fail();
        } catch (ArithmeticException e) {}
        f = new BigFloat(Byte.MIN_VALUE - 1, BigFloat.BINARY128);
        assertEquals(Byte.MAX_VALUE, f.byteValue());
        try {
            f.byteValueExact();
            fail();
        } catch (ArithmeticException e) {}
        f = new BigFloat(Short.MAX_VALUE + 1, BigFloat.BINARY128);
        assertEquals(Short.MIN_VALUE, f.shortValue());
        try {
            f.shortValueExact();
            fail();
        } catch (ArithmeticException e) {}
        f = new BigFloat(Short.MIN_VALUE - 1, BigFloat.BINARY128);
        assertEquals(Short.MAX_VALUE, f.shortValue());
        try {
            f.shortValueExact();
            fail();
        } catch (ArithmeticException e) {}
        f = new BigFloat(Integer.MAX_VALUE + 1L, BigFloat.BINARY128);
        assertEquals(Integer.MIN_VALUE, f.intValue());
        try {
            f.intValueExact();
            fail();
        } catch (ArithmeticException e) {}
        f = new BigFloat(Integer.MIN_VALUE - 1L, BigFloat.BINARY128);
        assertEquals(Integer.MAX_VALUE, f.intValue());
        try {
            f.intValueExact();
            fail();
        } catch (ArithmeticException e) {}
        f = new BigFloat(BigInteger.valueOf(Long.MAX_VALUE).add(BigInteger.valueOf(1)), BigFloat.BINARY128);
        assertEquals(Long.MIN_VALUE, f.longValue());
        try {
            f.longValueExact();
            fail();
        } catch (ArithmeticException e) {}
        f = new BigFloat(BigInteger.valueOf(Long.MIN_VALUE).subtract(BigInteger.valueOf(1)), BigFloat.BINARY128);
        assertEquals(Long.MAX_VALUE, f.longValue());
        try {
            f.longValueExact();
            fail();
        } catch (ArithmeticException e) {}
        f = new BigFloat(Long.MAX_VALUE, BigFloat.BINARY32);
        assertNotEquals(f.longValue(), Long.MAX_VALUE);
    }
    
    @Test
    public void testToString() {
        BigFloat f = new BigFloat(0.5, BigFloat.BINARY32);
        assertEquals("5e-01", f.toString());
        f = new BigFloat(0.1, BigFloat.BINARY32);
        assertEquals("1.00000001e-01", f.toString());
        assertEquals(f, new BigFloat(f.toString(), BigFloat.BINARY32));
    }
    
    @Test
    public void testIEEECompare() {
        BigFloat f = new BigFloat(0.5, BigFloat.BINARY32);
        BigFloat f2 = new BigFloat(0.4, BigFloat.BINARY32);
        assertTrue(f.gt(f2));
        assertTrue(f2.lt(f));
        assertTrue(f.ge(f2));
        assertTrue(f2.le(f));
        assertTrue(f.eq(f));
        BigFloat f3 = new BigFloat(0.5, BigFloat.BINARY64);
        assertTrue(f.eq(f3));
        assertFalse(nan.eq(nan));
        assertFalse(nan.eq(f));
        assertFalse(nan.lt(nan));
        assertFalse(nan.lt(f));
        assertFalse(nan.gt(nan));
        assertFalse(nan.gt(f));
        assertFalse(nan.ge(nan));
        assertFalse(nan.ge(f));
        assertFalse(nan.le(nan));
        assertFalse(nan.le(f));
        assertTrue(nan.ne(nan));
        assertTrue(nan.ne(f));
        assertFalse(inf.lt(f));
        assertTrue(inf.eq(inf));
        assertTrue(f.lt(inf));
        assertTrue(neginf.lt(inf));
        assertTrue(neginf.lt(f));
        assertTrue(neginf.eq(neginf));
        assertFalse(f.lt(neginf));
    }
    
    @Test
    public void testJavaCompare() {
        BigFloat f = new BigFloat(0.5, BigFloat.BINARY32);
        BigFloat f2 = new BigFloat(0.4, BigFloat.BINARY32);
        assertTrue(f.compareTo(f2) > 0);
        assertTrue(f2.compareTo(f) < 0);
        assertTrue(f.compareTo(f) == 0);
        BigFloat f3 = new BigFloat(0.5, BigFloat.BINARY64);
        assertTrue(f.compareTo(f3) < 0); //less precision is less
        assertNotEquals(f, f3);
        assertTrue(negzero.compareTo(zero) < 0); // -0 < 0
        assertNotEquals(zero, negzero);
        assertTrue(zero.compareTo(negzero) > 0); // 0 > -0
        assertNotEquals(negzero, zero);
        assertTrue(neginf.compareTo(zero) < 0 && zero.compareTo(inf) < 0);
        assertNotEquals(neginf, zero);
        assertNotEquals(zero, inf);
        assertTrue(inf.compareTo(nan) < 0); //inf < nan
        assertNotEquals(inf, nan);
        assertTrue(nan.compareTo(inf) > 0); //nan > inf
        assertNotEquals(nan, inf);
        assertTrue(nan.compareTo(nan) == 0); //nan == nan
        assertEquals(nan, nan);
    }
    
    @Test
    public void testArithmetic() {
        MathContext mc = new MathContext(BigFloat.BINARY32.getPrecision(), RoundingMode.UNNECESSARY);
        BigFloat three = new BigFloat(3, mc);
        try {
            one.divide(zero, mc);
        } catch (ArithmeticException e) {}
        assertEquals(BigFloat.positiveInfinity(mc.getPrecision()), inf);
        assertEquals(2, one.add(one, mc).longValueExact());
        assertEquals(0, one.subtract(one, mc).longValueExact());
        assertEquals(9, three.multiply(three, mc).longValueExact());
        assertEquals(one, three.divide(three, mc));
        assertEquals(zero, three.remainder(three, mc));
        
        assertEquals(inf, inf.add(one, mc));
        assertEquals(nan, inf.add(neginf, mc));
        assertEquals(neginf, neginf.add(one, mc));
        assertEquals(nan, neginf.add(inf, mc));
        
        assertEquals(inf, neginf.abs());
        assertEquals(one, one.abs());
        
        assertEquals(neginf, inf.negate());
        assertEquals(inf, neginf.negate());
        assertEquals(negzero, zero.negate());
        
        assertEquals(inf, inf.subtract(one, mc));
        assertEquals(nan, inf.subtract(inf, mc));
        assertEquals(neginf, neginf.subtract(one, mc));
        assertEquals(nan, neginf.subtract(neginf, mc));
        
        assertEquals(inf, inf.multiply(one, mc));
        assertEquals(neginf, inf.multiply(one.negate(), mc));
        assertEquals(nan, inf.multiply(zero, mc));
        assertEquals(neginf, neginf.multiply(one, mc));
        assertEquals(inf, neginf.multiply(one.negate(), mc));
        assertEquals(nan, neginf.multiply(zero, mc));
        
        assertEquals(nan, inf.divide(inf, mc));
        assertEquals(nan, inf.divide(neginf, mc));
        assertEquals(nan, neginf.divide(inf, mc));
        assertEquals(nan, neginf.divide(neginf, mc));
        assertEquals(inf, inf.divide(one, mc));
        assertEquals(neginf, inf.divide(one.negate(), mc));
        assertEquals(zero, one.divide(inf, mc));
        assertEquals(negzero, one.divide(neginf, mc));
        assertEquals(inf, one.divide(zero, mc));
        
        assertEquals(nan, inf.remainder(one, mc));
        assertEquals(nan, one.remainder(zero, mc));
        assertEquals(one, one.remainder(inf, mc));
        
        assertEquals(nan, nan.sin(mc));
        assertEquals(nan, inf.sin(mc));
        assertEquals(zero, zero.sin(mc));
        assertEquals(negzero, negzero.sin(mc));
        
        assertEquals(nan, nan.cos(mc));
        assertEquals(nan, inf.cos(mc));
        
        assertEquals(nan, nan.tan(mc));
        assertEquals(nan, inf.tan(mc));
        assertEquals(zero, zero.tan(mc));
        assertEquals(negzero, negzero.tan(mc));
        
        assertEquals(nan, nan.asin(mc));
        assertEquals(nan, three.asin(mc));
        assertEquals(zero, zero.asin(mc));
        assertEquals(negzero, negzero.asin(mc));
        
        assertEquals(nan, nan.acos(mc));
        assertEquals(nan, three.acos(mc));
        
        assertEquals(nan, nan.atan(mc));
        assertEquals(zero, zero.atan(mc));
        assertEquals(negzero, negzero.atan(mc));
        
        assertEquals(nan, nan.exp(mc));
        assertEquals(inf, inf.exp(mc));
        assertEquals(zero, neginf.exp(mc));
        
        assertEquals(nan, nan.log(mc));
        assertEquals(nan, one.negate().log(mc));
        assertEquals(inf, inf.log(mc));
        assertEquals(neginf, zero.log(mc));
        assertEquals(neginf, negzero.log(mc));
        assertEquals(nan, nan.log10(mc));
        assertEquals(nan, one.negate().log10(mc));
        assertEquals(inf, inf.log10(mc));
        assertEquals(neginf, zero.log10(mc));
        assertEquals(neginf, negzero.log10(mc));
        assertEquals(one, new BigFloat(10, mc).log10(mc));
        
        assertEquals(one, one.ceil());
        assertEquals(zero, zero.ceil());
        assertEquals(negzero, negzero.ceil());
        assertEquals(inf, inf.ceil());
        assertEquals(neginf, neginf.ceil());
        assertEquals(nan, nan.ceil());
        assertEquals(negzero, new BigFloat(-0.1, BigFloat.BINARY32).ceil());
        
        assertEquals(one, one.floor());
        assertEquals(zero, zero.floor());
        assertEquals(negzero, negzero.floor());
        assertEquals(inf, inf.floor());
        assertEquals(neginf, neginf.floor());
        assertEquals(nan, nan.floor());
        assertEquals(zero, new BigFloat(0.1, BigFloat.BINARY32).floor());
        
        assertEquals(nan, BigFloat.atan2(nan, one, mc));
        assertEquals(nan, BigFloat.atan2(one, nan, mc));
        assertEquals(zero, BigFloat.atan2(zero, one, mc));
        assertEquals(zero, BigFloat.atan2(one, inf, mc));
        assertEquals(negzero, BigFloat.atan2(negzero, one, mc));
        assertEquals(negzero, BigFloat.atan2(one.negate(), inf, mc));
        assertEquals(pi, BigFloat.atan2(zero, one.negate(), BigFloat.BINARY32));
        assertEquals(pi, BigFloat.atan2(one, neginf, BigFloat.BINARY32));
        assertEquals(pi.negate(), BigFloat.atan2(negzero, one.negate(), BigFloat.BINARY32));
        assertEquals(pi.negate(), BigFloat.atan2(one.negate(), neginf, BigFloat.BINARY32));
        BigFloat two = BigFloat.valueOf(2, mc);
        BigFloat halfpi = pi.divide(two, mc);
        assertEquals(halfpi, BigFloat.atan2(one, zero, BigFloat.BINARY32));
        assertEquals(halfpi, BigFloat.atan2(one, negzero, BigFloat.BINARY32));
        assertEquals(halfpi, BigFloat.atan2(inf, one, BigFloat.BINARY32));
        assertEquals(halfpi.negate(), BigFloat.atan2(one.negate(), zero, BigFloat.BINARY32));
        assertEquals(halfpi.negate(), BigFloat.atan2(one.negate(), negzero, BigFloat.BINARY32));
        assertEquals(halfpi.negate(), BigFloat.atan2(neginf, one, BigFloat.BINARY32));
        assertEquals(halfpi.divide(two, BigFloat.BINARY32), BigFloat.atan2(inf, inf, BigFloat.BINARY32));
        BigFloat threeQuartersPi = halfpi.multiply(three, BigFloat.BINARY32).divide(two, BigFloat.BINARY32);
        assertEquals(threeQuartersPi, BigFloat.atan2(inf, neginf, BigFloat.BINARY32));
        assertEquals(halfpi.divide(two, BigFloat.BINARY32).negate(), BigFloat.atan2(neginf, inf, BigFloat.BINARY32));
        assertEquals(threeQuartersPi.negate(), BigFloat.atan2(neginf, neginf, BigFloat.BINARY32));
        

        assertEquals(one, BigFloat.min(nan, one, mc));
        assertEquals(one, BigFloat.min(one, nan, mc));
        
        assertEquals(one, BigFloat.max(nan, one, mc));
        assertEquals(one, BigFloat.max(one, nan, mc));
    }
    
    @Test
    public void testRoundingModes() {
        assertEquals(4, new BigFloat(5, new MathContext(2, RoundingMode.HALF_EVEN)).longValueExact());
        assertEquals(4, new BigFloat(5, new MathContext(2, RoundingMode.DOWN)).longValueExact());
        assertEquals(4, new BigFloat(5, new MathContext(2, RoundingMode.FLOOR)).longValueExact());
        assertEquals(6, new BigFloat(5, new MathContext(2, RoundingMode.CEILING)).longValueExact());
        assertEquals(6, new BigFloat(5, new MathContext(2, RoundingMode.UP)).longValueExact());
        assertEquals(-4, new BigFloat(-5, new MathContext(2, RoundingMode.HALF_EVEN)).longValueExact());
        assertEquals(-4, new BigFloat(-5, new MathContext(2, RoundingMode.DOWN)).longValueExact());
        assertEquals(-6, new BigFloat(-5, new MathContext(2, RoundingMode.FLOOR)).longValueExact());
        assertEquals(-4, new BigFloat(-5, new MathContext(2, RoundingMode.CEILING)).longValueExact());
        assertEquals(-6, new BigFloat(-5, new MathContext(2, RoundingMode.UP)).longValueExact());
        assertEquals(8, new BigFloat(7, new MathContext(2, RoundingMode.HALF_EVEN)).longValueExact());
        assertEquals(6, new BigFloat(7, new MathContext(2, RoundingMode.DOWN)).longValueExact());
        assertEquals(6, new BigFloat(7, new MathContext(2, RoundingMode.FLOOR)).longValueExact());
        assertEquals(8, new BigFloat(7, new MathContext(2, RoundingMode.CEILING)).longValueExact());
        assertEquals(8, new BigFloat(7, new MathContext(2, RoundingMode.UP)).longValueExact());
        assertEquals(-8, new BigFloat(-7, new MathContext(2, RoundingMode.HALF_EVEN)).longValueExact());
        assertEquals(-6, new BigFloat(-7, new MathContext(2, RoundingMode.DOWN)).longValueExact());
        assertEquals(-8, new BigFloat(-7, new MathContext(2, RoundingMode.FLOOR)).longValueExact());
        assertEquals(-6, new BigFloat(-7, new MathContext(2, RoundingMode.CEILING)).longValueExact());
        assertEquals(-8, new BigFloat(-7, new MathContext(2, RoundingMode.UP)).longValueExact());
    }
}
