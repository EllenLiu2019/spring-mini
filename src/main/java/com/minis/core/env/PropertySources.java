package com.minis.core.env;

public interface PropertySources extends Iterable<PropertySource<?>>{

    PropertySource<?> get(String name);
}
