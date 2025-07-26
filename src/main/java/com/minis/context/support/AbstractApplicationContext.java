package com.minis.context.support;

import com.minis.beans.BeansException;
import com.minis.beans.factory.BeanFactory;
import com.minis.beans.factory.config.BeanFactoryPostProcessor;
import com.minis.beans.factory.config.BeanPostProcessor;
import com.minis.beans.factory.support.ConfigurableListableBeanFactory;
import com.minis.context.ApplicationContext;
import com.minis.context.ApplicationContextAware;
import com.minis.context.ApplicationEventPublisher;
import com.minis.context.ConfigurableApplicationContext;
import com.minis.core.env.Environment;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

public abstract class AbstractApplicationContext implements ConfigurableApplicationContext {
    private Environment environment;
    private final List<BeanFactoryPostProcessor> beanFactoryPostProcessors = new ArrayList<>();
    private long startupDate;
    private final AtomicBoolean active = new AtomicBoolean();
    private final AtomicBoolean closed = new AtomicBoolean();
    private ApplicationEventPublisher applicationEventPublisher;

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    @Override
    public Environment getEnvironment() {
        return this.environment;
    }

    @Override
    // TODO: it's sub-class override this method, so this method only serves servlet context,
    //  means all servlet controller beans extended ApplicationContextAware can obtain controller context
    public Object getBean(String beanName) throws BeansException, ReflectiveOperationException {
        return getBeanFactory().getBean(beanName);
    }

    public List<BeanFactoryPostProcessor> getBeanFactoryPostProcessors() {
        return this.beanFactoryPostProcessors;
    }

    @Override
    public void addBeanFactoryPostProcessor(BeanFactoryPostProcessor postProcessor) {
        this.beanFactoryPostProcessors.add(postProcessor);
    }

    public void refresh() {
        ConfigurableListableBeanFactory beanFactory = obtainFreshBeanFactory();

        prepareBeanFactory(beanFactory);

        try {
            postProcessBeanFactory(beanFactory);

            // TODO:
            //  getBean 并 invoke beanFactory processors：
            //  eg: 1. ConfigurationClassPostProcessor: 通过注册的 configuration classes 获得更多的 bean definitions，
            //           包含：实例化 @import 的类 AutoConfigurationImportSelector.class，
            //           加载 @AutoConfiguration 拼接的文件：META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports
            //      2. AopAutoConfiguration#ClassProxyingConfiguration: 注册 proxy creator 的 beanDefinition
            //  带有 @Configuration 的配置类，会被替换为 cglib 的 proxy
            //  带有 @Async 的类，会被替换为 jdk 的 proxy ？
            invokeBeanFactoryPostProcessors(beanFactory);

            // TODO: Register bean processors that intercept bean creation.
            //  注册 bean processors: 实例化 beanDefinition 注册中心中注册的 postProcessor beans
            //  并放入 beanFactory 的成员变量 List<BeanPostProcessor> beanPostProcessors
            //  eg. CommonAnnotationBeanPostProcessor
            //      AutowiredAnnotationBeanPostProcessor
            registerBeanPostProcessors();

            initApplicationEventPublisher();

            // TODO: start tomcat server
            onRefresh();

            // TODO: Instantiate all remaining (non-lazy-init) singletons.
            //  过程中会调用 bean processors 处理 实例化后的 bean
            finishBeanFactoryInitialization(beanFactory);

            registerListeners();

            // Last step: publish corresponding event.
            finishRefresh();
        } catch (ReflectiveOperationException | BeansException e) {
            throw new RuntimeException(e);
        }
    }

    private void invokeBeanFactoryPostProcessors(ConfigurableListableBeanFactory beanFactory) throws ReflectiveOperationException, BeansException {
        PostProcessorRegistrationDelegate.invokeBeanFactoryPostProcessors(beanFactory);
    }

    private ConfigurableListableBeanFactory obtainFreshBeanFactory() {
        refreshBeanFactory();
        return getBeanFactory();
    }

    protected void prepareBeanFactory(ConfigurableListableBeanFactory beanFactory) {
        beanFactory.addBeanPostProcessor(new ApplicationContextAwareProcessor(this));
    }
    //---------------------------------------------------------------------
    // Abstract methods that must be implemented by subclasses
    //---------------------------------------------------------------------

    /**
     * Subclasses must implement this method to perform the actual configuration load.
     * The method is invoked by {@link #refresh()} before any other initialization work.
     * <p>A subclass will either create a new bean factory and hold a reference to it,
     * or return a single BeanFactory instance that it holds. In the latter case, it will
     * usually throw an IllegalStateException if refreshing the context more than once.
     * @throws BeansException if initialization of the bean factory failed
     * @throws IllegalStateException if already initialized and multiple refresh
     * attempts are not supported
     */
    protected abstract void refreshBeanFactory() throws IllegalStateException;
    public void finishBeanFactoryInitialization(ConfigurableListableBeanFactory beanFactory) {
        beanFactory.preInstantiateSingletons();
    }

    public abstract void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory);

    public abstract void registerBeanPostProcessors();

    public abstract void initApplicationEventPublisher();

    protected void onRefresh() {
    }

    ;

    public abstract void registerListeners();

    public abstract void finishRefresh();

    @Override
    public void registerSingleton(String beanName, Object singletonObject) {
        getBeanFactory().registerSingleton(beanName, singletonObject);
    }

    @Override
    public boolean containsBean(String name) {
        return getBeanFactory().containsBean(name);
    }

    @Override
    public boolean isSingleton(String name) {
        return getBeanFactory().isSingleton(name);
    }

    @Override
    public boolean isPrototype(String name) {
        return getBeanFactory().isPrototype(name);
    }

    @Override
    public Class<?> getType(String name) {
        return getBeanFactory().getType(name);
    }

    @Override
    public Object getSingleton(String beanName) {
        return getBeanFactory().getSingleton(beanName);
    }

    @Override
    public boolean containsSingleton(String beanName) {
        return getBeanFactory().containsSingleton(beanName);
    }

    @Override
    public String[] getSingletonNames() {
        return getBeanFactory().getSingletonNames();
    }

    @Override
    public boolean containsBeanDefinition(String beanName) {
        return getBeanFactory().containsBeanDefinition(beanName);
    }

    @Override
    public int getBeanDefinitionCount() {
        return getBeanFactory().getBeanDefinitionCount();
    }

    @Override
    public String[] getBeanDefinitionNames() {
        return getBeanFactory().getBeanDefinitionNames();
    }

    @Override
    public String[] getBeanNamesForType(Class<?> type) {
        return getBeanFactory().getBeanNamesForType(type);
    }

    @Override
    public <T> Map<String, T> getBeansOfType(Class<T> type) throws BeansException, ReflectiveOperationException {
        return getBeanFactory().getBeansOfType(type);
    }

    @Override
    public void addBeanPostProcessor(BeanPostProcessor beanPostProcessor) {
        getBeanFactory().addBeanPostProcessor(beanPostProcessor);

    }

    @Override
    public int getBeanPostProcessorCount() {
        return getBeanFactory().getBeanPostProcessorCount();
    }

    @Override
    public String[] getDependentBeans(String beanName) {
        return getBeanFactory().getDependentBeans(beanName);
    }

    @Override
    public String[] getDependenciesForBean(String beanName) {
        return getBeanFactory().getDependenciesForBean(beanName);
    }

    @Override
    public String getApplicationName() {
        return "";
    }

    @Override
    public long getStartupDate() {
        return this.startupDate;
    }

    @Override
    public void close() {
    }

    @Override
    public boolean isActive() {
        return true;
    }

    public ApplicationEventPublisher getApplicationEventPublisher() {
        return this.applicationEventPublisher;
    }

    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }
}
