package com.minis.context.annotation;

import com.minis.stereotype.Component;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface Configuration {

    String value() default "";
    boolean proxyBeanMethods() default true;
}
