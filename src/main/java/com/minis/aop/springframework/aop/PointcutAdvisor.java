package com.minis.aop.springframework.aop;

public interface PointcutAdvisor extends Advisor {
    /**
     * TODO：Get the Pointcut that drives this advisor. 用于决定切面应用的位置
     */
    Pointcut getPointcut();
}
