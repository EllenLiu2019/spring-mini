package com.minis.beans.factory.annotation;

import com.minis.beans.BeansException;
import com.minis.beans.factory.support.AutowireCapableBeanFactory;
import com.minis.beans.factory.config.BeanPostProcessor;

import java.lang.reflect.Field;

// TODO: 为了支持 @Autowired， 新增 AutowiredAnnotationBeanPostProcessor 类
//  该类实现 BeanPostProcessor 接口
//  方法：postProcessBeforeInitialization
//  主要用途：1. 扫描类中所有带 @Autowired 注解的属性，并设置属性值
public class AutowiredAnnotationBeanPostProcessor implements BeanPostProcessor {
    private AutowireCapableBeanFactory beanFactory;
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException, ReflectiveOperationException {
        System.out.println("postProcessBeforeInitialization for bean: " + beanName);

        Object result = bean;
        Class<?> clazz = bean.getClass();
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            if (field.isAnnotationPresent(Autowired.class)) {
                String fieldName = field.getName();
                Object autowiredObj = this.beanFactory.getBean(fieldName);
                field.setAccessible(true);
                field.set(bean, autowiredObj);
                System.out.println("autowire " + fieldName + " for bean " + beanName);
            }
        }
        return result;
    }

    /*@Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        return null;
    }*/

    public void setBeanFactory(AutowireCapableBeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }
}
