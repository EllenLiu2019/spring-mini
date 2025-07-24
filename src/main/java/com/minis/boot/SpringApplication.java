package com.minis.boot;

import com.minis.beans.factory.support.AbstractAutowireCapableBeanFactory;
import com.minis.beans.factory.support.BeanDefinitionRegistry;
import com.minis.beans.factory.support.ConfigurableListableBeanFactory;
import com.minis.context.ApplicationContext;
import com.minis.context.support.AbstractApplicationContext;
import com.minis.core.io.SpringFactoriesLoader;
import com.minis.utils.ClassUtils;
import com.minis.context.ConfigurableApplicationContext;
import com.minis.utils.CollectionUtils;

import java.util.*;

public class SpringApplication {

    private final Set<Class<?>> primarySources;
    private List<ApplicationContextInitializer<?>> initializers;

    private Class<?> mainApplicationClass;
    private ApplicationContextFactory applicationContextFactory = ApplicationContextFactory.DEFAULT;

    final ApplicationProperties properties = new ApplicationProperties();

    @SuppressWarnings({"unchecked", "rawtypes"})
    public SpringApplication(Class<?>[] primarySources) {
        this.primarySources = new LinkedHashSet<>(Arrays.asList(primarySources));
        this.properties.setWebApplicationType(WebApplicationType.deduceFromClasspath());
        this.setInitializers((Collection) getSpringFactoriesInstances(ApplicationContextInitializer.class));
        this.mainApplicationClass = primarySources[0];

    }

    public void setInitializers(Collection<? extends ApplicationContextInitializer<?>> initializers) {
        this.initializers = new ArrayList<>(initializers);
    }

    public static ConfigurableApplicationContext run(Class<?> primarySource, String... args) {
        return run(new Class<?>[]{primarySource}, args);
    }

    public static ConfigurableApplicationContext run(Class<?>[] primarySources, String... args) {
        return new SpringApplication(primarySources).run(args);
    }

    public ConfigurableApplicationContext run(String... args) {
        ConfigurableApplicationContext context = null;
        context = this.createApplicationContext();
        prepareContext(context);
        refreshContext(context);
        return context;
    }

    private void refreshContext(ConfigurableApplicationContext context) {
        refresh(context);
    }

    protected void refresh(ConfigurableApplicationContext applicationContext) {
        applicationContext.refresh();
    }

    private void prepareContext(ConfigurableApplicationContext context) {
        ConfigurableListableBeanFactory beanFactory = context.getBeanFactory();
        if (beanFactory instanceof AbstractAutowireCapableBeanFactory autowireCapableBeanFactory) {
            autowireCapableBeanFactory.setAllowCircularReferences(this.properties.isAllowCircularReferences());
        }
        Set<Object> sources = getAllSources();
        this.load(context, sources.toArray(new Object[0]));
    }

    protected void load(ApplicationContext context, Object[] sources) {
        BeanDefinitionLoader loader = createBeanDefinitionLoader(getBeanDefinitionRegistry(context), sources);
        loader.load();
    }

    public Set<Object> getAllSources() {
        Set<Object> allSources = new LinkedHashSet<>();
        if (!CollectionUtils.isEmpty(this.primarySources)) {
            allSources.addAll(this.primarySources);
        }
        return Collections.unmodifiableSet(allSources);
    }

    protected ConfigurableApplicationContext createApplicationContext() {
        return this.applicationContextFactory.create(this.properties.getWebApplicationType());
    }
    private BeanDefinitionRegistry getBeanDefinitionRegistry(ApplicationContext context) {
        if (context instanceof BeanDefinitionRegistry registry) {
            return registry;
        }
        if (context instanceof AbstractApplicationContext abstractApplicationContext) {
            return (BeanDefinitionRegistry) abstractApplicationContext.getBeanFactory();
        }
        throw new IllegalStateException("Could not locate BeanDefinitionRegistry");
    }

    protected BeanDefinitionLoader createBeanDefinitionLoader(BeanDefinitionRegistry registry, Object[] sources) {
        return new BeanDefinitionLoader(registry, sources);
    }

    public ClassLoader getClassLoader() {
        return ClassUtils.getDefaultClassLoader();
    }

    private <T> List<T> getSpringFactoriesInstances(Class<T> type) {
        return SpringFactoriesLoader.forDefaultResourceLocation(getClassLoader()).load(type);
    }
}
