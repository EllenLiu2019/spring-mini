package com.minis.aop.aopalliance.intercept;

import com.minis.aop.aopalliance.aop.Advice;

// TODO: 拦截器
//  可以拦截程序运行期间发生的各种事件，这些事件由 “runtime joinpoint” 表示，例如：
//  Invocation 、 field access 、 exception throwing
public interface Interceptor extends Advice {
}
