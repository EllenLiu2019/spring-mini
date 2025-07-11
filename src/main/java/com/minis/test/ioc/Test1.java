package com.minis.test.ioc;


import com.minis.beans.BeansException;
import com.minis.context.ClassPathXmlApplicationContext;

public class Test1 {
    public static void main(String[] args) throws BeansException, ReflectiveOperationException {
        ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext("beans.xml");
        applicationContext.refresh();
        AServiceImpl aService = (AServiceImpl) applicationContext.getBean("aservice");
        aService.sayHello();
        aService.getRef1().sayHello();
    }
}
