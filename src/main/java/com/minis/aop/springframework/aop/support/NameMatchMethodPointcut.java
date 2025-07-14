package com.minis.aop.springframework.aop.support;

import com.minis.aop.springframework.aop.MethodMatcher;
import com.minis.aop.springframework.aop.Pointcut;
import com.minis.utils.PatternMatchUtils;

import java.lang.reflect.Method;

public class NameMatchMethodPointcut implements MethodMatcher, Pointcut {
    private String mappedName = "";

    public void setMappedName(String mappedName) {
        this.mappedName = mappedName;
    }

    @Override
    public boolean matches(Method method, Class<?> targetClass) {
        return mappedName.equals(method.getName()) || isMatch(method.getName(), mappedName);
    }

    private boolean isMatch(String methodName, String mappedName) {
        return PatternMatchUtils.simpleMatch(mappedName, methodName);
    }

    @Override
    public MethodMatcher getMethodMatcher() {
        // TODO: 兼具 匹配规则 和 pointcut 的功能
        return this;
    }
}
