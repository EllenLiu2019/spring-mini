package com.minis.context.annotation;

import com.minis.core.type.MethodMetadata;

final class BeanMethod {


    protected final MethodMetadata metadata;

    protected final ConfigurationClass configurationClass;

    BeanMethod(MethodMetadata metadata, ConfigurationClass configurationClass) {
        this.metadata = metadata;
        this.configurationClass = configurationClass;
    }

    public MethodMetadata getMetadata() {
        return this.metadata;
    }

    public ConfigurationClass getConfigurationClass() {
        return this.configurationClass;
    }
}
