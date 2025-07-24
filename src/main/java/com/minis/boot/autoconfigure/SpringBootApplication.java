package com.minis.boot.autoconfigure;

import com.minis.boot.SpringBootConfiguration;
import com.minis.context.annotation.ComponentScan;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@SpringBootConfiguration
@EnableAutoConfiguration
@ComponentScan
public @interface SpringBootApplication {
    Class<?>[] exclude() default {};

    String[] scanBasePackages() default {};
}
