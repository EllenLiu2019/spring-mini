package com.minis.context.annotation;

import com.minis.beans.factory.config.BeanDefinition;
import com.minis.beans.factory.config.BeanDefinitionHolder;
import com.minis.beans.factory.support.BeanDefinitionRegistry;
import com.minis.core.annotation.AnnotationAttributes;
import com.minis.utils.ClassUtils;

import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.Set;

class ComponentScanAnnotationParser {

    private final BeanDefinitionRegistry registry;

    public ComponentScanAnnotationParser(BeanDefinitionRegistry registry) {
        this.registry = registry;
    }

    public Set<BeanDefinitionHolder> parse(AnnotationAttributes componentScan, String declaringClass) throws IOException {
        ClassPathBeanDefinitionScanner scanner = new ClassPathBeanDefinitionScanner(this.registry);
        Set<String> basePackages = new LinkedHashSet<>();
        String[] basePackagesArray = componentScan.getStringArray("basePackages");
        for (String basePackage : basePackagesArray) {
            basePackages.add(ClassUtils.getPackageName(basePackage));
        }
        if (basePackages.isEmpty()) {
            basePackages.add(ClassUtils.getPackageName(declaringClass));
        }
        scanner.addExcludeFilter(declaringClass);
        return scanner.doScan(basePackages);
    }

}
