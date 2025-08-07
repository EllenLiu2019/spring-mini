package com.minis.beans.factory.support;

import com.minis.beans.BeansException;
import com.minis.beans.TypeConverter;
import com.minis.beans.factory.annotation.AnnotatedBeanDefinition;
import com.minis.beans.factory.annotation.Value;
import com.minis.beans.factory.config.BeanDefinition;
import com.minis.beans.factory.config.DependencyDescriptor;
import com.minis.core.annotation.AnnotatedElementUtils;
import com.minis.core.annotation.AnnotationAttributes;
import com.minis.core.type.MethodMetadata;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.annotation.Annotation;
import java.util.*;

public class DefaultListableBeanFactory extends AbstractAutowireCapableBeanFactory
        implements ConfigurableListableBeanFactory {
    private static final Logger LOGGER = LogManager.getLogger(DefaultListableBeanFactory.class);

    private ConfigurableListableBeanFactory parentBeanFactory;

    @Override
    public int getBeanDefinitionCount() {
        return this.beanDefinitionMap.size();
    }

    @Override
    public String[] getBeanDefinitionNames() {
        return this.beanDefinitionNames.toArray(new String[0]);
    }

    @Override
    public String[] getBeanNamesForType(Class<?> type) {
        List<String> result = new ArrayList<>();
        for (String beanName : this.beanDefinitionNames) {
            String beanClassName = null;
            Class<?> classToMatch;
            BeanDefinition beanDefinition = this.getBeanDefinition(beanName);
            if (beanDefinition.getFactoryMethodName() != null) {
                if (beanDefinition instanceof AnnotatedBeanDefinition annotatedBeanDefinition) {
                    MethodMetadata methodMetadata = annotatedBeanDefinition.getFactoryMethodMetadata();
                    beanClassName = methodMetadata.getReturnTypeName();
                }
            }
            if (beanClassName == null) {
                beanClassName = beanDefinition.getBeanClassName() == null ?
                        beanDefinition.getClassName() : beanDefinition.getBeanClassName();
            }

            try {
                classToMatch = Class.forName(beanClassName);
                if (type.isAssignableFrom(classToMatch)) {
                    result.add(beanName);
                }
            } catch (ClassNotFoundException ignored) {
            }
        }
        return result.toArray(new String[0]);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> Map<String, T> getBeansOfType(Class<T> type) throws BeansException, ReflectiveOperationException {
        String[] beanNamesForType = this.getBeanNamesForType(type);
        Map<String, T> result = new LinkedHashMap<>(beanNamesForType.length);
        for (String beanName : beanNamesForType) {
            Object beanInstance = getBean(beanName);
            result.put(beanName, (T) beanInstance);
        }
        return result;
    }

    public void setParent(ConfigurableListableBeanFactory beanFactory) {
        this.parentBeanFactory = beanFactory;
    }

    @Override
    public Object getBean(String beanName) throws ReflectiveOperationException, BeansException {
        Object result = super.getBean(beanName);
        if (result == null && this.parentBeanFactory != null) {
            LOGGER.debug("getting bean={} from IoC", beanName);
            result = this.parentBeanFactory.getBean(beanName);
            LOGGER.debug("got bean={} from IoC, result={}", beanName, result.getClass());
        }
        return result;
    }

    @Override
    public BeanDefinition getBeanDefinition(String beanName) throws BeansException {
        BeanDefinition bd = this.beanDefinitionMap.get(beanName);
        if (bd == null) {
            LOGGER.trace("No bean named '" + beanName + "' found in " + this);
            throw new BeansException(beanName);
        }
        return bd;
    }

    @Override
    public void preInstantiateSingletons() {
        List<String> beanNames = new ArrayList<>(this.beanDefinitionNames);
        try {
            for (String beanName : beanNames) {
                getBean(beanName);
            }
        } catch (ReflectiveOperationException | BeansException e) {
            throw new RuntimeException(e);
        }
    }

    public Object resolveDependency(DependencyDescriptor descriptor, String requestingBeanName,
                                    Set<String> autowiredBeanNames) throws BeansException {

        return doResolveDependency(descriptor);
    }

    public Object doResolveDependency(DependencyDescriptor descriptor) throws BeansException {
        try {
            Class<?> type = descriptor.getDependencyType();
            Object value = this.findValue(descriptor.getAnnotations());
            if (value != null) {
                String resolvedValue = null;
                if (value instanceof String strValue) {
                    resolvedValue = resolveEmbeddedValue(strValue);
                }
                TypeConverter converter = getTypeConverter();
                return converter.convertIfNecessary(resolvedValue, type, descriptor.getTypeDescriptor());
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    protected Object findValue(Annotation[] annotationsToSearch) {
        if (annotationsToSearch.length > 0) {   // qualifier annotations have to be local
            AnnotationAttributes attr = AnnotatedElementUtils.getMergedAnnotationAttributes(
                    AnnotatedElementUtils.forAnnotations(annotationsToSearch), Value.class);
            if (attr != null) {
                return extractValue(attr);
            }
        }
        return null;
    }

    protected Object extractValue(AnnotationAttributes attr) {
        Object value = attr.get("value");
        if (value == null) {
            throw new IllegalStateException("Value annotation must have a value attribute");
        }
        return value;
    }
}
