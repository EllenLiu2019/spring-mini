package com.minis.aop.springframework.aop.framework;

import com.minis.aop.aopalliance.intercept.MethodInvocation;
import com.minis.aop.springframework.aop.support.AopUtils;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Method;
/**
 * Spring's implementation of the AOP Alliance
 * {@link com.minis.aop.aopalliance.intercept.MethodInvocation} interface,
 * implementing the extended
 * { com.minis.aop.springframework.aop.ProxyMethodInvocation} interface.
 *
 * <p>Invokes the target object using reflection. Subclasses can override the
 * {@link #invokeJoinpoint()} method to change this behavior, so this is also
 * a useful base class for more specialized MethodInvocation implementations.
 *
 *
 * <p><b>NOTE:</b> This class is considered internal and should not be
 * directly accessed. The sole reason for it being public is compatibility
 * with existing framework integrations (e.g. Pitchfork). For any other
 * purposes, use the { ProxyMethodInvocation} interface instead.
 *
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @author Adrian Colyer
 * @see #proceed
 */
public class ReflectiveMethodInvocation implements MethodInvocation {
    protected final Object proxy;
    protected final Object target;
    protected final Method method;
    protected final Object[] arguments;

    public ReflectiveMethodInvocation(Object proxy, Object target, Method method, Object[] arguments) {
        this.proxy = proxy;
        this.target = target;
        this.method = method;
        this.arguments = arguments;
    }

    @Override
    public Method getMethod() {
        return this.method;
    }

    @Override
    public Object[] getArguments() {
        return this.arguments;
    }

    @Override
    public Object getThis() {
        return this.target;
    }

    @Override
    public final AccessibleObject getStaticPart() {
        return this.method;
    }
    @Override
    // TODO: the real target method invoking
    public Object proceed() throws Throwable {
        return this.method.invoke(target, arguments);
    }


    /**
     * Invoke the joinpoint using reflection.
     * Subclasses can override this to use custom invocation.
     * @return the return value of the joinpoint
     * @throws Throwable if invoking the joinpoint resulted in an exception
     */
    protected Object invokeJoinpoint() throws Throwable {
        return AopUtils.invokeJoinpointUsingReflection(this.target, this.method, this.arguments);
    }
}
