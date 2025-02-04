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
import javax.lang.model.type.TypeKind;
import java.io.IOException;
import java.io.Writer;

public class ArrayFieldAccessorWriter extends AbstractFieldAccessorWriter {

    public ArrayFieldAccessorWriter(TypeKind kind, boolean variable, String typeName) {
        super(kind, variable, typeName);
    }

    @Override
    public void writeWriter(Writer out, String accessorName, Element fieldEl, CcSerializable ccSerializable, boolean isRecord, Element classElement) throws IOException {
        String variableCount = ", -1";
        CcArray annotation = CodeWriterUtils.getAnnotation(CcArray.class, fieldEl, accessorName, classElement);
        boolean nullIsEmpty = ccSerializable.nullIsZeroOrEmpty();
        String charset = "UTF-8";
        boolean componentVariable = true;
        if (annotation != null) {
            variableCount = ", " + annotation.count();
            charset = annotation.stringCharsetName();
            componentVariable = annotation.componentCount() == -1;
        }
        String typeName = CodeWriterUtils.getTypeName(fieldEl.asType());
        boolean stringAsWchar = annotation != null && annotation.stringAsCharArray() && typeName.equals("java.lang.String");
        boolean isSstring = (annotation == null || !annotation.stringAsCharArray()) && typeName.equals("java.lang.String");
        String writerMethodName = CodeWriterUtils.readerFor(this.typeName, this.variable, componentVariable, false);
        out.write("\t\tBinaryUtils." + (stringAsWchar ? "writeChars" : writerMethodName) +
                "(out, obj." + accessorName + (stringAsWchar ? ".toCharArray()" : "") + variableCount +
                (!isSstring ? "" : (", \"" + charset + "\"")) +");\n");
    }

    @Override
    public void writeReader(Writer out, String accessorName, Element fieldEl, CcSerializable ccSerializable, boolean isRecord, Element classElement) throws IOException {
        String variableCount = ", -1";
        String charset = "UTF-8";
        CcArray annotation = CodeWriterUtils.getAnnotation(CcArray.class, fieldEl, accessorName, classElement);
        String ctorArgName = toCtorArgName(accessorName, isRecord);
        String setterName = toSetterName(accessorName, isRecord);
        boolean componentVariable = true;
        if (annotation != null) {
            variableCount = ", " + annotation.count();
            charset = annotation.stringCharsetName();
            componentVariable = annotation.componentCount() == -1;
        }
        String typeName = CodeWriterUtils.getTypeName(fieldEl.asType());
        boolean stringAsWchar = annotation != null && annotation.stringAsCharArray() && typeName.equals("java.lang.String");
        boolean isString = (annotation == null || !annotation.stringAsCharArray()) && typeName.equals("java.lang.String");
        String template = "\t\t<if(ctor)><typeName> <ctorArgName> = <else>obj.<setterName>(<endif>" +
                "<if(stringAsWchar)>new String(<endif>BinaryUtils.<readerMethodName>(in<variableCount><if(isString)>, \"<charset>\"<endif>)<if(stringAsWchar)>)<endif>"+
                "<if(!ctor)>)<endif>;\n";
        String readerMethodName = CodeWriterUtils.readerFor(this.typeName, this.variable, componentVariable, true);
        ST st = new ST(template);
        st.add("variable", variable);
        st.add("stringAsWchar", stringAsWchar);
        st.add("isString", isString);
        st.add("charset", charset);
        st.add("accessorName", accessorName);
        st.add("variableCount", variableCount);
        st.add("typeName", typeName);
        st.add("ctorArgName", ctorArgName);
        st.add("setterName", setterName);
        st.add("ctor", ccSerializable.accessorType() == AccessorType.CONSTRUCTOR);
        st.add("readerMethodName", stringAsWchar ? "readChars" : readerMethodName);
        out.write(st.render());
    }

}
