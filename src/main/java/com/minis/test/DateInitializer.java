package com.minis.test;

import com.minis.web.WebBindingInitializer;
import com.minis.web.WebDataBinder;

import java.util.Date;

public class DateInitializer implements WebBindingInitializer {
    @Override
    public void registerBinder(WebDataBinder binder) {
        binder.registerCustomEditor(Date.class, new CustomDateEditor("yyyy-MM-dd", false));
    }
}
