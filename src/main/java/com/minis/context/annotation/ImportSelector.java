package com.minis.context.annotation;

import com.minis.core.type.AnnotationMetadata;

/**
 * Interface to be implemented by types that determine which @{@link Configuration}
 * class(es) should be imported based on a given selection criteria, usually one or
 * more annotation attributes.
 *
 * An {@link ImportSelector} may implement any of the following Aware interfaces,
 * and their respective methods will be called prior to {@link #selectImports}:
 * EnvironmentAware
 * BeanFactoryAware
 * BeanClassLoaderAware
 * ResourceLoaderAware
 *
 * Alternatively, the class may provide a single constructor with one or more of
 * the following supported parameter types:
 * Environment
 * BeanFactory
 * ClassLoader
 * ResourceLoader
 *
 * ImportSelector implementations are usually processed in the same way
 * as regular @Import annotations, however, it is also possible to defer
 * selection of imports until all @Configuration classes have been processed
 * (see {@link DeferredImportSelector} for details).
 */
public interface ImportSelector {

    /**
     * Select and return the names of which class(es) should be imported
     * based on the AnnotationMetadata of the importing @Configuration class.
     * @param importingClassMetadata
     * @Returns: the class names, or an empty array if none
     */
    String[] selectImports(AnnotationMetadata importingClassMetadata);
}
