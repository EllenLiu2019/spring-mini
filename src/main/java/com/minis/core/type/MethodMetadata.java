package com.minis.core.type;

public interface MethodMetadata extends AnnotationMetadata {

    boolean isStatic();

    String getMethodName();

    String getReturnTypeName();
}
