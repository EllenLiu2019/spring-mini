package com.minis.test;


import com.minis.beans.BeansException;
import com.minis.context.ClassPathXmlApplicationContext;

public class Test1 {
    public static void main(String[] args) throws BeansException, ReflectiveOperationException {
        ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext("beans.xml");
        AService aService = (AService) applicationContext.getBean("aservice");
        aService.sayHello();  
    }
}
