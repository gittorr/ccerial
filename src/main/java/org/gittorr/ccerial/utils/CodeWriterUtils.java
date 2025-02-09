package org.gittorr.ccerial.utils;

import javax.lang.model.element.*;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.PrimitiveType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public final class CodeWriterUtils {

    static final Pattern COLLECTION_PATTERN = Pattern.compile("^(\\w[0-9a-z_.])<(\\w[0-9a-z_.])>$");
    static final Pattern MAP_PATTERN = Pattern.compile("^(\\w[0-9a-z_.])<(\\w[0-9a-z_.]),(\\w[0-9a-z_.])>$");
    static Types typeUtils;
    static Elements elementUtils;

    public static void setTypeUtils(Types typeUtils) {
        CodeWriterUtils.typeUtils = typeUtils;
    }
    public static void setElementUtils(Elements elementUtils) {
        CodeWriterUtils.elementUtils = elementUtils;
    }

    public static String getTypeName(TypeMirror type) {
        String typeString = type.toString();
        int annotationStart = typeString.lastIndexOf('@');
        if (annotationStart != -1) {
            int spcIdx = typeString.indexOf(' ', annotationStart);
            return typeString.substring(spcIdx + 1);
        }
        return typeString;
    }

    public static TypeMirror getTypeMirror(String type) {
        return elementUtils.getTypeElement(type).asType();
    }

    public static TypeMirror getCollectionComponentType(TypeMirror type) {
        DeclaredType dtype = (DeclaredType) type;
        return dtype.getTypeArguments().get(0);
    }

    public static TypeMirror getTypeArgs(TypeMirror type, int argi) {
        DeclaredType dtype = (DeclaredType) type;
        return dtype.getTypeArguments().get(argi);
    }

    public static boolean isWrapperType(TypeMirror typeMirror) {
        if (typeMirror.getKind() != TypeKind.DECLARED) {
            return false;
        }
        try {
            return typeMirror.toString().equals("java.lang.String") || typeUtils.unboxedType(typeMirror) != null;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    public static TypeMirror wrapperFor(TypeMirror typeMirror) {
        if (typeMirror.getKind().isPrimitive()) {
            return typeUtils.boxedClass((PrimitiveType)typeMirror).asType();
        }
        return typeMirror;
    }

    public static String readerFor(String typeName, boolean variable, boolean reader) {
        return readerFor(typeName, variable, false, reader);
    }

    public static String readerFor(String typeName, boolean variable, boolean componentVariable, boolean reader) {
        String prefix = reader ? "read" : "write";
        switch (typeName) {
            case "byte":
            case "java.lang.Byte":
                return prefix + "Byte";
            case "short":
            case "java.lang.Short":
                return prefix + "Short";
            case "char":
            case "java.lang.Character":
                return prefix + "Char";
            case "boolean":
            case "java.lang.Boolean":
                return prefix + "Boolean";
            case "int":
            case "java.lang.Integer":
                return variable ? prefix + "VarInt" : prefix + "Int";
            case "long":
            case "java.lang.Long":
                return variable ? prefix + "VarLong" : prefix + "Long";
            case "float":
            case "java.lang.Float":
                return variable ? prefix + "VarFloat" : prefix + "Float";
            case "double":
            case "java.lang.Double":
                return variable ? prefix + "VarDouble" : prefix + "Double";
            case "java.lang.String":
                return prefix + "String";
            case "byte[]":
                return prefix + "Bytes";
            case "short[]":
                return prefix + "Shorts";
            case "char[]":
                return prefix + "Chars";
            case "boolean[]":
                return prefix + "Booleans";
            case "int[]":
                return prefix + (componentVariable ? "Var" : "") + "Ints";
            case "long[]":
                return prefix + (componentVariable ? "Var" : "") + "Longs";
            case "float[]":
                return prefix + (componentVariable ? "Var" : "") + "Floats";
            case "double[]":
                return prefix + (componentVariable ? "Var" : "") + "Doubles";
        }
        return null;
    }

    public static String getPrimitiveTypeName(TypeKind typeKind) {
        switch (typeKind) {
            case BOOLEAN:
                return "boolean";
            case BYTE:
                return "byte";
            case SHORT:
                return "short";
            case INT:
                return "int";
            case LONG:
                return "long";
            case CHAR:
                return "char";
            case FLOAT:
                return "float";
            case DOUBLE:
                return "double";
            default:
                return null;
        }
    }

    public static boolean isCollection(TypeMirror typeMirror) {
        TypeMirror collectionType = elementUtils.getTypeElement(Collection.class.getCanonicalName()).asType();
        return typeUtils.isAssignable(typeUtils.erasure(typeMirror), collectionType);
    }

    public static boolean isMap(TypeMirror typeMirror) {
        TypeMirror collectionType = elementUtils.getTypeElement(Map.class.getCanonicalName()).asType();
        return typeUtils.isAssignable(typeUtils.erasure(typeMirror), collectionType);
    }

    public static boolean isString(TypeMirror typeMirror) {
        TypeMirror stringType = elementUtils.getTypeElement(String.class.getCanonicalName()).asType();
        return typeUtils.isAssignable(typeMirror, stringType);
    }

    public static boolean isEnum(TypeMirror typeMirror) {
        Element element = elementUtils.getTypeElement(typeMirror.toString());
        return element != null && element.getKind() == ElementKind.ENUM;
    }

    public static boolean typeEquals(TypeMirror typeMirror, Class<?> otherClass) {
        TypeMirror otherType = elementUtils.getTypeElement(otherClass.getCanonicalName()).asType();
        return typeUtils.isSameType(typeUtils.erasure(typeMirror), typeUtils.erasure(otherType));
    }

    public static boolean typeEquals(TypeMirror typeMirror, TypeMirror otherType) {
        return typeUtils.isSameType(typeMirror, otherType);
    }

    public static boolean isInterfaceOrAbstractClass(TypeMirror typeMirror) {
        if (!(typeMirror instanceof DeclaredType)) {
            return false;
        }

        Element element = ((DeclaredType) typeMirror).asElement();

        if (element instanceof TypeElement) {
            TypeElement typeElement = (TypeElement) element;

            if (typeElement.getKind().isInterface()) {
                return true;
            }

            return typeElement.getModifiers().contains(Modifier.ABSTRACT);
        }
        return false;
    }

    public static boolean isAnonymousClass(TypeMirror typeMirror) {
        if (!(typeMirror instanceof DeclaredType)) {
            return false;
        }

        Element element = ((DeclaredType) typeMirror).asElement();

        if (element.getKind() == ElementKind.CLASS) {
            Name name = element.getSimpleName();
            return name.toString().isEmpty();  // Classes anônimas não têm nome simples
        }

        return false;
    }

    public static TypeMirror fromClass(Class<?> clazz, TypeMirror... arguments) {
        var typeElement = elementUtils.getTypeElement(clazz.getCanonicalName());
        if (typeElement == null) {
            throw new IllegalArgumentException("Class not found: " + clazz.getCanonicalName());
        }

        return typeUtils.getDeclaredType(typeElement, arguments);
    }

    public static Class<?> toClass(TypeMirror typeMirror) {
        if (!(typeMirror instanceof DeclaredType)) {
            throw new IllegalArgumentException("The provided TypeMirror is not a DeclaredType");
        }

        Element element = ((DeclaredType) typeMirror).asElement();

        if (element instanceof TypeElement) {
            String qualifiedName = ((TypeElement) element).getQualifiedName().toString();
            try {
                return Class.forName(qualifiedName);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException("Could not load class: " + qualifiedName, e);
            }
        }
        throw new IllegalArgumentException("Could not get class from TypeMirror.");
    }

    public static <A extends Annotation> A getAnnotation(Class<A> annotClass, Element fieldEl, String accessorName, Element classElement) {
        A annotation = fieldEl.getAnnotation(annotClass);
        if (annotation == null) {
            Map<String, ? extends Element> methods = classElement.getEnclosedElements().stream().filter(field -> field.getKind().equals(ElementKind.METHOD)
                    && !field.getModifiers().contains(Modifier.STATIC)).collect(Collectors.toMap(Object::toString, field -> field));
            Element element = methods.get(accessorName);
            annotation = element.getAnnotation(annotClass);
        }
        return annotation;
    }

    public static boolean isArray(TypeMirror type) {
        return type.getKind() == TypeKind.ARRAY;
    }
}
