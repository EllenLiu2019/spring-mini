package com.minis.aop.springframework.aop;

import com.minis.aop.aopalliance.intercept.MethodInterceptor;

/**
 * TODO: 用于封装 AOP 通知（在连接点处的增强行为），并含有过滤器（应用条件）用于决定哪些方法被增强；
 *  Spring AOP 包含 类型：
 *  *around advice*:  delivered via method [interception]
 *  *before & after*:  which need not be implemented using interception
 */
public interface Advisor {
    MethodInterceptor getMethodInterceptor();
    void setMethodInterceptor(MethodInterceptor methodInterceptor);
}
