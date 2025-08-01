package com.minis.beans;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PropertyValues {
    private final List<PropertyValue> propertyValueList;

    public PropertyValues() {
        this.propertyValueList = new ArrayList<>(0);
    }

    public PropertyValues(Map<String, Object> map) {
        this.propertyValueList = new ArrayList<>(10);
        for (Map.Entry<String, Object> e : map.entrySet()) {
            PropertyValue pv = new PropertyValue(e.getKey(), e.getValue());
            this.propertyValueList.add(pv);
        }
    }

    public List<PropertyValue> getPropertyValueList() {
        return this.propertyValueList;
    }

    public int size() {
        return this.propertyValueList.size();
    }

    public void addPropertyValue(PropertyValue pv) {
        this.propertyValueList.add(pv);
    }

    public PropertyValue[] getPropertyValues() {
        return this.propertyValueList.toArray(new PropertyValue[0]);
    }

    public PropertyValue getPropertyValue(String propertyName) {
        for (PropertyValue pv : this.propertyValueList) {
            if (pv.getName().equals(propertyName)) {
                return pv;
            }
        }
        return null;
    }

    public Object get(String propertyName) {
        PropertyValue pv = getPropertyValue(propertyName);
        return pv != null ? pv.getValue() : null;
    }

    public boolean isEmpty() {
        return this.propertyValueList.isEmpty();
    }


    public void add(String propertyName, Object propertyValue) {
        this.propertyValueList.add(new PropertyValue(propertyName, propertyValue));
    }

}
