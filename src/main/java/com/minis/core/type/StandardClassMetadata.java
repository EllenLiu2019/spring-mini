package com.minis.core.type;

import com.minis.utils.StringUtils;

import java.lang.reflect.Modifier;
import java.util.LinkedHashSet;

public class StandardClassMetadata implements ClassMetadata {

    private final Class<?> introspectedClass;

    public StandardClassMetadata(Class<?> introspectedClass) {
        this.introspectedClass = introspectedClass;
    }

    @Override
    public String getClassName() {
        return this.introspectedClass.getName();
    }

    @Override
    public boolean isInterface() {
        return this.introspectedClass.isInterface();
    }

    @Override
    public boolean isAbstract() {
        return Modifier.isAbstract(this.introspectedClass.getModifiers());
    }

    @Override
    public boolean isFinal() {
        return Modifier.isFinal(this.introspectedClass.getModifiers());
    }

    @Override
    public boolean isIndependent() {
        return (!hasEnclosingClass() ||
                (this.introspectedClass.getDeclaredClasses() != null &&
                        Modifier.isStatic(this.introspectedClass.getModifiers())));
    }

    @Override
    public String getEnclosingClassName() {
        Class<?> enclosingClass = this.introspectedClass.getEnclosingClass();
        return (enclosingClass != null) ? enclosingClass.getName() : null;
    }

    @Override
    public String getSuperClassName() {
        Class<?> superClass = this.introspectedClass.getSuperclass();
        return (superClass != null ? superClass.getName() : null);
    }

    @Override
    public String[] getInterfaceNames() {
        Class<?>[] ifcs = this.introspectedClass.getInterfaces();
        String[] ifcNames = new String[ifcs.length];
        for (int i = 0; i < ifcs.length; i++) {
            ifcNames[i] = ifcs[i].getName();
        }
        return ifcNames;
    }

    @Override
    public String[] getMemberClassNames() {
        LinkedHashSet<String> memberClassNames = new LinkedHashSet<>(4);
        for (Class<?> nestedClass : this.introspectedClass.getDeclaredClasses()) {
            memberClassNames.add(nestedClass.getName());
        }
        return StringUtils.toStringArray(memberClassNames);
    }

    public final Class<?> getIntrospectedClass() {
        return this.introspectedClass;
    }

    @Override
    public boolean equals(Object other) {
        return (this == other || (other instanceof StandardClassMetadata that &&
                getIntrospectedClass().equals(that.getIntrospectedClass())));
    }

    @Override
    public int hashCode() {
        return getIntrospectedClass().hashCode();
    }

    @Override
    public String toString() {
        return getClassName();
    }
}
