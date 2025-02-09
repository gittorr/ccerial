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

import org.gittorr.ccerial.utils.impl.*;

import javax.lang.model.type.ArrayType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;

import static org.gittorr.ccerial.utils.CodeWriterUtils.getTypeName;
import static org.gittorr.ccerial.utils.CodeWriterUtils.isWrapperType;


public final class FieldAccessorWriterManager {

    public static FieldAccessorWriter getFieldAccessorWriter(TypeMirror type, boolean variable) {
        TypeKind kind = type.getKind();
        String typeName = null;
        if (kind == TypeKind.ARRAY) {
            ArrayType at = (ArrayType) type;
            TypeMirror componentType = at.getComponentType();
            if (componentType.getKind().isPrimitive()) {
                typeName = getTypeName(at);
                return new ArrayFieldAccessorWriter(kind, variable, typeName);
            } else {
                if (isWrapperType(componentType)) {
                    typeName = componentType.toString();
                } else {
                    typeName = "java.lang.Object";
                }
            }
            return new ObjectArrayFieldAccessorWriter(kind, variable, typeName);
        } else if (kind == TypeKind.DECLARED && CodeWriterUtils.isString(type)) {
            return new ArrayFieldAccessorWriter(kind, variable, getTypeName(type));
        } else if (kind.isPrimitive() || (kind == TypeKind.DECLARED && CodeWriterUtils.isWrapperType(type))) {
            typeName = getTypeName(type);
            return new PrimitiveFieldAccessorWriter(kind, variable, typeName);
        } else if (kind == TypeKind.DECLARED && CodeWriterUtils.isCollection(type)) {
            typeName = getTypeName(type);
            TypeMirror componentType = CodeWriterUtils.getCollectionComponentType(type);
            return new CollectionFieldAccessorWriter(kind, variable, typeName, componentType);
        } else if (kind == TypeKind.DECLARED && CodeWriterUtils.isMap(type)) {
            typeName = getTypeName(type);
            return new MapFieldAccessorWriter(kind, variable, typeName);
        } else if (kind.isPrimitive()) {
            typeName = getTypeName(type);
            return new PrimitiveFieldAccessorWriter(kind, variable, typeName);
        } else if (CodeWriterUtils.isEnum(type)) {
            typeName = getTypeName(type);
            return new SimpleEnumFieldAccessorWriter(kind, variable, typeName);
        } else if (kind == TypeKind.DECLARED) {
            typeName = getTypeName(type);
            return new ObjectFieldAccessorWriter(kind, variable, typeName);
        }
        return null;
    }

}
