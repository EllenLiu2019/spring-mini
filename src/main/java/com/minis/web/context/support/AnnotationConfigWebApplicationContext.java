package com.minis.web.context.support;

import com.minis.aop.springframework.aop.framework.autoproxy.BeanNameAutoProxyCreator;
import com.minis.beans.factory.annotation.AutowiredAnnotationBeanPostProcessor;
import com.minis.beans.factory.config.BeanDefinition;
import com.minis.beans.factory.support.ConfigurableListableBeanFactory;
import com.minis.beans.factory.support.DefaultListableBeanFactory;
import com.minis.context.*;
import com.minis.context.event.ApplicationEvent;
import com.minis.context.event.ApplicationListener;
import com.minis.context.event.ContextRefreshedEvent;
import com.minis.context.support.AbstractApplicationContext;
import com.minis.scheduling.annotation.AsyncAnnotationBeanPostProcessor;
import com.minis.web.context.WebApplicationContext;
import jakarta.servlet.ServletContext;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.lang.reflect.Modifier;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

@Slf4j
public class AnnotationConfigWebApplicationContext extends AbstractApplicationContext implements WebApplicationContext {
    private ServletContext servletContext;
    private WebApplicationContext parentApplicationContext; // TODO: 持有对parent application context的单项引用
    private final DefaultListableBeanFactory beanFactory;

    public AnnotationConfigWebApplicationContext(String fileName, WebApplicationContext parentApplicationContext) {
        this.parentApplicationContext = parentApplicationContext;
        this.servletContext = parentApplicationContext.getServletContext(); // TODO: Ioc 与 servlet context 中 持有的 servlet context 是同一个
        this.beanFactory = new DefaultListableBeanFactory();
        this.beanFactory.setParent(this.parentApplicationContext.getBeanFactory());

        initApplicationContext(fileName);
    }

    private void initApplicationContext(String fileName) {
        URL xmlPath;
        try {
            xmlPath = this.getServletContext().getResource(fileName);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
        List<String> packageNames = XmlScanComponentHelper.getNodeValue(xmlPath);
        List<String> controllerNames = scanPackages(packageNames);
        loadBeanDefinitions(controllerNames);
        log.debug("-------------> [Servlet] webApplicationContext refresh START <-------------");
        refresh();
        log.debug("-------------> [Servlet] webApplicationContext refresh END <-------------");
    }

    private void loadBeanDefinitions(List<String> controllerNames) {
        for (String controllerName : controllerNames) {
            String beanId = controllerName.substring(controllerName.lastIndexOf(".") + 1);
            beanId = beanId.substring(0, 1).toLowerCase() + beanId.substring(1);
            BeanDefinition beanDefinition = new BeanDefinition(beanId, controllerName);
            this.beanFactory.registerBeanDefinition(beanId, beanDefinition);
        }
    }

    private List<String> scanPackages(List<String> packageNames) {
        List<String> tempControllerNames = new ArrayList<>();
        for (String packageName : packageNames) {
            tempControllerNames.addAll(scanPackage(packageName));
        }
        return tempControllerNames;
    }

    private Collection<String> scanPackage(String packageName) {
        List<String> instantiableClassNames = new ArrayList<>();
        URI uri;
        try {
            uri = Objects.requireNonNull(this.getClass().getResource("/" + packageName.replaceAll("\\.", "/"))).toURI();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        File dir = new File(uri);
        for (File file : Objects.requireNonNull(dir.listFiles())) {
            if (file.isDirectory()) {
                Collection<String> classNames = scanPackage(packageName + "." + file.getName());
                instantiableClassNames.addAll(classNames);
            } else {
                if (file.getName().endsWith(".class")) {
                    String className = packageName + "." + file.getName().replace(".class", "");
                    try {
                        Class<?> clazz = Class.forName(className);
                        if (isInstantiable(clazz)) {
                            instantiableClassNames.add(className);
                        }
                    } catch (ClassNotFoundException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
        return instantiableClassNames;
    }

    public boolean isInstantiable(Class<?> clazz) {
        return
                !clazz.isInterface() &&
                        !Modifier.isAbstract(clazz.getModifiers()) &&
                        !clazz.isEnum() &&
                        !clazz.isArray() &&
                        !clazz.isPrimitive() &&
                        hasNoArgsConstructor(clazz);
    }

    private boolean hasNoArgsConstructor(Class<?> clazz) {
        try {
            clazz.getDeclaredConstructor();
            return true;
        } catch (NoSuchMethodException e) {
            return false;
        }
    }

    @Override
    public void setServletContext(ServletContext servletContext) {
        this.servletContext = servletContext;
    }

    @Override
    public ServletContext getServletContext() {
        return this.servletContext;
    }

    @Override
    public void publishEvent(ApplicationEvent event) {
        this.getApplicationEventPublisher().publishEvent(event);
    }

    @Override
    public void addApplicationListener(ApplicationListener<?> listener) {
        this.getApplicationEventPublisher().addApplicationListener(listener);
    }

    @Override
    public void registerListeners() {
        String[] bdNames = this.beanFactory.getBeanDefinitionNames();
        for (String bdName : bdNames) {
            Object bean;
            try {
                bean = this.beanFactory.getBean(bdName);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            if (bean instanceof ApplicationListener<?> listener) {
                this.getApplicationEventPublisher().addApplicationListener(listener);
            }
        }
    }

    @Override
    public void initApplicationEventPublisher() {
        ApplicationEventPublisher aep = new SimpleApplicationEventPublisher();
        this.setApplicationEventPublisher(aep);
    }

    @Override
    protected void refreshBeanFactory() throws IllegalStateException {

    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) {
    }

    @Override
    public void registerBeanPostProcessors() {
        this.beanFactory.addBeanPostProcessor(new AutowiredAnnotationBeanPostProcessor());
        this.beanFactory.addBeanPostProcessor(new BeanNameAutoProxyCreator());
        this.beanFactory.addBeanPostProcessor(new AsyncAnnotationBeanPostProcessor());
    }

    @Override
    public void onRefresh() {
        this.beanFactory.refresh();
    }

    @Override
    public void finishRefresh() {
        this.publishEvent(new ContextRefreshedEvent(this));
    }

    @Override
    public ConfigurableListableBeanFactory getBeanFactory() throws IllegalStateException {
        return this.beanFactory;
    }

}
