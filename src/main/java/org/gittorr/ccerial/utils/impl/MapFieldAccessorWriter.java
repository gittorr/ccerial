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
import org.gittorr.ccerial.CcMap;
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
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentNavigableMap;
import java.util.concurrent.ConcurrentSkipListMap;

public class MapFieldAccessorWriter extends AbstractFieldAccessorWriter {

    public MapFieldAccessorWriter(TypeKind kind, boolean variable, String typeName) {
        super(kind, variable, typeName);
    }

    @Override
    public void writeWriter(Writer out, String accessorName, Element fieldEl, CcSerializable ccSerializable, boolean isRecord, Element classElement) throws IOException {
        int variableCount = -1;
        int keyCount = -1;
        int valueCount = -1;
        CcMap annotation = CodeWriterUtils.getAnnotation(CcMap.class, fieldEl, accessorName, classElement);
        String charset = "UTF-8";
        TypeMirror mapImpl = CodeWriterUtils.fromClass(Map.class);
        boolean unmodifiable = false;
        if (annotation != null) {
            variableCount = annotation.count();
            keyCount = annotation.keyCount();
            valueCount = annotation.valueCount();
            charset = annotation.stringCharsetName();
            mapImpl = CodeWriterUtils.getTypeMirror(annotation.mapImpl());
            unmodifiable = annotation.unmodifiableMap();
        }
        String typeName = CodeWriterUtils.getTypeName(fieldEl.asType());

        TypeMirror keyType = CodeWriterUtils.getTypeArgs(fieldEl.asType(), 0);
        TypeMirror valueType = CodeWriterUtils.getTypeArgs(fieldEl.asType(), 1);
        String keyWriterMethodName = CodeWriterUtils.readerFor(CodeWriterUtils.getTypeName(keyType), keyCount == -1, false);
        String keyWriterMethodNameVar = CodeWriterUtils.readerFor(CodeWriterUtils.getTypeName(keyType), true, false);
        String valueWriterMethodName = CodeWriterUtils.readerFor(CodeWriterUtils.getTypeName(valueType), valueCount == -1, false);
        String valueWriterMethodNameVar = CodeWriterUtils.readerFor(CodeWriterUtils.getTypeName(valueType), true, false);
        boolean keyIsObject = keyWriterMethodName == null;
        keyWriterMethodName = keyWriterMethodName != null ? keyWriterMethodName : "writeObject";
        boolean valueIsObject = valueWriterMethodName == null;
        valueWriterMethodName = valueWriterMethodName != null ? valueWriterMethodName : "writeObject";
        boolean keyIsString = keyType.toString().equals("java.lang.String");
        boolean valueIsString = valueType.toString().equals("java.lang.String");
        TypeMirror mapClass = findSuitableImplementation(mapImpl, fieldEl.asType(), keyType, valueType);
        String keyClass = CodeWriterUtils.isInterfaceOrAbstractClass(keyType) ? "null" : (CodeWriterUtils.getTypeName(keyType) + ".class");
        String valueClass = CodeWriterUtils.isInterfaceOrAbstractClass(valueType) ? "null" : (CodeWriterUtils.getTypeName(valueType) + ".class");
        String mapCtor = getCollectionCtor(mapClass);
        String template = """
                        BinaryUtils.writeMap(out, obj.<accessorName>, <if(!variable)>featureForceVariableSize ? -1 : <endif><variableCount>,
                        <if(keyIsObject)>
                            (out2, v) -> BinaryUtils.<keyWriterMethodName>(out2, v, <keyClass>, this)
                        <else>
                            <if(keyVariable)>
                                <if(keyIsString)>(out2, v) -> BinaryUtils.<keyWriterMethodName>(out2, v, <keyCount>, "<charset>")
                                <else>BinaryUtils::<keyWriterMethodName><endif>
                            <else>
                                (out2, v) ->
                                    <if(keyIsString)>BinaryUtils.<keyWriterMethodName>(out2, v, <keyCount>, "<charset>")
                                    <else>writeWithFeature(BinaryUtils::<keyWriterMethodNameVar>, BinaryUtils::<keyWriterMethodName>,out2, v)
                                    <endif>
                            <endif>
                        <endif>,
                        <if(valueIsObject)>
                            (out2, v) -> BinaryUtils.<valueWriterMethodName>(out2, v, <valueClass>, this)
                        <else>
                            <if(valueVariable)>
                                <if(valueIsString)>(out2, v) -> BinaryUtils.<valueWriterMethodName>(out2, v, <valueCount>, "<charset>")
                                <else>BinaryUtils::<valueWriterMethodName><endif>
                            <else>
                                (out2, v) ->
                                    <if(valueIsString)>BinaryUtils.<valueWriterMethodName>(out2, v, <valueCount>, "<charset>")
                                    <else>writeWithFeature(BinaryUtils::<valueWriterMethodNameVar>, BinaryUtils::<valueWriterMethodName>,out2, v)
                                    <endif>
                            <endif>
                        <endif>);
                """;
        ST st = new ST(template);
        st.add("variable", variable);
        st.add("charset", charset);
        st.add("accessorName", accessorName);
        st.add("keyCount", keyCount);
        st.add("valueCount", valueCount);
        st.add("keyClass", keyClass);
        st.add("valueClass", valueClass);
        st.add("keyVariable", keyCount == -1);
        st.add("valueVariable", valueCount == -1);
        st.add("keyIsString", keyIsString);
        st.add("valueIsString", valueIsString);
        st.add("variableCount", variableCount);
        st.add("unmodifiable", unmodifiable);
        st.add("typeName", typeName);
        st.add("mapCtor", mapCtor);
        st.add("keyWriterMethodName", keyWriterMethodName);
        st.add("keyWriterMethodNameVar", keyWriterMethodNameVar);
        st.add("valueWriterMethodName", valueWriterMethodName);
        st.add("valueWriterMethodNameVar", valueWriterMethodNameVar);
        st.add("keyIsObject", keyIsObject);
        st.add("valueIsObject", valueIsObject);
        out.write(st.render());
    }

    @Override
    public void writeReader(Writer out, String accessorName, Element fieldEl, CcSerializable ccSerializable, boolean isRecord, Element classElement) throws IOException {
        int variableCount = -1;
        int keyCount = -1;
        int valueCount = -1;
        boolean nullIsEmpty = ccSerializable.nullIsZeroOrEmpty();
        CcMap annotation = CodeWriterUtils.getAnnotation(CcMap.class, fieldEl, accessorName, classElement);
        String ctorArgName = toCtorArgName(accessorName, isRecord);
        String setterName = toSetterName(accessorName, isRecord);
        String charset = "UTF-8";
        TypeMirror mapImpl = CodeWriterUtils.fromClass(Map.class);
        boolean unmodifiable = false;
        if (annotation != null) {
            nullIsEmpty = annotation.nullIsEmpty();
            variableCount = annotation.count();
            keyCount = annotation.keyCount();
            valueCount = annotation.valueCount();
            charset = annotation.stringCharsetName();
            mapImpl = CodeWriterUtils.getTypeMirror(annotation.mapImpl());
            unmodifiable = annotation.unmodifiableMap();
        }
        String typeName = CodeWriterUtils.getTypeName(fieldEl.asType());

        TypeMirror keyType = CodeWriterUtils.getTypeArgs(fieldEl.asType(), 0);
        TypeMirror valueType = CodeWriterUtils.getTypeArgs(fieldEl.asType(), 1);
        String keyReaderMethodName = CodeWriterUtils.readerFor(CodeWriterUtils.getTypeName(keyType), keyCount == -1, true);
        String keyReaderMethodNameVar = CodeWriterUtils.readerFor(CodeWriterUtils.getTypeName(keyType), true, true);
        String valueReaderMethodName = CodeWriterUtils.readerFor(CodeWriterUtils.getTypeName(valueType), valueCount == -1, true);
        String valueReaderMethodNameVar = CodeWriterUtils.readerFor(CodeWriterUtils.getTypeName(valueType), true, true);
        boolean keyIsObject = keyReaderMethodName == null;
        keyReaderMethodName = keyReaderMethodName != null ? keyReaderMethodName : "writeObject";
        boolean valueIsObject = valueReaderMethodName == null;
        valueReaderMethodName = valueReaderMethodName != null ? valueReaderMethodName : "writeObject";
        boolean keyIsString = keyType.toString().equals("java.lang.String");
        boolean valueIsString = valueType.toString().equals("java.lang.String");
        TypeMirror mapClass = findSuitableImplementation(mapImpl, fieldEl.asType(), keyType, valueType);
        String keyClass = CodeWriterUtils.isInterfaceOrAbstractClass(keyType) ? "null" : (CodeWriterUtils.getTypeName(keyType) + ".class");
        String valueClass = CodeWriterUtils.isInterfaceOrAbstractClass(valueType) ? "null" : (CodeWriterUtils.getTypeName(valueType) + ".class");
        String mapCtor = getCollectionCtor(mapClass);
        String template = """
                        <if(ctor)><typeName> <ctorArgName> = <else>obj.<setterName>(<endif>
                            <if(unmodifiable)>CollectionUtils.makeUnmodifiable(<endif>
                            <if(variable)>nullIfEmptyOrZero(<endif>
                            (<typeName>)BinaryUtils.readMap(in, <if(!variable)>featureForceVariableSize ? -1 : <endif><variableCount>,
                            <if(keyIsObject)>
                                (in2) -> BinaryUtils.<keyReaderMethodName>(in2, <kClass>, this)
                            <else>
                                <if(keyVariable)>
                                    <if(keyIsString)>(in2) -> BinaryUtils.<keyReaderMethodName>(in2, <keyCount>, "<charset>")
                                    <else>BinaryUtils::<keyReaderMethodName><endif>
                                <else>
                                    (in2) ->
                                        <if(keyIsString)>BinaryUtils.<keyReaderMethodName>(in2, featureForceVariableSize ? -1 : <keyCount>, "<charset>")
                                        <else>featureForceVariableSize ? BinaryUtils.<keyReaderMethodNameVar>(in2) : BinaryUtils.<keyReaderMethodName>(in2)<endif>
                                <endif>
                            <endif>,
                            <if(valueIsObject)>
                                (in2) -> BinaryUtils.<valueReaderMethodName>(in2, <valueClass>, this)
                            <else>
                                <if(valueVariable)>
                                    <if(valueIsString)>(in2) -> BinaryUtils.<valueReaderMethodName>(in2, <valueCount>, "<charset>")
                                    <else>BinaryUtils::<valueReaderMethodName><endif>
                                <else>
                                    (in2) ->
                                        <if(valueIsString)>BinaryUtils.<valueReaderMethodName>(in2, featureForceVariableSize ? -1 : <valueCount>, "<charset>")
                                        <else>featureForceVariableSize ? BinaryUtils.<valueReaderMethodNameVar>(in2) : BinaryUtils.<valueReaderMethodName>(in2)<endif>
                                <endif>
                            <endif>,
                            <mapCtor>)<if(variable)>, <nullIsEmpty>)<endif>
                            <if(unmodifiable)>)<endif>
                        <if(!ctor)>)<endif>;
                """;
        ST st = new ST(template);
        st.add("variable", variable);
        st.add("nullIsEmpty", nullIsEmpty);
        st.add("charset", charset);
        st.add("accessorName", accessorName);
        st.add("keyCount", keyCount);
        st.add("valueCount", valueCount);
        st.add("keyClass", keyClass);
        st.add("valueClass", valueClass);
        st.add("keyVariable", keyCount == -1);
        st.add("valueVariable", valueCount == -1);
        st.add("keyIsString", keyIsString);
        st.add("valueIsString", valueIsString);
        st.add("variableCount", variableCount);
        st.add("unmodifiable", unmodifiable);
        st.add("typeName", typeName);
        st.add("mapCtor", mapCtor);
        st.add("ctorArgName", ctorArgName);
        st.add("setterName", setterName);
        st.add("ctor", ccSerializable.accessorType() == AccessorType.CONSTRUCTOR);
        st.add("keyReaderMethodName", keyReaderMethodName);
        st.add("keyReaderMethodNameVar", keyReaderMethodNameVar);
        st.add("valueReaderMethodName", valueReaderMethodName);
        st.add("valueReaderMethodNameVar", valueReaderMethodNameVar);
        st.add("keyIsObject", keyIsObject);
        st.add("valueIsObject", valueIsObject);
        out.write(st.render());
    }

    private String getCollectionCtor(TypeMirror collectionType) {
        Class<?> collectionClass = CodeWriterUtils.toClass(collectionType);
        Constructor<?> constructor = null;
        try {
            constructor = collectionClass.getDeclaredConstructor(int.class);
        } catch (NoSuchMethodException ignored) {
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

    private TypeMirror findSuitableImplementation(TypeMirror mapImpl, TypeMirror fieldType, TypeMirror keyType, TypeMirror valueType) {
        if (CodeWriterUtils.typeEquals(mapImpl, Map.class)) {
            mapImpl = fieldType;
        }
        if (CodeWriterUtils.isInterfaceOrAbstractClass(mapImpl)) {
            if (CodeWriterUtils.typeEquals(mapImpl, ConcurrentNavigableMap.class)) {
                return CodeWriterUtils.fromClass(ConcurrentSkipListMap.class, keyType, valueType);
            } else if (CodeWriterUtils.typeEquals(mapImpl, ConcurrentMap.class)) {
                return CodeWriterUtils.fromClass(ConcurrentHashMap.class, keyType, valueType);
            } else if (CodeWriterUtils.typeEquals(mapImpl, SortedMap.class) || CodeWriterUtils.typeEquals(mapImpl, NavigableMap.class)) {
                return CodeWriterUtils.fromClass(TreeMap.class, keyType, valueType);
            } else if (CodeWriterUtils.typeEquals(mapImpl, Map.class)) {
                return CodeWriterUtils.fromClass(HashMap.class, keyType, valueType);
            } else {
                throw new IllegalArgumentException("Can't find a suitable implementation for " + CodeWriterUtils.getTypeName(mapImpl));
            }
        } else if (CodeWriterUtils.isAnonymousClass(mapImpl)) {
            throw new IllegalArgumentException("The map implementation cannot be an anonymous class");
        } else {
            return mapImpl;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MapFieldAccessorWriter that)) return false;

        if (variable != that.variable) return false;
        return kind == that.kind && typeName.equals(that.typeName);
    }
}
