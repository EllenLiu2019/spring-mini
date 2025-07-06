package com.minis.beans.factory.xml;

import com.minis.beans.factory.config.*;
import com.minis.beans.factory.support.SimpleBeanFactory;
import com.minis.core.Resource;
import org.dom4j.Element;

import java.util.List;


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

            // TODO: properties
            List<Element> propertyElements = element.elements("property");
            PropertyValues pvs = new PropertyValues();
            for (Element propertyElement : propertyElements) {
                String propertyName = propertyElement.attributeValue("name");
                String propertyValue = propertyElement.attributeValue("value");
                String propertyType = propertyElement.attributeValue("type");
                pvs.addPropertyValue(new PropertyValue(propertyName, propertyValue, propertyType));
            }
            beanDefinition.setPropertyValues(pvs);

            //TODO: 构造函数参数
            List<Element> argsElems = element.elements("constructor-arg");
            ArgumentValues args = new ArgumentValues();
            for (Element argsElem : argsElems) {
                String argType = argsElem.attributeValue("type");
                String argValue = argsElem.attributeValue("value");
                String argName = argsElem.attributeValue("name");
                args.addArgumentValue(new ArgumentValue(argType, argValue, argName));
            }
            beanDefinition.setConstructorArgumentValues(args);

            this.simpleBeanFactory.registerBeanDefinition(beanId, beanDefinition);
        }
    }

}
