package com.minis.scheduling.annotation;

import com.minis.aop.springframework.aop.framework.ProxyFactoryBean;
import com.minis.beans.factory.BeanFactory;
import com.minis.beans.factory.BeanFactoryAware;
import com.minis.beans.factory.config.BeanPostProcessor;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;

@Slf4j
public class AsyncAnnotationBeanPostProcessor implements BeanPostProcessor, BeanFactoryAware {

    private BeanFactory beanFactory;

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) {
        Object result = null;
        Method[] methods = bean.getClass().getDeclaredMethods();
        for (Method method : methods) {
            if (method.isAnnotationPresent(Async.class)) {
                log.debug("method = {} is Async.", method.getName());
                ProxyFactoryBean proxyFactoryBean = new ProxyFactoryBean();
                proxyFactoryBean.setTarget(bean);
                proxyFactoryBean.setBeanFactory(this.beanFactory);
                proxyFactoryBean.setInterceptorName("asyncAnnotationAdvisor");
                result = proxyFactoryBean;
            }
            break;
        }
        if (result != null) {
            bean = result;
        }
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) {
        return bean;
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }
}
