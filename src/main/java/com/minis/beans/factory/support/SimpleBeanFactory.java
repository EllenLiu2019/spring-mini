package com.minis.beans.factory.support;

import com.minis.beans.BeanFactory;
import com.minis.beans.BeansException;
import com.minis.beans.factory.config.BeanDefinition;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SimpleBeanFactory extends DefaultSingletonBeanRegistry implements BeanFactory, BeanDefinitionRegistry {
    private Map<String, BeanDefinition> beanDefinitionMap = new ConcurrentHashMap<>();
    private List<String> beanDefinitionNames = new ArrayList<>();

    @Override
    public Object getBean(String beanName) throws BeansException, ReflectiveOperationException {
        Object singleton = this.singletons.get(beanName);
        if (singleton == null) {
            BeanDefinition beanDefinition = this.beanDefinitionMap.get(beanName);
            if (beanDefinition == null) {
                throw new BeansException("No bean named '" + beanName + "' is defined");
            }
            singleton = Class.forName(beanDefinition.getClassName()).newInstance();
            this.registerSingleton(beanName, singleton);
        }
        return singleton;
    }

    // TODO: why need this method? BeanFactory holds BeanDefinition,
    //   SingletonBeanRegistry holds singleton object
    @Override
    public void registerBean(String beanName, Object obj) {
        this.registerSingleton(beanName, obj);
    }

    // TODO: why need this method? BeanFactory holds BeanDefinition,
    //  SingletonBeanRegistry holds singleton object
    @Override
    public boolean containsBean(String beanName) {
        return containsSingleton(beanName);
    }

    @Override
    public boolean isSingleton(String name) {
        return this.beanDefinitionMap.get(name).isSingleton();
    }
    @Override
    public boolean isPrototype(String name) {
        return this.beanDefinitionMap.get(name).isPrototype();
    }
    @Override
    public Class<?> getType(String beanName) {
        return this.beanDefinitionMap.get(beanName).getBeanClass();
    }

    @Override
    public void registerBeanDefinition(String name, BeanDefinition beanDefinition) {
        this.beanDefinitionMap.put(name, beanDefinition);
        this.beanDefinitionNames.add(name);
        // TODO: 注册单例，非懒加载则直接创建 bean 实例
        if (!beanDefinition.isLazyInit()) {
            try {
                getBean(name);
            } catch (BeansException | ReflectiveOperationException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }
    @Override
    public void removeBeanDefinition(String name) {
        this.beanDefinitionMap.remove(name);
        this.beanDefinitionNames.remove(name);
        this.removeSingleton(name);
    }

    public BeanDefinition getBeanDefinition(String name) {
        return this.beanDefinitionMap.get(name);
    }
    public boolean containsBeanDefinition(String name) {
        return this.beanDefinitionMap.containsKey(name);
    }


}
