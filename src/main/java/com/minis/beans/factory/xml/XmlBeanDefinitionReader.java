package com.minis.beans.factory.xml;

import com.minis.beans.factory.config.*;
import com.minis.beans.factory.support.AbstractBeanFactory;
import com.minis.core.Resource;
import org.dom4j.Element;

import java.util.ArrayList;
import java.util.List;


public class XmlBeanDefinitionReader {
    private AbstractBeanFactory abstractBeanFactory;
    public XmlBeanDefinitionReader(AbstractBeanFactory abstractBeanFactory) {
        this.abstractBeanFactory = abstractBeanFactory;
    }
    public void loadBeanDefinitions(Resource resource) {
        while (resource.hasNext()) {
            Element element = (Element) resource.next();
            String beanId = element.attributeValue("id");
            String className = element.attributeValue("class");
            BeanDefinition beanDefinition = new BeanDefinition(beanId, className);

            //TODO: 构造函数参数
            List<Element> argsElems = element.elements("constructor-arg");
            ConstructorArgumentValues args = new ConstructorArgumentValues();
            for (Element argsElem : argsElems) {
                String argType = argsElem.attributeValue("type");
                String argValue = argsElem.attributeValue("value");
                String argName = argsElem.attributeValue("name");
                args.addArgumentValue(new ConstructorArgumentValue(argType, argValue, argName));
            }
            beanDefinition.setConstructorArgumentValues(args);

            // TODO: properties
            List<Element> propertyElements = element.elements("property");
            PropertyValues pvs = new PropertyValues();
            List<String> refs = new ArrayList<>();
            for (Element propertyElement : propertyElements) {
                String propertyName = propertyElement.attributeValue("name");
                String propertyValue = propertyElement.attributeValue("value");
                String propertyType = propertyElement.attributeValue("type");
                String pRef = propertyElement.attributeValue("ref");
                boolean isRef = false;
                if (pRef != null && !pRef.isEmpty()) {
                    propertyValue = pRef;
                    isRef = true;
                    refs.add(pRef);
                }
                pvs.addPropertyValue(new PropertyValue(propertyName, propertyValue, propertyType, isRef));
            }
            beanDefinition.setPropertyValues(pvs);
            String[] refArray = refs.toArray(new String[0]);
            beanDefinition.setDependsOn(refArray);

            this.abstractBeanFactory.registerBeanDefinition(beanId, beanDefinition);
        }
    }

}
