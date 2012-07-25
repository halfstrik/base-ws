package org.eu.base.ws.query;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(value = {ElementType.TYPE, ElementType.FIELD, ElementType.METHOD})
@Retention(value = RetentionPolicy.RUNTIME)
public @interface QueryParam {
    public static int LIKE_TYPE = 1;
    public static int EQ_TYPE = 2;
    public static int COMP_TYPE = 6;
    public static int LT_TYPE = 3;
    public static int GT_TYPE = 4;
    public static int BETWEEN_TYPE = 5;

    public int type();

    public String format() default "none";
}
