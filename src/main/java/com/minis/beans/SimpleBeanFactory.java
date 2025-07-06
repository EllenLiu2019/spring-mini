package com.minis.beans;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SimpleBeanFactory implements BeanFactory {
    private List<BeanDefinition> beanDefinitions = new ArrayList<>();
    private List<String> beanNames = new ArrayList<>();
    private Map<String, Object> singletons = new HashMap<>();

    @Override
    public Object getBean(String beanName) throws BeansException {
        Object newInstance = this.singletons.get(beanName);
        if (newInstance == null) {
            int index = this.beanNames.indexOf(beanName);
            if (index == -1) {
                throw new BeansException("No bean named '" + beanName + "' is defined");
            }
            BeanDefinition beanDefinition = this.beanDefinitions.get(index);
            try {
                newInstance = Class.forName(beanDefinition.getClassName()).getConstructor().newInstance();
            } catch (ReflectiveOperationException e) {
                throw new RuntimeException(e);
            }
            this.singletons.put(beanName, newInstance);
        }
        return newInstance;
    }

    @Override
    public void registerBeanDefinition(BeanDefinition beanDefinition) {
        this.beanDefinitions.add(beanDefinition);
        this.beanNames.add(beanDefinition.getId());
    }

}
