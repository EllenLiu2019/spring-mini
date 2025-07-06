package com.minis.beans.factory.xml;

import com.minis.beans.factory.support.SimpleBeanFactory;
import com.minis.beans.factory.config.BeanDefinition;
import com.minis.core.Resource;
import org.dom4j.Element;


public class XmlBeanDefinitionReader {
    private SimpleBeanFactory simpleBeanFactory;
    public XmlBeanDefinitionReader(SimpleBeanFactory simpleBeanFactory) {
        this.simpleBeanFactory = simpleBeanFactory;
    }
    public void loadBeanDefinitions(Resource resource) {
        while (resource.hasNext()) {
            Element element = (Element) resource.next();
            String beanId = element.attributeValue("id");
            String className = element.attributeValue("class");
            BeanDefinition beanDefinition = new BeanDefinition(beanId, className);
            this.simpleBeanFactory.registerBeanDefinition(beanId, beanDefinition);
        }
    }

}
