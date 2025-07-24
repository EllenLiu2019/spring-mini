package com.minis.boot.autoconfigure;


import com.minis.context.annotation.Import;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@AutoConfigurationPackage // TODO: 自动配置包
@Import(AutoConfigurationImportSelector.class) // TODO: 借助 @Import 收集并注册自动装配的 BeanDefinitions
public @interface EnableAutoConfiguration {
    String ENABLED_OVERRIDE_PROPERTY = "spring.boot.enableautoconfiguration";

    Class<?>[] exclude() default {};

    String[] excludeName() default {};
}
