package com.minis.web.servlet;

import com.minis.context.ApplicationContext;
import com.minis.context.ApplicationContextAware;
import com.minis.web.bind.annotation.ResponseBody;
import com.minis.web.converter.HttpMessageConverter;
import com.minis.web.bind.support.WebBindingInitializer;
import com.minis.web.bind.WebDataBinder;
import com.minis.web.bind.support.WebDataBinderFactory;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Setter;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;

public class RequestMappingHandlerAdapter implements HandlerAdapter, ApplicationContextAware {
    private ApplicationContext webApplicationContext;

    @Setter
    private WebBindingInitializer webBindingInitializer;

    @Setter
    private HttpMessageConverter messageConverter;

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        handleInternal(request, response, (HandlerMethod) handler);
    }

    private void handleInternal(HttpServletRequest request, HttpServletResponse response, HandlerMethod handler) throws Exception {
        invokeHandlerMethod(request, response, handler);
    }

    private void invokeHandlerMethod(HttpServletRequest request, HttpServletResponse response, HandlerMethod handler) throws Exception {
        Parameter[] methodParameters = handler.getMethod().getParameters();

        boolean isRequestParam = false;
        List<Object> methodsServletParmaObjs = new ArrayList<>();

        List<Object> methodParamObjList = new ArrayList<>();
        List<String> methodParamObjNameList = new ArrayList<>();
        Object[] methodParamObjs = new Object[methodParameters.length];
        for (Parameter methodParameter : methodParameters) {
            if (methodParameter.getType() == HttpServletRequest.class || methodParameter.getType() == HttpServletResponse.class) {
                isRequestParam = true;
                methodsServletParmaObjs.add(methodParameter.getType() == HttpServletRequest.class ? request : response);
            } else {
                Object methodParamObj = methodParameter.getType().getConstructor().newInstance();
                methodParamObjList.add(methodParamObj);
                methodParamObjNameList.add(methodParameter.getName());
            }
        }
        if (isRequestParam) {
            methodParamObjs = methodsServletParmaObjs.toArray();
        } else if (!methodParamObjList.isEmpty()) {
            WebDataBinder wdb = new WebDataBinderFactory().createBinder(request, methodParamObjList, methodParamObjNameList);
            webBindingInitializer.registerBinder(wdb);
            wdb.bind(request);
            methodParamObjs = methodParamObjList.toArray();
        }

        Method invocableMethod = handler.getMethod();
        Object returnObj = invocableMethod.invoke(handler.getBean(), methodParamObjs);
        Class<?> returnType = invocableMethod.getReturnType();

        if (invocableMethod.isAnnotationPresent(ResponseBody.class)) {
            this.messageConverter.write(returnObj, response);
        } else if (returnType == void.class) {
            // ignore
        } else if (returnType == String.class) {
            response.getWriter().write((String) returnObj);
        } else {
            this.messageConverter.write(returnObj, response);
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        // TODO: this is a servlet context, so it should be a subclass of AbstractApplicationContext
        //  and it can obtain parent context, it has the ability to obtain all beans
        this.webApplicationContext = applicationContext;
    }
}
