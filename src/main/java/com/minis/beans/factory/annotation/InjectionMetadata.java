package com.minis.beans.factory.annotation;

import com.minis.beans.PropertyValues;
import com.minis.utils.CollectionUtils;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class InjectionMetadata {

    private final Class<?> targetClass;

    private final Collection<InjectedElement> injectedElements;

    private volatile Set<InjectedElement> checkedElements;

    public InjectionMetadata(Class<?> targetClass, Collection<InjectedElement> elements) {
        this.targetClass = targetClass;
        this.injectedElements = elements;
    }

    public static InjectionMetadata forElements(List<InjectedElement> elements, Class<?> clazz) {
        return (elements.isEmpty() ? new InjectionMetadata(clazz, Collections.emptyList()) :
                new InjectionMetadata(clazz, elements));
    }

    public static boolean needsRefresh(InjectionMetadata metadata, Class<?> clazz) {
        return (metadata == null || metadata.needsRefresh(clazz));
    }

    protected boolean needsRefresh(Class<?> clazz) {
        return (this.targetClass != clazz);
    }

    public void inject(Object target, String beanName, PropertyValues pvs) throws Throwable {
        Collection<InjectedElement> checkedElements = this.checkedElements;
        Collection<InjectedElement> elementsToIterate =
                (checkedElements != null ? checkedElements : this.injectedElements);
        if (!elementsToIterate.isEmpty()) {
            for (InjectedElement element : elementsToIterate) {
                element.inject(target, beanName, pvs);
            }
        }
    }

    public void checkConfigMembers() {
        if (this.injectedElements.isEmpty()) {
            this.checkedElements = Collections.emptySet();
        } else {
            Set<InjectedElement> checkedElements = CollectionUtils.newLinkedHashSet(this.injectedElements.size());
            checkedElements.addAll(this.injectedElements);
            this.checkedElements = checkedElements;
        }
    }


    public abstract static class InjectedElement {

        protected final Member member;

        protected final boolean isField;

        protected final PropertyDescriptor pd;

        protected InjectedElement(Member member, PropertyDescriptor pd) {
            this.member = member;
            this.isField = (member instanceof Field);
            this.pd = pd;
        }

        protected void inject(Object target, String requestingBeanName, PropertyValues pvs) throws Throwable {
        }

    }
}
