package com.minis.aop.springframework.aop.framework.autoproxy;

import com.minis.aop.springframework.aop.PointcutAdvisor;
import com.minis.aop.springframework.aop.framework.ProxyFactoryBean;
import com.minis.beans.BeansException;
import com.minis.beans.factory.BeanFactory;
import com.minis.beans.factory.BeanFactoryAware;
import com.minis.beans.factory.config.BeanPostProcessor;
import com.minis.beans.factory.config.InstantiationAwareBeanPostProcessor;
import com.minis.utils.PatternMatchUtils;
import lombok.Setter;

public class BeanNameAutoProxyCreator implements BeanPostProcessor, BeanFactoryAware {

    @Setter
    private String pattern;
    private BeanFactory beanFactory;
    @Setter
    private String interceptorName;
    private PointcutAdvisor advisor;

    public BeanNameAutoProxyCreator() {
        this.pattern = "*Action";
        this.interceptorName = "advisor";
    }

    @Override
    // TODO: Create proxy here if we have a custom TargetSource.
    //   Suppresses unnecessary default instantiation of the target bean:
    //   The TargetSource will handle target instances in a custom fashion.
    public Object postProcessBeforeInitialization(Object bean, String beanName) {
        Object result = null;
        if (this.isMatch(beanName, this.pattern)) {
            ProxyFactoryBean proxyFactoryBean = new ProxyFactoryBean();
            proxyFactoryBean.setTarget(bean);
            proxyFactoryBean.setBeanFactory(this.beanFactory);
            proxyFactoryBean.setInterceptorName(this.interceptorName);
            result = proxyFactoryBean;
        }
        if (result != null) {
            bean = result;
        }
        return bean;

    }

    private boolean isMatch(String beanName, String pattern) {
        return PatternMatchUtils.simpleMatch(pattern, beanName);
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }
}
