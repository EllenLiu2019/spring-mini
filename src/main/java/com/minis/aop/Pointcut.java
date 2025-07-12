package com.minis.aop;

public interface Pointcut {
    MethodMatcher getMethodMatcher(); // TODO: 获得匹配规则
}
