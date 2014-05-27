// Copyright (c) 2014 K Team. All Rights Reserved.
package org.kframework.mpfr;

import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.ObjectStreamException;
import java.io.Serializable;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CodingErrorAction;
import java.util.Arrays;

import org.fusesource.hawtjni.runtime.*;

import static org.fusesource.hawtjni.runtime.ArgFlag.*;
import static org.fusesource.hawtjni.runtime.ClassFlag.*;
import static org.fusesource.hawtjni.runtime.FieldFlag.*;
import static org.fusesource.hawtjni.runtime.MethodFlag.*;


/**
 * Contains all the native interface code, including both JNI generation code and
 * code pertaining to interacting with things like C pointers. For code specific
 * to the interface between which {@link BigFloat} wraps MPFR, see {@link BigFloat}.
 * 
 * For information detailing the behavior of POSIX native functions in this class, see
 * {@code man strlen}, {@code man malloc}, and {@code man memmove}. 
 * 
 * For information detailing the behavior of native MPFR functions, see
 * http://www.mpfr.org/mpfr-current/mpfr.html
 * 
 * For information detailing the behavior of native GMP functions, see
 * https://gmplib.org/manual/Integer-Functions.html
 * 
 * @author Dwight Guth
 *
 */
@JniClass
final class mpfr {
    private mpfr() {}
    
    private static final Library LIBRARY = new Library("mpfr_java", mpfr.class);
    static {
        LIBRARY.load();
        init();
    }

    @JniMethod(flags={CONSTANT_INITIALIZER})
    private static native void init();
    
    @JniMethod(cast="size_t")
    private static native int strlen(@JniArg(cast="char *") long str);
    
    @JniMethod(cast="void *")
    private static native long calloc(
            @JniArg(cast="size_t") long num,
            @JniArg(cast="size_t") long size);
    
    private static native void free(
            @JniArg(cast="void *") long ptr);
    
    private static native void memmove(
            @JniArg(cast="void *", flags={NO_IN, CRITICAL}) long dest, 
            @JniArg(cast="const void *", flags={NO_OUT, CRITICAL}) byte[] src, 
            @JniArg(cast="size_t") long size);
    private static native void memmove(
            @JniArg(cast="void *", flags={NO_IN, CRITICAL}) byte[] dest, 
            @JniArg(cast="const void *", flags={NO_OUT, CRITICAL}) long src, 
            @JniArg(cast="size_t") long size);

    private static native int mpfr_asprintf(
            @JniArg(cast="char **") long[] str,
            String template,
            @JniArg(cast="mpfr_ptr", flags={POINTER_ARG}) mpfr_t x);
    
    private static native void mpfr_free_str(
            @JniArg(cast="char *") long ptr);
    
    static String mpfr_asprintf(
            String template,
            mpfr_t x) {
        long[] ptr = new long[1];
        try {
            int result = mpfr_asprintf(ptr, template, x);
            if (result < 0) {
                throw new IllegalStateException("mpfr_asprintf call failed");
            }
            byte[] bytes = new byte[strlen(ptr[0])];
            memmove(bytes, ptr[0], bytes.length);
            return new String(bytes);
        } finally {
            mpfr_free_str(ptr[0]);
        }
    }
    
    
    private static native void mpfr_init2(
            @JniArg(cast="mpfr_ptr", flags={POINTER_ARG}) mpfr_t x,
            @JniArg(cast="mpfr_prec_t") int prec);
    private static native void mpfr_init2(
            @JniArg(cast="mpfr_ptr", flags={POINTER_ARG}) mpfr_t_full x,
            @JniArg(cast="mpfr_prec_t") int prec);
    private static native void mpfr_clear(
            @JniArg(cast="mpfr_ptr", flags={POINTER_ARG}) mpfr_t x);
    private static native void mpfr_clear(
            @JniArg(cast="mpfr_ptr", flags={POINTER_ARG}) mpfr_t_full x);

    static native boolean mpfr_nan_p(@JniArg(cast="mpfr_ptr", flags={POINTER_ARG}) mpfr_t op);
    static native boolean mpfr_inf_p(@JniArg(cast="mpfr_ptr", flags={POINTER_ARG}) mpfr_t op);
    static native boolean mpfr_zero_p(@JniArg(cast="mpfr_ptr", flags={POINTER_ARG}) mpfr_t op);
    static native boolean mpfr_integer_p(@JniArg(cast="mpfr_ptr", flags={POINTER_ARG}) mpfr_t op);

    static native float mpfr_get_flt(
            @JniArg(cast="mpfr_ptr", flags={POINTER_ARG}) mpfr_t op,
            @JniArg(cast="mpfr_rnd_t") int rnd);
    static native double mpfr_get_d(
            @JniArg(cast="mpfr_ptr", flags={POINTER_ARG}) mpfr_t op,
            @JniArg(cast="mpfr_rnd_t") int rnd);
    static native int mpfr_get_z(
            @JniArg(cast="mpz_ptr", flags={POINTER_ARG}) mpz_t rop,
            @JniArg(cast="mpfr_ptr", flags={POINTER_ARG}) mpfr_t op,
            @JniArg(cast="mpfr_rnd_t") int rnd);
    @JniMethod(cast="mpfr_exp_t")
    static native long mpfr_get_z_2exp(
            @JniArg(cast="mpz_ptr", flags={POINTER_ARG}) mpz_t rop,
            @JniArg(cast="mpfr_ptr", flags={POINTER_ARG}) mpfr_t op);

    static native int mpfr_set(
            @JniArg(cast="mpfr_ptr", flags={POINTER_ARG}) mpfr_t rop,
            @JniArg(cast="mpfr_ptr", flags={POINTER_ARG}) mpfr_t op,
            @JniArg(cast="mpfr_rnd_t") int rnd);
    static native int mpfr_set(
            @JniArg(cast="mpfr_ptr", flags={POINTER_ARG}) mpfr_t_full rop,
            @JniArg(cast="mpfr_ptr", flags={POINTER_ARG}) mpfr_t op,
            @JniArg(cast="mpfr_rnd_t") int rnd);
    static native int mpfr_set(
            @JniArg(cast="mpfr_ptr", flags={POINTER_ARG}) mpfr_t rop,
            @JniArg(cast="mpfr_ptr", flags={POINTER_ARG}) mpfr_t_full op,
            @JniArg(cast="mpfr_rnd_t") int rnd);
    static native int mpfr_set_si_2exp(
            @JniArg(cast="mpfr_ptr", flags={POINTER_ARG}) mpfr_t rop,
            int op,
            @JniArg(cast="mpfr_exp_t") long e,
            @JniArg(cast="mpfr_rnd_t") int rnd);
    static native int mpfr_set_d(
            @JniArg(cast="mpfr_ptr", flags={POINTER_ARG}) mpfr_t rop,
            double op,
            @JniArg(cast="mpfr_rnd_t") int rnd);
    static native int mpfr_set_z(
            @JniArg(cast="mpfr_ptr", flags={POINTER_ARG}) mpfr_t rop,
            @JniArg(cast="mpz_ptr", flags={POINTER_ARG}) mpz_t op,
            @JniArg(cast="mpfr_rnd_t") int rnd);
    static native int mpfr_set_z_2exp(
            @JniArg(cast="mpfr_ptr", flags={POINTER_ARG}) mpfr_t rop,
            @JniArg(cast="mpz_ptr", flags={POINTER_ARG}) mpz_t op,
            @JniArg(cast="mpfr_exp_t") long e,
            @JniArg(cast="mpfr_rnd_t") int rnd);
    private static native int mpfr_strtofr(
            @JniArg(cast="mpfr_ptr", flags={POINTER_ARG}) mpfr_t rop,
            long nptr,
            @JniArg(cast="char **")long[] endptr,
            int base,
            @JniArg(cast="mpfr_rnd_t") int rnd);
    
    /**
     * Wraps {@link #mpfr_set_str(mpfr_t, byte[], int, int)} to accept a String.
     * Because MPFR does not accept Unicode strings, we use the US-ASCII charset and
     * throw a NumberFormatException if the string is not expressible in ASCII.
     * @param rop the struct to initialize
     * @param s the String to read a float from
     * @param base The base to pass to {@link #mpfr_strtofr}.
     * @param rnd The rounding mode to pass to {@link #mpfr_strtofr}
     * @return true if the result was rounded; false otherwise
     * @throws NumberFormatException if the string did not parse as a float.
     */
    static int mpfr_set_str(
            mpfr_t rop,
            String s,
            int base,
            int rnd) {
        try {
            ByteBuffer bytes = Charset.forName("US-ASCII").newEncoder()
                    .onMalformedInput(CodingErrorAction.REPORT)
                    .onUnmappableCharacter(CodingErrorAction.REPORT)
                    .encode(CharBuffer.wrap(s.toCharArray()));
            byte[] byteArray = new byte[bytes.remaining()];
            bytes.get(byteArray);
            return mpfr_set_str(rop, byteArray, base, rnd);
        } catch (CharacterCodingException e) {
            throw new NumberFormatException(s);
        }
    }
    
    /**
     * In order to support {@link RoundingMode.UNNECESSARY}, we need the ternary result of
     * {@link #mpfr_strtofr}. However, making this function act like MPFR's 
     * {@code mpfr_set_str} is nontrivial, because you have to allocate
     * the string being read on the native heap, then call {@link #strlen} on the
     * value returned in endptr. But this is all functionality specific
     * to C and JNI rather than anything relevant to floating points, so I encapsulate
     * this functionality in this class instead of putting it in {@link BigFloat}.
     * @param rop the struct to initialize
     * @return true if the result was rounded; false otherwise
     * @throws NumberFormatException if the string did not parse as a float.
     */
    static int mpfr_set_str(
            mpfr_t rop,
            byte[] s,
            int base,
            int rnd) {
        long ptr = 0;
        try {
            ptr = calloc(s.length + 1, 1); // +1 for null byte
            memmove(ptr, s, s.length);
            long[] endptr = new long[1];
            int result = mpfr_strtofr(rop, ptr, endptr, base, rnd);
            if (strlen(endptr[0]) != 0) {
                //didn't read the entire string, therefore, it was not a float
                throw new NumberFormatException(new String(s));
            }
            return result;
        } finally {
            if (ptr != 0) {
                free(ptr);
            }
        }
    }
    
    static native void mpfr_set_inf(
            @JniArg(cast="mpfr_ptr", flags={POINTER_ARG}) mpfr_t x,
            int sign);
    static native void mpfr_set_zero(
            @JniArg(cast="mpfr_ptr", flags={POINTER_ARG}) mpfr_t x,
            int sign);
    
    static native int mpfr_add(
            @JniArg(cast="mpfr_ptr", flags={POINTER_ARG}) mpfr_t rop,
            @JniArg(cast="mpfr_ptr", flags={POINTER_ARG}) mpfr_t op1,
            @JniArg(cast="mpfr_ptr", flags={POINTER_ARG}) mpfr_t op2,
            @JniArg(cast="mpfr_rnd_t") int rnd);
    static native int mpfr_sub(
            @JniArg(cast="mpfr_ptr", flags={POINTER_ARG}) mpfr_t rop,
            @JniArg(cast="mpfr_ptr", flags={POINTER_ARG}) mpfr_t op1,
            @JniArg(cast="mpfr_ptr", flags={POINTER_ARG}) mpfr_t op2,
            @JniArg(cast="mpfr_rnd_t") int rnd);
    static native int mpfr_mul(
            @JniArg(cast="mpfr_ptr", flags={POINTER_ARG}) mpfr_t rop,
            @JniArg(cast="mpfr_ptr", flags={POINTER_ARG}) mpfr_t op1,
            @JniArg(cast="mpfr_ptr", flags={POINTER_ARG}) mpfr_t op2,
            @JniArg(cast="mpfr_rnd_t") int rnd);
    static native int mpfr_div(
            @JniArg(cast="mpfr_ptr", flags={POINTER_ARG}) mpfr_t rop,
            @JniArg(cast="mpfr_ptr", flags={POINTER_ARG}) mpfr_t op1,
            @JniArg(cast="mpfr_ptr", flags={POINTER_ARG}) mpfr_t op2,
            @JniArg(cast="mpfr_rnd_t") int rnd);
    static native int mpfr_remainder(
            @JniArg(cast="mpfr_ptr", flags={POINTER_ARG}) mpfr_t r,
            @JniArg(cast="mpfr_ptr", flags={POINTER_ARG}) mpfr_t x,
            @JniArg(cast="mpfr_ptr", flags={POINTER_ARG}) mpfr_t y,
            @JniArg(cast="mpfr_rnd_t") int rnd);
    static native int mpfr_pow(
            @JniArg(cast="mpfr_ptr", flags={POINTER_ARG}) mpfr_t rop,
            @JniArg(cast="mpfr_ptr", flags={POINTER_ARG}) mpfr_t op1,
            @JniArg(cast="mpfr_ptr", flags={POINTER_ARG}) mpfr_t op2,
            @JniArg(cast="mpfr_rnd_t") int rnd);
    static native int mpfr_root(
            @JniArg(cast="mpfr_ptr", flags={POINTER_ARG}) mpfr_t rop,
            @JniArg(cast="mpfr_ptr", flags={POINTER_ARG}) mpfr_t op,
            int k,
            @JniArg(cast="mpfr_rnd_t") int rnd);
    static native int mpfr_neg(
            @JniArg(cast="mpfr_ptr", flags={POINTER_ARG}) mpfr_t rop,
            @JniArg(cast="mpfr_ptr", flags={POINTER_ARG}) mpfr_t op,
            @JniArg(cast="mpfr_rnd_t") int rnd);
    static native int mpfr_abs(
            @JniArg(cast="mpfr_ptr", flags={POINTER_ARG}) mpfr_t rop,
            @JniArg(cast="mpfr_ptr", flags={POINTER_ARG}) mpfr_t op,
            @JniArg(cast="mpfr_rnd_t") int rnd);
    static native int mpfr_log(
            @JniArg(cast="mpfr_ptr", flags={POINTER_ARG}) mpfr_t rop,
            @JniArg(cast="mpfr_ptr", flags={POINTER_ARG}) mpfr_t op,
            @JniArg(cast="mpfr_rnd_t") int rnd);
    static native int mpfr_log10(
            @JniArg(cast="mpfr_ptr", flags={POINTER_ARG}) mpfr_t rop,
            @JniArg(cast="mpfr_ptr", flags={POINTER_ARG}) mpfr_t op,
            @JniArg(cast="mpfr_rnd_t") int rnd);
    static native int mpfr_exp(
            @JniArg(cast="mpfr_ptr", flags={POINTER_ARG}) mpfr_t rop,
            @JniArg(cast="mpfr_ptr", flags={POINTER_ARG}) mpfr_t op,
            @JniArg(cast="mpfr_rnd_t") int rnd);
    static native int mpfr_exp10(
            @JniArg(cast="mpfr_ptr", flags={POINTER_ARG}) mpfr_t rop,
            @JniArg(cast="mpfr_ptr", flags={POINTER_ARG}) mpfr_t op,
            @JniArg(cast="mpfr_rnd_t") int rnd);
    static native int mpfr_sin(
            @JniArg(cast="mpfr_ptr", flags={POINTER_ARG}) mpfr_t rop,
            @JniArg(cast="mpfr_ptr", flags={POINTER_ARG}) mpfr_t op,
            @JniArg(cast="mpfr_rnd_t") int rnd);
    static native int mpfr_cos(
            @JniArg(cast="mpfr_ptr", flags={POINTER_ARG}) mpfr_t rop,
            @JniArg(cast="mpfr_ptr", flags={POINTER_ARG}) mpfr_t op,
            @JniArg(cast="mpfr_rnd_t") int rnd);
    static native int mpfr_tan(
            @JniArg(cast="mpfr_ptr", flags={POINTER_ARG}) mpfr_t rop,
            @JniArg(cast="mpfr_ptr", flags={POINTER_ARG}) mpfr_t op,
            @JniArg(cast="mpfr_rnd_t") int rnd);
    static native int mpfr_sec(
            @JniArg(cast="mpfr_ptr", flags={POINTER_ARG}) mpfr_t rop,
            @JniArg(cast="mpfr_ptr", flags={POINTER_ARG}) mpfr_t op,
            @JniArg(cast="mpfr_rnd_t") int rnd);
    static native int mpfr_csc(
            @JniArg(cast="mpfr_ptr", flags={POINTER_ARG}) mpfr_t rop,
            @JniArg(cast="mpfr_ptr", flags={POINTER_ARG}) mpfr_t op,
            @JniArg(cast="mpfr_rnd_t") int rnd);
    static native int mpfr_cot(
            @JniArg(cast="mpfr_ptr", flags={POINTER_ARG}) mpfr_t rop,
            @JniArg(cast="mpfr_ptr", flags={POINTER_ARG}) mpfr_t op,
            @JniArg(cast="mpfr_rnd_t") int rnd);
    static native int mpfr_asin(
            @JniArg(cast="mpfr_ptr", flags={POINTER_ARG}) mpfr_t rop,
            @JniArg(cast="mpfr_ptr", flags={POINTER_ARG}) mpfr_t op,
            @JniArg(cast="mpfr_rnd_t") int rnd);
    static native int mpfr_acos(
            @JniArg(cast="mpfr_ptr", flags={POINTER_ARG}) mpfr_t rop,
            @JniArg(cast="mpfr_ptr", flags={POINTER_ARG}) mpfr_t op,
            @JniArg(cast="mpfr_rnd_t") int rnd);
    static native int mpfr_atan(
            @JniArg(cast="mpfr_ptr", flags={POINTER_ARG}) mpfr_t rop,
            @JniArg(cast="mpfr_ptr", flags={POINTER_ARG}) mpfr_t op,
            @JniArg(cast="mpfr_rnd_t") int rnd);
    static native int mpfr_atan2(
            @JniArg(cast="mpfr_ptr", flags={POINTER_ARG}) mpfr_t rop,
            @JniArg(cast="mpfr_ptr", flags={POINTER_ARG}) mpfr_t y,
            @JniArg(cast="mpfr_ptr", flags={POINTER_ARG}) mpfr_t x,
            @JniArg(cast="mpfr_rnd_t") int rnd);
    static native int mpfr_cosh(
            @JniArg(cast="mpfr_ptr", flags={POINTER_ARG}) mpfr_t rop,
            @JniArg(cast="mpfr_ptr", flags={POINTER_ARG}) mpfr_t op,
            @JniArg(cast="mpfr_rnd_t") int rnd);
    static native int mpfr_sinh(
            @JniArg(cast="mpfr_ptr", flags={POINTER_ARG}) mpfr_t rop,
            @JniArg(cast="mpfr_ptr", flags={POINTER_ARG}) mpfr_t op,
            @JniArg(cast="mpfr_rnd_t") int rnd);
    static native int mpfr_tanh(
            @JniArg(cast="mpfr_ptr", flags={POINTER_ARG}) mpfr_t rop,
            @JniArg(cast="mpfr_ptr", flags={POINTER_ARG}) mpfr_t op,
            @JniArg(cast="mpfr_rnd_t") int rnd);
    static native int mpfr_sech(
            @JniArg(cast="mpfr_ptr", flags={POINTER_ARG}) mpfr_t rop,
            @JniArg(cast="mpfr_ptr", flags={POINTER_ARG}) mpfr_t op,
            @JniArg(cast="mpfr_rnd_t") int rnd);
    static native int mpfr_csch(
            @JniArg(cast="mpfr_ptr", flags={POINTER_ARG}) mpfr_t rop,
            @JniArg(cast="mpfr_ptr", flags={POINTER_ARG}) mpfr_t op,
            @JniArg(cast="mpfr_rnd_t") int rnd);
    static native int mpfr_coth(
            @JniArg(cast="mpfr_ptr", flags={POINTER_ARG}) mpfr_t rop,
            @JniArg(cast="mpfr_ptr", flags={POINTER_ARG}) mpfr_t op,
            @JniArg(cast="mpfr_rnd_t") int rnd);
    static native int mpfr_acosh(
            @JniArg(cast="mpfr_ptr", flags={POINTER_ARG}) mpfr_t rop,
            @JniArg(cast="mpfr_ptr", flags={POINTER_ARG}) mpfr_t op,
            @JniArg(cast="mpfr_rnd_t") int rnd);
    static native int mpfr_asinh(
            @JniArg(cast="mpfr_ptr", flags={POINTER_ARG}) mpfr_t rop,
            @JniArg(cast="mpfr_ptr", flags={POINTER_ARG}) mpfr_t op,
            @JniArg(cast="mpfr_rnd_t") int rnd);
    static native int mpfr_atanh(
            @JniArg(cast="mpfr_ptr", flags={POINTER_ARG}) mpfr_t rop,
            @JniArg(cast="mpfr_ptr", flags={POINTER_ARG}) mpfr_t op,
            @JniArg(cast="mpfr_rnd_t") int rnd);
    static native int mpfr_rint(
            @JniArg(cast="mpfr_ptr", flags={POINTER_ARG}) mpfr_t rop,
            @JniArg(cast="mpfr_ptr", flags={POINTER_ARG}) mpfr_t op,
            @JniArg(cast="mpfr_rnd_t") int rnd);
    static native int mpfr_min(
            @JniArg(cast="mpfr_ptr", flags={POINTER_ARG}) mpfr_t rop,
            @JniArg(cast="mpfr_ptr", flags={POINTER_ARG}) mpfr_t op1,
            @JniArg(cast="mpfr_ptr", flags={POINTER_ARG}) mpfr_t op2,
            @JniArg(cast="mpfr_rnd_t") int rnd);
    static native int mpfr_max(
            @JniArg(cast="mpfr_ptr", flags={POINTER_ARG}) mpfr_t rop,
            @JniArg(cast="mpfr_ptr", flags={POINTER_ARG}) mpfr_t op1,
            @JniArg(cast="mpfr_ptr", flags={POINTER_ARG}) mpfr_t op2,
            @JniArg(cast="mpfr_rnd_t") int rnd);
    static native int mpfr_const_pi(
            @JniArg(cast="mpfr_ptr", flags={POINTER_ARG}) mpfr_t rop,
            @JniArg(cast="mpfr_rnd_t") int rnd);
    static native int mpfr_const_euler(
            @JniArg(cast="mpfr_ptr", flags={POINTER_ARG}) mpfr_t rop,
            @JniArg(cast="mpfr_rnd_t") int rnd);
    static native void mpfr_nexttoward(
            @JniArg(cast="mpfr_ptr", flags={POINTER_ARG}) mpfr_t x,
            @JniArg(cast="mpfr_ptr", flags={POINTER_ARG}) mpfr_t y);
    static native void mpfr_nextabove(
            @JniArg(cast="mpfr_ptr", flags={POINTER_ARG}) mpfr_t x);
    static native void mpfr_nextbelow(
            @JniArg(cast="mpfr_ptr", flags={POINTER_ARG}) mpfr_t x);

    static native int mpfr_cmp(
            @JniArg(cast="mpfr_ptr", flags={POINTER_ARG}) mpfr_t op1,
            @JniArg(cast="mpfr_ptr", flags={POINTER_ARG}) mpfr_t op2);
    static native int mpfr_cmp_d(
            @JniArg(cast="mpfr_ptr", flags={POINTER_ARG}) mpfr_t op1,
            double op2);
    static native boolean mpfr_greater_p(
            @JniArg(cast="mpfr_ptr", flags={POINTER_ARG}) mpfr_t op1,
            @JniArg(cast="mpfr_ptr", flags={POINTER_ARG}) mpfr_t op2);
    static native boolean mpfr_greaterequal_p(
            @JniArg(cast="mpfr_ptr", flags={POINTER_ARG}) mpfr_t op1,
            @JniArg(cast="mpfr_ptr", flags={POINTER_ARG}) mpfr_t op2);
    static native boolean mpfr_less_p(
            @JniArg(cast="mpfr_ptr", flags={POINTER_ARG}) mpfr_t op1,
            @JniArg(cast="mpfr_ptr", flags={POINTER_ARG}) mpfr_t op2);
    static native boolean mpfr_lessequal_p(
            @JniArg(cast="mpfr_ptr", flags={POINTER_ARG}) mpfr_t op1,
            @JniArg(cast="mpfr_ptr", flags={POINTER_ARG}) mpfr_t op2);
    static native boolean mpfr_equal_p(
            @JniArg(cast="mpfr_ptr", flags={POINTER_ARG}) mpfr_t op1,
            @JniArg(cast="mpfr_ptr", flags={POINTER_ARG}) mpfr_t op2);
    
    static native boolean mpfr_prec_round(
            @JniArg(cast="mpfr_ptr", flags={POINTER_ARG}) mpfr_t x,
            @JniArg(cast="mpfr_prec_t") int prec,
            @JniArg(cast="mpfr_rnd_t") int rnd);
    

    static native boolean mpfr_set_emin(
            @JniArg(cast="mpfr_exp_t") long exp);
    static native boolean mpfr_set_emax(
            @JniArg(cast="mpfr_exp_t") long exp);
    static native int mpfr_check_range(
            @JniArg(cast="mpfr_ptr", flags={POINTER_ARG}) mpfr_t x,
            int t,
            @JniArg(cast="mpfr_rnd_t") int rnd);
    static native boolean mpfr_subnormalize(
            @JniArg(cast="mpfr_ptr", flags={POINTER_ARG}) mpfr_t x,
            int t,
            @JniArg(cast="mpfr_rnd_t") int rnd);
    
    static native boolean mpfr_signbit(
            @JniArg(cast="mpfr_ptr", flags={POINTER_ARG}) mpfr_t op);
    static native boolean mpfr_setsign(
            @JniArg(cast="mpfr_ptr", flags={POINTER_ARG}) mpfr_t rop,
            @JniArg(cast="mpfr_ptr", flags={POINTER_ARG}) mpfr_t op,
            boolean s,
            @JniArg(cast="mpfr_rnd_t") int rnd);
    
    @JniField(flags={CONSTANT})
    static int MPFR_RNDN; // round to nearest, with ties to even
    @JniField(flags={CONSTANT})
    static int MPFR_RNDZ; // round toward zero
    @JniField(flags={CONSTANT})
    static int MPFR_RNDU; // round toward +Inf
    @JniField(flags={CONSTANT})
    static int MPFR_RNDD; // round toward -Inf
    @JniField(flags={CONSTANT})
    static int MPFR_RNDA; // round away from zero
    
    @JniField(flags={CONSTANT})
    static long MPFR_PREC_MIN;
    @JniField(flags={CONSTANT})
    static long MPFR_PREC_MAX;
    @JniField(flags={CONSTANT}, accessor="mpfr_get_emax()")
    static long MPFR_EMAX_DEFAULT;
    @JniField(flags={CONSTANT}, accessor="mpfr_get_emin()")
    static long MPFR_EMIN_DEFAULT;
    
    @JniField(flags={CONSTANT}, accessor="sizeof(mp_limb_t)")
    static short LIMB_SIZE;

    /**
     * A Java representation of the C mpfr_t type.
     * 
     * Technically accessing the struct directly is a hack because we're not supposed to
     * look internally at the struct that mpfr_t contains, however, it's the only way to
     * do this with the JNI framework we're using.
     * @author Dwight Guth
     *
     */
    @JniClass(name="__mpfr_struct", flags={STRUCT, TYPEDEF})
    static final class mpfr_t implements Serializable {
        static {
            LIBRARY.load();
        } 
        
        /**
         * Construct a new mpfr_t object and initialize it with the specified
         * {@code precision}.
         * @param precision The bits of precision to call {@link mpfr#mpfr_init2} with.
         */
        mpfr_t(int precision) {
            if (precision < MPFR_PREC_MIN || precision > MPFR_PREC_MAX) {
                throw new IllegalArgumentException("invalid precision");
            }
            mpfr_init2(this, precision);
        }
        
        /**
         * Construct a new mpfr_t object copied from {@code copy}.
         * @param copy The struct to copy.
         */
        mpfr_t(mpfr_t copy) {
            mpfr_init2(this, copy._mpfr_prec);
            int i = mpfr_set(this, copy, MPFR_RNDN);
            assert i == 0 : "should not ever lose copying an mpfr_t";
        }
        
        /**
         * Construct a new mpfr_t object copied from {@code copy}.
         * @param copy The struct to copy.
         */
        mpfr_t(mpfr_t_full copy) {
            mpfr_init2(this, copy._mpfr_prec);
            _mpfr_prec = copy._mpfr_prec;
            _mpfr_sign = copy._mpfr_sign;
            _mpfr_exp = copy._mpfr_exp;
            memmove(_mpfr_d, copy._mpfr_d, copy._mpfr_d.length);
        }
        
        /**
         * Release native resources for this struct.
         */
        @Override
        protected void finalize() throws Throwable {
            if (_mpfr_d != 0) {
                mpfr_clear(this);
            }
        }
        
        @JniField(cast="mpfr_prec_t") int _mpfr_prec;
        @JniField(cast="mpfr_sign_t") int _mpfr_sign;
        @JniField(cast="mpfr_exp_t") long _mpfr_exp;
        @JniField(cast="mp_limb_t *") long _mpfr_d;
        
        private Object writeReplace() throws ObjectStreamException {
            return new mpfr_t_full(this);
        }
        
        private void readObject(ObjectInputStream stream) throws InvalidObjectException {
            throw new InvalidObjectException("Proxy required");
        }
    }
    
    /**
     * A Java representation of the C mpfr_t type, including the complete
     * significand.
     * 
     * This class is used as a serialization proxy for the mpfr_t class. We do
     * this because otherwise only the pointer to the limbs would be written
     * during serialization, which would cause the information to be lost
     * when the native memory was cleaned up. We otherwise do not use this
     * class, because it would require copying the entire significand in
     * memory every time an operation was performed.
     * @author Dwight Guth
     *
     */
    @JniClass(name="__mpfr_struct", flags={STRUCT, TYPEDEF})
    static final class mpfr_t_full implements Serializable {

        private static final long serialVersionUID = -16063242942983590L;

        static {
            LIBRARY.load();
        } 
        
        /**
         * Construct a new mpfr_t object copied from {@code copy}.
         * @param copy The struct to copy.
         */
        mpfr_t_full(mpfr_t copy) {
            //mpfr_init2(this, copy._mpfr_prec);
            _mpfr_prec = copy._mpfr_prec;
            _mpfr_sign = copy._mpfr_sign;
            _mpfr_exp = copy._mpfr_exp;
            _mpfr_d = new byte[getNumberOfBytesInLimbs()];
            memmove(_mpfr_d, copy._mpfr_d, _mpfr_d.length);
        }
        
        @JniField(cast="mpfr_prec_t") int _mpfr_prec;
        @JniField(cast="mpfr_sign_t") int _mpfr_sign;
        @JniField(cast="mpfr_exp_t") long _mpfr_exp;
        @JniField(cast="mp_limb_t *") byte[] _mpfr_d;
        
        private int getNumberOfBytesInLimbs() {
            int numLimbs = ceil(_mpfr_prec, LIMB_SIZE * 8);
            return numLimbs * LIMB_SIZE;
        }
        
        private Object readResolve() throws ObjectStreamException {
            return new mpfr_t(this);
        }
    }
    
    private static int ceil(int x, int n) {
        return (x + n - 1) / n;
    }
    
    private static native void mpz_init(
            @JniArg(cast="mpz_ptr", flags={POINTER_ARG}) mpz_t x);
    private static native void mpz_clear(
            @JniArg(cast="mpz_ptr", flags={POINTER_ARG}) mpz_t x);
    @JniMethod(cast="size_t")
    private static native int mpz_sizeinbase(
            @JniArg(cast="mpz_ptr", flags={POINTER_ARG}) mpz_t op,
            int base);
    private static native String mpz_get_str(
            byte[] str,
            int base,
            @JniArg(cast="mpz_ptr", flags={POINTER_ARG}) mpz_t op);
    private static native int mpz_set_str(
            @JniArg(cast="mpz_ptr", flags={POINTER_ARG}) mpz_t rop,
            String str,
            int base);
    
    /**
     * Return a string containing the digits of the specified mpz_t object in the specified base.
     * @param base The base to get a list of digits in. Behavior is undefined if base is not in
     * the range 2..62 or -2..-36.
     * @param op The mpz_t to print
     * @return A string containing the digits of the specified mpz_t.
     */
    static String mpz_get_str(
            int base,
            mpz_t op) {
        byte[] bytes = new byte[mpz_sizeinbase(op, base) + 2];
        mpz_get_str(bytes, base, op);
        for (int i = 0; i < bytes.length; i++) {
            if (bytes[i] == 0) {
                return new String(Arrays.copyOf(bytes, i));
            }
        }
        assert false : "result from mpz_get_str should be null terminated";
        return null;
    }
    
    /**
     * A Java representation of the C mpz_t type. Used only for interfacing between MPFR and the
     * Java {@link BigInteger} type.
     * 
     * Technically accessing the struct directly is a hack because we're not supposed to
     * look internally at the struct that mpz_t contains, however, it's the only way to
     * do this with the JNI framework we're using.
     * @author Dwight Guth
     *
     */
    @JniClass(name="__mpz_struct", flags={STRUCT, TYPEDEF})
    static final class mpz_t {
        /**
         * Construct a new mpz_t object and initialize it.
         */
        mpz_t() {
            mpz_init(this);
        }
        
        /**
         * Construct a new mpz_t object and initialize it with the
         * specified {@link BigInteger}.
         * @param val The value to initialize the struct with.
         */
        mpz_t(BigInteger val) {
            mpz_init(this);
            mpz_set_str(this, val.toString(), 10);
        }
        
        /**
         * Release native resources for this struct.
         */
        @Override
        protected void finalize() throws Throwable {
            if (_mp_d != 0) {
                mpz_clear(this);
            }
        }
        
        int _mp_alloc;
        int _mp_size;
        @JniField(cast="mp_limb_t *") long _mp_d;
    }
}	
