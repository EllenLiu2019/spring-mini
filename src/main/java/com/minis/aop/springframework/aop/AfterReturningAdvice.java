package com.minis.aop.springframework.aop;


import java.lang.reflect.Method;
/**
 * After returning advice is invoked only on normal method return, not if an
 * exception is thrown. Such advice can see the return value, but cannot change it.
 *
 * @author Rod Johnson
 * @see MethodBeforeAdvice
 */
public interface AfterReturningAdvice extends AfterAdvice {
    void afterReturning(Object returnValue, Method method, Object[] args, Object target) throws Throwable;
}
