package com.minis.beans.factory.support;

import com.minis.beans.BeansException;
import com.minis.beans.factory.annotation.AutowiredAnnotationBeanPostProcessor;

import java.util.ArrayList;
import java.util.List;

// TODO: 为了实现注解，新增AutowireCapableBeanFactory 继承自 AbstractBeanFactory
//  新增类中添加成员变量 List<AutowiredAnnotationBeanPostProcessor> beanPostProcessors 用于存储 AutowiredAnnotationBeanPostProcessor
public class AutowireCapableBeanFactory extends AbstractBeanFactory {
    private final List<AutowiredAnnotationBeanPostProcessor> beanPostProcessors = new ArrayList<>();
    public void addBeanPostProcessor(AutowiredAnnotationBeanPostProcessor beanPostProcessor) {
        this.beanPostProcessors.remove(beanPostProcessor);
        this.beanPostProcessors.add(beanPostProcessor);
    }
    public List<AutowiredAnnotationBeanPostProcessor> getBeanPostProcessors() {
        return beanPostProcessors;
    }
    @Override
    public Object applyBeanPostProcessorBeforeInitialization(Object existingBean, String beanName) throws BeansException, ReflectiveOperationException {
        Object result = existingBean;
        for(AutowiredAnnotationBeanPostProcessor beanPostProcessor: getBeanPostProcessors()) {
            beanPostProcessor.setBeanFactory(this);
            result = beanPostProcessor.postProcessBeforeInitialization(result, beanName);
        }
        return result;
    }
    /*@Override
    public Object applyBeanPostProcessorAfterInitialization(Object existingBean, String beanName) throws BeansException {
        return null;
    }*/
}
