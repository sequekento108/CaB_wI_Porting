package mod.chiselsandbits.fabric.plugin.asm;

import org.objectweb.asm.Type;

import java.lang.annotation.ElementType;
import java.util.Map;

public record AnnotationData(Type annotationType, ElementType targetType, Type clazz, String memberName, Map<String, Object> annotationData) {}