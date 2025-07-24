package com.minis.core.annotation;

import com.minis.utils.ReflectionUtils;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public abstract class AnnotationUtils {

    static Object invokeAnnotationMethod(Method method, Object annotation) {
        if (annotation == null) {
            return null;
        }
        if (Proxy.isProxyClass(annotation.getClass())) {
            try {
                InvocationHandler handler = Proxy.getInvocationHandler(annotation);
                return handler.invoke(annotation, method, null);
            }
            catch (Throwable ex) {
                // Ignore and fall back to reflection below
            }
        }
        return ReflectionUtils.invokeMethod(method, annotation);
    }
}
