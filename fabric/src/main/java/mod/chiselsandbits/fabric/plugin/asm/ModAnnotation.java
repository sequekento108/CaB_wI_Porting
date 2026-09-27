package mod.chiselsandbits.fabric.plugin.asm;


import com.google.common.base.MoreObjects;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.objectweb.asm.Type;

import java.lang.annotation.ElementType;
import java.util.ArrayList;
import java.util.Map;

public class ModAnnotation {
    private final ElementType type;
    private final Type asmType;
    private final String member;
    private final Map<String, Object> values = Maps.newHashMap();
    private ArrayList<Object> arrayList;
    private String arrayName;

    public static AnnotationData fromModAnnotation(Type clazz, ModAnnotation annotation) {
        return new AnnotationData(annotation.asmType, annotation.type, clazz, annotation.member, annotation.values);
    }

    public ModAnnotation(ElementType type, Type asmType, String member) {
        this.type = type;
        this.asmType = asmType;
        this.member = member;
    }

    public ModAnnotation(Type asmType, ModAnnotation parent) {
        this.type = parent.type;
        this.asmType = asmType;
        this.member = parent.member;
    }

    public String toString() {
        return MoreObjects.toStringHelper("Annotation").add("type", this.type).add("name", this.asmType.getClassName()).add("member", this.member).add("values", this.values).toString();
    }

    public ElementType getType() {
        return this.type;
    }

    public Type getASMType() {
        return this.asmType;
    }

    public String getMember() {
        return this.member;
    }

    public Map<String, Object> getValues() {
        return this.values;
    }

    public void addArray(String name) {
        this.arrayList = Lists.newArrayList();
        this.arrayName = name;
    }

    public void addProperty(String key, Object value) {
        if (this.arrayList != null) {
            this.arrayList.add(value);
        } else {
            this.values.put(key, value);
        }

    }

    public void addEnumProperty(String key, String enumName, String value) {
        this.addProperty(key, new EnumHolder(enumName, value));
    }

    public void endArray() {
        this.values.put(this.arrayName, this.arrayList);
        this.arrayList = null;
    }

    public ModAnnotation addChildAnnotation(String name, String desc) {
        ModAnnotation child = new ModAnnotation(Type.getType(desc), this);
        this.addProperty(name, child.getValues());
        return child;
    }

    public static class EnumHolder {
        private final String desc;
        private final String value;

        public EnumHolder(String desc, String value) {
            this.desc = desc;
            this.value = value;
        }

        public String getDesc() {
            return this.desc;
        }

        public String getValue() {
            return this.value;
        }
    }
}
