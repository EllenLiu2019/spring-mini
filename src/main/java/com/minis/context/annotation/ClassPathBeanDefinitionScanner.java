package com.minis.context.annotation;

import com.minis.beans.factory.annotation.AnnotatedBeanDefinition;
import com.minis.beans.factory.config.BeanDefinition;
import com.minis.beans.factory.config.BeanDefinitionHolder;
import com.minis.beans.factory.support.BeanDefinitionRegistry;
import com.minis.beans.factory.support.BeanNameGenerator;
import com.minis.core.io.FileSystemResource;
import com.minis.core.io.Resource;
import com.minis.utils.ClassUtils;
import com.minis.utils.ResourceUtils;
import com.minis.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Modifier;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * TODO: A bean definition scanner that detects bean candidates on the classpath,
 *  registering corresponding bean definitions with a given registry (BeanFactory or ApplicationContext).
 *  Candidate classes are detected through configurable type filters.
 *  The default filters include classes that are annotated with Spring's @Component, @Repository, @Service, or @Controller stereotype.
 */

@Slf4j
public class ClassPathBeanDefinitionScanner {

    private static final String RESOURCE_PREFIX = "com";
    private static final String RESOURCE_SUFFIX = ".class";
    private final BeanDefinitionRegistry registry;

    private BeanNameGenerator beanNameGenerator = AnnotationBeanNameGenerator.INSTANCE;

    private final List<String> excludeFilters = new ArrayList<>();

    public ClassPathBeanDefinitionScanner(BeanDefinitionRegistry registry) {
        this.registry = registry;
    }

    public void addExcludeFilter(String excludeFilter) {
        this.excludeFilters.add(excludeFilter);
    }

    public Set<BeanDefinitionHolder> doScan(Set<String> basePackages) throws IOException {
        Set<BeanDefinitionHolder> beanDefinitions = new LinkedHashSet<>();
        for (String basePackage : basePackages) {
            Set<BeanDefinition> candidates = this.scanCandidateComponents(basePackage);
            for (BeanDefinition candidate : candidates) {
                String beanName = this.beanNameGenerator.generateBeanName(candidate, this.registry);
                BeanDefinitionHolder definitionHolder = new BeanDefinitionHolder(candidate, beanName);
                if (candidate instanceof AnnotatedBeanDefinition annotatedBeanDefinition) {
                    AnnotationConfigUtils.processCommonDefinitionAnnotations(annotatedBeanDefinition);
                }
                beanDefinitions.add(definitionHolder);
                this.registry.registerBeanDefinition(beanName, candidate);
            }
        }
        return beanDefinitions;
    }

    private Set<BeanDefinition> scanCandidateComponents(String basePackage) throws IOException {
        Set<Resource> resources = this.findAllClassPathResources(basePackage);
        return this.selectCandidates(resources);
    }

    Set<Resource> findAllClassPathResources(String classPath) throws IOException {
        String resourcePath = ClassUtils.convertClassNameToResourcePath(classPath);
        Set<Resource> result = new LinkedHashSet<>(16);
        Predicate<Path> isMatchingFile = path -> (!path.equals(Path.of(resourcePath)) &&
                StringUtils.cleanPath(path.toString()).endsWith(RESOURCE_SUFFIX));
        Enumeration<URL> resourceUrls = this.getClass().getClassLoader().getResources(resourcePath);
        while (resourceUrls.hasMoreElements()) {
            URL url = resourceUrls.nextElement();
            String rootPath;
            try {
                rootPath = ResourceUtils.toURI(url).getSchemeSpecificPart();
            } catch (URISyntaxException e) {
                throw new RuntimeException(e);
            }
            try (Stream<Path> files = Files.walk(Paths.get(rootPath), FileVisitOption.FOLLOW_LINKS)) {
                files.filter(isMatchingFile)
                        .map(FileSystemResource::new)
                        .forEach(result::add);
            }
        }
        return result;
    }

    Set<BeanDefinition> selectCandidates(Set<Resource> resources) {
        Set<BeanDefinition> candidates = new LinkedHashSet<>();
        for (Resource resource : resources) {
            String path = ((FileSystemResource) resource).getPath().replace(File.separator, ".");
            String className = path.substring(path.indexOf(RESOURCE_PREFIX), path.length() - 6);
            try {
                Class<?> clazz = Class.forName(className);
                if (isInstantiable(clazz) && !this.excludeFilters.contains(className)) {
                    ScannedGenericBeanDefinition beanDefinition = new ScannedGenericBeanDefinition(clazz);
                    if (ConfigurationClassUtils.isConfigurationCandidate(beanDefinition.getMetadata())) {
                        candidates.add(beanDefinition);
                    }
                }
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
        return candidates;
    }

    public boolean isInstantiable(Class<?> clazz) {
        return !clazz.isInterface() &&
                !Modifier.isAbstract(clazz.getModifiers()) &&
                !clazz.isEnum() &&
                !clazz.isArray() &&
                !clazz.isPrimitive() &&
                hasNoArgsConstructor(clazz);
    }

    private boolean hasNoArgsConstructor(Class<?> clazz) {
        try {
            clazz.getDeclaredConstructor();
            return true;
        } catch (NoSuchMethodException e) {
            return false;
        }
    }

    public final BeanDefinitionRegistry getRegistry() {
        return this.registry;
    }

}
