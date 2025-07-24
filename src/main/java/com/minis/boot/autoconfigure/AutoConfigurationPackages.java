package com.minis.boot.autoconfigure;

import com.minis.beans.factory.config.BeanDefinition;
import com.minis.beans.factory.config.ConstructorArgumentValue;
import com.minis.beans.factory.config.ConstructorArgumentValues;
import com.minis.beans.factory.support.BeanDefinitionRegistry;
import com.minis.beans.factory.support.RootBeanDefinition;
import com.minis.context.annotation.ImportBeanDefinitionRegistrar;
import com.minis.core.annotation.AnnotationAttributes;
import com.minis.core.type.AnnotationMetadata;
import com.minis.utils.ClassUtils;
import com.minis.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.stream.Stream;

@Slf4j
public abstract class AutoConfigurationPackages {

    private static final String BEAN = AutoConfigurationPackages.class.getName();

    public static void register(BeanDefinitionRegistry registry, String... packageNames) {
        if (registry.containsBeanDefinition(BEAN)) {
            addBasePackages(registry.getBeanDefinition(BEAN), packageNames);
        } else {
            RootBeanDefinition beanDefinition = new RootBeanDefinition(BasePackages.class);
            beanDefinition.setConstructorArgumentValues(new ConstructorArgumentValues());
            addBasePackages(beanDefinition, packageNames);
            registry.registerBeanDefinition(BEAN, beanDefinition);
        }
    }

    private static void addBasePackages(BeanDefinition beanDefinition, String[] additionalBasePackages) {
        ConstructorArgumentValues constructorArgumentValues = beanDefinition.getConstructorArgumentValues();
        if (constructorArgumentValues != null && constructorArgumentValues.getArgumentCount() == 1) {
            String[] existingPackages = (String[]) constructorArgumentValues.getIndexedArgumentValue(0).getValue();
            String[] basePackages = Stream.concat(Stream.of(existingPackages), Stream.of(additionalBasePackages))
                    .distinct().toArray(String[]::new);
            constructorArgumentValues.addArgumentValue(0, basePackages);
        } else {
            ConstructorArgumentValue constructorArgumentValue =
                    new ConstructorArgumentValue("String[]", additionalBasePackages, "packages");
            constructorArgumentValues.addArgumentValue(constructorArgumentValue);
        }
    }

    /**
     * TODO: to enable Automatic @Repository and @Entity scanning.
     *
     * {@link Registrar} to register a beanDefinition as a {@link BasePackages} class into registry
     * {@link AutoConfigurationPackages}.
     */
    static class Registrar implements ImportBeanDefinitionRegistrar {
        @Override
        public void registerBeanDefinitions(AnnotationMetadata metadata, BeanDefinitionRegistry registry) {
            register(registry, new PackageImports(metadata).getPackageNames().toArray(new String[0]));
        }

    }

    private static final class PackageImports {

        private final List<String> packageNames;

        // TODO: Find the basePackages of @AutoConfigurationPackage,
        //  and use as base packages scanning for Automatic @Repository and @Entity
        PackageImports(AnnotationMetadata metadata) {
            AnnotationAttributes attributes = AnnotationAttributes
                    .fromMap(metadata.getAnnotationAttributes(AutoConfigurationPackage.class.getName(), false));
            List<String> packageNames = new ArrayList<>(Arrays.asList(attributes.getStringArray("basePackages")));
            for (Class<?> basePackageClass : attributes.getClassArray("basePackageClasses")) {
                packageNames.add(basePackageClass.getPackage().getName());
            }
            if (packageNames.isEmpty()) {
                packageNames.add(ClassUtils.getPackageName(metadata.getClassName()));
            }
            this.packageNames = Collections.unmodifiableList(packageNames);
        }

        List<String> getPackageNames() {
            return this.packageNames;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null || getClass() != obj.getClass()) {
                return false;
            }
            return this.packageNames.equals(((PackageImports) obj).packageNames);
        }

        @Override
        public int hashCode() {
            return this.packageNames.hashCode();
        }

        @Override
        public String toString() {
            return "Package Imports " + this.packageNames;
        }

    }

    static final class BasePackages {

        private final List<String> packages;

        private boolean loggedBasePackageInfo;

        BasePackages(String... names) {
            List<String> packages = new ArrayList<>();
            for (String name : names) {
                if (StringUtils.hasText(name)) {
                    packages.add(name);
                }
            }
            this.packages = packages;
        }

        List<String> get() {
            if (!this.loggedBasePackageInfo) {
                if (this.packages.isEmpty()) {
                    log.warn("@EnableAutoConfiguration was declared on a class "
                            + "in the default package. Automatic @Repository and "
                            + "@Entity scanning is not enabled.");
                } else {
                    String packageNames = StringUtils.collectionToCommaDelimitedString(this.packages);
                    log.debug("@EnableAutoConfiguration was declared on a class in the package '" + packageNames
                            + "'. Automatic @Repository and @Entity scanning is enabled.");
                }
                this.loggedBasePackageInfo = true;
            }
            return this.packages;
        }

    }
}
