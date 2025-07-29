package com.minis.context.support;

import com.minis.beans.BeansException;
import com.minis.beans.factory.annotation.AutowiredAnnotationBeanPostProcessor;
import com.minis.beans.factory.config.BeanFactoryPostProcessor;
import com.minis.beans.factory.support.ConfigurableListableBeanFactory;
import com.minis.beans.factory.support.DefaultListableBeanFactory;
import com.minis.beans.factory.xml.XmlBeanDefinitionReader;
import com.minis.context.*;
import com.minis.context.event.ApplicationEvent;
import com.minis.context.event.ApplicationListener;
import com.minis.context.event.ContextRefreshedEvent;
import com.minis.core.ClassPathXmlResource;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Slf4j
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
        log.debug("-------------> [IoC] webApplicationContext refresh START<-------------");
        refresh();
        log.debug("-------------> [IoC] webApplicationContext refresh END <-------------");
    }

    // TODO: IoC beanFactory 与 Servlet beanFactory 是创建的两个（new DefaultListableBeanFactory()）
    //  List<beanPostProcessor> beanPostProcessors 是 beanFactory 中的成员变量；
    //  beanPostProcessors 在不同的 applicationContext 各自为政，互不干扰！
    //  所以，这里会新 new 一个 postProcessor 放入 beanFactory；
    //  否则 虽然是两个beanFactory, 但 使用了一个 postProcessor 的引用，
    //  会导致同一个 postProcessor 同时在 两个 application context 中生效，不符合设计原则：职责单一
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
        String[] bdNames = this.beanFactory.getBeanDefinitionNames();
        for (String bdName : bdNames) {
            Object bean;
            try {
                bean = this.beanFactory.getBean(bdName);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            if (bean instanceof ApplicationListener<?> listener) {
                this.getApplicationEventPublisher().addApplicationListener(listener);
            }
        }
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
    public void addApplicationListener(ApplicationListener<?> listener) {
        this.getApplicationEventPublisher().addApplicationListener(listener);
    }

    @Override
    public void addBeanFactoryPostProcessor(BeanFactoryPostProcessor postProcessor) {
        this.beanFactoryPostProcessors.add(postProcessor);
    }

    @Override
    protected void refreshBeanFactory() throws IllegalStateException {

    }

    @Override
    public ConfigurableListableBeanFactory getBeanFactory() throws IllegalStateException {
        return this.beanFactory;
    }

    @Override
    public void finishRefresh() {
        this.publishEvent(new ContextRefreshedEvent(this));
    }
}
