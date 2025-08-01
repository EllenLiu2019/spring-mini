package com.minis.core.type.classreading;

import com.minis.core.io.Resource;
import com.minis.core.type.AnnotationMetadata;
import com.minis.core.type.ClassMetadata;
import org.objectweb.asm.ClassReader;


import java.io.IOException;
import java.io.InputStream;

public class SimpleMetadataReader implements MetadataReader {

    private static final int PARSING_OPTIONS =
            (ClassReader.SKIP_DEBUG | ClassReader.SKIP_CODE | ClassReader.SKIP_FRAMES);

    private final Resource resource;

    private final AnnotationMetadata annotationMetadata;

    SimpleMetadataReader(Resource resource) throws IOException {
        SimpleAnnotationMetadataReadingVisitor visitor = new SimpleAnnotationMetadataReadingVisitor();
        getClassReader(resource).accept(visitor, PARSING_OPTIONS);
        this.resource = resource;
        this.annotationMetadata = visitor.getMetadata();
    }

    private static ClassReader getClassReader(Resource resource) throws IOException {
        try (InputStream is = resource.getInputStream()) {
            try {
                return new ClassReader(is); // read bytecode
            }
            catch (IllegalArgumentException ex) {
                throw new IOException("ASM ClassReader failed to parse class file - " +
                        "probably due to a new Java class file version that is not supported yet. " +
                        "Consider compiling with a lower '-target' or upgrade your framework version. " +
                        "Affected class: " + resource, ex);
            }
        }
    }

    @Override
    public Resource getResource() {
        return this.resource;
    }

    @Override
    public ClassMetadata getClassMetadata() {
        return this.annotationMetadata;
    }

    @Override
    public AnnotationMetadata getAnnotationMetadata() {
        return this.annotationMetadata;
    }
}
