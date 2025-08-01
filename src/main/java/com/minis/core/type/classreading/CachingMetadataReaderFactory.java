package com.minis.core.type.classreading;

import com.minis.core.io.Resource;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CachingMetadataReaderFactory extends SimpleMetadataReaderFactory {

    private final Map<String, MetadataReader> classNameCache;
    private final Map<Resource, MetadataReader> metadataReaderCache;

    public CachingMetadataReaderFactory() {
        super();
        this.classNameCache = new ConcurrentHashMap<>();
        this.metadataReaderCache = new ConcurrentHashMap<>();
    }

    @Override
    public MetadataReader getMetadataReader(Resource resource) throws IOException {
        MetadataReader metadataReader = this.metadataReaderCache.get(resource);
        if (metadataReader == null) {
            metadataReader = super.getMetadataReader(resource);
            this.metadataReaderCache.put(resource, metadataReader);
            this.classNameCache.put(metadataReader.getClassMetadata().getClassName(), metadataReader);
        }
        return metadataReader;
    }

    @Override
    public MetadataReader getMetadataReader(String className) throws IOException {
        MetadataReader metadataReader = this.classNameCache.get(className);
        if (metadataReader == null) {
            metadataReader = super.getMetadataReader(className);
            this.classNameCache.put(className, metadataReader);
        }
        return metadataReader;
    }
}
