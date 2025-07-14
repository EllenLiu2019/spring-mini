package com.minis.aop.springframework.aop;

import java.lang.reflect.Method;
/**
 * Advice invoked before a method is invoked. Such advices cannot
 * prevent the method call proceeding, unless they throw a Throwable.
 *
 * @author Rod Johnson
 * @see AfterReturningAdvice
 */
public interface MethodBeforeAdvice extends BeforeAdvice {
    void before(Method method, Object[] args, Object target) throws Throwable;
}
