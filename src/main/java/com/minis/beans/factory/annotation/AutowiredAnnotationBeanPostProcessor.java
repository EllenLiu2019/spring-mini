package com.minis.beans.factory.annotation;

import com.minis.beans.PropertyValues;
import com.minis.beans.factory.BeanFactory;
import com.minis.beans.BeansException;
import com.minis.beans.factory.BeanFactoryAware;
import com.minis.beans.factory.config.BeanDefinition;
import com.minis.beans.factory.config.BeanPostProcessor;
import com.minis.beans.factory.config.DependencyDescriptor;
import com.minis.beans.factory.config.InstantiationAwareBeanPostProcessor;
import com.minis.beans.factory.support.ConfigurableListableBeanFactory;
import com.minis.beans.factory.support.MergedBeanDefinitionPostProcessor;
import com.minis.core.annotation.MergedAnnotation;
import com.minis.core.annotation.MergedAnnotations;
import com.minis.utils.ReflectionUtils;
import com.minis.utils.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

// TODO: 为了支持 @Autowired， 新增 AutowiredAnnotationBeanPostProcessor 类
//  该类实现 BeanPostProcessor 接口
//  方法：postProcessBeforeInitialization
//  主要用途：1. 扫描类中所有带 @Autowired 注解的属性，并设置属性值
public class AutowiredAnnotationBeanPostProcessor implements BeanPostProcessor, BeanFactoryAware,
        InstantiationAwareBeanPostProcessor, MergedBeanDefinitionPostProcessor {
    private static final Logger LOGGER = LogManager.getLogger(AutowiredAnnotationBeanPostProcessor.class.getName());
    private ConfigurableListableBeanFactory beanFactory;

    private final Map<String, InjectionMetadata> injectionMetadataCache = new ConcurrentHashMap<>(256);

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException, ReflectiveOperationException {
        LOGGER.debug("post Process Before Initialization for bean: " + beanName);

        Object result = bean;
        Class<?> clazz = bean.getClass();
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            if (field.isAnnotationPresent(Autowired.class)) {
                String fieldName = field.getName();
                Object autowiredObj = this.beanFactory.getBean(fieldName);
                if (autowiredObj == null) {
                    Class<?> type = field.getType();
                    String[] beanNames = this.beanFactory.getBeanNamesForType(type);
                    autowiredObj = this.beanFactory.getBean(beanNames[0]);
                }
                field.setAccessible(true);
                field.set(bean, autowiredObj);
                LOGGER.debug("autowire " + fieldName + " for bean: " + beanName);
            }
        }
        return result;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        LOGGER.debug("postProcess After Initialization for bean: " + beanName);
        return bean;
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) {
        if (!(beanFactory instanceof ConfigurableListableBeanFactory clbf)) {
            throw new IllegalArgumentException(
                    "AutowiredAnnotationBeanPostProcessor requires a ConfigurableListableBeanFactory: " + beanFactory);
        }
        this.beanFactory = clbf;
    }

    @Override
    public PropertyValues postProcessProperties(PropertyValues pvs, Object bean, String beanName) {
        InjectionMetadata metadata = findAutowiringMetadata(beanName, bean.getClass(), pvs);
        try {
            metadata.inject(bean, beanName, pvs);
        } catch (Throwable ex) {
            throw new BeansException(beanName + " Injection of autowired dependencies failed: " + ex);
        }
        return pvs;
    }

    private InjectionMetadata findAutowiringMetadata(String beanName, Class<?> clazz, PropertyValues pvs) {
        // Fall back to class name as cache key, for backwards compatibility with custom callers.
        String cacheKey = (StringUtils.hasLength(beanName) ? beanName : clazz.getName());
        // Quick check on the concurrent map first, with minimal locking.
        InjectionMetadata metadata = this.injectionMetadataCache.get(cacheKey);
        if (InjectionMetadata.needsRefresh(metadata, clazz)) {
            synchronized (this.injectionMetadataCache) {
                metadata = this.injectionMetadataCache.get(cacheKey);
                if (InjectionMetadata.needsRefresh(metadata, clazz)) {
                    metadata = buildAutowiringMetadata(clazz);
                    this.injectionMetadataCache.put(cacheKey, metadata);
                }
            }
        }
        return metadata;
    }

    private InjectionMetadata buildAutowiringMetadata(Class<?> clazz) {
        final List<InjectionMetadata.InjectedElement> elements = new ArrayList<>();
        Class<?> targetClass = clazz;
        do {
            final List<InjectionMetadata.InjectedElement> fieldElements = new ArrayList<>();
            ReflectionUtils.doWithLocalFields(targetClass, field -> {
                MergedAnnotation<?> ann = findAutowiredAnnotation(field);
                if (ann != null) {
                    if (Modifier.isStatic(field.getModifiers())) {
                        LOGGER.info("Autowired annotation is not supported on static fields: " + field);
                        return;
                    }
                    fieldElements.add(new AutowiredFieldElement(field));
                }
            });

            elements.addAll(0, fieldElements);
            targetClass = targetClass.getSuperclass();
        } while (targetClass != null && targetClass != Object.class);

        return InjectionMetadata.forElements(elements, clazz);
    }

    private MergedAnnotation<?> findAutowiredAnnotation(AccessibleObject ao) {
        MergedAnnotations annotations = MergedAnnotations.from(ao);
        MergedAnnotation<?> annotation = annotations.get(Value.class);
        if (annotation != null && annotation.isPresent()) {
            return annotation;
        }
        return null;
    }

    @Override
    public void postProcessMergedBeanDefinition(BeanDefinition beanDefinition, String beanName) {
        // Register externally managed config members on bean definition.
        Class<?> beanClass;
        try {
            beanClass = Class.forName(beanName);
        } catch (ClassNotFoundException e) {
            try {
                beanClass = beanDefinition.getBeanClass();
            } catch (IllegalStateException e1) {
                LOGGER.warn(e1);
                return;
            }
        }
        findInjectionMetadata(beanName, beanClass);
    }

    private InjectionMetadata findInjectionMetadata(String beanName, Class<?> beanType) {
        InjectionMetadata metadata = findAutowiringMetadata(beanName, beanType, null);
        metadata.checkConfigMembers();
        return metadata;
    }

    private void registerDependentBeans(String beanName, Set<String> autowiredBeanNames) {
        if (beanName != null) {
            for (String autowiredBeanName : autowiredBeanNames) {
                if (this.beanFactory != null && this.beanFactory.containsBean(autowiredBeanName)) {
                    //this.beanFactory.registerDependentBean(autowiredBeanName, beanName);
                }
                LOGGER.trace("Autowiring by type from bean name '" + beanName +
                        "' to bean named '" + autowiredBeanName + "'");
            }
        }
    }

    private class AutowiredFieldElement extends InjectionMetadata.InjectedElement {

        private volatile boolean cached;

        private volatile Object cachedFieldValue;

        public AutowiredFieldElement(Field field) {
            super(field, null);
        }

        @Override
        protected void inject(Object bean, String beanName, PropertyValues pvs) throws Throwable {
            Field field = (Field) this.member;
            Object value = resolveFieldValue(field, bean, beanName);
            if (value != null) {
                ReflectionUtils.makeAccessible(field);
                field.set(bean, value);
            }
        }

        private Object resolveFieldValue(Field field, Object bean, String beanName) {
            DependencyDescriptor desc = new DependencyDescriptor(field);
            desc.setContainingClass(bean.getClass());
            Set<String> autowiredBeanNames = new LinkedHashSet<>(2);
            Object value;
            try {
                value = beanFactory.resolveDependency(desc, beanName, autowiredBeanNames);
            } catch (BeansException ex) {
                throw new BeansException("failed to resolve dependency: " + desc + "caused by: " + ex);
            }
            synchronized (this) {
                if (!this.cached) {
                    if (value != null) {
                        Object cachedFieldValue = desc;
                        registerDependentBeans(beanName, autowiredBeanNames);
                        this.cachedFieldValue = cachedFieldValue;
                        this.cached = true;
                    }
                }
            }
            return value;
        }
    }
}
