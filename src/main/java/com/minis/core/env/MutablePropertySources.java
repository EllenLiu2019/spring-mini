package com.minis.core.env;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class MutablePropertySources implements PropertySources {

    private final List<PropertySource<?>> propertySourceList = new CopyOnWriteArrayList<>();

    public MutablePropertySources() {
    }

    /**
     * Create a new {@code MutablePropertySources} from the given propertySources
     * object, preserving the original order of contained {@code PropertySource} objects.
     */
    public MutablePropertySources(PropertySources propertySources) {
        this();
        for (PropertySource<?> propertySource : propertySources) {
            addLast(propertySource);
        }
    }

    @Override
    public PropertySource<?> get(String name) {
        for (PropertySource<?> propertySource : this.propertySourceList) {
            if (propertySource.getName().equals(name)) {
                return propertySource;
            }
        }
        return null;
    }

    public void addFirst(PropertySource<?> propertySource) {
        synchronized (this.propertySourceList) {
            removeIfPresent(propertySource);
            this.propertySourceList.add(0, propertySource);
        }
    }

    public void addLast(PropertySource<?> propertySource) {
        synchronized (this.propertySourceList) {
            removeIfPresent(propertySource);
            this.propertySourceList.add(propertySource);
        }
    }

    protected void removeIfPresent(PropertySource<?> propertySource) {
        this.propertySourceList.remove(propertySource);
    }


    public int size() {
        return this.propertySourceList.size();
    }

    @Override
    public String toString() {
        return this.propertySourceList.toString();
    }

    @Override
    public Iterator<PropertySource<?>> iterator() {
        return this.propertySourceList.iterator();
    }
}
