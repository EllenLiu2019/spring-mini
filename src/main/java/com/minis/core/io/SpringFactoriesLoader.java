package com.minis.core.io;

import com.minis.utils.ClassUtils;
import com.minis.utils.StringUtils;

import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class SpringFactoriesLoader {
    public static final String FACTORIES_RESOURCE_LOCATION = "META-INF/spring.factories";
    static final Map<ClassLoader, Map<String, SpringFactoriesLoader>> cache = new ConcurrentHashMap<>();
    private final ClassLoader classLoader;
    private final Map<String, List<String>> factories;

    protected SpringFactoriesLoader(ClassLoader classLoader, Map<String, List<String>> factories) {
        this.classLoader = classLoader;
        this.factories = factories;
    }

    /**
     * TODO: 获取 SpringFactoriesLoader 实例, 这些类实例会从 “META-INF/spring.factories” 加载并实例化 factory
     *
     * @param classLoader - the ClassLoader to use for loading
     * @return a {@link SpringFactoriesLoader} instance
     */
    public static SpringFactoriesLoader forDefaultResourceLocation(ClassLoader classLoader) {
        return forResourceLocation(FACTORIES_RESOURCE_LOCATION, classLoader);
    }

    public static SpringFactoriesLoader forResourceLocation(String resourceLocation, ClassLoader classLoader) {
        ClassLoader resourceClassLoader = (classLoader != null ? classLoader : SpringFactoriesLoader.class.getClassLoader());
        Map<String, SpringFactoriesLoader> loaders = cache.computeIfAbsent(resourceClassLoader, key -> new ConcurrentHashMap<>());
        return loaders.computeIfAbsent(resourceLocation, key ->
                new SpringFactoriesLoader(classLoader, loadFactoriesResource(resourceClassLoader, resourceLocation)));
    }

    public <T> List<T> load(Class<T> factoryType) {
        List<String> implementationNames = loadFactoryNames(factoryType);
        List<T> result = new ArrayList<>(implementationNames.size());
        for (String implementationName : implementationNames) {
            T factory = instantiateFactory(implementationName, factoryType);
            if (factory != null) {
                result.add(factory);
            }
        }
        return result;
    }

    protected <T> T instantiateFactory(String implementationName, Class<T> type) {
        try {
            Class<T> factoryImplementationClass = (Class<T>) ClassUtils.forName(implementationName, this.classLoader);
            return factoryImplementationClass.getDeclaredConstructor().newInstance();
        } catch (Throwable ex) {
            return null;
        }
    }

    public static <T> List<T> loadFactories(Class<T> factoryType, ClassLoader classLoader) {
        return forDefaultResourceLocation(classLoader).load(factoryType);
    }

    private List<String> loadFactoryNames(Class<?> factoryType) {
        return this.factories.getOrDefault(factoryType.getName(), Collections.emptyList());
    }

    protected static Map<String, List<String>> loadFactoriesResource(ClassLoader classLoader, String resourceLocation) {
        Map<String, List<String>> result = new LinkedHashMap<>();
        try {
            Enumeration<URL> urls = classLoader.getResources(resourceLocation);
            while (urls.hasMoreElements()) {
                UrlResource resource = new UrlResource(urls.nextElement());
                Properties properties = PropertiesLoaderUtils.loadProperties(resource);
                properties.forEach((name, value) -> {
                    String[] factoryImplementationNames = StringUtils.commaDelimitedListToStringArray((String) value);
                    List<String> implementations = result.computeIfAbsent(((String) name).trim(),
                            key -> new ArrayList<>(factoryImplementationNames.length));
                    Arrays.stream(factoryImplementationNames).map(String::trim).forEach(implementations::add);
                });
            }
            result.replaceAll(SpringFactoriesLoader::toDistinctUnmodifiableList);
        } catch (IOException ex) {
            throw new IllegalArgumentException("Unable to load factories from location [" + resourceLocation + "]", ex);
        }
        return Collections.unmodifiableMap(result);
    }

    private static List<String> toDistinctUnmodifiableList(String factoryType, List<String> implementations) {
        return implementations.stream().distinct().toList();
    }

}
