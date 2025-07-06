package com.minis.beans;

import com.minis.core.Resource;
import org.dom4j.Element;


public class XmlBeanDefinitionReader {
    private BeanFactory beanFactory;
    public XmlBeanDefinitionReader(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }
    public void loadBeanDefinitions(Resource resource) {
        while (resource.hasNext()) {
            Element element = (Element) resource.next();
            String beanId = element.attributeValue("id");
            String className = element.attributeValue("class");
            this.beanFactory.registerBeanDefinition(new BeanDefinition(beanId, className));
        }
    }

}
