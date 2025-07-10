package com.minis.web.bind.support;

import com.minis.web.bind.WebDataBinder;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

public class WebDataBinderFactory {
    public WebDataBinder createBinder(HttpServletRequest request, List<Object> targets, List<String> objectNames) {
        return new WebDataBinder(targets, objectNames);
    }

}
