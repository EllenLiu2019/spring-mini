package com.minis.web.bind.support;

import com.minis.core.convert.support.DefaultConversionService;
import com.minis.web.bind.WebDataBinder;
import jakarta.servlet.http.HttpServletRequest;


public class WebDataBinderFactory {

    private WebBindingInitializer initializer;

    public WebDataBinderFactory(WebBindingInitializer initializer) {
        this.initializer = initializer;
    }
    public WebDataBinder createBinder(HttpServletRequest request, Object target, String objectName) {
        return createBinderInternal(target, objectName);
    }

    private WebDataBinder createBinderInternal(Object target, String objectName) {
        WebDataBinder dataBinder = new WebDataBinder(target, objectName);
        dataBinder.setConversionService(new DefaultConversionService());
        if (this.initializer != null) {
            this.initializer.initBinder(dataBinder);
        }
        return dataBinder;
    }

}
