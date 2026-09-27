package mod.chiselsandbits.fabric.plugin.asm;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import java.lang.annotation.ElementType;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ModClassVisitor extends ClassVisitor
{
    private Type asmType;
    private final LinkedList<ModAnnotation> annotations = new LinkedList<>();

    public ModClassVisitor()
    {
        super(Opcodes.ASM9);
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces)
    {
        this.asmType = Type.getObjectType(name);
    }

    @Override
    public AnnotationVisitor visitAnnotation(final String annotationName, final boolean runtimeVisible)
    {
        ModAnnotation ann = new ModAnnotation(ElementType.TYPE, Type.getType(annotationName), this.asmType.getClassName());
        annotations.addFirst(ann);
        return new ModAnnotationVisitor(annotations, ann);
    }

    public Set<AnnotationData> annotationData()
    {
        return annotations.stream().map(a -> ModAnnotation.fromModAnnotation(this.asmType, a)).collect(Collectors.toSet());
    }
}

