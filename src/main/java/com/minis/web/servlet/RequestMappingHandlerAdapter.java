package com.minis.web.servlet;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.lang.reflect.Method;

public class RequestMappingHandlerAdapter implements HandlerAdapter {
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        handleInternal(request, response, (HandlerMethod) handler);
    }

    private void handleInternal(HttpServletRequest request, HttpServletResponse response, HandlerMethod handler) {
        Method method = handler.getMethod();
        Object bean = handler.getBean();
        Object objResult;
        try {
            objResult = method.invoke(bean);
        } catch (Exception e) {
            System.out.println("request resource failed: " + e.getMessage());
            throw new RuntimeException(e);
        }
        try {
            response.getWriter().append(objResult.toString());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
