package com.minis.beans.factory.support;

import com.minis.beans.PropertyEditorRegistrar;
import com.minis.beans.TypeConverter;
import com.minis.beans.factory.BeanFactory;
import com.minis.beans.factory.config.BeanPostProcessor;
import com.minis.beans.factory.config.SingletonBeanRegistry;
import com.minis.core.convert.ConversionService;
import com.minis.utils.StringValueResolver;

public interface ConfigurableBeanFactory extends BeanFactory, SingletonBeanRegistry {
    String SCOPE_SINGLETON = "singleton";
    String SCOPE_PROTOTYPE = "prototype";
    void addBeanPostProcessor(BeanPostProcessor beanPostProcessor);
    int getBeanPostProcessorCount();
    String[] getDependentBeans(String beanName);
    String[] getDependenciesForBean(String beanName);

    void addEmbeddedValueResolver(StringValueResolver valueResolver);

    String resolveEmbeddedValue(String value);

    void addPropertyEditorRegistrar(PropertyEditorRegistrar registrar);

    TypeConverter getTypeConverter();

    ConversionService getConversionService();

    void setConversionService(ConversionService conversionService);
}
