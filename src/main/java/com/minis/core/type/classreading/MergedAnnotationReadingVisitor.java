package com.minis.core.type.classreading;

import com.minis.core.annotation.MergedAnnotation;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Consumer;

class MergedAnnotationReadingVisitor<A extends Annotation> extends AnnotationVisitor {

    private final Object source;

    private final Class<A> annotationType;

    private final Consumer<MergedAnnotation<A>> consumer;

    private final Map<String, Object> attributes = new LinkedHashMap<>(4);

    protected MergedAnnotationReadingVisitor(Object source,
                                             Class<A> annotationType, Consumer<MergedAnnotation<A>> consumer) {
        super(Opcodes.ASM9);
        this.source = source;
        this.annotationType = annotationType;
        this.consumer = consumer;
    }

    static <A extends Annotation> AnnotationVisitor get(Object source, String descriptor, boolean visible,
                                                        Consumer<MergedAnnotation<A>> consumer) {
        if (!visible) {
            return null;
        }

        String typeName = Type.getType(descriptor).getClassName();
        try {
            Class<A> annotationType = (Class<A>) Class.forName(typeName);
            return new MergedAnnotationReadingVisitor<>(source, annotationType, consumer);
        } catch (ClassNotFoundException | LinkageError ex) {
            return null;
        }
    }

    @Override
    public void visit(String name, Object value) {
        if (value instanceof Type type) {
            value = type.getClassName();
        }
        this.attributes.put(name, value);
    }

    @Override
    public void visitEnd() {
        Map<String, Object> compactedAttributes = (this.attributes.isEmpty() ? Collections.emptyMap() : this.attributes);
        MergedAnnotation<A> annotation = MergedAnnotation.of(this.source, this.annotationType, compactedAttributes);
        this.consumer.accept(annotation);
    }
}
