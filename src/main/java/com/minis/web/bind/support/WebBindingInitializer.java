package com.minis.web.bind.support;

import com.minis.web.bind.WebDataBinder;

public interface WebBindingInitializer {
    void registerBinder(WebDataBinder binder);
}
