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
import org.gittorr.ccerial.CcValue;
import org.gittorr.ccerial.utils.CodeWriterUtils;
import org.stringtemplate.v4.ST;

import javax.lang.model.element.Element;
import javax.lang.model.type.ArrayType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import java.io.IOException;
import java.io.Writer;

public class ObjectArrayFieldAccessorWriter extends AbstractFieldAccessorWriter {

    public ObjectArrayFieldAccessorWriter(TypeKind kind, boolean variable, String typeName) {
        super(kind, variable, typeName);
    }

    @Override
    public void writeWriter(Writer out, String accessorName, Element fieldEl, CcSerializable ccSerializable, boolean isRecord, Element classElement) throws IOException {
        int variableCount = -1;
        CcArray annotation = CodeWriterUtils.getAnnotation(CcArray.class, fieldEl, accessorName, classElement);
        String charset = "UTF-8";
        int componentCount = -1;
        if (annotation != null) {
            variableCount = annotation.count();
            charset = annotation.stringCharsetName();
            componentCount = annotation.componentCount();
        }
        TypeMirror componentType = ((ArrayType) fieldEl.asType()).getComponentType();
        String typeName = CodeWriterUtils.getTypeName(componentType);
        String componentClass = CodeWriterUtils.isInterfaceOrAbstractClass(componentType) ? "null" : (typeName + ".class");
        String writerMethodName = CodeWriterUtils.readerFor(typeName, variable, false);
        String writerMethodNameVar = CodeWriterUtils.readerFor(typeName, true, true, false);
        boolean isObject = writerMethodName == null;
        boolean isArray  = CodeWriterUtils.isArray(componentType);
        boolean stringArray = typeName.equals("java.lang.String");
        String template = "\t\tBinaryUtils.writeGenericArray(out, obj.<accessorName>, <variableCount>, " +
                "<if(isArray)>" +
                    "(out2, v) -> " +
                        "<if(!variable)>writeWithFeature(BinaryUtils::<writerMethodNameVar>, BinaryUtils::<writerMethodName>, " +
                        "<else>BinaryUtils.<writerMethodName>(" +
                        "<endif>" +
                        "out2, v, <componentCount>)"+
                "<else>" +
                    "<if(isObject)>" +
                        "(out2, v) -> BinaryUtils.writeObject(out2, v, <componentClass>, this)" +
                    "<else>"+
                        "<if(stringArray)>BinaryUtils.createStringWriter(<if(!variable)>featureForceVariableSize ? -1 : <endif><componentCount>, \"<charset>\")" +
                        "<else>" +
                            "(out2, v) -> " +
                            "<if(!variable)>" +
                                "writeWithFeature(BinaryUtils::<writerMethodNameVar>, BinaryUtils::<writerMethodName>, " +
                            "<else>" +
                                "BinaryUtils.<writerMethodName>(" +
                            "<endif>" +
                            "out2, v)" +
                        "<endif>" +
                    "<endif>" +
                "<endif>);\n";
        ST st = new ST(template);
        st.add("stringArray", stringArray);
        st.add("charset", charset);
        st.add("variable", variable);
        st.add("accessorName", accessorName);
        st.add("variableCount", variableCount);
        st.add("isObject", isObject);
        st.add("isArray", isArray);
        st.add("typeName", typeName);
        st.add("writerMethodName", writerMethodName);
        st.add("writerMethodNameVar", writerMethodNameVar);
        st.add("componentCount", componentCount);
        st.add("componentClass", componentClass);
        out.write(st.render());
    }

    @Override
    public void writeReader(Writer out, String accessorName, Element fieldEl, CcSerializable ccSerializable, boolean isRecord, Element classElement) throws IOException {
        int variableCount = -1;
        boolean nullIsEmpty = ccSerializable.nullIsZeroOrEmpty();
        boolean variable = this.variable;
        CcArray annotation = CodeWriterUtils.getAnnotation(CcArray.class, fieldEl, accessorName, classElement);
        String ctorArgName = toCtorArgName(accessorName, isRecord);
        String setterName = toSetterName(accessorName, isRecord);
        String charset = "UTF-8";
        int componentCount = -1;
        if (annotation != null) {
            variableCount = annotation.count();
            variable = annotation.count() == -1;
            nullIsEmpty = annotation.nullIsEmpty();
            charset = annotation.stringCharsetName();
            componentCount = annotation.componentCount();
        }
        TypeMirror componentType = ((ArrayType) fieldEl.asType()).getComponentType();
        String typeName = CodeWriterUtils.getTypeName(componentType);
        String componentClass = CodeWriterUtils.isInterfaceOrAbstractClass(componentType) ? "null" : (typeName + ".class");
        String readerMethodName = CodeWriterUtils.readerFor(typeName, variable, componentCount == -1, true);
        String readerMethodNameVar = CodeWriterUtils.readerFor(typeName, true, true, true);
        boolean isObject = readerMethodName == null;
        boolean isArray  = CodeWriterUtils.isArray(componentType);
        boolean stringArray = typeName.equals("java.lang.String");
        String template = "\t\t<if(ctor)><typeName>[] <ctorArgName> = <else>obj.<setterName>(<endif>" +
                "<if(variable)>nullIfEmptyOrZero(<endif>" +
                "BinaryUtils.readGenericArray(in, <variableCount>, " +
                "<if(isArray)>" +
                    "(in2) -> <if(!variable)>featureForceVariableSize ? BinaryUtils.<readerMethodNameVar>(in2, -1) : <endif>BinaryUtils.<readerMethodName>(in2, <componentCount>)" +
                "<else>" +
                    "<if(isObject)>" +
                        "(in2) -> BinaryUtils.readObject(in2, <componentClass>, this)" +
                    "<else>" +
                        "<if(stringArray)>in1 -> BinaryUtils.<readerMethodName>(in1, <if(!variable)>featureForceVariableSize ? -1 : <endif><componentCount>, \"<charset>\")" +
                        "<else>" +
                            "(in2) -> " +
                            "<if(!variable)>" +
                                "featureForceVariableSize ? BinaryUtils.<readerMethodNameVar>(in2) :" +
                            "<endif>" +
                            "BinaryUtils.<readerMethodName>(in2)" +
                        "<endif>" +
                    "<endif>" +
                "<endif>, " +
                "<typeName>[]::new)" +
                "<if(variable)>, <nullIsEmpty>)<endif>" +
                "<if(!ctor)>)<endif>;\n";
        ST st = new ST(template);
        st.add("variable", variable);
        st.add("nullIsEmpty", nullIsEmpty);
        st.add("stringArray", stringArray);
        st.add("charset", charset);
        st.add("accessorName", accessorName);
        st.add("variableCount", variableCount);
        st.add("isObject", isObject);
        st.add("isArray", isArray);
        st.add("typeName", typeName);
        st.add("ctorArgName", ctorArgName);
        st.add("setterName", setterName);
        st.add("ctor", ccSerializable.accessorType() == AccessorType.CONSTRUCTOR);
        st.add("readerMethodName", readerMethodName);
        st.add("readerMethodNameVar", readerMethodNameVar);
        st.add("componentCount", componentCount);
        st.add("componentClass", componentClass);
        out.write(st.render());
    }

}
