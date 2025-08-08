package com.minis.web.servlet;

import com.minis.core.MethodParameter;
import com.minis.utils.WebUtils;
import com.minis.web.bind.WebDataBinder;
import com.minis.web.bind.support.WebDataBinderFactory;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class HandlerMethodArgumentResolverComposite {

    Map<Integer, Pair<String, Object>> parameters = new ConcurrentHashMap<>();

    public Object resolveArgument(MethodParameter parameter, HttpServletRequest request, WebDataBinderFactory binderFactory) {
        Object arg = resolveName(parameter, request);
        if (arg != null) {
            arg = convertIfNecessary(parameter, request, binderFactory, arg);
        }
        return arg;
    }

    protected Object resolveName(MethodParameter parameter, HttpServletRequest request) {
        Object obj = null;
        Map<Integer, Pair<String, Object>> parameters = this.parameters;
        if (parameters.isEmpty()) {
            parameters = WebUtils.getParametersStartingWith(request, "");
        }
        this.parameters = parameters;
        Pair<String, Object> param = parameters.get(parameter.getParameterIndex());
        if (param != null) {
            parameter.setParameterName(param.getLeft());
            obj = param.getRight();
        }
        return obj;
    }

    private Object convertIfNecessary(MethodParameter parameter, HttpServletRequest request,
                                      WebDataBinderFactory binderFactory, Object arg) {
        WebDataBinder binder = binderFactory.createBinder(request, null, parameter.getParameterName());
        Class<?> parameterType = parameter.getParameterType();
        arg = binder.convertIfNecessary(arg, parameterType, parameter);
        return arg;
    }
}
