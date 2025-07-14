package com.minis.aop.aopalliance.intercept;

import java.lang.reflect.AccessibleObject;

/**
 * This interface represents a generic runtime joinpoint (in the AOP terminology).
 *
 * <p>A runtime joinpoint is an <i>event</i> that occurs on a static
 * joinpoint (i.e. a location in a program). For instance, an
 * invocation is the runtime joinpoint on a method (static joinpoint).
 * The static part of a given joinpoint can be generically retrieved
 * using the {@link #getStaticPart()} method.
 *
 * <p>In the context of an interception framework, a runtime joinpoint
 * is then the reification of an access to an accessible object (a
 * method, a constructor, a field), i.e. the static part of the
 * joinpoint. It is passed to the interceptors that are installed on
 * the static joinpoint.
 *
 * @author Rod Johnson
 * @see Interceptor
 */

/**
 * TODO:
 *  Joinpoint 代表 AOP 【运行时连接点】：
 *      Runtime Joinpoint: 程序执行过程中的某个事件，比如方法调用；
 *      Static Joinpoint: 程序中固定的位置，比如某个方法定义；
 *  作用：提供对运行时连接点的抽象，使得拦截器可以插入到这些位置进行增强处理。
 *  Joinpoint 是 AOP 拦截机制的核心接口之一，它提供了对运行时连接点的统一抽象。
 *  通过 proceed() 可以控制拦截器链的流转；
 *  通过 getThis() 和 getStaticPart() 可以访问目标对象及静态结构，便于进行日志记录、性能监控、安全控制等横切逻辑的注入。
 */
public interface Joinpoint {

    // TODO: #TBD 这里只是简单的实现；忽略了拦截器链，直接使用反射机制，调用了目标方法；
    //  In spring framework, this is used to proceed to the next interceptor in the chain； 继续执行拦截器链中的下一个拦截器
    Object proceed() throws Throwable;

    // TODO: 返回当前连接点所属的目标对象，
    //  子类 MethodInvocation 中返回的是 该方法所在的对象实例
    Object getThis();

    /**
     * TODO: Return the static part of this joinpoint. 例如，方法、构造函数、属性
     * <p>The static part is an accessible object on which a chain of interceptors are installed.
     */
    AccessibleObject getStaticPart();
}
