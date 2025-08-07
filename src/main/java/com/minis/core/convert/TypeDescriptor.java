package com.minis.core.convert;

import com.minis.core.ResolvableType;
import com.minis.utils.ClassUtils;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class TypeDescriptor implements Serializable {

    private static final Map<Class<?>, TypeDescriptor> commonTypesCache = new HashMap<>(32);

    private static final Class<?>[] CACHED_COMMON_TYPES = {
            boolean.class, Boolean.class, byte.class, Byte.class, char.class, Character.class,
            double.class, Double.class, float.class, Float.class, int.class, Integer.class,
            long.class, Long.class, short.class, Short.class, String.class, Object.class};

    static {
        for (Class<?> preCachedClass : CACHED_COMMON_TYPES) {
            commonTypesCache.put(preCachedClass, valueOf(preCachedClass));
        }
    }

    private final Class<?> type;

    private final ResolvableType resolvableType;

    public TypeDescriptor(ResolvableType resolvableType, Class<?> type) {
        this.resolvableType = resolvableType;
        this.type = (type != null ? type : resolvableType.toClass());
    }

    public static TypeDescriptor forObject(Object source) {
        return (source != null ? valueOf(source.getClass()) : null);
    }

    public static TypeDescriptor valueOf(Class<?> type) {
        if (type == null) {
            type = Object.class;
        }
        TypeDescriptor desc = commonTypesCache.get(type);
        return (desc != null ? desc : new TypeDescriptor(ResolvableType.forClass(type), null));
    }

    public Class<?> getType() {
        return this.type;
    }

    public boolean isPrimitive() {
        return getType().isPrimitive();
    }

    public Class<?> getObjectType() {
        return ClassUtils.resolvePrimitiveIfNecessary(getType());
    }

    public ResolvableType getResolvableType() {
        return this.resolvableType;
    }

    public boolean isAssignableTo(TypeDescriptor typeDescriptor) {
        boolean typesAssignable = typeDescriptor.getObjectType().isAssignableFrom(getObjectType());
        return typesAssignable;
    }

    public boolean isArray() {
        return getType().isArray();
    }


}
