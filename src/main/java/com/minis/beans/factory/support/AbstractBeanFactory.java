package com.minis.beans.factory.support;

import com.minis.beans.*;
import com.minis.beans.factory.FactoryBean;
import com.minis.beans.factory.config.*;
import com.minis.core.convert.ConversionService;
import com.minis.utils.StringValueResolver;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public abstract class AbstractBeanFactory extends FactoryBeanRegistrySupport implements ConfigurableBeanFactory, BeanDefinitionRegistry {
    protected final Map<String, BeanDefinition> beanDefinitionMap = new ConcurrentHashMap<>();
    protected final List<String> beanDefinitionNames = new ArrayList<>();
    private final Map<String, Object> earlySingletonObjects = new HashMap<>(16);

    private final List<StringValueResolver> embeddedValueResolvers = new CopyOnWriteArrayList<>();

    private final Set<PropertyEditorRegistrar> defaultEditorRegistrars = new LinkedHashSet<>(4);

    private TypeConverter typeConverter;

    private ConversionService conversionService;

    @Override
    public Object getBean(String beanName) throws BeansException, ReflectiveOperationException {
        Object singleton = doGetBean(beanName);
        if (singleton instanceof FactoryBean) {
            singleton = this.getObjectForBeanInstance(singleton, beanName);
        }
        return singleton;
    }

    private Object getObjectForBeanInstance(Object singleton, String beanName) {
        Object object = getCachedObjectForFactoryBean(beanName);
        if (object == null) {
            FactoryBean<?> factory = (FactoryBean<?>) singleton;
            object = getObjectFromFactoryBean(factory, beanName);

        }
        return object;
    }

    public void refresh() {
        for (String beanName : beanDefinitionNames) {
            try {
                getBean(beanName);
            } catch (ReflectiveOperationException | BeansException e) {
                throw new RuntimeException(e);
            }
        }
    }

    // TODO: why need this method? BeanFactory holds BeanDefinition,
    //  SingletonBeanRegistry holds singleton object
    @Override
    public boolean containsBean(String beanName) {
        return containsSingleton(beanName);
    }

    @Override
    public boolean isSingleton(String name) {
        return this.beanDefinitionMap.get(name).isSingleton();
    }

    @Override
    public boolean isPrototype(String name) {
        return this.beanDefinitionMap.get(name).isPrototype();
    }

    @Override
    public Class<?> getType(String beanName) {
        try {
            return Class.forName(this.beanDefinitionMap.get(beanName).getClassName());
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void registerBeanDefinition(String name, BeanDefinition beanDefinition) {
        this.beanDefinitionMap.put(name, beanDefinition);
        this.beanDefinitionNames.add(name);
    }

    @Override
    public void removeBeanDefinition(String name) {
        this.beanDefinitionMap.remove(name);
        this.beanDefinitionNames.remove(name);
        this.removeSingleton(name);
    }

    @Override
    public void addEmbeddedValueResolver(StringValueResolver valueResolver) {
        this.embeddedValueResolvers.add(valueResolver);
    }

    @Override
    public String resolveEmbeddedValue(String value) {
        if (value == null) {
            return null;
        }
        String result = value;
        for (StringValueResolver resolver : this.embeddedValueResolvers) {
            result = resolver.resolveStringValue(result);
            if (result == null) {
                return null;
            }
        }
        return result;
    }

    @Override
    public void addPropertyEditorRegistrar(PropertyEditorRegistrar registrar) {
        this.defaultEditorRegistrars.add(registrar);
    }

    protected void initBeanWrapper(BeanWrapper bw) {
        this.registerCustomEditors(bw);
    }

    /**
     * Optional, add a registrar {@link BeanFactoryDefaultEditorRegistrar}, which used to register additional default Property Editors
     * the only implementation is in {@link PropertyEditorRegistrySupport}
     * BeanWrapperImpl can register additional default Property Editors
     * Each BeanWrapperImpl has typeConverterDelegate which include the beanWrapperImpl itself,
     * this reference is named propertyEditorRegistry and will invoke getDefaultEditor method
     * @param registry -> beanWrapperImpl & SimpletypeConverter extend {@link PropertyEditorRegistrySupport}
     */
    protected void registerCustomEditors(PropertyEditorRegistry registry) {
        if (registry instanceof PropertyEditorRegistrySupport registrySupport) {
            if (!this.defaultEditorRegistrars.isEmpty()) {
                // Optimization: lazy overriding of default editors only when needed
                registrySupport.setDefaultEditorRegistrar(new BeanFactoryDefaultEditorRegistrar());
            }
        }
    }

    /**
     * Process this.defaultEditorRegistrars:
     * add some specified editors into property -> overriddenDefaultEditors
     *
     * @param registry   {@link PropertyEditorRegistrySupport}
     * @param registrars {@link ResourceEditorRegistrar}
     */
    private void applyEditorRegistrars(PropertyEditorRegistry registry, Set<PropertyEditorRegistrar> registrars) {
        for (PropertyEditorRegistrar registrar : registrars) {
            registrar.registerCustomEditors(registry);
        }
    }

    @Override
    public TypeConverter getTypeConverter() {
        TypeConverter customConverter = getCustomTypeConverter();
        if (customConverter != null) {
            return customConverter;
        } else {
            // Build default TypeConverter, registering custom editors.
            SimpleTypeConverter typeConverter = new SimpleTypeConverter();
            typeConverter.setConversionService(getConversionService());
            this.registerCustomEditors(typeConverter);
            return typeConverter;
        }
    }

    protected TypeConverter getCustomTypeConverter() {
        return this.typeConverter;
    }

    @Override
    public ConversionService getConversionService() {
        return this.conversionService;
    }

    @Override
    public void setConversionService(ConversionService conversionService) {
        this.conversionService = conversionService;
    }

    public abstract BeanDefinition getBeanDefinition(String name) throws BeansException;

    @Override
    public boolean containsBeanDefinition(String name) {
        return this.beanDefinitionMap.containsKey(name);
    }

    protected abstract Object doGetBean(String beanName) throws ReflectiveOperationException, BeansException;

    abstract Object createBeanInstance(String beanName, Class<?> clz, BeanDefinition bd) throws ReflectiveOperationException;

    class BeanFactoryDefaultEditorRegistrar implements PropertyEditorRegistrar {

        @Override
        public void registerCustomEditors(PropertyEditorRegistry registry) {
            applyEditorRegistrars(registry, defaultEditorRegistrars);
        }
    }

}
