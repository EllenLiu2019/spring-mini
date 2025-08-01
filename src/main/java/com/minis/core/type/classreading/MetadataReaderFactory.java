package com.minis.core.type.classreading;

import com.minis.core.io.Resource;

import java.io.IOException;

public interface MetadataReaderFactory {

    MetadataReader getMetadataReader(Resource resource) throws IOException;

    MetadataReader getMetadataReader(String className) throws IOException;
}
