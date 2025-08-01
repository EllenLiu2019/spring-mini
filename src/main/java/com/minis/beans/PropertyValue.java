package com.minis.beans;

public class PropertyValue {
    private final String name;
    private final Object value;
    private final String type;
    private final boolean isRef;

    public PropertyValue(String name, Object value, String type, boolean isRef) {
        this.name = name;
        this.value = value;
        this.type = type;
        this.isRef = isRef;
    }

    public PropertyValue(String name, Object value) {
        this(name, value,"", false);
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

    public boolean getIsRef() {
        return this.isRef;
    }
}
