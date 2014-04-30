package org.kframework.mpfr;

import static org.junit.Assert.*;

import java.math.BigInteger;
import java.math.RoundingMode;

import org.junit.Test;

public class BigFloatTest {
    
    private BigFloat zero = new BigFloat(0, BinaryMathContext.BINARY32);
    private BigFloat negzero = new BigFloat(-0.0, BinaryMathContext.BINARY32);
    private BigFloat inf = new BigFloat(1.0/0.0, BinaryMathContext.BINARY32);
    private BigFloat neginf = new BigFloat(-1.0/0.0, BinaryMathContext.BINARY32);
    private BigFloat nan = new BigFloat(0.0/0.0, BinaryMathContext.BINARY32);
    private BigFloat one = new BigFloat(1, BinaryMathContext.BINARY32);
    private BigFloat pi = BigFloat.pi(BinaryMathContext.BINARY32);

    @Test
    public void testMaxValue() {
        BigFloat f = new BigFloat(Long.MAX_VALUE, BinaryMathContext.BINARY128);
        assertEquals(Long.MAX_VALUE, f.longValueExact());
        f = new BigFloat(Long.MIN_VALUE, BinaryMathContext.BINARY128);
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
        assertEquals(0, f.byteValueExact());
        assertEquals(0, f.intValueExact());
        assertEquals(0, f.shortValueExact());
        assertTrue(f.isPositiveZero());
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
        f = BigFloat.positiveInfinity(5);
        assertDoubleEquals(1.0/0.0, f.doubleValueExact());
        assertTrue(f.isInfinite());
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
        f = BigFloat.valueOf(0.5, BinaryMathContext.BINARY32);
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
        f = BigFloat.pi(BinaryMathContext.BINARY64);
        assertDoubleEquals(f.doubleValueExact(), Math.PI);
        f = BigFloat.e(BinaryMathContext.BINARY64);
        assertDoubleEquals(f.doubleValueExact(), Math.E);
        try {
            BigFloat.e(BinaryMathContext.BINARY64.withRoundingMode(RoundingMode.UNNECESSARY));
        } catch (ArithmeticException e) {}
        try {
            BigFloat.pi(BinaryMathContext.BINARY64.withRoundingMode(RoundingMode.UNNECESSARY));
        } catch (ArithmeticException e) {}
    }
    
    @Test
    public void testRounding() {
        BigFloat q = new BigFloat("0.1", BinaryMathContext.BINARY128);
        assertDoubleEquals(q.doubleValue(), 0.1);
        assertDoubleEquals(q.floatValue(), 0.1F);
        try {
            q.doubleValueExact();
            fail();
        } catch (ArithmeticException e) {}
        BigFloat f = new BigFloat("0.1", BinaryMathContext.BINARY32);
        assertFloatEquals(f.floatValueExact(), 0.1F);
        BigFloat d = new BigFloat("0.1", BinaryMathContext.BINARY64);
        assertDoubleEquals(d.doubleValueExact(), 0.1);
        assertEquals(q.round(BinaryMathContext.BINARY64), d);
        assertEquals(q.plus(BinaryMathContext.BINARY64), d);
        assertEquals(q.round(BinaryMathContext.BINARY64).hashCode(), d.hashCode());
        assertEquals(q.round(BinaryMathContext.BINARY32), f);
        assertEquals(q.plus(BinaryMathContext.BINARY32), f);
        assertEquals(q.round(BinaryMathContext.BINARY32).hashCode(), f.hashCode());
        assertEquals(d.round(BinaryMathContext.BINARY32), f);
        assertEquals(d.plus(BinaryMathContext.BINARY32), f);
        assertEquals(d.round(BinaryMathContext.BINARY32).hashCode(), f.hashCode());
        f = new BigFloat(100, BinaryMathContext.BINARY32);
        BinaryMathContext insufficient = new BinaryMathContext(2, 15, RoundingMode.UNNECESSARY);
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
        BigFloat f = new BigFloat("0.5".getBytes(), BinaryMathContext.BINARY32);
        assertDoubleEquals(0.5, f.doubleValueExact());
        assertEquals(24, f.precision());
        assertEquals(Math.getExponent(f.doubleValueExact()), f.exponent());
        f = new BigFloat("0.5", BinaryMathContext.BINARY32);
        assertDoubleEquals(0.5, f.doubleValueExact());
        assertEquals(24, f.precision());
        f = new BigFloat(0.5, BinaryMathContext.BINARY32);
        assertDoubleEquals(0.5, f.doubleValueExact());
        assertEquals(24, f.precision());
        f = new BigFloat(BigInteger.valueOf(1), BinaryMathContext.BINARY32);
        assertEquals(BigInteger.valueOf(1), f.toBigIntegerExact());
        assertEquals(24, f.precision());
        f = new BigFloat(1, BinaryMathContext.BINARY32);
        assertEquals(1L, f.longValueExact());
        assertEquals(24, f.precision());
        try {
            new BigFloat("0.5 ", BinaryMathContext.BINARY32);
            fail();
        } catch (NumberFormatException e) {}
        try {
            new BigFloat("foo", BinaryMathContext.BINARY32);
            fail();
        } catch (NumberFormatException e) {}
        try {
            new BigFloat("0.5", new BinaryMathContext(5, 5, RoundingMode.HALF_UP));
            fail();
        } catch (IllegalArgumentException e) {}
        try {
            new BigFloat("0.5", new BinaryMathContext(5, 5, RoundingMode.HALF_DOWN));
            fail();
        } catch (IllegalArgumentException e) {}
        try {
            new BigFloat("0.5", new BinaryMathContext(1, 15, RoundingMode.HALF_EVEN));
            fail();
        } catch (IllegalArgumentException e) {}
    }
    
    @Test
    public void testOverflowConversion() {
        BigFloat f = new BigFloat(Byte.MAX_VALUE + 1, BinaryMathContext.BINARY128);
        assertEquals(Byte.MIN_VALUE, f.byteValue());
        try {
            f.byteValueExact();
            fail();
        } catch (ArithmeticException e) {}
        f = new BigFloat(Byte.MIN_VALUE - 1, BinaryMathContext.BINARY128);
        assertEquals(Byte.MAX_VALUE, f.byteValue());
        try {
            f.byteValueExact();
            fail();
        } catch (ArithmeticException e) {}
        f = new BigFloat(Short.MAX_VALUE + 1, BinaryMathContext.BINARY128);
        assertEquals(Short.MIN_VALUE, f.shortValue());
        try {
            f.shortValueExact();
            fail();
        } catch (ArithmeticException e) {}
        f = new BigFloat(Short.MIN_VALUE - 1, BinaryMathContext.BINARY128);
        assertEquals(Short.MAX_VALUE, f.shortValue());
        try {
            f.shortValueExact();
            fail();
        } catch (ArithmeticException e) {}
        f = new BigFloat(Integer.MAX_VALUE + 1L, BinaryMathContext.BINARY128);
        assertEquals(Integer.MIN_VALUE, f.intValue());
        try {
            f.intValueExact();
            fail();
        } catch (ArithmeticException e) {}
        f = new BigFloat(Integer.MIN_VALUE - 1L, BinaryMathContext.BINARY128);
        assertEquals(Integer.MAX_VALUE, f.intValue());
        try {
            f.intValueExact();
            fail();
        } catch (ArithmeticException e) {}
        f = new BigFloat(BigInteger.valueOf(Long.MAX_VALUE).add(BigInteger.valueOf(1)), BinaryMathContext.BINARY128);
        assertEquals(Long.MIN_VALUE, f.longValue());
        try {
            f.longValueExact();
            fail();
        } catch (ArithmeticException e) {}
        f = new BigFloat(BigInteger.valueOf(Long.MIN_VALUE).subtract(BigInteger.valueOf(1)), BinaryMathContext.BINARY128);
        assertEquals(Long.MAX_VALUE, f.longValue());
        try {
            f.longValueExact();
            fail();
        } catch (ArithmeticException e) {}
        f = new BigFloat(Long.MAX_VALUE, BinaryMathContext.BINARY32);
        assertNotEquals(f.longValue(), Long.MAX_VALUE);
    }
    
    @Test
    public void testToString() {
        BigFloat f = new BigFloat(0.5, BinaryMathContext.BINARY32);
        assertEquals("5e-01", f.toString());
        f = new BigFloat(0.1, BinaryMathContext.BINARY32);
        assertEquals("1.00000001e-01", f.toString());
        assertEquals(f, new BigFloat(f.toString(), BinaryMathContext.BINARY32));
    }
    
    @Test
    public void testIEEECompare() {
        BigFloat f = new BigFloat(0.5, BinaryMathContext.BINARY32);
        BigFloat f2 = new BigFloat(0.4, BinaryMathContext.BINARY32);
        assertTrue(f.gt(f2));
        assertTrue(f2.lt(f));
        assertTrue(f.ge(f2));
        assertTrue(f2.le(f));
        assertTrue(f.eq(f));
        assertFalse(f.ne(f));
        assertFalse(f.eq(f2));
        BigFloat f3 = new BigFloat(0.5, BinaryMathContext.BINARY64);
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
        BigFloat f = new BigFloat(0.5, BinaryMathContext.BINARY32);
        BigFloat f2 = new BigFloat(0.4, BinaryMathContext.BINARY32);
        assertTrue(f.compareTo(f2) > 0);
        assertTrue(f2.compareTo(f) < 0);
        assertTrue(f.compareTo(f) == 0);
        BigFloat f3 = new BigFloat(0.5, BinaryMathContext.BINARY64);
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
        assertNotEquals(nan, null);
        assertNotEquals(one, 1.0);
    }
    
    @Test
    public void testArithmetic() {
        BinaryMathContext mc = BinaryMathContext.BINARY32.withRoundingMode(RoundingMode.UNNECESSARY);
        BigFloat three = new BigFloat(3, mc);
        BigFloat half = new BigFloat(0.5, mc);
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
        
        assertEquals(inf, inf.plus());
        assertEquals(neginf, neginf.plus());
        assertEquals(negzero, negzero.plus());
        
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
        assertEquals(negzero, new BigFloat(-0.1, BinaryMathContext.BINARY32).ceil());
        
        assertEquals(one, one.floor());
        assertEquals(zero, zero.floor());
        assertEquals(negzero, negzero.floor());
        assertEquals(inf, inf.floor());
        assertEquals(neginf, neginf.floor());
        assertEquals(nan, nan.floor());
        assertEquals(zero, new BigFloat(0.1, BinaryMathContext.BINARY32).floor());
        
        assertEquals(nan, BigFloat.atan2(nan, one, mc));
        assertEquals(nan, BigFloat.atan2(one, nan, mc));
        assertEquals(zero, BigFloat.atan2(zero, one, mc));
        assertEquals(zero, BigFloat.atan2(one, inf, mc));
        assertEquals(negzero, BigFloat.atan2(negzero, one, mc));
        assertEquals(negzero, BigFloat.atan2(one.negate(), inf, mc));
        assertEquals(pi, BigFloat.atan2(zero, one.negate(), BinaryMathContext.BINARY32));
        assertEquals(pi, BigFloat.atan2(one, neginf, BinaryMathContext.BINARY32));
        assertEquals(pi.negate(), BigFloat.atan2(negzero, one.negate(), BinaryMathContext.BINARY32));
        assertEquals(pi.negate(), BigFloat.atan2(one.negate(), neginf, BinaryMathContext.BINARY32));
        BigFloat two = BigFloat.valueOf(2, mc);
        BigFloat halfpi = pi.divide(two, mc);
        assertEquals(halfpi, BigFloat.atan2(one, zero, BinaryMathContext.BINARY32));
        assertEquals(halfpi, BigFloat.atan2(one, negzero, BinaryMathContext.BINARY32));
        assertEquals(halfpi, BigFloat.atan2(inf, one, BinaryMathContext.BINARY32));
        assertEquals(halfpi.negate(), BigFloat.atan2(one.negate(), zero, BinaryMathContext.BINARY32));
        assertEquals(halfpi.negate(), BigFloat.atan2(one.negate(), negzero, BinaryMathContext.BINARY32));
        assertEquals(halfpi.negate(), BigFloat.atan2(neginf, one, BinaryMathContext.BINARY32));
        assertEquals(halfpi.divide(two, BinaryMathContext.BINARY32), BigFloat.atan2(inf, inf, BinaryMathContext.BINARY32));
        BigFloat threeQuartersPi = halfpi.multiply(three, BinaryMathContext.BINARY32).divide(two, BinaryMathContext.BINARY32);
        assertEquals(threeQuartersPi, BigFloat.atan2(inf, neginf, BinaryMathContext.BINARY32));
        assertEquals(halfpi.divide(two, BinaryMathContext.BINARY32).negate(), BigFloat.atan2(neginf, inf, BinaryMathContext.BINARY32));
        assertEquals(threeQuartersPi.negate(), BigFloat.atan2(neginf, neginf, BinaryMathContext.BINARY32));
        
        assertEquals(one, three.pow(zero, mc));
        assertEquals(one, three.pow(negzero, mc));
        assertEquals(three, three.pow(one, mc));
        assertEquals(nan, three.pow(nan, mc));
        assertEquals(nan, nan.pow(one, mc));
        assertEquals(one, nan.pow(zero, mc));
        assertEquals(inf, three.pow(inf, mc));
        assertEquals(inf, three.negate().pow(inf, mc));
        assertEquals(inf, half.pow(neginf, mc));
        assertEquals(inf, half.negate().pow(neginf, mc));
        assertEquals(zero, three.pow(neginf, mc));
        assertEquals(zero, three.negate().pow(neginf, mc));
        assertEquals(zero, half.pow(inf, mc));
        assertEquals(zero, half.negate().pow(inf, mc));
        assertEquals(one, one.pow(inf, mc));
        assertEquals(one, one.pow(neginf, mc));
        assertEquals(one, one.negate().pow(inf, mc));
        assertEquals(one, one.negate().pow(neginf, mc));
        assertEquals(zero, zero.pow(one, mc));
        assertEquals(zero, inf.pow(one.negate(), mc));
        assertEquals(inf, zero.pow(one.negate(), mc));
        assertEquals(inf, inf.pow(one, mc));
        assertEquals(zero, negzero.pow(two, mc));
        assertEquals(zero, negzero.pow(inf, mc));
        assertEquals(zero, neginf.pow(two.negate(), mc));
        assertEquals(zero, neginf.pow(neginf, mc));
        assertEquals(negzero, negzero.pow(three, mc));
        assertEquals(negzero, neginf.pow(three.negate(), mc));
        assertEquals(inf, negzero.pow(two.negate(), mc));
        assertEquals(inf, negzero.pow(neginf, mc));
        assertEquals(inf, neginf.pow(two, mc));
        assertEquals(inf, neginf.pow(inf, mc));
        assertEquals(neginf, negzero.pow(three.negate(), mc));
        assertEquals(neginf, neginf.pow(three, mc));
        assertEquals(three.negate().pow(two, mc), three.pow(two, mc));
        assertEquals(three.negate().pow(three, mc), three.pow(three, mc).negate());
        assertEquals(nan, three.negate().pow(one.divide(three, BinaryMathContext.BINARY32), mc));
        
        assertEquals(zero, zero.abs());
        assertEquals(zero, negzero.abs());
        assertEquals(inf, inf.abs());
        assertEquals(inf, neginf.abs());
        assertEquals(nan, nan.abs());

        assertEquals(one, BigFloat.min(nan, one, mc));
        assertEquals(one, BigFloat.min(one, nan, mc));
        assertEquals(negzero, BigFloat.min(negzero, zero, mc));
        
        assertEquals(one, BigFloat.max(nan, one, mc));
        assertEquals(one, BigFloat.max(one, nan, mc));
        assertEquals(zero, BigFloat.max(negzero, zero, mc));
        
        assertDoubleEquals(0.0, zero.signum());
        assertDoubleEquals(-0.0, negzero.signum());
        assertDoubleEquals(1.0, three.signum());
        assertDoubleEquals(-1.0, three.negate().signum());
        assertDoubleEquals(1.0, inf.signum());
        assertDoubleEquals(-1.0, neginf.signum());
        assertDoubleEquals(Double.NaN, nan.signum());
        
        assertEquals(nan, nan.sinh(mc));
        assertEquals(inf, inf.sinh(mc));
        assertEquals(neginf, neginf.sinh(mc));
        assertEquals(zero, zero.sinh(mc));
        assertEquals(negzero, negzero.sinh(mc));
        
        assertEquals(nan, nan.cosh(mc));
        assertEquals(inf, inf.cosh(mc));
        assertEquals(inf, neginf.cosh(mc));
        assertEquals(one, zero.cosh(mc));
        assertEquals(one, negzero.cosh(mc));
        
        assertEquals(nan, nan.tanh(mc));
        assertEquals(zero, zero.tanh(mc));
        assertEquals(negzero, negzero.tanh(mc));
        assertEquals(one, inf.tanh(mc));
        assertEquals(one.negate(), neginf.tanh(mc));
        
        long emin = mc.getMinExponent();
        long emax = mc.getMaxExponent();
        BigFloat nanDouble = BigFloat.NaN(53);
        assertEquals(nanDouble, nanDouble.nextAfter(one, emin, emax));
        assertEquals(nanDouble, one.nextAfter(nanDouble, emin, emax));
        assertEquals(nanDouble, nan.nextAfter(nanDouble, emin, emax));
        assertEquals(zero, negzero.nextAfter(zero, emin, emax));
        assertEquals(negzero, zero.nextAfter(negzero, emin, emax));
        assertEquals(zero, BigFloat.minValue(24, emin).nextAfter(neginf, emin, emax));
        assertEquals(zero, BigFloat.minValue(24, emin).nextDown(emin, emax));
        assertEquals(negzero, BigFloat.minValue(24, emin).negate().nextAfter(inf, emin, emax));
        assertEquals(negzero, BigFloat.minValue(24, emin).negate().nextUp(emin, emax));
        assertEquals(BigFloat.maxValue(24, emax), inf.nextAfter(zero, emin, emax));
        assertEquals(BigFloat.maxValue(24, emax), inf.nextDown(emin, emax));
        assertEquals(BigFloat.maxValue(24, emax).negate(), neginf.nextAfter(zero, emin, emax));
        assertEquals(BigFloat.maxValue(24, emax).negate(), neginf.nextUp(emin, emax));
        assertEquals(inf, BigFloat.maxValue(24, emax).nextAfter(inf, emin, emax));
        assertEquals(inf, BigFloat.maxValue(24, emax).nextUp(emin, emax));
        assertEquals(neginf, BigFloat.maxValue(24, emax).negate().nextAfter(neginf, emin, emax));
        assertEquals(neginf, BigFloat.maxValue(24, emax).negate().nextDown(emin, emax));
        try {
            BigFloat.minValue(53, Double.MIN_EXPONENT).floatValueExact();
        } catch (ArithmeticException e) {}
        
        assertEquals(Math.getExponent(50), new BigFloat(50, BinaryMathContext.BINARY32).exponent());
        try {
            nan.exponent();
        } catch (ArithmeticException e) {}
        try {
            inf.exponent();
        } catch (ArithmeticException e) {}
        try {
            neginf.exponent();
        } catch (ArithmeticException e) {}
        try {
            zero.exponent();
        } catch (ArithmeticException e) {}
        try {
            negzero.exponent();
        } catch (ArithmeticException e) {}
        assertEquals(-1074, BigFloat.minValue(53, Double.MIN_EXPONENT).exponent());
        assertEquals(-1022, BigFloat.minNormal(53, Double.MIN_EXPONENT).exponent());
    }
    
    @Test
    public void testRoundingModes() {
        assertEquals(4, new BigFloat(5, new BinaryMathContext(2, RoundingMode.HALF_EVEN)).longValueExact());
        assertEquals(4, new BigFloat(5, new BinaryMathContext(2, RoundingMode.DOWN)).longValueExact());
        assertEquals(4, new BigFloat(5, new BinaryMathContext(2, RoundingMode.FLOOR)).longValueExact());
        assertEquals(6, new BigFloat(5, new BinaryMathContext(2, RoundingMode.CEILING)).longValueExact());
        assertEquals(6, new BigFloat(5, new BinaryMathContext(2, RoundingMode.UP)).longValueExact());
        assertEquals(-4, new BigFloat(-5, new BinaryMathContext(2, RoundingMode.HALF_EVEN)).longValueExact());
        assertEquals(-4, new BigFloat(-5, new BinaryMathContext(2, RoundingMode.DOWN)).longValueExact());
        assertEquals(-6, new BigFloat(-5, new BinaryMathContext(2, RoundingMode.FLOOR)).longValueExact());
        assertEquals(-4, new BigFloat(-5, new BinaryMathContext(2, RoundingMode.CEILING)).longValueExact());
        assertEquals(-6, new BigFloat(-5, new BinaryMathContext(2, RoundingMode.UP)).longValueExact());
        assertEquals(8, new BigFloat(7, new BinaryMathContext(2, RoundingMode.HALF_EVEN)).longValueExact());
        assertEquals(6, new BigFloat(7, new BinaryMathContext(2, RoundingMode.DOWN)).longValueExact());
        assertEquals(6, new BigFloat(7, new BinaryMathContext(2, RoundingMode.FLOOR)).longValueExact());
        assertEquals(8, new BigFloat(7, new BinaryMathContext(2, RoundingMode.CEILING)).longValueExact());
        assertEquals(8, new BigFloat(7, new BinaryMathContext(2, RoundingMode.UP)).longValueExact());
        assertEquals(-8, new BigFloat(-7, new BinaryMathContext(2, RoundingMode.HALF_EVEN)).longValueExact());
        assertEquals(-6, new BigFloat(-7, new BinaryMathContext(2, RoundingMode.DOWN)).longValueExact());
        assertEquals(-8, new BigFloat(-7, new BinaryMathContext(2, RoundingMode.FLOOR)).longValueExact());
        assertEquals(-6, new BigFloat(-7, new BinaryMathContext(2, RoundingMode.CEILING)).longValueExact());
        assertEquals(-8, new BigFloat(-7, new BinaryMathContext(2, RoundingMode.UP)).longValueExact());
    }
    
    @Test
    public void testExponentRange() {
        assertDoubleEquals(Double.MAX_VALUE, BigFloat.maxValue(53, Double.MAX_EXPONENT).doubleValueExact());
        assertDoubleEquals(Double.MIN_NORMAL, BigFloat.minNormal(53, Double.MIN_EXPONENT).doubleValueExact());
        assertDoubleEquals(Double.MIN_VALUE, BigFloat.minValue(53, Double.MIN_EXPONENT).doubleValueExact());
        assertFloatEquals(Float.MAX_VALUE, BigFloat.maxValue(24, Float.MAX_EXPONENT).floatValueExact());
        assertFloatEquals(Float.MIN_NORMAL, BigFloat.minNormal(24, Float.MIN_EXPONENT).floatValueExact());
        assertFloatEquals(Float.MIN_VALUE, BigFloat.minValue(24, Float.MIN_EXPONENT).floatValueExact());
        try {
            BigFloat.minValue(53, -52);
        } catch (ArithmeticException e) {}
    }
    
    @Test
    public void testSubnormal() {
        BigFloat a = new BigFloat("34.3", BinaryMathContext.BINARY64);
        BigFloat b = new BigFloat("0x1.1235P-1021", BinaryMathContext.BINARY64);
        a = a.divide(b, BinaryMathContext.BINARY64);
        assertDoubleEquals(34.3/0x1.1235P-1021, a.doubleValueExact());
    }
}
