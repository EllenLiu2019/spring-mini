package com.minis.context;

import com.minis.beans.BeansException;
import com.minis.beans.factory.annotation.AutowiredAnnotationBeanPostProcessor;
import com.minis.beans.factory.config.BeanFactoryPostProcessor;
import com.minis.beans.factory.support.ConfigurableListableBeanFactory;
import com.minis.beans.factory.support.DefaultListableBeanFactory;
import com.minis.beans.factory.xml.XmlBeanDefinitionReader;
import com.minis.core.ClassPathXmlResource;

import java.util.ArrayList;
import java.util.List;

public class ClassPathXmlApplicationContext extends AbstractApplicationContext {
    private DefaultListableBeanFactory beanFactory;
    private final List<BeanFactoryPostProcessor> beanFactoryPostProcessors = new ArrayList<>();

    public ClassPathXmlApplicationContext(String fileName){
        //TODO: 将xml文件中的内容读入内存，解析成BeanDefinition，
        // 并将BeanDefinition注册到beanFactory中
        ClassPathXmlResource resource = new ClassPathXmlResource(fileName);
        DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();
        XmlBeanDefinitionReader reader = new XmlBeanDefinitionReader(beanFactory);
        reader.loadBeanDefinitions(resource);
        //TODO: 将BeanFactory的实例保存为当前类的成员变量以供后续使用（getBean()）
        this.beanFactory = beanFactory;
    }

    public void registerBeanPostProcessors() {
        this.beanFactory.addBeanPostProcessor(new AutowiredAnnotationBeanPostProcessor());
    }

    public void onRefresh() {
        this.beanFactory.refresh();
    }

    @Override
    public Object getBean(String beanName) throws BeansException, ReflectiveOperationException {
        return this.beanFactory.getBean(beanName);
    }

    @Override
    public void registerListeners() {
        ApplicationListener listener = new ApplicationListener();
        this.getApplicationEventPublisher().addApplicationListener(listener);
    }

    @Override
    public void initApplicationEventPublisher() {
        ApplicationEventPublisher aep = new SimpleApplicationEventPublisher();
        this.setApplicationEventPublisher(aep);
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) {
    }

    @Override
    public void publishEvent(ApplicationEvent event) {
        this.getApplicationEventPublisher().publishEvent(event);
    }

    @Override
    public void addApplicationListener(ApplicationListener listener) {
        this.getApplicationEventPublisher().addApplicationListener(listener);
    }

    @Override
    public void addBeanFactoryPostProcessor(BeanFactoryPostProcessor postProcessor) {
        this.beanFactoryPostProcessors.add(postProcessor);
    }

    @Override
    public ConfigurableListableBeanFactory getBeanFactory() throws IllegalStateException {
        return this.beanFactory;
    }

    @Override
    public void finishRefresh() {
        publishEvent(new ContextRefreshEvent("Ioc Context Refreshed..."));
    }
}
