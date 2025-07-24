package com.minis.boot.autoconfigure;

import com.minis.context.annotation.Import;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@Import(AutoConfigurationPackages.Registrar.class)
public @interface AutoConfigurationPackage {

    String[] basePackages() default {};

    Class<?>[] basePackageClasses() default {};
}
