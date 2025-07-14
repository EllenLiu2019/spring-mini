package com.minis.aop.aopalliance.intercept;

/**
 * This interface represents an invocation in the program.
 *
 * <p>An invocation is a joinpoint and can be intercepted by an
 * interceptor.
 *
 * @author Rod Johnson
 */

/**
 * TODO: 表示程序中的一个连接点，即：调用；（方法调用、构造函数调用等）
 */
public interface Invocation extends Joinpoint {

    // TODO: 获取当前调用的参数数组。
    //  允许修改数组中的元素值，从而在拦截器中动态更改传入的实际参数。
    Object[] getArguments();
}
