package com.minis.context;

import com.minis.beans.BeanFactory;
import com.minis.beans.BeansException;
import com.minis.beans.factory.support.SimpleBeanFactory;
import com.minis.beans.factory.xml.XmlBeanDefinitionReader;
import com.minis.core.ClassPathXmlResource;

public class ClassPathXmlApplicationContext implements BeanFactory, ApplicationEventPublisher {
    private BeanFactory beanFactory;

    public ClassPathXmlApplicationContext(String fileName) {
        //TODO: 将xml文件中的内容读入内存，解析成BeanDefinition，
        // 并将BeanDefinition注册到beanFactory中
        SimpleBeanFactory beanFactory = new SimpleBeanFactory();
        XmlBeanDefinitionReader reader = new XmlBeanDefinitionReader(beanFactory);
        reader.loadBeanDefinitions(new ClassPathXmlResource(fileName));
        //TODO: 将BeanFactory的实例保存为当前类的成员变量以供后续使用（getBean()）
        this.beanFactory = beanFactory;
    }
    @Override
    public Object getBean(String beanName) throws BeansException, ReflectiveOperationException {
        return this.beanFactory.getBean(beanName);
    }

    @Override
    public boolean containsBean(String beanName) {
        return false;
    }

    @Override
    public boolean isSingleton(String beanName) {
        return false;
    }

    @Override
    public boolean isPrototype(String beanName) {
        return false;
    }

    @Override
    public Class<?> getType(String beanName) {
        return null;
    }

    @Override
    public void registerBean(String beanName, Object obj) {

    }

    @Override
    public void publishEvent(ApplicationEvent event) {

    }
}
