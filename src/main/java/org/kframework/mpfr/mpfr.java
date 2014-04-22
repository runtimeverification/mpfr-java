// Copyright (c) 2014 K Team. All Rights Reserved.
package org.kframework.mpfr;

import org.fusesource.hawtjni.runtime.*;

import static org.fusesource.hawtjni.runtime.ArgFlag.*;
import static org.fusesource.hawtjni.runtime.ClassFlag.*;
import static org.fusesource.hawtjni.runtime.FieldFlag.*;
import static org.fusesource.hawtjni.runtime.MethodFlag.*;


@JniClass
class mpfr {
    private static final Library LIBRARY = new Library("mpfr_java", mpfr.class);
    static {
        LIBRARY.load();
    }
    
    @JniMethod(cast="void *")
    static final native long malloc(
      @JniArg(cast="size_t") long size);
    
    static final native void mpfr_init2(
            @JniArg(cast="mpfr_ptr", flags={POINTER_ARG}) __mpfr_struct x,
            @JniArg(cast="mpfr_prec_t") long prec);
    
    static final native int mpfr_nan_p(@JniArg(cast="mpfr_ptr", flags={POINTER_ARG}) __mpfr_struct op);
   
    
    /**
     * Technically this is a hack because we're not supposed to look internally at the struct
     * that mpfr_t contains, however, it's the only way to do this with the JNI framework 
     * we're using.
     * @author dwightguth
     *
     */
    @JniClass(flags={STRUCT, TYPEDEF})
    static class __mpfr_struct {
        static {
            LIBRARY.load();
            init();
        } 
        
        @JniMethod(flags={CONSTANT_INITIALIZER})
        private static final native void init();

        @JniField(flags={CONSTANT}, accessor="sizeof(__mpfr_struct)")
        static short SIZE_OF;
        
        static final native void memmove (
                @JniArg(cast="void *", flags={NO_IN, CRITICAL}) __mpfr_struct dest,
                @JniArg(cast="const void *", flags={NO_OUT, CRITICAL}) long src,
                @JniArg(cast="size_t") long size);
        
        static final native void memmove (
                @JniArg(cast="void *", flags={NO_IN, CRITICAL}) long dest,
                @JniArg(cast="const void *", flags={NO_OUT, CRITICAL}) __mpfr_struct src,
                @JniArg(cast="size_t") long size);
        
        @JniField(flags={FIELD_SKIP})
        long ptr;
        
        public __mpfr_struct() {
            
//            ptr = malloc(SIZE_OF);
//            __mpfr_struct.memmove(ptr, this, SIZE_OF);
        }
        
        @JniField(cast="mpfr_prec_t") long _mpfr_prec;
        @JniField(cast="mpfr_sign_t") int _mpfr_sign;
        @JniField(cast="mpfr_exp_t") long _mpfr_exp;
        @JniField(cast="mp_limb_t *") long _mpfr_d;
    }
}	
