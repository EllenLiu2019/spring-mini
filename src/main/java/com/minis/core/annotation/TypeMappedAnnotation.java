package com.minis.core.annotation;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.function.Function;

public class TypeMappedAnnotation<A extends Annotation> implements MergedAnnotation<A> {

    private final AnnotationTypeMapping mapping;

    private final Object source;

    private final Object rootAttributes;


    TypeMappedAnnotation(AnnotationTypeMapping mapping, Object source, Object rootAttributes) {
        this.mapping = mapping;
        this.source = source;
        this.rootAttributes = rootAttributes;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Class<A> getType() {
        return (Class<A>) this.mapping.getAnnotationType();
    }

    @Override
    public boolean isPresent() {
        return true;
    }

    @Override
    public Object getSource() {
        return this.source;
    }

    @Override
    public MergedAnnotation<?> getMetaSource() {
        AnnotationTypeMapping metaSourceMapping = this.mapping.getSource();
        if (metaSourceMapping == null) {
            return null;
        }
        return new TypeMappedAnnotation<>(metaSourceMapping, this.source, this.rootAttributes);
    }

    @Override
    public AnnotationAttributes asAnnotationAttributes() {
        return asMap(mergedAnnotation -> new AnnotationAttributes(mergedAnnotation.getType()));
    }

    /**
     * 获取注解所有属性值并封装成Map
     * @param factory 初始化封装对象的工厂方法
     * @return 注解信息，包含： name & type & 属性值的Map
     * @param <T> AnnotationAttributes
     */
    @Override
    public <T extends Map<String, Object>> T asMap(Function<MergedAnnotation<?>, T> factory) {
        T map = factory.apply(this);
        AttributeMethods attributes = this.mapping.getAttributes();
        for (int i = 0; i < attributes.size(); i++) {
            Method attribute = attributes.get(i);
            Object value = getValue(attribute);
            if (value != null) {
                map.put(attribute.getName(),value);
            }
        }
        return map;
    }

    @SuppressWarnings("unchecked")
    private <T> T getValue(Method attribute) {
        Object value = AnnotationUtils.invokeAnnotationMethod(attribute, this.mapping.getAnnotation());
        if (value == null) {
            value = attribute.getDefaultValue();
        }
        return (T) value;
    }


    @Override
    public boolean isDirectlyPresent() {
        return isPresent() && this.getDistance() == 0;
    }

    @Override
    public boolean isMetaPresent() {
        return isPresent() && getDistance() > 0;
    }

    @Override
    public int getDistance() {
        return this.mapping.getDistance();
    }

}
