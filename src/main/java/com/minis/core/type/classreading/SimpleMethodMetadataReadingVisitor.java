package com.minis.core.type.classreading;

import com.minis.core.annotation.MergedAnnotation;
import com.minis.core.annotation.MergedAnnotations;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

class SimpleMethodMetadataReadingVisitor extends MethodVisitor {

    private final String declaringClassName;

    private final int access;

    private final String methodName;

    private final String descriptor;

    private final List<MergedAnnotation<?>> annotations = new ArrayList<>(4);

    private final Consumer<SimpleMethodMetadata> consumer;

    private Source source;


    SimpleMethodMetadataReadingVisitor(String declaringClassName,
                                       int access, String methodName, String descriptor, Consumer<SimpleMethodMetadata> consumer) {
        super(Opcodes.ASM9);
        this.declaringClassName = declaringClassName;
        this.access = access;
        this.methodName = methodName;
        this.descriptor = descriptor;
        this.consumer = consumer;
    }


    @Override
    public AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {
        return MergedAnnotationReadingVisitor.get(getSource(), descriptor, visible, this.annotations::add);
    }

    @Override
    public void visitEnd() {
        String returnTypeName = Type.getReturnType(this.descriptor).getClassName();
        MergedAnnotations annotations = MergedAnnotations.of(this.annotations);
        SimpleMethodMetadata metadata = new SimpleMethodMetadata(this.methodName, this.access,
                this.declaringClassName, returnTypeName, getSource(), annotations);
        this.consumer.accept(metadata);
    }

    private Object getSource() {
        Source source = this.source;
        if (source == null) {
            source = new Source(this.declaringClassName, this.methodName, this.descriptor);
            this.source = source;
        }
        return source;
    }


    /**
     * {@link com.minis.core.annotation.MergedAnnotation} source.
     */
    static final class Source {

        private final String declaringClassName;

        private final String methodName;

        private final String descriptor;

        private String toStringValue;

        Source(String declaringClassName, String methodName, String descriptor) {
            this.declaringClassName = declaringClassName;
            this.methodName = methodName;
            this.descriptor = descriptor;
        }

        @Override
        public int hashCode() {
            int result = 1;
            result = 31 * result + this.declaringClassName.hashCode();
            result = 31 * result + this.methodName.hashCode();
            result = 31 * result + this.descriptor.hashCode();
            return result;
        }

        @Override
        public boolean equals(Object other) {
            if (this == other) {
                return true;
            }
            if (other == null || getClass() != other.getClass()) {
                return false;
            }
            Source otherSource = (Source) other;
            return (this.declaringClassName.equals(otherSource.declaringClassName) &&
                    this.methodName.equals(otherSource.methodName) && this.descriptor.equals(otherSource.descriptor));
        }

        @Override
        public String toString() {
            String value = this.toStringValue;
            if (value == null) {
                StringBuilder builder = new StringBuilder();
                builder.append(this.declaringClassName);
                builder.append('.');
                builder.append(this.methodName);
                Type[] argumentTypes = Type.getArgumentTypes(this.descriptor);
                builder.append('(');
                for (int i = 0; i < argumentTypes.length; i++) {
                    if (i != 0) {
                        builder.append(',');
                    }
                    builder.append(argumentTypes[i].getClassName());
                }
                builder.append(')');
                value = builder.toString();
                this.toStringValue = value;
            }
            return value;
        }
    }
}
