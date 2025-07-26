package com.minis.tx.interceptor;

import com.minis.aop.aopalliance.intercept.MethodInterceptor;
import com.minis.aop.aopalliance.intercept.MethodInvocation;
import com.minis.beans.factory.annotation.Autowired;
import com.minis.tx.transaction.TransactionManager;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;


@Slf4j
public class TransactionInterceptor implements MethodInterceptor {

    @Autowired
    private TransactionManager txManager;

    @Override
    // TODO: Enhanced method invoking
    public Object invoke(MethodInvocation methodInvocation) throws Throwable {
        this.txManager.doBegin();
        log.info("set auto commit to false");
        Object result = methodInvocation.proceed();
        this.txManager.doCommit();
        log.info("DB method=[{}] success, transaction commit", methodInvocation.getMethod());
        return result;
    }
}
