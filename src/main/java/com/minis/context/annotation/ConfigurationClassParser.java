package com.minis.context.annotation;

import com.minis.beans.factory.annotation.AnnotatedBeanDefinition;
import com.minis.beans.factory.config.BeanDefinition;
import com.minis.beans.factory.config.BeanDefinitionHolder;
import com.minis.beans.factory.support.BeanDefinitionRegistry;
import com.minis.core.annotation.AnnotationAttributes;
import com.minis.core.type.AnnotationMetadata;
import com.minis.core.type.StandardAnnotationMetadata;
import com.minis.core.type.classreading.MetadataReader;
import com.minis.utils.ClassUtils;
import com.minis.utils.CollectionUtils;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.function.Predicate;

@Slf4j
class ConfigurationClassParser {

    private static final Predicate<String> DEFAULT_EXCLUSION_FILTER = className ->
            (className.startsWith("java.lang.annotation.") || className.startsWith("org.springframework.stereotype."));

    private final BeanDefinitionRegistry registry;
    private final ComponentScanAnnotationParser componentScanParser;

    private final Map<ConfigurationClass, ConfigurationClass> configurationClasses = new LinkedHashMap<>();

    private final Map<String, ConfigurationClass> knownSuperclasses = new LinkedHashMap<>();
    private final DeferredImportSelectorHandler deferredImportSelectorHandler = new DeferredImportSelectorHandler();
    private SourceClass objectSourceClass = new SourceClass(Object.class);
    //private ImportStack importStack = new ImportStack();


    public ConfigurationClassParser(BeanDefinitionRegistry registry) {
        this.registry = registry;
        this.componentScanParser = new ComponentScanAnnotationParser(registry);
    }


    public void parse(Set<BeanDefinitionHolder> configCandidates) {
        for (BeanDefinitionHolder holder : configCandidates) {
            BeanDefinition bd = holder.getBeanDefinition();
            try {
                ConfigurationClass configClass;
                if (bd instanceof AnnotatedBeanDefinition annotatedBeanDef) {
                    configClass = parse(annotatedBeanDef, holder.getBeanName());
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        // TODO: @Import 注解 中 import的 deferred candidates start to process
        //  eg: AutoConfigurationImportSelector invoke .selectImport() ,
        //  即，加载配置文件：META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports
        this.deferredImportSelectorHandler.process();
    }

    public Set<ConfigurationClass> getConfigurationClasses() {
        return configurationClasses.keySet();
    }

    private ConfigurationClass parse(AnnotatedBeanDefinition beanDef, String beanName) {
        ConfigurationClass configClass = new ConfigurationClass(beanDef.getMetadata(), beanName);
        processConfigurationClass(configClass, DEFAULT_EXCLUSION_FILTER);
        return configClass;
    }

    private void processConfigurationClass(ConfigurationClass configClass, Predicate<String> filter) {
        // Recursively process the configuration class and its superclass hierarchy.
        SourceClass sourceClass = null;
        try {
            sourceClass = asSourceClass(configClass, filter);
            do {
                assert sourceClass != null;
                sourceClass = doProcessConfigurationClass(configClass, sourceClass, filter);
            }
            while (sourceClass != null);
        } catch (IOException ex) {
            throw new RuntimeException("I/O failure while processing configuration class [" + sourceClass + "]", ex);
        }

        this.configurationClasses.put(configClass, configClass);
    }

    private SourceClass asSourceClass(ConfigurationClass configurationClass, Predicate<String> filter) throws IOException {
        AnnotationMetadata metadata = configurationClass.getMetadata();
        if (metadata instanceof StandardAnnotationMetadata standardAnnotationMetadata) {
            return asSourceClass(standardAnnotationMetadata.getIntrospectedClass());
        }
        return null;
    }

    SourceClass asSourceClass(Class<?> classType, Predicate<String> filter) {
        if (classType == null || filter.test(classType.getName())) {
            return this.objectSourceClass;
        }
        return new SourceClass(classType);
    }

    SourceClass asSourceClass(String className, Predicate<String> filter) {
        if (className == null || filter.test(className)) {
            return this.objectSourceClass;
        }
        try {
            return new SourceClass(ClassUtils.forName(className, this.getClass().getClassLoader()));
        } catch (ClassNotFoundException e) {
            //throw new RuntimeException(e);
            log.trace("Class not found: " + className);
            return null;
        }
    }

    SourceClass asSourceClass(Class<?> classType) {
        return new SourceClass(classType);
    }

    protected final SourceClass doProcessConfigurationClass(ConfigurationClass configClass, SourceClass sourceClass,
                                                            Predicate<String> filter) throws IOException {
        // TODO: 包扫描，
        //  searching for @ComponentScan meta-annotations
        Set<AnnotationAttributes> componentScans = AnnotationConfigUtils.attributesForRepeatable(sourceClass.getMetadata(),
                ComponentScan.class);

        if (!componentScans.isEmpty()) {
            for (AnnotationAttributes componentScan : componentScans) {
                // TODO: The config class is annotated with @ComponentScan -> perform the scan immediately,
                //  All configuration class will be found and constructed as beanDefinition and registered into registry
                Set<BeanDefinition> scannedBeanDefinitions =
                        this.componentScanParser.parse(componentScan, sourceClass.getMetadata().getClassName());
                // TODO: Check the set of scanned beanDefinitions for any further config classes,
                //  because these config classes should be parsed recursively
                for (BeanDefinition bdCand : scannedBeanDefinitions) {
                    if (ConfigurationClassUtils.checkConfigurationClassCandidate(bdCand)) {
                        // TODO: 对于进行包扫描后，新扫描到的配置类进行递归解析
                        parse((AnnotatedBeanDefinition) bdCand, bdCand.getClassName());
                    }
                }
            }
        }

        // TODO: @Import annotations process, 并实例化 @Import 的 value,
        //  例如：@Import(AutoConfigurationImportSelector.class)
        //  实例化 AutoConfigurationImportSelector.class，
        //  因其继承自 DeferredImportSelector, 所以实例化后不会立即调用 .selectImport() 方法
        processImports(configClass, sourceClass, getImports(sourceClass), filter, true);

        // TODO：@Bean methods process
        /*Set<MethodMetadata> beanMethods = retrieveBeanMethodMetadata(sourceClass);
        for (MethodMetadata methodMetadata : beanMethods) {
            if (methodMetadata.isAnnotated("kotlin.jvm.JvmStatic") && !methodMetadata.isStatic()) {
                continue;
            }
            configClass.addBeanMethod(new BeanMethod(methodMetadata, configClass));
        }*/

        //processInterfaces(configClass, sourceClass);

        // Process superclass, if any
        if (sourceClass.getMetadata().hasSuperClass()) {
            String superclass = sourceClass.getMetadata().getSuperClassName();
            if (superclass != null && !superclass.startsWith("java")) {
                boolean superclassKnown = this.knownSuperclasses.containsKey(superclass);
                this.knownSuperclasses.put(superclass, configClass);
                if (!superclassKnown) {
                    // Superclass found, return its annotation metadata and recurse
                    return sourceClass.getSuperClass();
                }
            }
        }

        // No superclass -> processing is complete
        return null;
    }


    // TODO: 获取到 @Import 的 value
    //  例如：对与 @SpringBootApplication，
    //  imports：# Set<SourceClass>
    //  ------------------------------------------------
    //  autoconfigure.AutoConfigurationPackages$Registrar
    //  autoconfigure.AutoConfigurationImportSelector
    //  ------------------------------------------------
    private Set<SourceClass> getImports(SourceClass sourceClass) throws IOException {
        Set<SourceClass> imports = new LinkedHashSet<>();
        collectImports(sourceClass, imports, new HashSet<>());
        return imports;
    }

    private void collectImports(SourceClass sourceClass, Set<SourceClass> imports, HashSet<Object> visited) {
        if (visited.add(sourceClass)) {
            for (SourceClass annotation : sourceClass.getAnnotations()) {
                String annName = annotation.getMetadata().getClassName();
                if (!annName.equals(Import.class.getName())) {
                    collectImports(annotation, imports, visited);
                }
            }
            imports.addAll(sourceClass.getAnnotationAttributes(Import.class.getName(), "value"));
        }
    }

    /*private Set<MethodMetadata> retrieveBeanMethodMetadata(SourceClass sourceClass) {
        // TODO: 找到 类中带有 @Bean 的方法集合
    }*/

    // TODO: importCandidates
    //  ------------------------------------------------
    //  1. autoconfigure.AutoConfigurationImportSelector
    //  2. autoconfigure.AutoConfigurationPackages$Registrar
    //  ------------------------------------------------
    private void processImports(ConfigurationClass configClass, SourceClass currentSourceClass,
                                Collection<SourceClass> importCandidates, Predicate<String> filter, boolean checkForCircularImports) {

        if (importCandidates.isEmpty()) {
            return;
        }

        try {
            for (SourceClass candidate : importCandidates) {
                if (candidate.isAssignable(ImportSelector.class)) {
                    // TODO: to determine imports auto configuration classes
                    //  Candidate is AutoConfigurationImportSelector
                    Class<?> candidateClass = candidate.loadClass();
                    ImportSelector selector = ParserStrategyUtils.instantiateClass(candidateClass, this.registry);
                    if (selector instanceof DeferredImportSelector deferredImportSelector) {
                        // TODO: deferred, only store into variable, waiting all config class load and register
                        this.deferredImportSelectorHandler.handle(configClass, deferredImportSelector);
                    } else {
                        // TODO: process and invoke .selectImports() immediately
                        String[] importClassNames = selector.selectImports(currentSourceClass.getMetadata());
                        Collection<SourceClass> importSourceClasses = asSourceClasses(importClassNames, filter);
                        processImports(configClass, currentSourceClass, importSourceClasses, filter, false);
                    }
                } else if (candidate.isAssignable(ImportBeanDefinitionRegistrar.class)) {
                    // TODO: to enable JPA (@Repository and @Entity scanning)
                    //  Candidate is AutoConfigurationPackages$Registrar
                    //  add as a registrar, later will register BasePackages.class(base packages) as beanDefinition
                    Class<?> candidateClass = candidate.loadClass();
                    ImportBeanDefinitionRegistrar registrar = ParserStrategyUtils.instantiateClass(candidateClass, this.registry);
                    configClass.addImportBeanDefinitionRegistrar(registrar, currentSourceClass.getMetadata());
                } else {
                    // TODO: Candidate class not an ImportSelector or ImportBeanDefinitionRegistrar ->
                    //  process it as an @Configuration class
                    processConfigurationClass(candidate.asConfigClass(configClass), filter);
                }
            }
        } catch (Throwable ex) {
            throw new RuntimeException("Failed to process import candidates for configuration class [configClass.getMetadata().getClassName() + ]: " + ex.getMessage(), ex);
        }
    }


    private class DeferredImportSelectorHandler {
        private List<DeferredImportSelectorHolder> deferredImportSelectors = new ArrayList<>();

        /**
         * 将 DeferredImportSelector 封装为 DeferredImportSelectorHandler # DeferredImportSelectorHolder
         *
         * @param configClass
         * @param importSelector 继承自 DeferredImportSelector，需后续处理
         */
        public void handle(ConfigurationClass configClass, DeferredImportSelector importSelector) {
            DeferredImportSelectorHolder holder = new DeferredImportSelectorHolder(configClass, importSelector);
            /*if (this.deferredImportSelectors == null) { // for multiple threads handling
                DeferredImportSelectorGroupingHandler handler = new DeferredImportSelectorGroupingHandler();
                handler.register(holder);
                handler.processGroupImports();
            } else {*/
            this.deferredImportSelectors.add(holder);
            //}
        }

        void process() {
            //TODO: process imports,
            // getCandidateConfigurations();
            List<DeferredImportSelectorHolder> deferredImports = this.deferredImportSelectors;
            this.deferredImportSelectors = null;
            try {
                if (deferredImports != null) {
                    DeferredImportSelectorGroupingHandler handler = new DeferredImportSelectorGroupingHandler();
                    deferredImports.forEach(deferredImport -> handler.register(deferredImport));
                    handler.processGroupImports();
                }
            } finally {
                this.deferredImportSelectors = new ArrayList<>();
            }
        }
    }

    private class DeferredImportSelectorGroupingHandler {

        private final Map<Object, DeferredImportSelectorGrouping> groupings = new LinkedHashMap<>();

        private final Map<AnnotationMetadata, ConfigurationClass> configurationClasses = new HashMap<>();


        public void register(DeferredImportSelectorHolder deferredImport) {
            Class<? extends DeferredImportSelector.Group> group = deferredImport.getImportSelector().getImportGroup();
            DeferredImportSelectorGrouping grouping = this.groupings.computeIfAbsent(
                    (group != null ? group : deferredImport),
                    key -> new DeferredImportSelectorGrouping(createGroup(group)));
            grouping.add(deferredImport);
            this.configurationClasses.put(deferredImport.getConfigurationClass().getMetadata(),
                    deferredImport.getConfigurationClass());
        }

        private DeferredImportSelector.Group createGroup(Class<? extends DeferredImportSelector.Group> type) {
            try {
                Constructor<?> ctor = type.getDeclaredConstructor();
                if ((!Modifier.isPublic(ctor.getModifiers()) ||
                        !Modifier.isPublic(ctor.getDeclaringClass().getModifiers())) && !ctor.isAccessible()) {
                    ctor.setAccessible(true);
                }
                return (DeferredImportSelector.Group) ctor.newInstance();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        public void processGroupImports() {
            for (DeferredImportSelectorGrouping grouping : this.groupings.values()) {
                Predicate<String> filter = grouping.getCandidateFilter();
                grouping.getImports().forEach(entry -> {
                    // TODO：配置文件 META-INF/spring/*.imports 中的配置类
                    ConfigurationClass configurationClass = this.configurationClasses.get(entry.getMetadata());
                    try {
                        SourceClass sourceClass = asSourceClass(entry.getImportClassName(), filter);
                        if (sourceClass != null) {
                            processImports(configurationClass, asSourceClass(configurationClass, filter),
                                    Collections.singleton(sourceClass), filter, false);
                        }
                    } catch (Throwable ex) {
                        throw new RuntimeException("Failed to process import candidates for configuration class [" + configurationClass.getMetadata().getClassName() + "]", ex);
                    }
                });
            }
        }
    }

    private static class DeferredImportSelectorHolder {
        private final ConfigurationClass configurationClass;
        private final DeferredImportSelector importSelector;

        DeferredImportSelectorHolder(ConfigurationClass configClass, DeferredImportSelector selector) {
            this.configurationClass = configClass;
            this.importSelector = selector;
        }

        ConfigurationClass getConfigurationClass() {
            return this.configurationClass;
        }

        DeferredImportSelector getImportSelector() {
            return this.importSelector;
        }

    }

    private Collection<SourceClass> asSourceClasses(String[] classNames, Predicate<String> filter) {
        List<SourceClass> annotatedClasses = new ArrayList<>(classNames.length);
        for (String className : classNames) {
            SourceClass sourceClass = asSourceClass(className, filter);
            if (this.objectSourceClass != sourceClass) {
                annotatedClasses.add(sourceClass);
            }
        }
        return annotatedClasses;
    }

    /*private void processInterfaces(ConfigurationClass configClass, SourceClass sourceClass) throws IOException {
        for (SourceClass ifc : sourceClass.getInterfaces()) {
            Set<MethodMetadata> beanMethods = retrieveBeanMethodMetadata(ifc);
            for (MethodMetadata methodMetadata : beanMethods) {
                if (!methodMetadata.isAbstract()) {
                    // A default method or other concrete method on a Java 8+ interface...
                    configClass.addBeanMethod(new BeanMethod(methodMetadata, configClass));
                }
            }
            processInterfaces(configClass, ifc);
        }
    }*/

    // TODO: import with its associated configuration class,
    //  invoke AutoConfigurationImportSelector
    private static class DeferredImportSelectorGrouping {

        private final DeferredImportSelector.Group group;

        private final List<DeferredImportSelectorHolder> deferredImports = new ArrayList<>();

        DeferredImportSelectorGrouping(DeferredImportSelector.Group group) {
            this.group = group;
        }

        Iterable<DeferredImportSelector.Group.Entry> getImports() {
            for (DeferredImportSelectorHolder deferredImport : this.deferredImports) {
                this.group.process(deferredImport.getConfigurationClass().getMetadata(),
                        deferredImport.getImportSelector());
            }
            return this.group.selectImports();
        }

        public void add(DeferredImportSelectorHolder deferredImport) {
            this.deferredImports.add(deferredImport);
        }

        public Predicate<String> getCandidateFilter() {
            Predicate<String> mergedFilter = DEFAULT_EXCLUSION_FILTER;
            /*for (DeferredImportSelectorHolder deferredImport : this.deferredImports) {
                Predicate<String> selectorFilter = deferredImport.getImportSelector().getExclusionFilter();
                if (selectorFilter != null) {
                    mergedFilter = mergedFilter.or(selectorFilter);
                }
            }*/
            return mergedFilter;
        }
    }

    private class SourceClass {

        private final Object source;

        private final AnnotationMetadata metadata;

        public SourceClass(Object source) {
            this.source = source;
            if (source instanceof Class<?> sourceClass) {
                this.metadata = AnnotationMetadata.introspect(sourceClass);
            } else {
                this.metadata = ((MetadataReader) source).getAnnotationMetadata();
            }
        }

        public final AnnotationMetadata getMetadata() {
            return this.metadata;
        }

        public SourceClass getSuperClass() {
            if (this.source instanceof Class<?> sourceClass) {
                return asSourceClass(sourceClass.getSuperclass(), DEFAULT_EXCLUSION_FILTER);
            }
            log.error("source is not a Class, ignore...");
            throw new RuntimeException("source is not a Class");
        }

        public Set<SourceClass> getAnnotations() {
            Set<SourceClass> result = new LinkedHashSet<>();
            if (this.source instanceof Class<?> sourceClass) {
                for (Annotation ann : sourceClass.getDeclaredAnnotations()) {
                    Class<?> annType = ann.annotationType();
                    if (!annType.getName().startsWith("java")) {
                        try {
                            result.add(asSourceClass(annType, DEFAULT_EXCLUSION_FILTER));
                        } catch (Throwable ex) {
                            // An annotation not present on the classpath is being ignored
                            // by the JVM's class loading -> ignore here as well.
                        }
                    }
                }
            } else {
                for (String className : this.metadata.getAnnotationTypes()) {
                    if (!className.startsWith("java")) {
                        try {
                            result.add(getRelated(className));
                        } catch (Throwable ex) {
                            // An annotation not present on the classpath is being ignored
                            // by the JVM's class loading -> ignore here as well.
                        }
                    }
                }
            }
            return result;
        }

        private SourceClass getRelated(String className) {
            return asSourceClass(className, DEFAULT_EXCLUSION_FILTER);
        }

        public Collection<SourceClass> getAnnotationAttributes(String annType, String attribute) {
            Map<String, Object> annotationAttributes = this.metadata.getAnnotationAttributes(annType, true);
            if (annotationAttributes == null || !annotationAttributes.containsKey(attribute)) {
                return Collections.emptySet();
            }
            Class<?>[] classNames = (Class<?>[]) annotationAttributes.get(attribute);
            Set<SourceClass> result = CollectionUtils.newLinkedHashSet(classNames.length);
            for (Class<?> className : classNames) {
                result.add(asSourceClass(className));
            }
            return result;
        }

        public boolean isAssignable(Class<?> clazz) {
            if (this.source instanceof Class<?> sourceClass) {
                return clazz.isAssignableFrom(sourceClass);
            }
            return false;
        }

        public Class<?> loadClass() {
            if (this.source instanceof Class<?> sourceClass) {
                return sourceClass;
            }
            return null;
        }

        public ConfigurationClass asConfigClass(ConfigurationClass importedBy) {
            if (this.source instanceof Class<?> sourceClass) {
                return new ConfigurationClass(sourceClass, importedBy);
            }
            return null;
        }

        @Override
        public boolean equals(Object other) {
            return (this == other || (other instanceof SourceClass that &&
                    this.metadata.getClassName().equals(that.metadata.getClassName())));
        }

        @Override
        public int hashCode() {
            return this.metadata.getClassName().hashCode();
        }

        @Override
        public String toString() {
            return this.metadata.getClassName();
        }
    }
}
