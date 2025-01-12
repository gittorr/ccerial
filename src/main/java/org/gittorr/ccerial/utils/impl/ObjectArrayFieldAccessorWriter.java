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
import org.stringtemplate.v4.ST;

import javax.lang.model.element.Element;
import javax.lang.model.type.ArrayType;
import javax.lang.model.type.TypeKind;
import java.io.IOException;
import java.io.Writer;

public class ObjectArrayFieldAccessorWriter extends AbstractFieldAccessorWriter {

    final String writerMethodName;
    final String readerMethodName;

    public ObjectArrayFieldAccessorWriter(TypeKind kind, boolean variable, String typeName, String writerMethodName, String readerMethodName) {
        super(kind, variable, typeName);
        this.writerMethodName = writerMethodName;
        this.readerMethodName = readerMethodName;
    }

    @Override
    public void writeWriter(Writer out, String accessorName, Element fieldEl, CcSerializable ccSerializable, boolean isRecord) throws IOException {
        String variableCount = ", obj." + accessorName + ".length";
        CcArray annotation = fieldEl.getAnnotation(CcArray.class);
        String charset = "UTF-8";
        if (annotation != null) {
            variableCount = ", " + annotation.count();
            charset = annotation.stringCharsetName();
        }
        String typeName = ((ArrayType) fieldEl.asType()).getComponentType().toString();
        boolean stringArray = typeName.equals("java.lang.String");
        String template = "\t\tBinaryUtils.writeGenericArray(out, obj.<accessorName><if(!variable)><variableCount><endif>"+
                ", <if(stringArray)>BinaryUtils.createStringWriter(\"<charset>\")<else>BinaryUtils::<writerMethodName><endif>);\n";
        ST st = new ST(template);
        st.add("stringArray", stringArray);
        st.add("charset", charset);
        st.add("variable", variable);
        st.add("accessorName", accessorName);
        st.add("variableCount", variableCount);
        st.add("typeName", typeName);
        st.add("writerMethodName", writerMethodName);

        out.write(st.render());
    }

    @Override
    public void writeReader(Writer out, String accessorName, Element fieldEl, CcSerializable ccSerializable, boolean isRecord) throws IOException {
        String variableCount = null;
        CcArray annotation = fieldEl.getAnnotation(CcArray.class);
        String ctorArgName = toCtorArgName(accessorName, isRecord);
        String setterName = toSetterName(accessorName, isRecord);
        String charset = "UTF-8";
        if (annotation != null) {
            variableCount = ", " + annotation.count();
            charset = annotation.stringCharsetName();
        }
        String typeName = ((ArrayType) fieldEl.asType()).getComponentType().toString();
        boolean stringArray = typeName.equals("java.lang.String");
        String template = "\t\t<if(ctor)><typeName>[] <ctorArgName> = <else>obj.<setterName>(<endif>" +
                "BinaryUtils.readGenericArray(in<if(!variable)><variableCount><endif>, " +
                "<if(stringArray)>in1 -> BinaryUtils.<readerMethodName>(in1, \"<charset>\")<else>BinaryUtils::<readerMethodName><endif>, " +
                "<typeName>[]::new)" +
                "<if(!ctor)>)<endif>;\n";
        ST st = new ST(template);
        st.add("variable", variable);
        st.add("stringArray", stringArray);
        st.add("charset", charset);
        st.add("accessorName", accessorName);
        st.add("variableCount", variableCount);
        st.add("typeName", typeName);
        st.add("ctorArgName", ctorArgName);
        st.add("setterName", setterName);
        st.add("ctor", ccSerializable.accessorType() == AccessorType.CONSTRUCTOR);
        st.add("readerMethodName", readerMethodName);
        out.write(st.render());
    }

}
