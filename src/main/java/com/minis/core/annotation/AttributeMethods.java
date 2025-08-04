package com.minis.core.annotation;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class AttributeMethods {

    static final AttributeMethods NONE = new AttributeMethods(null, new Method[0]);

    static final Map<Class<? extends Annotation>, AttributeMethods> cache = new ConcurrentHashMap<>();

    private final Class<? extends Annotation> annotationType;


    private final Method[] attributeMethods;

    private final boolean[] canThrowTypeNotPresentException;


    private AttributeMethods(Class<? extends Annotation> annotationType, Method[] attributeMethods) {
        this.annotationType = annotationType;
        this.attributeMethods = attributeMethods;
        this.canThrowTypeNotPresentException = new boolean[attributeMethods.length];
    }

    static AttributeMethods forAnnotationType(Class<? extends Annotation> annotationType) {
        if (annotationType == null) {
            return NONE;
        }
        return cache.computeIfAbsent(annotationType, AttributeMethods::compute);
    }

    private static AttributeMethods compute(Class<? extends Annotation> annotationType) {
        Method[] methods = annotationType.getDeclaredMethods();
        int size = methods.length;
        for (int i = 0; i < methods.length; i++) {
            if (!isAttributeMethod(methods[i])) {
                methods[i] = null;
                size--;
            }
        }
        if (size == 0) {
            return NONE;
        }
        Method[] attributeMethods = Arrays.copyOf(methods, size);
        return new AttributeMethods(annotationType, attributeMethods);
    }

    private static boolean isAttributeMethod(Method method) {
        return (method.getParameterCount() == 0 && method.getReturnType() != void.class);
    }

    public Method[] getAttributeMethods() {
        return this.attributeMethods;
    }
    public int size() {
        return this.attributeMethods.length;
    }

    public Method get(int index) {
        return this.attributeMethods[index];
    }

    public int indexOf(String name) {
        for (int i = 0; i < this.attributeMethods.length; i++) {
            if (this.attributeMethods[i].getName().equals(name)) {
                return i;
            }
        }
        return -1;
    }

    boolean canLoad(Annotation annotation) {
        for (int i = 0; i < size(); i++) {
            if (canThrowTypeNotPresentException(i)) {
                try {
                    AnnotationUtils.invokeAnnotationMethod(get(i), annotation);
                }
                catch (IllegalStateException ex) {
                    // Plain invocation failure to expose -> leave up to attribute retrieval
                    // (if any) where such invocation failure will be logged eventually.
                }
                catch (Throwable ex) {
                    // TypeNotPresentException etc. -> annotation type not actually loadable.
                    return false;
                }
            }
        }
        return true;
    }

    boolean canThrowTypeNotPresentException(int index) {
        return this.canThrowTypeNotPresentException[index];
    }
}
