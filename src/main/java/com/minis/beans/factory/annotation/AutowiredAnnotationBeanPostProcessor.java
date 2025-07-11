package com.minis.beans.factory.annotation;

import com.minis.beans.factory.BeanFactory;
import com.minis.beans.BeansException;
import com.minis.beans.factory.config.BeanPostProcessor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Field;

// TODO: 为了支持 @Autowired， 新增 AutowiredAnnotationBeanPostProcessor 类
//  该类实现 BeanPostProcessor 接口
//  方法：postProcessBeforeInitialization
//  主要用途：1. 扫描类中所有带 @Autowired 注解的属性，并设置属性值
public class AutowiredAnnotationBeanPostProcessor implements BeanPostProcessor {
    private static final Logger LOGGER = LogManager.getLogger(AutowiredAnnotationBeanPostProcessor.class.getName());
    private BeanFactory beanFactory;
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException, ReflectiveOperationException {
        LOGGER.debug("post Process Before Initialization for bean: " + beanName);

        Object result = bean;
        Class<?> clazz = bean.getClass();
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            if (field.isAnnotationPresent(Autowired.class)) {
                String fieldName = field.getName();
                Object autowiredObj = this.beanFactory.getBean(fieldName);
                field.setAccessible(true);
                field.set(bean, autowiredObj);
                LOGGER.debug("autowire " + fieldName + " for bean: " + beanName);
            }
        }
        return result;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        LOGGER.debug("postProcess After Initialization for bean: " + beanName);
        return bean;
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }
}
