package com.minis.boot.autoconfigure;

import com.minis.beans.factory.BeanFactory;
import com.minis.beans.factory.BeanFactoryAware;
import com.minis.beans.factory.support.ConfigurableListableBeanFactory;
import com.minis.boot.context.annotation.ImportCandidates;
import com.minis.context.annotation.DeferredImportSelector;
import com.minis.core.type.AnnotationMetadata;
import com.minis.utils.StringUtils;

import java.util.*;
import java.util.stream.Collectors;


public class AutoConfigurationImportSelector implements DeferredImportSelector, BeanFactoryAware {

    private final Class<?> autoConfigurationAnnotation;
    private ConfigurableListableBeanFactory beanFactory;

    public AutoConfigurationImportSelector() {
        this(null);
    }

    AutoConfigurationImportSelector(Class<?> autoConfigurationAnnotation) {
        this.autoConfigurationAnnotation = (autoConfigurationAnnotation != null) ? autoConfigurationAnnotation
                : AutoConfiguration.class;
    }


    /**
     * TODO: Return the AutoConfigurationImportSelector.AutoConfigurationEntry based on
     *  the AnnotationMetadata of the importing @Configuration class.
     */
    protected AutoConfigurationEntry getAutoConfigurationEntry() {
        List<String> configurations = getCandidateConfigurations();
        configurations = removeDuplicates(configurations);
        return new AutoConfigurationEntry(configurations);
    }

    protected List<String> getCandidateConfigurations() {
        ImportCandidates importCandidates = ImportCandidates.load(this.autoConfigurationAnnotation, this.getClass().getClassLoader());
        return importCandidates.getCandidates();
    }

    protected final <T> List<T> removeDuplicates(List<T> list) {
        return new ArrayList<>(new LinkedHashSet<>(list));
    }


    @Override
    public void setBeanFactory(BeanFactory beanFactory) {

    }

    @Override
    public String[] selectImports(AnnotationMetadata importingClassMetadata) {
        AutoConfigurationEntry autoConfigurationEntry = getAutoConfigurationEntry();
        return StringUtils.toStringArray(autoConfigurationEntry.getConfigurations());
    }

    @Override
    public Class<? extends Group> getImportGroup() {
        return AutoConfigurationGroup.class;
    }

    private static final class AutoConfigurationGroup implements DeferredImportSelector.Group {

        private final Map<String, AnnotationMetadata> entries = new LinkedHashMap<>();

        private final List<AutoConfigurationEntry> autoConfigurationEntries = new ArrayList<>();

        @Override
        public void process(AnnotationMetadata annotationMetadata, DeferredImportSelector selector) {
            AutoConfigurationImportSelector autoConfigurationImportSelector = (AutoConfigurationImportSelector) selector;
            AutoConfigurationEntry autoConfigurationEntry = autoConfigurationImportSelector.getAutoConfigurationEntry();
            this.autoConfigurationEntries.add(autoConfigurationEntry);
            for (String importClassName : autoConfigurationEntry.getConfigurations()) {
                this.entries.putIfAbsent(importClassName, annotationMetadata);
            }
        }

        @Override
        public Iterable<Entry> selectImports() {
            if (this.autoConfigurationEntries.isEmpty()) {
                return Collections.emptyList();
            }
            Set<String> allExclusions = this.autoConfigurationEntries.stream()
                    .map(AutoConfigurationEntry::getExclusions)
                    .flatMap(Collection::stream)
                    .collect(Collectors.toSet());
            Set<String> processedConfigurations = this.autoConfigurationEntries.stream()
                    .map(AutoConfigurationEntry::getConfigurations)
                    .flatMap(Collection::stream)
                    .collect(Collectors.toCollection(LinkedHashSet::new));
            processedConfigurations.removeAll(allExclusions);
            List<Entry> result = new ArrayList<>();
            for(String importClassName : processedConfigurations) {
                result.add(new Entry(this.entries.get(importClassName), importClassName));
            }
            return result;
        }
    }

    protected static class AutoConfigurationEntry {

        private final List<String> configurations;

        private final Set<String> exclusions;

        AutoConfigurationEntry(Collection<String> configurations) {
            this.configurations = new ArrayList<>(configurations);
            this.exclusions = Collections.emptySet();
        }

        public List<String> getConfigurations() {
            return this.configurations;
        }

        public Set<String> getExclusions() {
            return this.exclusions;
        }

    }

}
