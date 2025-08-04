package com.minis.beans.factory.annotation;

import java.lang.annotation.*;

@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Value {

    /**
     * The actual value expression such as <code>#{systemProperties.myProp}</code>
     * or property placeholder such as <code>${my.app.myProp}</code>.
     */
    String value();

}
