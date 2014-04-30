// Copyright (c) 2014 K Team. All Rights Reserved.
package org.kframework.mpfr;

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


@JniClass
final class mpfr {
    private static final Library LIBRARY = new Library("mpfr_java", mpfr.class);
    static {
        LIBRARY.load();
        init();
    }

    @JniMethod(flags={CONSTANT_INITIALIZER})
    private static native void init();
    
    @JniMethod(cast="size_t")
    static native int strlen(@JniArg(cast="char *") long str);
    
    @JniMethod(cast="void *")
    public static native long calloc(
            @JniArg(cast="size_t") long num,
            @JniArg(cast="size_t") long size);
    
    public static native void free(
            @JniArg(cast="void *") long ptr);
    
    public static native void memmove(
            @JniArg(cast="void *", flags={NO_IN, CRITICAL}) long dest, 
            @JniArg(cast="const void *", flags={NO_OUT, CRITICAL}) byte[] src, 
            @JniArg(cast="size_t") long size);
    public static native void memmove(
            @JniArg(cast="void *", flags={NO_IN, CRITICAL}) byte[] dest, 
            @JniArg(cast="const void *", flags={NO_OUT, CRITICAL}) long src, 
            @JniArg(cast="size_t") long size);

    static native int mpfr_asprintf(
            @JniArg(cast="char **") long[] str,
            String template,
            @JniArg(cast="mpfr_ptr", flags={POINTER_ARG}) __mpfr_struct x);
    
    static native void mpfr_free_str(
            @JniArg(cast="char *") long ptr);
    
    static String mpfr_asprintf(
            String template,
            __mpfr_struct x) {
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
    
    
    static native void mpfr_init2(
            @JniArg(cast="mpfr_ptr", flags={POINTER_ARG}) __mpfr_struct x,
            @JniArg(cast="mpfr_prec_t") int prec);
    static native void mpfr_clear(
            @JniArg(cast="mpfr_ptr", flags={POINTER_ARG}) __mpfr_struct x);

    static native boolean mpfr_nan_p(@JniArg(cast="mpfr_ptr", flags={POINTER_ARG}) __mpfr_struct op);
    static native boolean mpfr_inf_p(@JniArg(cast="mpfr_ptr", flags={POINTER_ARG}) __mpfr_struct op);
    static native boolean mpfr_zero_p(@JniArg(cast="mpfr_ptr", flags={POINTER_ARG}) __mpfr_struct op);
    static native boolean mpfr_integer_p(@JniArg(cast="mpfr_ptr", flags={POINTER_ARG}) __mpfr_struct op);

    static native float mpfr_get_flt(
            @JniArg(cast="mpfr_ptr", flags={POINTER_ARG}) __mpfr_struct op,
            @JniArg(cast="mpfr_rnd_t") int rnd);
    static native double mpfr_get_d(
            @JniArg(cast="mpfr_ptr", flags={POINTER_ARG}) __mpfr_struct op,
            @JniArg(cast="mpfr_rnd_t") int rnd);
    static native int mpfr_get_z(
            @JniArg(cast="mpz_ptr", flags={POINTER_ARG}) __mpz_struct rop,
            @JniArg(cast="mpfr_ptr", flags={POINTER_ARG}) __mpfr_struct op,
            @JniArg(cast="mpfr_rnd_t") int rnd);

    static native int mpfr_set(
            @JniArg(cast="mpfr_ptr", flags={POINTER_ARG}) __mpfr_struct rop,
            @JniArg(cast="mpfr_ptr", flags={POINTER_ARG}) __mpfr_struct op,
            @JniArg(cast="mpfr_rnd_t") int rnd);
    static native int mpfr_set_si_2exp(
            @JniArg(cast="mpfr_ptr", flags={POINTER_ARG}) __mpfr_struct rop,
            int op,
            @JniArg(cast="mpfr_exp_t") long e,
            @JniArg(cast="mpfr_rnd_t") int rnd);
    static native int mpfr_set_d(
            @JniArg(cast="mpfr_ptr", flags={POINTER_ARG}) __mpfr_struct rop,
            double op,
            @JniArg(cast="mpfr_rnd_t") int rnd);
    static native int mpfr_set_z(
            @JniArg(cast="mpfr_ptr", flags={POINTER_ARG}) __mpfr_struct rop,
            @JniArg(cast="mpz_ptr", flags={POINTER_ARG}) __mpz_struct op,
            @JniArg(cast="mpfr_rnd_t") int rnd);
    static native int mpfr_strtofr(
            @JniArg(cast="mpfr_ptr", flags={POINTER_ARG}) __mpfr_struct rop,
            long nptr,
            @JniArg(cast="char **")long[] endptr,
            int base,
            @JniArg(cast="mpfr_rnd_t") int rnd);
    
    /**
     * Wraps {@link #mpfr_set_str(__mpfr_struct, byte[], int, int)} to accept a String.
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
            __mpfr_struct rop,
            String s,
            int base,
            int rnd) throws NumberFormatException {
        try {
            ByteBuffer bytes = Charset.forName("US-ASCII").newEncoder()
                    .onMalformedInput(CodingErrorAction.REPORT)
                    .onUnmappableCharacter(CodingErrorAction.REPORT)
                    .encode(CharBuffer.wrap(s.toCharArray()));
            byte[] byteArray = new byte[bytes.remaining()];
            bytes.get(byteArray);
            return mpfr_set_str(rop, byteArray, base, rnd);
        } catch (CharacterCodingException e) {
            throw new NumberFormatException();
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
            __mpfr_struct rop,
            byte[] s,
            int base,
            int rnd) throws NumberFormatException {
        long ptr = 0;
        try {
            ptr = calloc(s.length + 1, 1); // +1 for null byte
            memmove(ptr, s, s.length);
            long[] endptr = new long[1];
            int result = mpfr_strtofr(rop, ptr, endptr, base, rnd);
            if (strlen(endptr[0]) != 0) {
                //didn't read the entire string, therefore, it was not a float
                throw new NumberFormatException();
            }
            return result;
        } finally {
            if (ptr != 0) {
                free(ptr);
            }
        }
    }
    
    static native void mpfr_set_inf(
            @JniArg(cast="mpfr_ptr", flags={POINTER_ARG}) __mpfr_struct x,
            int sign);
    static native void mpfr_set_zero(
            @JniArg(cast="mpfr_ptr", flags={POINTER_ARG}) __mpfr_struct x,
            int sign);
    
    static native int mpfr_add(
            @JniArg(cast="mpfr_ptr", flags={POINTER_ARG}) __mpfr_struct rop,
            @JniArg(cast="mpfr_ptr", flags={POINTER_ARG}) __mpfr_struct op1,
            @JniArg(cast="mpfr_ptr", flags={POINTER_ARG}) __mpfr_struct op2,
            @JniArg(cast="mpfr_rnd_t") int rnd);
    static native int mpfr_sub(
            @JniArg(cast="mpfr_ptr", flags={POINTER_ARG}) __mpfr_struct rop,
            @JniArg(cast="mpfr_ptr", flags={POINTER_ARG}) __mpfr_struct op1,
            @JniArg(cast="mpfr_ptr", flags={POINTER_ARG}) __mpfr_struct op2,
            @JniArg(cast="mpfr_rnd_t") int rnd);
    static native int mpfr_mul(
            @JniArg(cast="mpfr_ptr", flags={POINTER_ARG}) __mpfr_struct rop,
            @JniArg(cast="mpfr_ptr", flags={POINTER_ARG}) __mpfr_struct op1,
            @JniArg(cast="mpfr_ptr", flags={POINTER_ARG}) __mpfr_struct op2,
            @JniArg(cast="mpfr_rnd_t") int rnd);
    static native int mpfr_div(
            @JniArg(cast="mpfr_ptr", flags={POINTER_ARG}) __mpfr_struct rop,
            @JniArg(cast="mpfr_ptr", flags={POINTER_ARG}) __mpfr_struct op1,
            @JniArg(cast="mpfr_ptr", flags={POINTER_ARG}) __mpfr_struct op2,
            @JniArg(cast="mpfr_rnd_t") int rnd);
    static native int mpfr_remainder(
            @JniArg(cast="mpfr_ptr", flags={POINTER_ARG}) __mpfr_struct r,
            @JniArg(cast="mpfr_ptr", flags={POINTER_ARG}) __mpfr_struct x,
            @JniArg(cast="mpfr_ptr", flags={POINTER_ARG}) __mpfr_struct y,
            @JniArg(cast="mpfr_rnd_t") int rnd);
    static native int mpfr_pow(
            @JniArg(cast="mpfr_ptr", flags={POINTER_ARG}) __mpfr_struct rop,
            @JniArg(cast="mpfr_ptr", flags={POINTER_ARG}) __mpfr_struct op1,
            @JniArg(cast="mpfr_ptr", flags={POINTER_ARG}) __mpfr_struct op2,
            @JniArg(cast="mpfr_rnd_t") int rnd);
    static native int mpfr_root(
            @JniArg(cast="mpfr_ptr", flags={POINTER_ARG}) __mpfr_struct rop,
            @JniArg(cast="mpfr_ptr", flags={POINTER_ARG}) __mpfr_struct op,
            int k,
            @JniArg(cast="mpfr_rnd_t") int rnd);
    static native int mpfr_neg(
            @JniArg(cast="mpfr_ptr", flags={POINTER_ARG}) __mpfr_struct rop,
            @JniArg(cast="mpfr_ptr", flags={POINTER_ARG}) __mpfr_struct op,
            @JniArg(cast="mpfr_rnd_t") int rnd);
    static native int mpfr_abs(
            @JniArg(cast="mpfr_ptr", flags={POINTER_ARG}) __mpfr_struct rop,
            @JniArg(cast="mpfr_ptr", flags={POINTER_ARG}) __mpfr_struct op,
            @JniArg(cast="mpfr_rnd_t") int rnd);
    static native int mpfr_log(
            @JniArg(cast="mpfr_ptr", flags={POINTER_ARG}) __mpfr_struct rop,
            @JniArg(cast="mpfr_ptr", flags={POINTER_ARG}) __mpfr_struct op,
            @JniArg(cast="mpfr_rnd_t") int rnd);
    static native int mpfr_log10(
            @JniArg(cast="mpfr_ptr", flags={POINTER_ARG}) __mpfr_struct rop,
            @JniArg(cast="mpfr_ptr", flags={POINTER_ARG}) __mpfr_struct op,
            @JniArg(cast="mpfr_rnd_t") int rnd);
    static native int mpfr_exp(
            @JniArg(cast="mpfr_ptr", flags={POINTER_ARG}) __mpfr_struct rop,
            @JniArg(cast="mpfr_ptr", flags={POINTER_ARG}) __mpfr_struct op,
            @JniArg(cast="mpfr_rnd_t") int rnd);
    static native int mpfr_exp10(
            @JniArg(cast="mpfr_ptr", flags={POINTER_ARG}) __mpfr_struct rop,
            @JniArg(cast="mpfr_ptr", flags={POINTER_ARG}) __mpfr_struct op,
            @JniArg(cast="mpfr_rnd_t") int rnd);
    static native int mpfr_sin(
            @JniArg(cast="mpfr_ptr", flags={POINTER_ARG}) __mpfr_struct rop,
            @JniArg(cast="mpfr_ptr", flags={POINTER_ARG}) __mpfr_struct op,
            @JniArg(cast="mpfr_rnd_t") int rnd);
    static native int mpfr_cos(
            @JniArg(cast="mpfr_ptr", flags={POINTER_ARG}) __mpfr_struct rop,
            @JniArg(cast="mpfr_ptr", flags={POINTER_ARG}) __mpfr_struct op,
            @JniArg(cast="mpfr_rnd_t") int rnd);
    static native int mpfr_tan(
            @JniArg(cast="mpfr_ptr", flags={POINTER_ARG}) __mpfr_struct rop,
            @JniArg(cast="mpfr_ptr", flags={POINTER_ARG}) __mpfr_struct op,
            @JniArg(cast="mpfr_rnd_t") int rnd);
    static native int mpfr_sec(
            @JniArg(cast="mpfr_ptr", flags={POINTER_ARG}) __mpfr_struct rop,
            @JniArg(cast="mpfr_ptr", flags={POINTER_ARG}) __mpfr_struct op,
            @JniArg(cast="mpfr_rnd_t") int rnd);
    static native int mpfr_csc(
            @JniArg(cast="mpfr_ptr", flags={POINTER_ARG}) __mpfr_struct rop,
            @JniArg(cast="mpfr_ptr", flags={POINTER_ARG}) __mpfr_struct op,
            @JniArg(cast="mpfr_rnd_t") int rnd);
    static native int mpfr_cot(
            @JniArg(cast="mpfr_ptr", flags={POINTER_ARG}) __mpfr_struct rop,
            @JniArg(cast="mpfr_ptr", flags={POINTER_ARG}) __mpfr_struct op,
            @JniArg(cast="mpfr_rnd_t") int rnd);
    static native int mpfr_asin(
            @JniArg(cast="mpfr_ptr", flags={POINTER_ARG}) __mpfr_struct rop,
            @JniArg(cast="mpfr_ptr", flags={POINTER_ARG}) __mpfr_struct op,
            @JniArg(cast="mpfr_rnd_t") int rnd);
    static native int mpfr_acos(
            @JniArg(cast="mpfr_ptr", flags={POINTER_ARG}) __mpfr_struct rop,
            @JniArg(cast="mpfr_ptr", flags={POINTER_ARG}) __mpfr_struct op,
            @JniArg(cast="mpfr_rnd_t") int rnd);
    static native int mpfr_atan(
            @JniArg(cast="mpfr_ptr", flags={POINTER_ARG}) __mpfr_struct rop,
            @JniArg(cast="mpfr_ptr", flags={POINTER_ARG}) __mpfr_struct op,
            @JniArg(cast="mpfr_rnd_t") int rnd);
    static native int mpfr_atan2(
            @JniArg(cast="mpfr_ptr", flags={POINTER_ARG}) __mpfr_struct rop,
            @JniArg(cast="mpfr_ptr", flags={POINTER_ARG}) __mpfr_struct y,
            @JniArg(cast="mpfr_ptr", flags={POINTER_ARG}) __mpfr_struct x,
            @JniArg(cast="mpfr_rnd_t") int rnd);
    static native int mpfr_cosh(
            @JniArg(cast="mpfr_ptr", flags={POINTER_ARG}) __mpfr_struct rop,
            @JniArg(cast="mpfr_ptr", flags={POINTER_ARG}) __mpfr_struct op,
            @JniArg(cast="mpfr_rnd_t") int rnd);
    static native int mpfr_sinh(
            @JniArg(cast="mpfr_ptr", flags={POINTER_ARG}) __mpfr_struct rop,
            @JniArg(cast="mpfr_ptr", flags={POINTER_ARG}) __mpfr_struct op,
            @JniArg(cast="mpfr_rnd_t") int rnd);
    static native int mpfr_tanh(
            @JniArg(cast="mpfr_ptr", flags={POINTER_ARG}) __mpfr_struct rop,
            @JniArg(cast="mpfr_ptr", flags={POINTER_ARG}) __mpfr_struct op,
            @JniArg(cast="mpfr_rnd_t") int rnd);
    static native int mpfr_sech(
            @JniArg(cast="mpfr_ptr", flags={POINTER_ARG}) __mpfr_struct rop,
            @JniArg(cast="mpfr_ptr", flags={POINTER_ARG}) __mpfr_struct op,
            @JniArg(cast="mpfr_rnd_t") int rnd);
    static native int mpfr_csch(
            @JniArg(cast="mpfr_ptr", flags={POINTER_ARG}) __mpfr_struct rop,
            @JniArg(cast="mpfr_ptr", flags={POINTER_ARG}) __mpfr_struct op,
            @JniArg(cast="mpfr_rnd_t") int rnd);
    static native int mpfr_coth(
            @JniArg(cast="mpfr_ptr", flags={POINTER_ARG}) __mpfr_struct rop,
            @JniArg(cast="mpfr_ptr", flags={POINTER_ARG}) __mpfr_struct op,
            @JniArg(cast="mpfr_rnd_t") int rnd);
    static native int mpfr_acosh(
            @JniArg(cast="mpfr_ptr", flags={POINTER_ARG}) __mpfr_struct rop,
            @JniArg(cast="mpfr_ptr", flags={POINTER_ARG}) __mpfr_struct op,
            @JniArg(cast="mpfr_rnd_t") int rnd);
    static native int mpfr_asinh(
            @JniArg(cast="mpfr_ptr", flags={POINTER_ARG}) __mpfr_struct rop,
            @JniArg(cast="mpfr_ptr", flags={POINTER_ARG}) __mpfr_struct op,
            @JniArg(cast="mpfr_rnd_t") int rnd);
    static native int mpfr_atanh(
            @JniArg(cast="mpfr_ptr", flags={POINTER_ARG}) __mpfr_struct rop,
            @JniArg(cast="mpfr_ptr", flags={POINTER_ARG}) __mpfr_struct op,
            @JniArg(cast="mpfr_rnd_t") int rnd);
    static native int mpfr_rint(
            @JniArg(cast="mpfr_ptr", flags={POINTER_ARG}) __mpfr_struct rop,
            @JniArg(cast="mpfr_ptr", flags={POINTER_ARG}) __mpfr_struct op,
            @JniArg(cast="mpfr_rnd_t") int rnd);
    static native int mpfr_min(
            @JniArg(cast="mpfr_ptr", flags={POINTER_ARG}) __mpfr_struct rop,
            @JniArg(cast="mpfr_ptr", flags={POINTER_ARG}) __mpfr_struct op1,
            @JniArg(cast="mpfr_ptr", flags={POINTER_ARG}) __mpfr_struct op2,
            @JniArg(cast="mpfr_rnd_t") int rnd);
    static native int mpfr_max(
            @JniArg(cast="mpfr_ptr", flags={POINTER_ARG}) __mpfr_struct rop,
            @JniArg(cast="mpfr_ptr", flags={POINTER_ARG}) __mpfr_struct op1,
            @JniArg(cast="mpfr_ptr", flags={POINTER_ARG}) __mpfr_struct op2,
            @JniArg(cast="mpfr_rnd_t") int rnd);
    static native int mpfr_const_pi(
            @JniArg(cast="mpfr_ptr", flags={POINTER_ARG}) __mpfr_struct rop,
            @JniArg(cast="mpfr_rnd_t") int rnd);
    static native int mpfr_const_euler(
            @JniArg(cast="mpfr_ptr", flags={POINTER_ARG}) __mpfr_struct rop,
            @JniArg(cast="mpfr_rnd_t") int rnd);
    static native void mpfr_nexttoward(
            @JniArg(cast="mpfr_ptr", flags={POINTER_ARG}) __mpfr_struct x,
            @JniArg(cast="mpfr_ptr", flags={POINTER_ARG}) __mpfr_struct y);
    static native void mpfr_nextabove(
            @JniArg(cast="mpfr_ptr", flags={POINTER_ARG}) __mpfr_struct x);
    static native void mpfr_nextbelow(
            @JniArg(cast="mpfr_ptr", flags={POINTER_ARG}) __mpfr_struct x);

    static native int mpfr_cmp(
            @JniArg(cast="mpfr_ptr", flags={POINTER_ARG}) __mpfr_struct op1,
            @JniArg(cast="mpfr_ptr", flags={POINTER_ARG}) __mpfr_struct op2);
    static native int mpfr_cmp_d(
            @JniArg(cast="mpfr_ptr", flags={POINTER_ARG}) __mpfr_struct op1,
            double op2);
    static native boolean mpfr_greater_p(
            @JniArg(cast="mpfr_ptr", flags={POINTER_ARG}) __mpfr_struct op1,
            @JniArg(cast="mpfr_ptr", flags={POINTER_ARG}) __mpfr_struct op2);
    static native boolean mpfr_greaterequal_p(
            @JniArg(cast="mpfr_ptr", flags={POINTER_ARG}) __mpfr_struct op1,
            @JniArg(cast="mpfr_ptr", flags={POINTER_ARG}) __mpfr_struct op2);
    static native boolean mpfr_less_p(
            @JniArg(cast="mpfr_ptr", flags={POINTER_ARG}) __mpfr_struct op1,
            @JniArg(cast="mpfr_ptr", flags={POINTER_ARG}) __mpfr_struct op2);
    static native boolean mpfr_lessequal_p(
            @JniArg(cast="mpfr_ptr", flags={POINTER_ARG}) __mpfr_struct op1,
            @JniArg(cast="mpfr_ptr", flags={POINTER_ARG}) __mpfr_struct op2);
    static native boolean mpfr_equal_p(
            @JniArg(cast="mpfr_ptr", flags={POINTER_ARG}) __mpfr_struct op1,
            @JniArg(cast="mpfr_ptr", flags={POINTER_ARG}) __mpfr_struct op2);
    
    static native boolean mpfr_prec_round(
            @JniArg(cast="mpfr_ptr", flags={POINTER_ARG}) __mpfr_struct x,
            @JniArg(cast="mpfr_prec_t") int prec,
            @JniArg(cast="mpfr_rnd_t") int rnd);
    

    static native boolean mpfr_set_emin(
            @JniArg(cast="mpfr_exp_t") long exp);
    static native boolean mpfr_set_emax(
            @JniArg(cast="mpfr_exp_t") long exp);
    static native int mpfr_check_range(
            @JniArg(cast="mpfr_ptr", flags={POINTER_ARG}) __mpfr_struct x,
            int t,
            @JniArg(cast="mpfr_rnd_t") int rnd);
    static native boolean mpfr_subnormalize(
            @JniArg(cast="mpfr_ptr", flags={POINTER_ARG}) __mpfr_struct x,
            int t,
            @JniArg(cast="mpfr_rnd_t") int rnd);
    
    static native boolean mpfr_signbit(
            @JniArg(cast="mpfr_ptr", flags={POINTER_ARG}) __mpfr_struct op);
    
    
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

    /**
     * Technically this is a hack because we're not supposed to look internally at the struct
     * that mpfr_t contains, however, it's the only way to do this with the JNI framework 
     * we're using.
     * @author dwightguth
     *
     */
    @JniClass(flags={STRUCT, TYPEDEF})
    static final class __mpfr_struct {
        static {
            LIBRARY.load();
            init();
        } 
        
        /**
         * Construct a new __mpfr_struct object and initialize it with the specified
         * {@code precision}.
         * @param precision The bits of precision to call {@link mpfr#mpfr_init2} with.
         */
        __mpfr_struct(int precision) {
            if (precision < 2) {
                throw new IllegalArgumentException("invalid precision");
            }
            mpfr_init2(this, precision);
        }
        
        /**
         * Construct a new __mpfr_struct object copied from {@code copy}.
         * @param copy The struct to copy.
         */
        __mpfr_struct(__mpfr_struct copy) {
            mpfr_init2(this, copy._mpfr_prec);
            //should not ever lose precision
            mpfr_set(this, copy, MPFR_RNDN);
        }
        
        @Override
        protected void finalize() throws Throwable {
            mpfr_clear(this);
        }
        
        @JniMethod(flags={CONSTANT_INITIALIZER})
        private static native void init();

        @JniField(flags={CONSTANT}, accessor="sizeof(__mpfr_struct)")
        private static short SIZE_OF;
        
        @JniField(cast="mpfr_prec_t") int _mpfr_prec;
        @JniField(cast="mpfr_sign_t") int _mpfr_sign;
        @JniField(cast="mpfr_exp_t") long _mpfr_exp;
        @JniField(cast="mp_limb_t *") long _mpfr_d;
    }
    
    static native void mpz_init(
            @JniArg(cast="mpz_ptr", flags={POINTER_ARG}) __mpz_struct x);
    static native void mpz_clear(
            @JniArg(cast="mpz_ptr", flags={POINTER_ARG}) __mpz_struct x);
    @JniMethod(cast="size_t")
    static native int mpz_sizeinbase(
            @JniArg(cast="mpz_ptr", flags={POINTER_ARG}) __mpz_struct op,
            int base);
    static native String mpz_get_str(
            byte[] str,
            int base,
            @JniArg(cast="mpz_ptr", flags={POINTER_ARG}) __mpz_struct op);
    static native int mpz_set_str(
            @JniArg(cast="mpz_ptr", flags={POINTER_ARG}) __mpz_struct rop,
            String str,
            int base);
    
    static String mpz_get_str(
            int base,
            __mpz_struct op) {
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
    
    @JniClass(flags={STRUCT, TYPEDEF})
    static final class __mpz_struct {
        int _mp_alloc;
        int _mp_size;
        @JniField(cast="mp_limb_t *") long _mp_d;

        __mpz_struct() {
            mpz_init(this);
        }
        
        @Override
        protected void finalize() throws Throwable {
            mpz_clear(this);
        }
        
        /**
         * Construct a new __mpz_struct object and initialize it with the
         * specified {@link BigInteger}.
         * @param val The value to initialize the struct with.
         */
        __mpz_struct(BigInteger val) {
            mpz_init(this);
            mpz_set_str(this, val.toString(), 10);
        }
    }
}	
