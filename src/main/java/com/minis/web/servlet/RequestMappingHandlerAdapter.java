package com.minis.web.servlet;

import com.minis.beans.factory.annotation.Autowired;
import com.minis.context.ApplicationContext;
import com.minis.context.ApplicationContextAware;
import com.minis.web.bind.annotation.ResponseBody;
import com.minis.web.converter.HttpMessageConverter;
import com.minis.web.bind.support.WebBindingInitializer;
import com.minis.web.bind.WebDataBinder;
import com.minis.web.bind.support.WebDataBinderFactory;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;

public class RequestMappingHandlerAdapter implements HandlerAdapter, ApplicationContextAware {
    private ApplicationContext webApplicationContext;

    @Autowired
    private WebBindingInitializer initializer;

    @Autowired
    private HttpMessageConverter converter;

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        handleInternal(request, response, (HandlerMethod) handler);
    }

    private void handleInternal(HttpServletRequest request, HttpServletResponse response, HandlerMethod handler) throws Exception {
        invokeHandlerMethod(request, response, handler);
    }

    private void invokeHandlerMethod(HttpServletRequest request, HttpServletResponse response, HandlerMethod handler) throws Exception {
        InvocableHandlerMethod invocableMethod = new InvocableHandlerMethod(handler, this.initializer, this.converter);
        invocableMethod.invokeAndHandle(request, response);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        // TODO: this is a servlet context, so it should be a subclass of AbstractApplicationContext
        //  and it can obtain parent context, it has the ability to obtain all beans
        this.webApplicationContext = applicationContext;
    }
}
