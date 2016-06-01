package com.shorty.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * setting table name
 * add where class header and name the table
 * Created by yue.huang on 2016/4/13.
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface DatabaseTable {
    String tableName() default "";
}
