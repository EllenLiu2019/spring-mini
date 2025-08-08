package com.minis.web.servlet;


import com.minis.core.MethodParameter;
import com.minis.utils.ObjectUtils;
import com.minis.web.bind.annotation.ResponseBody;
import com.minis.web.bind.support.WebBindingInitializer;
import com.minis.web.bind.support.WebDataBinderFactory;
import com.minis.web.converter.HttpMessageConverter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.lang.reflect.Method;


public class InvocableHandlerMethod extends HandlerMethod {

    private static final Object[] EMPTY_ARGS = new Object[0];

    private HttpMessageConverter defaultHttpMessageConverter;

    private final WebDataBinderFactory dataBinderFactory;

    private final HandlerMethodArgumentResolverComposite resolvers;

    public InvocableHandlerMethod(HandlerMethod handlerMethod, WebBindingInitializer webBindingInitializer, HttpMessageConverter converter) {
        super(handlerMethod);
        this.defaultHttpMessageConverter = converter;
        this.dataBinderFactory = new WebDataBinderFactory(webBindingInitializer);
        this.resolvers = new HandlerMethodArgumentResolverComposite();
    }

    public void invokeAndHandle(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Object[] methodParamObjs = getMethodArgumentValues(request, response);

        Method invocableMethod = getMethod();
        Object returnObj = invocableMethod.invoke(getBean(), methodParamObjs);
        Class<?> returnType = invocableMethod.getReturnType();

        if (invocableMethod.isAnnotationPresent(ResponseBody.class)) {
            this.defaultHttpMessageConverter.write(returnObj, response);
        } else if (returnType == void.class) {
            // ignore
        } else if (returnType == String.class) {
            response.getWriter().write((String) returnObj);
        } else {
            this.defaultHttpMessageConverter.write(returnObj, response);
        }
    }

    private Object[] getMethodArgumentValues(HttpServletRequest request, HttpServletResponse response) {
        MethodParameter[] parameters = getMethodParameters();
        if (ObjectUtils.isEmpty(parameters)) {
            return EMPTY_ARGS;
        }

        Object[] args = new Object[parameters.length];
        for (int i = 0; i < parameters.length; i++) {
            MethodParameter parameter = parameters[i];
            if (parameter.getParameterType() == HttpServletRequest.class || parameter.getParameterType() == HttpServletResponse.class) {
                args[i] = parameter.getParameterType() == HttpServletRequest.class ? request : response;
            } else {
                args[i] = resolvers.resolveArgument(parameter, request, this.dataBinderFactory);
            }
        }
        return args;
    }
}
