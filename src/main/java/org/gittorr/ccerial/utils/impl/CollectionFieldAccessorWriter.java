/*
 * Copyright 2025 GitTorr
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * For inquiries, visit https://gittorr.org
 */
package org.gittorr.ccerial.utils.impl;

import org.gittorr.ccerial.AccessorType;
import org.gittorr.ccerial.CcArray;
import org.gittorr.ccerial.CcSerializable;
import org.gittorr.ccerial.utils.CodeWriterUtils;
import org.stringtemplate.v4.ST;

import javax.lang.model.element.Element;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.*;

public class CollectionFieldAccessorWriter extends AbstractFieldAccessorWriter {

    public CollectionFieldAccessorWriter(TypeKind kind, boolean variable, String typeName, TypeMirror componentType) {
        super(kind, variable, typeName);
        String compTypeName = componentType != null ? componentType.toString() : "java.lang.Object";
    }

    @Override
    public void writeWriter(Writer out, String accessorName, Element fieldEl, CcSerializable ccSerializable, boolean isRecord, Element classElement) throws IOException {
        int variableCount = -1;
        CcArray annotation = CodeWriterUtils.getAnnotation(CcArray.class, fieldEl, accessorName, classElement);
        String charset = "UTF-8";
        TypeMirror collectionImpl = CodeWriterUtils.fromClass(Collection.class);
        boolean unmodifiable = false;
        int componentCount = -1;
        if (annotation != null) {
            variableCount = annotation.count();
            charset = annotation.stringCharsetName();
            collectionImpl = CodeWriterUtils.getTypeMirror(annotation.collectionImpl());
            unmodifiable = annotation.unmodifiableCollection();
            componentCount = annotation.componentCount();
        }
        String typeName = CodeWriterUtils.getTypeName(fieldEl.asType());

        TypeMirror componentType = CodeWriterUtils.getCollectionComponentType(fieldEl.asType());
        String writerMethodName = CodeWriterUtils.readerFor(CodeWriterUtils.getTypeName(componentType), variable, false);
        boolean isObject = writerMethodName == null;
        writerMethodName = writerMethodName != null ? writerMethodName : "writeObject";
        boolean componentIsString = componentType.toString().equals("java.lang.String");
        TypeMirror collectionClass = findSuitableImplementation(collectionImpl, fieldEl.asType(), componentType);
        String componentClass = CodeWriterUtils.isInterfaceOrAbstractClass(componentType) ? "null" : (CodeWriterUtils.getTypeName(componentType) + ".class");
        String collectionCtor = getCollectionCtor(collectionClass);
        String template = """
                        BinaryUtils.writeCollection(out,obj.<accessorName>, <variableCount>,
                        <if(isObject)>
                            (out2, v) -> BinaryUtils.<writerMethodName>(out2, v, <componentClass>, this)
                        <else>
                            <if(componentVariable)>
                                <if(componentIsString)>(out2, v) -> BinaryUtils.<writerMethodName>(out2, v, <componentCount>, "<charset>")
                                <else>BinaryUtils::<writerMethodName><endif>
                            <else>
                                (out2, v) -> BinaryUtils.<writerMethodName>(out2, v
                                <if(componentIsString)>, <componentCount>, "<charset>"<endif>)
                            <endif>
                        <endif>);
                """;
        ST st = new ST(template);
        st.add("variable", variable);
        st.add("charset", charset);
        st.add("accessorName", accessorName);
        st.add("variableCount", variableCount);
        st.add("componentIsString", componentIsString);
        st.add("componentCount", componentCount);
        st.add("componentVariable", componentCount == -1);
        st.add("componentClass", componentClass);
        st.add("unmodifiable", unmodifiable);
        st.add("typeName", typeName);
        st.add("collectionCtor", collectionCtor);
        st.add("writerMethodName", writerMethodName);
        st.add("isObject", isObject);
        out.write(st.render());
    }

    @Override
    public void writeReader(Writer out, String accessorName, Element fieldEl, CcSerializable ccSerializable, boolean isRecord, Element classElement) throws IOException {
        int variableCount = -1;
        String charset = "UTF-8";
        CcArray annotation = CodeWriterUtils.getAnnotation(CcArray.class, fieldEl, accessorName, classElement);
        String ctorArgName = toCtorArgName(accessorName, isRecord);
        String setterName = toSetterName(accessorName, isRecord);
        TypeMirror collectionImpl = CodeWriterUtils.fromClass(Collection.class);
        boolean unmodifiable = false;
        int componentCount = -1;
        if (annotation != null) {
            variableCount = annotation.count();
            charset = annotation.stringCharsetName();
            collectionImpl = CodeWriterUtils.getTypeMirror(annotation.collectionImpl());
            unmodifiable = annotation.unmodifiableCollection();
            componentCount = annotation.componentCount();
        }
        String typeName = CodeWriterUtils.getTypeName(fieldEl.asType());
        TypeMirror componentType = CodeWriterUtils.getCollectionComponentType(fieldEl.asType());
        String readerMethodName = CodeWriterUtils.readerFor(CodeWriterUtils.getTypeName(componentType), variable, true);
        boolean isObject = readerMethodName == null;
        readerMethodName = readerMethodName != null ? readerMethodName : "readObject";

        String componentClass = CodeWriterUtils.isInterfaceOrAbstractClass(componentType) ? "null" : (CodeWriterUtils.getTypeName(componentType) + ".class");
        boolean componentIsString = componentType.toString().equals("java.lang.String");
        TypeMirror collectionClass = findSuitableImplementation(collectionImpl, fieldEl.asType(), componentType);
        String collectionCtor = getCollectionCtor(collectionClass);
        String template = """
                        <if(ctor)><typeName> <ctorArgName> = <else>obj.<setterName>(<endif>
                            <if(unmodifiable)>CollectionUtils.makeUnmodifiable(<endif>
                            (<typeName>)BinaryUtils.readCollection(in, <variableCount>, 
                            <if(isObject)>
                                (in2) -> BinaryUtils.<readerMethodName>(in2, <componentClass>, this)
                            <else>
                                <if(componentVariable)><if(componentIsString)>(in2) -> BinaryUtils.<readerMethodName>(in2, <componentCount>, "<charset>")<else>BinaryUtils::<readerMethodName><endif>
                                <else>(in2) -> BinaryUtils.<readerMethodName>(in2<if(componentIsString)>, <componentCount>, "<charset>"<endif>)<endif>
                            <endif>, <collectionCtor>)
                            <if(unmodifiable)>)<endif>
                        <if(!ctor)>)<endif>;
                """;
        ST st = new ST(template);
        st.add("variable", variable);
        st.add("charset", charset);
        st.add("accessorName", accessorName);
        st.add("variableCount", variableCount);
        st.add("componentIsString", componentIsString);
        st.add("componentCount", componentCount);
        st.add("componentVariable", componentCount == -1);
        st.add("componentClass", componentClass);
        st.add("unmodifiable", unmodifiable);
        st.add("typeName", typeName);
        st.add("collectionCtor", collectionCtor);
        st.add("ctorArgName", ctorArgName);
        st.add("setterName", setterName);
        st.add("ctor", ccSerializable.accessorType() == AccessorType.CONSTRUCTOR);
        st.add("readerMethodName", readerMethodName);
        st.add("isObject", isObject);
        out.write(st.render());
    }

    private String getCollectionCtor(TypeMirror collectionType) {
        Class<?> collectionClass = CodeWriterUtils.toClass(collectionType);
        Constructor<?> constructor = null;
        try {
            constructor = collectionClass.getDeclaredConstructor(int.class);
        } catch (NoSuchMethodException e) {
        }
        if (constructor == null || !Modifier.isPublic(constructor.getModifiers())) {
            try {
                constructor = collectionClass.getDeclaredConstructor();
            } catch (NoSuchMethodException e2) {
                throw new IllegalStateException("There's no default constructor for class " + collectionClass.getName());
            }
            if (!Modifier.isPublic(constructor.getModifiers())) {
                throw new IllegalStateException("There's no public default constructor for class " + collectionClass.getName());
            }
        }
        if (constructor.getParameterCount() == 1) {
            return constructor.getName() + "::new";
        } else {
            return "(_i) -> " + constructor.getName() + "()";
        }
    }

    private TypeMirror findSuitableImplementation(TypeMirror collectionImpl, TypeMirror fieldType, TypeMirror componentType) {
        if (CodeWriterUtils.typeEquals(collectionImpl, Collection.class)) {
            collectionImpl = fieldType;
        }
        if (CodeWriterUtils.isInterfaceOrAbstractClass(collectionImpl)) {
            if (CodeWriterUtils.typeEquals(collectionImpl, List.class) || CodeWriterUtils.typeEquals(collectionImpl, AbstractList.class)) {
                return CodeWriterUtils.fromClass(ArrayList.class, componentType);
            } else if (CodeWriterUtils.typeEquals(collectionImpl, Set.class) || CodeWriterUtils.typeEquals(collectionImpl, AbstractSet.class)) {
                return CodeWriterUtils.fromClass(HashSet.class, componentType);
            } else if (CodeWriterUtils.typeEquals(collectionImpl, SortedSet.class) || CodeWriterUtils.typeEquals(collectionImpl, NavigableSet.class)) {
                return CodeWriterUtils.fromClass(TreeSet.class, componentType);
            } else if (CodeWriterUtils.typeEquals(collectionImpl, Queue.class)) {
                return CodeWriterUtils.fromClass(LinkedList.class, componentType);
            } else if (CodeWriterUtils.typeEquals(collectionImpl, Deque.class)) {
                return CodeWriterUtils.fromClass(ArrayDeque.class, componentType);
            } else if (CodeWriterUtils.typeEquals(collectionImpl, Collection.class)) {
                return CodeWriterUtils.fromClass(ArrayList.class, componentType);
            } else {
                throw new IllegalArgumentException("Can't find a suitable implementation for " + CodeWriterUtils.getTypeName(collectionImpl));
            }
        } else if (CodeWriterUtils.isAnonymousClass(collectionImpl)) {
            throw new IllegalArgumentException("The collection implementation cannot be an anonymous class");
        } else {
            return collectionImpl;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CollectionFieldAccessorWriter that)) return false;

        if (variable != that.variable) return false;
        return kind == that.kind && typeName.equals(that.typeName);
    }
}
