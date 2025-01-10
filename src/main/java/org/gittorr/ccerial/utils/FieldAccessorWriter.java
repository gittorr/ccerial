package org.gittorr.ccerial.utils;

import org.gittorr.ccerial.CcSerializable;

import javax.lang.model.element.Element;
import javax.lang.model.type.TypeKind;
import java.io.IOException;
import java.io.Writer;

public interface FieldAccessorWriter {

    TypeKind getKind();

    boolean isVariable();

    String getTypeName();

    void writeWriter(Writer out, String accessorName, Element fieldEl, CcSerializable ccSerializable, boolean isRecord) throws IOException;

    void writeReader(Writer out, String accessorName, Element fieldEl, CcSerializable ccSerializable, boolean isRecord) throws IOException;

}
