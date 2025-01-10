package org.gittorr.ccerial.utils.impl;

import org.gittorr.ccerial.AccessorType;
import org.gittorr.ccerial.CcValue;
import org.gittorr.ccerial.CcSerializable;
import org.stringtemplate.v4.ST;

import javax.lang.model.element.Element;
import javax.lang.model.type.TypeKind;
import java.io.IOException;
import java.io.Writer;

public class SimpleFieldAccessorWriter extends AbstractFieldAccessorWriter {

    final String writerMethodName;
    final String readerMethodName;

    public SimpleFieldAccessorWriter(TypeKind kind, boolean variable, String typeName, String writerMethodName, String readerMethodName) {
        super(kind, variable, typeName);
        this.writerMethodName = writerMethodName;
        this.readerMethodName = readerMethodName;
    }

    @Override
    public void writeWriter(Writer out, String accessorName, Element fieldEl, CcSerializable ccSerializable, boolean isRecord) throws IOException {
        boolean nullIsZero = ccSerializable.nullIsZeroOrEmpty();
        CcValue valueAnnot = fieldEl.getAnnotation(CcValue.class);
        if (valueAnnot != null && !fieldEl.asType().getKind().isPrimitive()) {
            nullIsZero = valueAnnot.nullIsZeroOrEmpty();
        }
        out.write("\t\tBinaryUtils." + writerMethodName);
        out.write(String.format(nullIsZero ? "(out, BinaryUtils.zeroIfNull(obj.%s));\n": "(out, obj.%s);\n", accessorName));
    }

    @Override
    public void writeReader(Writer out, String accessorName, Element fieldEl, CcSerializable ccSerializable, boolean isRecord) throws IOException {
        String ctorArgName = toCtorArgName(accessorName, isRecord);
        String setterName = toSetterName(accessorName, isRecord);
        String typeName = fieldEl.asType().toString();
        String template = "\t\t<if(ctor)><typeName> <ctorArgName> = <else>obj.<setterName>(<endif>" +
                "BinaryUtils.<readerMethodName>(in)" +
                "<if(!ctor)>)<endif>;\n";
        ST st = new ST(template);
        st.add("variable", variable);
        st.add("accessorName", accessorName);
        st.add("typeName", typeName);
        st.add("ctorArgName", ctorArgName);
        st.add("setterName", setterName);
        st.add("ctor", ccSerializable.accessorType() == AccessorType.CONSTRUCTOR);
        st.add("readerMethodName", readerMethodName);
        out.write(st.render());
    }

}
