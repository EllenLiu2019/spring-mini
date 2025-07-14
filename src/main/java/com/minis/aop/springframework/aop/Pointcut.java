package com.minis.aop.springframework.aop;
/**
 * Core Spring pointcut abstraction.
 *
 * <p>A pointcut is composed of a {@@link ClassFilter} and a {@link MethodMatcher}.
 * Both these basic terms and a Pointcut itself can be combined to build up combinations
 *
 * @author Rod Johnson
 * @see MethodMatcher
 */
public interface Pointcut {
    MethodMatcher getMethodMatcher(); // TODO: 获得匹配规则
}
