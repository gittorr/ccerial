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
import org.gittorr.ccerial.CcSerializable;
import org.gittorr.ccerial.CcValue;
import org.gittorr.ccerial.utils.CodeWriterUtils;
import org.stringtemplate.v4.ST;

import javax.lang.model.element.Element;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import java.io.IOException;
import java.io.Writer;

public class PrimitiveFieldAccessorWriter extends AbstractFieldAccessorWriter {

    public PrimitiveFieldAccessorWriter(TypeKind kind, boolean variable, String typeName) {
        super(kind, variable, typeName);
    }

    @Override
    public void writeWriter(Writer out, String accessorName, Element fieldEl, CcSerializable ccSerializable, boolean isRecord, Element classElement) throws IOException {
        CcValue valueAnnot = CodeWriterUtils.getAnnotation(CcValue.class, fieldEl, accessorName, classElement);
        boolean variable = this.variable;
        if (valueAnnot != null) {
            variable = valueAnnot.variableSize();
        }
        TypeMirror type = fieldEl.asType();
        TypeMirror boxedType = CodeWriterUtils.wrapperFor(type);
        String writerMethodName = CodeWriterUtils.readerFor(this.typeName, variable, false);
        String writerMethodNameVar = CodeWriterUtils.readerFor(this.typeName, true, false);
        //
        String template = "\t\t" +
                "<if(!variable)>" +
                    "writeWithFeature(BinaryUtils::<writerMethodNameVar>, BinaryUtils::<writerMethodName>, " +
                "<else>" +
                    "BinaryUtils.<writerMethodName>(" +
                "<endif>" +
                "out, " +
                "<if(!isPrimitive)>BinaryUtils.zeroIfNull(obj.<accessorName>)<else>obj.<accessorName><endif>" +
                ");\n";
        ST st = new ST(template);
        st.add("variable", variable);
        st.add("accessorName", accessorName);
        st.add("typeName", typeName);
        st.add("boxedType", boxedType.toString());
        st.add("writerMethodName", writerMethodName);
        st.add("isPrimitive", type.getKind().isPrimitive());
        st.add("writerMethodNameVar", writerMethodNameVar);
        out.write(st.render());
    }

    @Override
    public void writeReader(Writer out, String accessorName, Element fieldEl, CcSerializable ccSerializable, boolean isRecord, Element classElement) throws IOException {
        CcValue valueAnnot = CodeWriterUtils.getAnnotation(CcValue.class, fieldEl, accessorName, classElement);
        boolean variable = this.variable;
        boolean nullIsZeroOrEmpty = ccSerializable.nullIsZeroOrEmpty();
        if (valueAnnot != null) {
            variable = valueAnnot.variableSize();
            nullIsZeroOrEmpty = valueAnnot.nullIsZeroOrEmpty();
        }
        String ctorArgName = toCtorArgName(accessorName, isRecord);
        String setterName = toSetterName(accessorName, isRecord);
        TypeMirror type = fieldEl.asType();
        String typeName = CodeWriterUtils.getTypeName(type);
        String template = "\t\t<if(ctor)>" +
                            "<typeName> <ctorArgName> = " +
                          "<else>" +
                            "obj.<setterName>(" +
                          "<endif>" +
                          "<if(!isPrimitive)>nullIfEmptyOrZero(<endif>" +
                              "<if(!variable)>" +
                                "featureForceVariableSize ? BinaryUtils.<readerMethodNameVar>(in) :" +
                              "<endif>" +
                              "BinaryUtils.<readerMethodName>(in)" +
                          "<if(!isPrimitive)>, <nullIsZeroOrEmpty>)<endif>" +
                          "<if(!ctor)>)<endif>;\n";
        String readerMethodName = CodeWriterUtils.readerFor(this.typeName, variable, true);
        String readerMethodNameVar = CodeWriterUtils.readerFor(this.typeName, true, true);
        ST st = new ST(template);
        st.add("variable", variable);
        st.add("nullIsZeroOrEmpty", nullIsZeroOrEmpty);
        st.add("accessorName", accessorName);
        st.add("typeName", typeName);
        st.add("ctorArgName", ctorArgName);
        st.add("setterName", setterName);
        st.add("ctor", ccSerializable.accessorType() == AccessorType.CONSTRUCTOR);
        st.add("readerMethodName", readerMethodName);
        st.add("isPrimitive", type.getKind().isPrimitive());
        st.add("readerMethodNameVar", readerMethodNameVar);
        out.write(st.render());
    }

}
