package com.minis.test.aop.service;

import com.minis.aop.MethodBeforeAdvice;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;

@Slf4j
public class MyBeforeAdvice implements MethodBeforeAdvice {
    @Override
    public void before(Method method, Object[] args, Object target) throws Throwable {
        log.info("my interceptor before method call");
    }
}
