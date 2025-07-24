package com.minis.boot;

import com.minis.context.ConfigurableApplicationContext;

public interface ApplicationContextInitializer<C extends ConfigurableApplicationContext> {
    void initialize(C applicationContext);
}
