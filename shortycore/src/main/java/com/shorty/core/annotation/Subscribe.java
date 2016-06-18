package com.shorty.core.annotation;

import android.content.Context;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Event listener onEvent method attrs setting
 * Created by yue.huang on 2016/6/1.
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Subscribe {
    int DEFAULT = 0;
    int MAIN_THREAD = 1;
    int BACKGROUND_THREAD = 2;
    int ASYNC_THREAD = 3;

    int threadLevel() default DEFAULT;
    boolean oneTime() default true;
    String destroyWhenFinish() default "";
}
