package com.minis.core;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.lang.reflect.*;
import java.util.concurrent.ConcurrentHashMap;

public class ResolvableType implements Serializable {

    public static final ResolvableType NONE = new ResolvableType(EmptyType.INSTANCE,  null);

    private static final ResolvableType[] EMPTY_TYPES_ARRAY = new ResolvableType[0];

    private static final ConcurrentHashMap<ResolvableType, ResolvableType> cache = new ConcurrentHashMap<>(256);
    private final Type type;
    private FieldTypeProvider typeProvider;

    private Class<?> resolved;

    private volatile ResolvableType superType;

    private volatile ResolvableType[] interfaces;

    private volatile ResolvableType[] generics;

    private VariableResolver variableResolver;

    private ResolvableType(Class<?> clazz) {
        this.resolved = (clazz != null ? clazz : Object.class);
        this.type = this.resolved;
    }

    private ResolvableType(Type type, FieldTypeProvider typeProvider, VariableResolver variableResolver) {
        this.type = type;
        this.typeProvider = typeProvider;
        this.variableResolver = variableResolver;
        this.resolved = resolveClass();;
    }

    private Class<?> resolveClass() {
        if (this.type == EmptyType.INSTANCE) {
            return null;
        }
        if (this.type instanceof Class<?> clazz) {
            return clazz;
        }
        return resolveType().resolve();
    }

    public static ResolvableType forClass(Class<?> clazz) {
        return new ResolvableType(clazz);
    }

    public Class<?> toClass() {
        return resolve(Object.class);
    }

    public Class<?> resolve(Class<?> fallback) {
        return (this.resolved != null ? this.resolved : fallback);
    }

    public Type getType() {
        return this.type;
    }

    public static ResolvableType forField(Field field, Class<?> implementationClass) {
        ResolvableType owner = forType(implementationClass).as(field.getDeclaringClass());
        return forType(null, new FieldTypeProvider(field), owner.asVariableResolver()).getNested();
    }

    static ResolvableType forType(
            Type type, FieldTypeProvider typeProvider, VariableResolver variableResolver) {

        if (type == null && typeProvider != null) {
            type = typeProvider.getType();
        }
        if (type == null) {
            return NONE;
        }

        // For simple Class references, build the wrapper right away -
        // no expensive resolution necessary, so not worth caching...
        if (type instanceof Class) {
            return new ResolvableType(type, typeProvider, variableResolver);
        }

        return null;
    }
    public static ResolvableType forType(Type type) {
        return new ResolvableType(type, null, null);
    }

    public ResolvableType getNested() {
        return this;
    }

    public ResolvableType as(Class<?> type) {
        if (this == NONE) {
            return NONE;
        }
        Class<?> resolved = resolve();
        if (resolved == null || resolved == type) {
            return this;
        }
        for (ResolvableType interfaceType : getInterfaces()) {
            ResolvableType interfaceAsType = interfaceType.as(type);
            if (interfaceAsType != NONE) {
                return interfaceAsType;
            }
        }
        return getSuperType().as(type);
    }

    public Class<?> resolve() {
        return this.resolved;
    }

    public ResolvableType[] getInterfaces() {
        Class<?> resolved = resolve();
        if (resolved == null) {
            return EMPTY_TYPES_ARRAY;
        }
        ResolvableType[] interfaces = this.interfaces;
        if (interfaces == null) {
            Type[] genericIfcs = resolved.getGenericInterfaces();
            if (genericIfcs.length > 0) {
                interfaces = new ResolvableType[genericIfcs.length];
                for (int i = 0; i < genericIfcs.length; i++) {
                    interfaces[i] = forType(genericIfcs[i], this);
                }
            } else {
                interfaces = EMPTY_TYPES_ARRAY;
            }
            this.interfaces = interfaces;
        }
        return interfaces;
    }

    public ResolvableType getSuperType() {
        Class<?> resolved = resolve();
        if (resolved == null) {
            return NONE;
        }
        try {
            Type superclass = resolved.getGenericSuperclass();
            if (superclass == null) {
                return NONE;
            }
            ResolvableType superType = this.superType;
            if (superType == null) {
                superType = forType(superclass, this);
                this.superType = superType;
            }
            return superType;
        } catch (TypeNotPresentException ex) {
            // Ignore non-present types in generic signature
            return NONE;
        }
    }

    public static ResolvableType forType(Type type, ResolvableType owner) {
        VariableResolver variableResolver = null;
        if (owner != null) {
            variableResolver = owner.asVariableResolver();
        }
        return forType(type, variableResolver);
    }

    static ResolvableType forType(Type type, VariableResolver variableResolver) {
        return new ResolvableType(type, variableResolver);
    }

    private ResolvableType(Type type, VariableResolver variableResolver) {
        this.type = type;
        this.variableResolver = variableResolver;
        this.resolved = resolveClass();
    }

    VariableResolver asVariableResolver() {
        if (this == NONE) {
            return null;
        }
        return new DefaultVariableResolver(this);
    }

    public ResolvableType[] getGenerics() {
        if (this == NONE) {
            return EMPTY_TYPES_ARRAY;
        }
        ResolvableType[] generics = this.generics;
        if (generics == null) {
            if (this.type instanceof Class<?> clazz) {
                Type[] typeParams = clazz.getTypeParameters();
                if (typeParams.length > 0) {
                    generics = new ResolvableType[typeParams.length];
                    for (int i = 0; i < generics.length; i++) {
                        generics[i] = ResolvableType.forType(typeParams[i], this);
                    }
                }
                else {
                    generics = EMPTY_TYPES_ARRAY;
                }
            }
            else if (this.type instanceof ParameterizedType parameterizedType) {
                Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
                if (actualTypeArguments.length > 0) {
                    generics = new ResolvableType[actualTypeArguments.length];
                    for (int i = 0; i < actualTypeArguments.length; i++) {
                        generics[i] = forType(actualTypeArguments[i], this.variableResolver);
                    }
                }
                else {
                    generics = EMPTY_TYPES_ARRAY;
                }
            }
            else {
                generics = resolveType().getGenerics();
            }
            this.generics = generics;
        }
        return generics;
    }

    ResolvableType resolveType() {
        if (this.type instanceof ParameterizedType parameterizedType) {
            return forType(parameterizedType.getRawType(), this.variableResolver);
        }
        if (this.type instanceof WildcardType wildcardType) {
            Type resolved = resolveBounds(wildcardType.getUpperBounds());
            if (resolved == null) {
                resolved = resolveBounds(wildcardType.getLowerBounds());
            }
            return forType(resolved, this.variableResolver);
        }
        if (this.type instanceof TypeVariable<?> variable) {
            // Try default variable resolution
            if (this.variableResolver != null) {
                ResolvableType resolved = this.variableResolver.resolveVariable(variable);
                if (resolved != null) {
                    return resolved;
                }
            }
            // Fallback to bounds
            return forType(resolveBounds(variable.getBounds()), this.variableResolver);
        }
        return NONE;
    }

    private static Type resolveBounds(Type[] bounds) {
        if (bounds.length == 0 || bounds[0] == Object.class) {
            return null;
        }
        return bounds[0];
    }


    interface VariableResolver extends Serializable {

        /**
         * Return the source of the resolver (used for hashCode and equals).
         */
        Object getSource();

        /**
         * Resolve the specified variable.
         *
         * @param variable the variable to resolve
         * @return the resolved variable, or {@code null} if not found
         */
        ResolvableType resolveVariable(TypeVariable<?> variable);
    }

    private static class DefaultVariableResolver implements VariableResolver {

        private final ResolvableType source;

        DefaultVariableResolver(ResolvableType resolvableType) {
            this.source = resolvableType;
        }

        @Override
        public ResolvableType resolveVariable(TypeVariable<?> variable) {
            return null;
        }

        @Override
        public Object getSource() {
            return this.source;
        }
    }

    static class EmptyType implements Type, Serializable {

        static final Type INSTANCE = new EmptyType();

        Object readResolve() {
            return INSTANCE;
        }
    }


    public boolean isArray() {
        if (this == NONE) {
            return false;
        }
        return ((this.type instanceof Class<?> clazz && clazz.isArray()) ||
                this.type instanceof GenericArrayType || resolveType().isArray());
    }

    static class FieldTypeProvider {

        private final String fieldName;

        private final Class<?> declaringClass;

        private transient Field field;

        public FieldTypeProvider(Field field) {
            this.fieldName = field.getName();
            this.declaringClass = field.getDeclaringClass();
            this.field = field;
        }

        public Type getType() {
            return this.field.getGenericType();
        }

        public Object getSource() {
            return this.field;
        }

        private void readObject(ObjectInputStream inputStream) throws IOException, ClassNotFoundException {
            inputStream.defaultReadObject();
            try {
                this.field = this.declaringClass.getDeclaredField(this.fieldName);
            } catch (Throwable ex) {
                throw new IllegalStateException("Could not find original class structure", ex);
            }
        }
    }


}
