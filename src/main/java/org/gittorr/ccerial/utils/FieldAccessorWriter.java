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
package org.gittorr.ccerial.utils;

import org.gittorr.ccerial.CcSerializable;

import javax.lang.model.element.Element;
import javax.lang.model.type.TypeKind;
import java.io.IOException;
import java.io.Writer;

/**
 * Internal class
 */
public interface FieldAccessorWriter {

    /**
     * @return the type kind
     */
    TypeKind getKind();

    /**
     * @return is variable
     */
    boolean isVariable();

    /**
     * @return type name
     */
    String getTypeName();

    /**
     * Write writer
     * @param out out
     * @param accessorName the accessorName
     * @param fieldEl the fieldEl
     * @param ccSerializable the ccSerializable
     * @param isRecord the isRecord
     * @throws IOException the IOException
     */
    void writeWriter(Writer out, String accessorName, Element fieldEl, CcSerializable ccSerializable, boolean isRecord) throws IOException;

    /**
     * Write reader
     * @param out out
     * @param accessorName the accessorName
     * @param fieldEl the fieldEl
     * @param ccSerializable the ccSerializable
     * @param isRecord the isRecord
     * @throws IOException the IOException
     */
    void writeReader(Writer out, String accessorName, Element fieldEl, CcSerializable ccSerializable, boolean isRecord) throws IOException;

}
