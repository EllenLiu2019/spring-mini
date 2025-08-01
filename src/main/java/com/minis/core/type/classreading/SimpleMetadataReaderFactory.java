package com.minis.core.type.classreading;

import com.minis.core.io.FileSystemResource;
import com.minis.core.io.Resource;
import com.minis.utils.ClassUtils;
import lombok.extern.slf4j.Slf4j;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.Map;

@Slf4j
public class SimpleMetadataReaderFactory implements MetadataReaderFactory {

    @Override
    public MetadataReader getMetadataReader(Resource resource) throws IOException {
        return new SimpleMetadataReader(resource);
    }

    @Override
    public MetadataReader getMetadataReader(String className) throws IOException {
        ClassLoader classLoader = ClassUtils.getDefaultClassLoader();
        String resourcePath = ClassUtils.convertClassNameToResourcePath(className) + ".class";
        URL url = classLoader.getResource(resourcePath);
        if (url == null) {
            throw new FileNotFoundException("Resource [" + resourcePath + "] cannot be resolved to absolute file path " +
                    "because it does not exist");
        }
        Resource resource = new FileSystemResource(url.getFile());
        return getMetadataReader(resource);
    }
}
