package com.minis.boot;

import com.minis.core.env.ConfigurableEnvironment;

public interface SpringApplicationRunListener {

    default void environmentPrepared(ConfigurableEnvironment environment) {
    }
}
