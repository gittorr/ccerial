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
import java.io.IOException;
import java.io.Writer;

public class ObjectFieldAccessorWriter extends AbstractFieldAccessorWriter {

    public ObjectFieldAccessorWriter(TypeKind kind, boolean variable, String typeName) {
        super(kind, variable, typeName);
    }

    @Override
    public void writeWriter(Writer out, String accessorName, Element fieldEl, CcSerializable ccSerializable, boolean isRecord, Element classElement) throws IOException {
        CcValue annotation = CodeWriterUtils.getAnnotation(CcValue.class, fieldEl, accessorName, classElement);
        boolean variable = this.variable;
        String className = null;
        if (annotation != null) {
            variable = annotation.variableSize();
            className = annotation.className();
        }
        boolean dynamic = className == null || className.isEmpty();
        String template = "\t\tBinaryUtils.writeObject(out, obj.<accessorName>, <if(dynamic)>null<else><className>.class<endif>, this);\n";
        ST st = new ST(template);
        st.add("dynamic", dynamic);
        st.add("accessorName", accessorName);
        st.add("className", className);

        out.write(st.render());
    }

    @Override
    public void writeReader(Writer out, String accessorName, Element fieldEl, CcSerializable ccSerializable, boolean isRecord, Element classElement) throws IOException {
        CcValue annotation = CodeWriterUtils.getAnnotation(CcValue.class, fieldEl, accessorName, classElement);
        String ctorArgName = toCtorArgName(accessorName, isRecord);
        String setterName = toSetterName(accessorName, isRecord);
        boolean variable = this.variable;
        String className = null;
        if (annotation != null) {
            variable = annotation.variableSize();
            className = annotation.className();
        }
        String typeName = CodeWriterUtils.getTypeName(fieldEl.asType());
        boolean dynamic = className == null || className.isEmpty();
        String template = "\t\t<if(ctor)><typeName> <ctorArgName> = <else>obj.<setterName>(<endif>" +
                "BinaryUtils.readObject(in, <if(dynamic)>null<else><className>.class<endif>, this)" +
                "<if(!ctor)>)<endif>;\n";
        ST st = new ST(template);
        st.add("variable", variable);
        st.add("accessorName", accessorName);
        st.add("typeName", typeName);
        st.add("className", className);
        st.add("ctorArgName", ctorArgName);
        st.add("setterName", setterName);
        st.add("dynamic", dynamic);
        st.add("ctor", ccSerializable.accessorType() == AccessorType.CONSTRUCTOR);
        out.write(st.render());
    }

}
