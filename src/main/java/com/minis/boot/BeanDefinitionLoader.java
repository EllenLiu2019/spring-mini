package com.minis.boot;

import com.minis.beans.factory.support.BeanDefinitionRegistry;
import com.minis.beans.factory.xml.XmlBeanDefinitionReader;
import com.minis.context.annotation.AnnotatedBeanDefinitionReader;
import com.minis.context.support.GenericApplicationContext;

public class BeanDefinitionLoader {

    private final Object[] sources;

    private final AnnotatedBeanDefinitionReader annotatedReader;

    private final XmlBeanDefinitionReader xmlReader;

    BeanDefinitionLoader(BeanDefinitionRegistry registry, Object... sources) {
        this.sources = sources;
        this.annotatedReader = new AnnotatedBeanDefinitionReader(registry);
        this.xmlReader = new XmlBeanDefinitionReader(((GenericApplicationContext) registry).getDefaultListableBeanFactory());
    }

    public void load() {
        for (Object source : this.sources) {
            load(source);
        }
    }

    private void load(Object source) {
        if (source instanceof Class<?> type) {
            load(type);
        }
    }

    private void load(Class<?> source) {
      this.annotatedReader.register(source);
    }
}
