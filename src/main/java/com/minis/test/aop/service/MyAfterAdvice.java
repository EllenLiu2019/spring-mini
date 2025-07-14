package com.minis.test.aop.service;

import com.minis.aop.springframework.aop.AfterReturningAdvice;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;

@Slf4j
public class MyAfterAdvice implements AfterReturningAdvice {
    @Override
    public void afterReturning(Object returnValue, Method method, Object[] args, Object target) throws Throwable {
        log.info("my interceptor after method call");
    }
}
