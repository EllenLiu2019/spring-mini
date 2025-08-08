package com.minis.app.converter;

import com.minis.beans.propertyeditors.CustomDateEditor;
import com.minis.web.bind.support.WebBindingInitializer;
import com.minis.web.bind.WebDataBinder;

import java.util.Date;

public class DateInitializer implements WebBindingInitializer {
    @Override
    public void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(Date.class, new CustomDateEditor("yyyy-MM-dd", false));
    }
}
