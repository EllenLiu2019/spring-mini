package com.minis.beans.factory.config;

public class PropertyValue {
    private final String name;
    private final Object value;
    private final String type;
    public PropertyValue(String name, Object value, String type) {
        this.name = name;
        this.value = value;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public Object getValue() {
        return value;
    }
    public String getType() {
        return type;
    }
}
