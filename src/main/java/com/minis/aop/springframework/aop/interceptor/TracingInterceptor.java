package com.minis.aop.springframework.aop.interceptor;

import com.minis.aop.aopalliance.intercept.MethodInvocation;
import com.minis.aop.aopalliance.intercept.MethodInterceptor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TracingInterceptor implements MethodInterceptor {

    @Override
    // TODO: Enhanced method invoking
    public Object invoke(MethodInvocation methodInvocation) throws Throwable {
        log.info("method=[{}] is called on methodInvocation=[{}] with args=[{}]", methodInvocation.getMethod(),
                methodInvocation.getThis(), methodInvocation.getArguments());
        Object result = methodInvocation.proceed();
        log.info("method=[{}] returns, result=[{}]", methodInvocation.getMethod(), result);
        return result;
    }
}
