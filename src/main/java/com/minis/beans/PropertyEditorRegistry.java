package com.minis.beans;

import java.beans.PropertyEditor;

public interface PropertyEditorRegistry {

    void registerCustomEditor(Class<?> requiredType, PropertyEditor propertyEditor);

    PropertyEditor getCustomEditor(Class<?> requiredType);
}
