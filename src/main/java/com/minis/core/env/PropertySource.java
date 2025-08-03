package com.minis.core.env;

import com.minis.utils.ObjectUtils;

import java.util.Objects;

public abstract class PropertySource<T> {

    protected final String name;

    protected final T source;

    public PropertySource(String name, T source) {
        this.name = name;
        this.source = source;
    }

    public String getName() {
        return this.name;
    }

    public T getSource() {
        return this.source;
    }

    public abstract Object getProperty(String name);


    @Override
    public boolean equals(Object other) {
        return (this == other || (other instanceof PropertySource<?> that &&
                ObjectUtils.nullSafeEquals(getName(), that.getName())));
    }


    @Override
    public int hashCode() {
        return Objects.hashCode(getName());
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "@" + System.identityHashCode(this) +
                " {name='" + getName() + "', properties=" + getSource() + "}";

    }

    public abstract String[] getPropertyNames();

}
