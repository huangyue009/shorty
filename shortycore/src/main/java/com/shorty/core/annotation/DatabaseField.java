package com.shorty.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * set database field name and attrs,
 * add where field header
 * Created by yue.huang on 2016/4/13.
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface DatabaseField {
    String columnName() default "";
    boolean isPrimary() default false;
    boolean canBeNull() default true;
}
