package com.minis.core.type.classreading;

import com.minis.core.annotation.MergedAnnotation;
import com.minis.core.annotation.MergedAnnotations;
import com.minis.core.type.MethodMetadata;
import com.minis.utils.ClassUtils;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import java.util.LinkedHashSet;
import java.util.Set;

public class SimpleAnnotationMetadataReadingVisitor extends ClassVisitor {

    public static final int ASM_VERSION = Opcodes.ASM9;

    private String className = "";

    private int access;

    private String superClassName;

    private String enclosingClassName;

    private boolean independentInnerClass;

    private final Set<String> interfaceNames = new LinkedHashSet<>(4);

    private final Set<String> memberClassNames = new LinkedHashSet<>(4);

    private final Set<MergedAnnotation<?>> annotations = new LinkedHashSet<>(4);

    private final Set<MethodMetadata> declaredMethods = new LinkedHashSet<>(4);

    private SimpleAnnotationMetadata metadata;

    private Source source;

    public SimpleAnnotationMetadataReadingVisitor() {
        super(ASM_VERSION);
    }

    @Override
    public void visit(int version, int access, String name, String signature,
                      String supername, String[] interfaces) {

        this.className = toClassName(name);
        this.access = access;
        if (supername != null && !isInterface(access)) {
            this.superClassName = toClassName(supername);
        }
        for (String element : interfaces) {
            this.interfaceNames.add(toClassName(element));
        }
    }

    @Override
    public AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {
        return MergedAnnotationReadingVisitor.get(getSource(), descriptor, visible, this.annotations::add);
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
        // Skip bridge methods and constructors - we're only interested in original user methods.
        if (isBridge(access) || name.equals("<init>")) {
            return null;
        }
        return new SimpleMethodMetadataReadingVisitor(this.className, access, name, descriptor, this.declaredMethods::add);
    }

    @Override
    public void visitEnd() {
        MergedAnnotations annotations = MergedAnnotations.of(this.annotations);
        this.metadata = new SimpleAnnotationMetadata(this.className, this.access,
                this.enclosingClassName, this.superClassName, this.independentInnerClass,
                this.interfaceNames, this.memberClassNames, this.declaredMethods, annotations);
    }

    public SimpleAnnotationMetadata getMetadata() {
        return this.metadata;
    }

    private String toClassName(String name) {
        return ClassUtils.convertResourcePathToClassName(name);
    }

    private boolean isInterface(int access) {
        return (access & Opcodes.ACC_INTERFACE) != 0;
    }

    private boolean isBridge(int access) {
        return (access & Opcodes.ACC_BRIDGE) != 0;
    }

    private Source getSource() {
        Source source = this.source;
        if (source == null) {
            source = new Source(this.className);
            this.source = source;
        }
        return source;
    }

    private static final class Source {

        private final String className;

        Source(String className) {
            this.className = className;
        }

        @Override
        public boolean equals(Object other) {
            return (this == other || (other instanceof Source that && this.className.equals(that.className)));
        }

        @Override
        public int hashCode() {
            return this.className.hashCode();
        }

        @Override
        public String toString() {
            return this.className;
        }
    }
}
