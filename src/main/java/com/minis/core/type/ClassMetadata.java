package com.minis.core.type;

public interface ClassMetadata {

    String getClassName();

    boolean isInterface();

    boolean isAbstract();

    default boolean isConcrete() {
        return !(isInterface() || isAbstract());
    }

    boolean isFinal();

    boolean isIndependent();

    default boolean hasEnclosingClass() {
        return (getEnclosingClassName() != null);
    }

    String getEnclosingClassName();

    default boolean hasSuperClass() {
        return (getSuperClassName() != null);
    }

    String getSuperClassName();

    String[] getInterfaceNames();

    String[] getMemberClassNames();

}
