package com.minis.boot;

import com.minis.core.env.ConfigurableEnvironment;

import java.util.List;
import java.util.function.Consumer;

class SpringApplicationRunListeners {

    private final List<SpringApplicationRunListener> listeners;

    SpringApplicationRunListeners(List<SpringApplicationRunListener> listeners) {
        this.listeners = List.copyOf(listeners);
    }

    void environmentPrepared(ConfigurableEnvironment environment) {
        doWithListeners((listener) -> listener.environmentPrepared(environment));
    }

    private void doWithListeners(Consumer<SpringApplicationRunListener> listenerAction) {
        this.listeners.forEach(listenerAction);
    }
}
