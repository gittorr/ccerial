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

import org.gittorr.ccerial.*;
import org.gittorr.ccerial.utils.CodeWriterUtils;
import org.stringtemplate.v4.ST;

import javax.lang.model.element.Element;
import javax.lang.model.type.TypeKind;
import java.io.IOException;
import java.io.Writer;

public class SimpleEnumFieldAccessorWriter extends AbstractFieldAccessorWriter {

    public SimpleEnumFieldAccessorWriter(TypeKind kind, boolean variable, String typeName) {
        super(kind, variable, typeName);
    }

    @Override
    public void writeWriter(Writer out, String accessorName, Element fieldEl, CcSerializable ccSerializable, boolean isRecord, Element classElement) throws IOException {
        EnumType value = EnumType.ORDINAL;
        int count = this.variable ? -1 : 0;
        String stringCharsetName = "UTF-8";
        boolean stringAsCharArray = false;
        CcEnum enumAnnot = CodeWriterUtils.getAnnotation(CcEnum.class, fieldEl, accessorName, classElement);

        if (enumAnnot != null) {
            value = enumAnnot.value();
            count = enumAnnot.count();
            stringCharsetName = enumAnnot.stringCharsetName();
            stringAsCharArray = enumAnnot.stringAsCharArray();
        }

        boolean variable = count == -1;

        String typeName = value == EnumType.ORDINAL ? "int" : (stringAsCharArray ? "char[]" : "java.lang.String");

        String template = "\t\tBinaryUtils.<writerMethodName>(out, obj.<accessorName><if(isString)>.name()<if(stringAsCharArray)>.toCharArray()<endif>," +
                " <count><if(!stringAsCharArray)>, \"<charset>\"<endif><else>.ordinal()<endif>);\n";
        String writerMethodName = CodeWriterUtils.readerFor(typeName, variable, false);

        ST st = new ST(template);
        st.add("variable", variable);
        st.add("count", count);
        st.add("accessorName", accessorName);
        st.add("charset", stringCharsetName);
        st.add("isString", value == EnumType.STRING);
        st.add("stringAsCharArray", stringAsCharArray);
        st.add("writerMethodName", writerMethodName);
        out.write(st.render());
    }

    @Override
    public void writeReader(Writer out, String accessorName, Element fieldEl, CcSerializable ccSerializable, boolean isRecord, Element classElement) throws IOException {
        EnumType value = EnumType.ORDINAL;
        int count = this.variable ? -1 : 0;
        String stringCharsetName = "UTF-8";
        boolean stringAsCharArray = false;

        CcEnum enumAnnot = CodeWriterUtils.getAnnotation(CcEnum.class, fieldEl, accessorName, classElement);

        if (enumAnnot != null) {
            value = enumAnnot.value();
            count = enumAnnot.count();
            stringCharsetName = enumAnnot.stringCharsetName();
            stringAsCharArray = enumAnnot.stringAsCharArray();
        }

        boolean variable = count == -1;

        String ctorArgName = toCtorArgName(accessorName, isRecord);
        String setterName = toSetterName(accessorName, isRecord);
        String typeName = value == EnumType.ORDINAL ? "int" : (stringAsCharArray ? "char[]" : "java.lang.String");

        String template = "\t\t<if(ctor)><typeName> <ctorArgName> = <else>obj.<setterName>(<endif>" +
                "<if(isString)><typeName>.valueOf(<else><typeName>.values()[<endif>" +
                "BinaryUtils.<readerMethodName>(in<if(isString)>, <count><if(!stringAsCharArray)>, \"<charset>\"<endif><endif>)" +
                "<if(isString)>)<else>]<endif>" +
                "<if(!ctor)>)<endif>;\n";
        String readerMethodName = CodeWriterUtils.readerFor(typeName, variable, true);
        ST st = new ST(template);
        st.add("variable", variable);
        st.add("count", count);
        st.add("accessorName", accessorName);
        st.add("charset", stringCharsetName);
        st.add("typeName", this.typeName);
        st.add("isString", value == EnumType.STRING);
        st.add("ctorArgName", ctorArgName);
        st.add("setterName", setterName);
        st.add("ctor", ccSerializable.accessorType() == AccessorType.CONSTRUCTOR);
        st.add("stringAsCharArray", stringAsCharArray);
        st.add("readerMethodName", readerMethodName);
        out.write(st.render());
    }

}
