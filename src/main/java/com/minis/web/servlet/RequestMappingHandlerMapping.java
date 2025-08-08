package com.minis.web.servlet;

import com.minis.beans.BeansException;
import com.minis.beans.factory.InitializingBean;
import com.minis.context.ApplicationContext;
import com.minis.context.ApplicationContextAware;
import com.minis.web.bind.annotation.RequestMapping;
import jakarta.servlet.http.HttpServletRequest;

import java.lang.reflect.Method;

public class RequestMappingHandlerMapping implements HandlerMapping, ApplicationContextAware, InitializingBean {
    private MappingRegistry mappingRegistry;
    private ApplicationContext applicationContext;

    public RequestMappingHandlerMapping() {
        this.mappingRegistry = new MappingRegistry();
    }

    @Override
    public void afterPropertiesSet() {
        this.registerMapping();
    }

    protected void registerMapping() {
        Object beanInstance;
        Method[] methods;

        String[] controllerNames = applicationContext.getBeanDefinitionNames();
        for (String controllerName : controllerNames) {
            // getBean from applicationContext,
            // find all methods with @RequestMapping
            // build up mapping for each url->beanInstance & url->method
            try {
                beanInstance = applicationContext.getBean(controllerName);
                methods = beanInstance.getClass().getDeclaredMethods();
            } catch (ReflectiveOperationException | BeansException e) {
                throw new RuntimeException(e);
            }

            for (Method method : methods) {
                if (method.isAnnotationPresent(RequestMapping.class)) {
                    String url = method.getAnnotation(RequestMapping.class).value();
                    this.mappingRegistry.getUrlMappingNames().add(url);
                    this.mappingRegistry.getMappingObjs().put(url, beanInstance);
                    HandlerMethod handlerMethod = new HandlerMethod(beanInstance, method);
                    this.mappingRegistry.getMappingMethods().put(url, handlerMethod);
                }
            }
        }
    }

    @Override
    public HandlerMethod getHandler(HttpServletRequest request) {
        // 通过 request 中的 path 获取 url 对应的 beanInstance & method
        //  build up HandlerMethod which is the "request-handler"
        String servletPath = request.getServletPath();
        if (!mappingRegistry.getUrlMappingNames().contains(servletPath)) {
            return null;
        }
        return mappingRegistry.getMappingMethods().get(servletPath);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }
}
