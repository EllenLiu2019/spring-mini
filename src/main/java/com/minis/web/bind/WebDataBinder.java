package com.minis.web.bind;

import com.minis.beans.*;
import com.minis.core.MethodParameter;
import com.minis.core.convert.ConversionService;


public class WebDataBinder extends PropertyEditorRegistrySupport {

    private ExtendedTypeConverter typeConverter;

    private ConversionService conversionService;

    private Object target;
    private String objectName;

    public WebDataBinder(Object target, String objectName) {
        this.target = target;
        this.objectName = objectName;
    }

    public <T> T convertIfNecessary(Object value, Class<T> requiredType, MethodParameter methodParam) {
        return getTypeConverter().convertIfNecessary(value, requiredType, methodParam);
    }

    public void setConversionService(ConversionService conversionService) {
        this.conversionService = conversionService;
    }

    private TypeConverter getTypeConverter() {
        if (this.typeConverter == null) {
            this.typeConverter = new ExtendedTypeConverter();
            if (this.conversionService != null) {
                this.typeConverter.setConversionService(this.conversionService);
            }
            copyCustomEditorsTo(this.typeConverter);
        }
        return this.typeConverter;
    }

    private static class ExtendedTypeConverter extends SimpleTypeConverter implements PropertyEditorRegistrar {

        @Override
        public void registerCustomEditors(PropertyEditorRegistry registry) {
            copyCustomEditorsTo(registry);
        }
    }
}
