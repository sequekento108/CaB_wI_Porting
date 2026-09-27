package mod.chiselsandbits.fabric.plugin.asm;

import org.objectweb.asm.AnnotationVisitor;

import java.util.LinkedList;

public class ModAnnotationVisitor extends AnnotationVisitor {
    private final ModAnnotation annotation;
    private LinkedList<ModAnnotation> annotations;
    private boolean array;
    private String name;
    private boolean isSubAnnotation;

    public ModAnnotationVisitor(LinkedList<ModAnnotation> annotations, ModAnnotation annotation) {
        super(589824);
        this.annotations = annotations;
        this.annotation = annotation;
    }

    public ModAnnotationVisitor(LinkedList<ModAnnotation> annotations, ModAnnotation annotation, String name) {
        this(annotations, annotation);
        this.array = true;
        this.name = name;
        annotation.addArray(name);
    }

    public ModAnnotationVisitor(LinkedList<ModAnnotation> annotations, ModAnnotation annotation, boolean isSubAnnotation) {
        this(annotations, annotation);
        this.isSubAnnotation = true;
    }

    public void visit(String key, Object value) {
        this.annotation.addProperty(key, value);
    }

    public void visitEnum(String name, String desc, String value) {
        this.annotation.addEnumProperty(name, desc, value);
    }

    public AnnotationVisitor visitArray(String name) {
        return new ModAnnotationVisitor(this.annotations, this.annotation, name);
    }

    public AnnotationVisitor visitAnnotation(String name, String desc) {
        ModAnnotation ma = (ModAnnotation)this.annotations.getFirst();
        ModAnnotation childAnnotation = ma.addChildAnnotation(name, desc);
        this.annotations.addFirst(childAnnotation);
        return new ModAnnotationVisitor(this.annotations, childAnnotation, true);
    }

    public void visitEnd() {
        if (this.array) {
            this.annotation.endArray();
        }

        if (this.isSubAnnotation) {
            ModAnnotation child = (ModAnnotation)this.annotations.removeFirst();
            this.annotations.addLast(child);
        }

    }
}