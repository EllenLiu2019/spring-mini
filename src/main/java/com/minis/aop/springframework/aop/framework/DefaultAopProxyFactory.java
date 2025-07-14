package com.minis.aop.springframework.aop.framework;

import com.minis.aop.springframework.aop.PointcutAdvisor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DefaultAopProxyFactory implements AopProxyFactory {
    @Override
    public AopProxy createAopProxy(Object target, PointcutAdvisor advisor) {
        log.debug("creating JdkDynamicAopProxy using target={}", target);
        return new JdkDynamicAopProxy(target, advisor);
    }
}
