package com.minis.beans;

import com.minis.beans.*;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

//TODO: 待完善，目前：
// BeanWrapper 同时也是源属性访问器 和 属性编辑器注册中心
// BeanWrapper 中封装目的属性信息
// 这个类的作用就是根据 不同的类型属性的特性，对属性值进行类型转换、赋值
public class BeanWrapperImpl extends PropertyEditorRegistrySupport {
    List<Object> wrappedObject;
    List<Class<?>> clz = new ArrayList<>();

    public BeanWrapperImpl(List<Object> object) {
        super();
        this.wrappedObject = object;
        initClz();
    }

    private void initClz() {
        for (Object obj : wrappedObject) {
            this.clz.add(obj.getClass());
        }
    }

    public void setPropertyValues(PropertyValues pvs) {
        if (isInstantiableNonPrimitiveClass(clz.get(0))) {
            doSetPropertyValues(pvs);
        } else {
            doSetPrimitiveValues(pvs);
        }
    }

    private void doSetPrimitiveValues(PropertyValues pvs) {
        for (int i = 0; i < clz.size(); i++) {
            PropertyValue pv = pvs.getPropertyValues()[i];
            Class<?> clazz = clz.get(i);
            PropertyEditor pe = this.getCustomEditor(clazz);
            if (pe == null) {
                pe = this.getDefaultEditor(clazz);
            }
            if (pe != null) {
                pe.setAsText((String) pv.getValue());
                this.wrappedObject.set(i, pe.getValue());
            } else {
                this.wrappedObject.set(i, pv.getValue());
            }
        }
    }

    private void doSetPropertyValues(PropertyValues pvs) {
        for (PropertyValue pv : pvs.getPropertyValues()) {
            Class<?> clazz = clz.get(0);
            BeanPropertyHandler propertyHandler = new BeanPropertyHandler(clazz, pv.getName());
            PropertyEditor pe = this.getCustomEditor(propertyHandler.getPropertyClz());

            if (pe == null) {
                pe = this.getDefaultEditor(propertyHandler.getPropertyClz());
            }

            if (pe != null) {
                pe.setAsText((String) pv.getValue());
                propertyHandler.setValue(pe.getValue(), 0);
            } else {
                propertyHandler.setValue(pv.getValue(), 0);
            }
        }
    }

    public boolean isInstantiableNonPrimitiveClass(Class<?> clazz) {
        // 排除 null
        if (clazz == null) return false;

        // 排除接口、数组、枚举、基本类型、void、包装类、String、Date 等简单类型
        return !clazz.isInterface()
                && !clazz.isArray()
                && !clazz.isEnum()
                && !clazz.isPrimitive()
                && !isSimpleType(clazz);
    }

    private boolean isSimpleType(Class<?> clazz) {
        return clazz.equals(String.class)
                || clazz.equals(Integer.class)
                || clazz.equals(Long.class)
                || clazz.equals(Double.class)
                || clazz.equals(Float.class)
                || clazz.equals(Boolean.class)
                || clazz.equals(Byte.class)
                || clazz.equals(Short.class)
                || clazz.equals(Character.class)
                || clazz.equals(java.util.Date.class)
                || clazz.equals(java.time.LocalDate.class)
                || clazz.equals(java.time.LocalDateTime.class)
                || clazz.equals(java.sql.Date.class);
    }

    class BeanPropertyHandler {
        Method writeMethod;
        Method readMethod;
        Class<?> propertyClz;

        public Class<?> getPropertyClz() {
            return propertyClz;
        }

        public BeanPropertyHandler(Class<?> clz, String propertyName) {
            try {
                Field field = clz.getDeclaredField(propertyName);
                propertyClz = field.getType();
                this.writeMethod = clz.getDeclaredMethod("set" + propertyName.substring(0, 1).toUpperCase() + propertyName.substring(1), propertyClz);
                this.readMethod = clz.getDeclaredMethod("get" + propertyName.substring(0, 1).toUpperCase() + propertyName.substring(1));
            } catch (NoSuchMethodException | NoSuchFieldException | SecurityException ignored) {
                propertyClz = clz;
            }
        }

        public Object getValue() {
            Object result;
            writeMethod.setAccessible(true);
            try {
                result = readMethod.invoke(wrappedObject);
            } catch (SecurityException | IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }
            return result;

        }

        public void setValue(Object value, int idx) {
            writeMethod.setAccessible(true);
            try {
                writeMethod.invoke(wrappedObject.get(idx), value);
            } catch (SecurityException | IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        }

    }

}
