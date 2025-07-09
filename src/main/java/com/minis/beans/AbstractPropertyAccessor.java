package com.minis.beans;

// TODO： 留待用
public abstract class AbstractPropertyAccessor extends PropertyEditorRegistrySupport {
    private PropertyValues propertyValues;

    public AbstractPropertyAccessor() {
        super();
    }

    public abstract void setPropertyValues(PropertyValues pvs);
}
