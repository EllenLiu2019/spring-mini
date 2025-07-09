package com.minis.web.servlet;

import com.minis.beans.BeansException;
import com.minis.web.WebBindingInitializer;
import com.minis.web.WebDataBinder;
import com.minis.web.WebDataBinderFactory;
import com.minis.web.context.WebApplicationContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;

public class RequestMappingHandlerAdapter implements HandlerAdapter {
    private WebApplicationContext wac;
    private WebBindingInitializer webBindingInitializer;

    public RequestMappingHandlerAdapter(WebApplicationContext wac) throws ReflectiveOperationException, BeansException {
        this.wac = wac;
        this.webBindingInitializer = (WebBindingInitializer) this.wac.getBean("dateInitializer");
    }

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        handleInternal(request, response, (HandlerMethod) handler);
    }

    private void handleInternal(HttpServletRequest request, HttpServletResponse response, HandlerMethod handler) throws Exception {
        Parameter[] methodParameters = handler.getMethod().getParameters();

        List<Object> methodParamObjList = new ArrayList<>();
        List<String> methodParamObjNameList = new ArrayList<>();
        for (Parameter methodParameter : methodParameters) {
            Object methodParamObj = methodParameter.getType().getConstructor().newInstance();
            methodParamObjList.add(methodParamObj);
            methodParamObjNameList.add(methodParameter.getName());
        }

        WebDataBinder wdb = new WebDataBinderFactory().createBinder(request, methodParamObjList, methodParamObjNameList);
        webBindingInitializer.registerBinder(wdb);
        wdb.bind(request);

        Object[] methodParamObjs = methodParamObjList.toArray();

        Method invocableMethod = handler.getMethod();
        Object returnObj = invocableMethod.invoke(handler.getBean(), methodParamObjs);

        response.getWriter().append(returnObj.toString());
    }
}
