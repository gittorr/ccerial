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

import org.gittorr.ccerial.utils.impl.ArrayFieldAccessorWriter;
import org.gittorr.ccerial.utils.impl.ObjectArrayFieldAccessorWriter;
import org.gittorr.ccerial.utils.impl.SimpleFieldAccessorWriter;

import javax.lang.model.type.ArrayType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Types;
import java.util.HashMap;
import java.util.Map;

public final class FieldAccessorWriterManager {

    private static final Map<FieldAccessorWriter, FieldAccessorWriter> ACCESSORS = new HashMap<>();
    private static Types typeUtils;

    private static void addAccessor(FieldAccessorWriter acc) {
        ACCESSORS.put(acc, acc);
    }

    static {
        // primitive types
        addAccessor(new SimpleFieldAccessorWriter(TypeKind.BYTE, false, "byte", "writeByte", "readByte"));
        addAccessor(new SimpleFieldAccessorWriter(TypeKind.BYTE, true, "byte", "writeByte", "readByte"));
        addAccessor(new SimpleFieldAccessorWriter(TypeKind.SHORT, false, "short", "writeShort", "readShort"));
        addAccessor(new SimpleFieldAccessorWriter(TypeKind.SHORT, true, "short", "writeShort", "readShort"));
        addAccessor(new SimpleFieldAccessorWriter(TypeKind.CHAR, false, "char", "writeChar", "readChar"));
        addAccessor(new SimpleFieldAccessorWriter(TypeKind.CHAR, true, "char", "writeChar", "readChar"));
        addAccessor(new SimpleFieldAccessorWriter(TypeKind.INT, false, "int", "writeInt", "readInt"));
        addAccessor(new SimpleFieldAccessorWriter(TypeKind.INT, true, "int", "writeVarInt", "readVarInt"));
        addAccessor(new SimpleFieldAccessorWriter(TypeKind.LONG, false, "long", "writeLong", "readLong"));
        addAccessor(new SimpleFieldAccessorWriter(TypeKind.LONG, true, "long", "writeVarLong", "readVarLong"));
        addAccessor(new SimpleFieldAccessorWriter(TypeKind.FLOAT, false, "float", "writeFloat", "readFloat"));
        addAccessor(new SimpleFieldAccessorWriter(TypeKind.FLOAT, true, "float", "writeVarFloat", "readVarFloat"));
        addAccessor(new SimpleFieldAccessorWriter(TypeKind.DOUBLE, false, "double", "writeDouble", "readDouble"));
        addAccessor(new SimpleFieldAccessorWriter(TypeKind.DOUBLE, true, "double", "writeVarDouble", "readVarDouble"));
        addAccessor(new SimpleFieldAccessorWriter(TypeKind.BOOLEAN, false, "boolean", "writeBoolean", "readBoolean"));
        addAccessor(new SimpleFieldAccessorWriter(TypeKind.BOOLEAN, true, "boolean", "writeBoolean", "readBoolean"));
        // wrappers
        addAccessor(new SimpleFieldAccessorWriter(TypeKind.DECLARED, false, "java.lang.Boolean", "writeBoolean", "readBoolean"));
        addAccessor(new SimpleFieldAccessorWriter(TypeKind.DECLARED, true, "java.lang.Boolean", "writeBoolean", "readBoolean"));
        addAccessor(new SimpleFieldAccessorWriter(TypeKind.DECLARED, false, "java.lang.Byte", "writeByte", "readByte"));
        addAccessor(new SimpleFieldAccessorWriter(TypeKind.DECLARED, true, "java.lang.Byte", "writeByte", "readByte"));
        addAccessor(new SimpleFieldAccessorWriter(TypeKind.DECLARED, false, "java.lang.Short", "writeShort", "readShort"));
        addAccessor(new SimpleFieldAccessorWriter(TypeKind.DECLARED, true, "java.lang.Short", "writeShort", "readShort"));
        addAccessor(new SimpleFieldAccessorWriter(TypeKind.DECLARED, false, "java.lang.Character", "writeChar", "readChar"));
        addAccessor(new SimpleFieldAccessorWriter(TypeKind.DECLARED, true, "java.lang.Character", "writeChar", "readChar"));
        addAccessor(new SimpleFieldAccessorWriter(TypeKind.DECLARED, false, "java.lang.Integer", "writeInt", "readInt"));
        addAccessor(new SimpleFieldAccessorWriter(TypeKind.DECLARED, true, "java.lang.Integer", "writeVarInt", "readVarInt"));
        addAccessor(new SimpleFieldAccessorWriter(TypeKind.DECLARED, false, "java.lang.Long", "writeLong", "readLong"));
        addAccessor(new SimpleFieldAccessorWriter(TypeKind.DECLARED, true, "java.lang.Long", "writeVarLong", "readVarLong"));
        addAccessor(new SimpleFieldAccessorWriter(TypeKind.DECLARED, false, "java.lang.Float", "writeFloat", "readFloat"));
        addAccessor(new SimpleFieldAccessorWriter(TypeKind.DECLARED, true, "java.lang.Float", "writeFloat", "readFloat"));
        addAccessor(new SimpleFieldAccessorWriter(TypeKind.DECLARED, false, "java.lang.Double", "writeDouble", "readDouble"));
        addAccessor(new SimpleFieldAccessorWriter(TypeKind.DECLARED, true, "java.lang.Double", "writeDouble", "readDouble"));
        addAccessor(new ArrayFieldAccessorWriter(TypeKind.DECLARED, false, "java.lang.String", "writeString", "readString"));
        addAccessor(new ArrayFieldAccessorWriter(TypeKind.DECLARED, true, "java.lang.String", "writeString", "readString"));
        // arrays
        addAccessor(new ArrayFieldAccessorWriter(TypeKind.ARRAY, false, "byte[]", "writeBytes", "readBytes"));
        addAccessor(new ArrayFieldAccessorWriter(TypeKind.ARRAY, true, "byte[]", "writeBytes", "readBytes"));
        addAccessor(new ArrayFieldAccessorWriter(TypeKind.ARRAY, false, "short[]", "writeShorts", "readShorts"));
        addAccessor(new ArrayFieldAccessorWriter(TypeKind.ARRAY, true, "short[]", "writeShorts", "readShorts"));
        addAccessor(new ArrayFieldAccessorWriter(TypeKind.ARRAY, false, "char[]", "writeChars", "readChars"));
        addAccessor(new ArrayFieldAccessorWriter(TypeKind.ARRAY, true, "char[]", "writeChars", "readChars"));
        addAccessor(new ArrayFieldAccessorWriter(TypeKind.ARRAY, false, "int[]", "writeInts", "readInts"));
        addAccessor(new ArrayFieldAccessorWriter(TypeKind.ARRAY, true, "int[]", "writeInts", "readInts"));
        addAccessor(new ArrayFieldAccessorWriter(TypeKind.ARRAY, false, "long[]", "writeLongs", "readLongs"));
        addAccessor(new ArrayFieldAccessorWriter(TypeKind.ARRAY, true, "long[]", "writeLongs", "readLongs"));
        addAccessor(new ArrayFieldAccessorWriter(TypeKind.ARRAY, false, "float[]", "writeFloats", "readFloats"));
        addAccessor(new ArrayFieldAccessorWriter(TypeKind.ARRAY, true, "float[]", "writeFloats", "readFloats"));
        addAccessor(new ArrayFieldAccessorWriter(TypeKind.ARRAY, false, "double[]", "writeDoubles", "readDoubles"));
        addAccessor(new ArrayFieldAccessorWriter(TypeKind.ARRAY, true, "double[]", "writeDoubles", "readDoubles"));
        // boxed arrays
        addAccessor(new ObjectArrayFieldAccessorWriter(TypeKind.ARRAY, false, "java.lang.Byte", "writeByte", "readByte"));
        addAccessor(new ObjectArrayFieldAccessorWriter(TypeKind.ARRAY, true, "java.lang.Byte", "writeByte", "readByte"));
        addAccessor(new ObjectArrayFieldAccessorWriter(TypeKind.ARRAY, false, "java.lang.Short", "writeShort", "readShort"));
        addAccessor(new ObjectArrayFieldAccessorWriter(TypeKind.ARRAY, true, "java.lang.Short", "writeShort", "readShort"));
        addAccessor(new ObjectArrayFieldAccessorWriter(TypeKind.ARRAY, false, "java.lang.Character", "writeChar", "readChar"));
        addAccessor(new ObjectArrayFieldAccessorWriter(TypeKind.ARRAY, true, "java.lang.Character", "writeChar", "readChar"));
        addAccessor(new ObjectArrayFieldAccessorWriter(TypeKind.ARRAY, false, "java.lang.Integer", "writeInt", "readInt"));
        addAccessor(new ObjectArrayFieldAccessorWriter(TypeKind.ARRAY, true, "java.lang.Integer", "writeInt", "readInt"));
        addAccessor(new ObjectArrayFieldAccessorWriter(TypeKind.ARRAY, false, "java.lang.Long", "writeLong", "readLong"));
        addAccessor(new ObjectArrayFieldAccessorWriter(TypeKind.ARRAY, true, "java.lang.Long", "writeLong", "readLong"));
        addAccessor(new ObjectArrayFieldAccessorWriter(TypeKind.ARRAY, false, "java.lang.Float", "writeFloat", "readFloat"));
        addAccessor(new ObjectArrayFieldAccessorWriter(TypeKind.ARRAY, true, "java.lang.Float", "writeFloat", "readFloat"));
        addAccessor(new ObjectArrayFieldAccessorWriter(TypeKind.ARRAY, false, "java.lang.Double", "writeDouble", "readDouble"));
        addAccessor(new ObjectArrayFieldAccessorWriter(TypeKind.ARRAY, true, "java.lang.Double", "writeDouble", "readDouble"));
        addAccessor(new ObjectArrayFieldAccessorWriter(TypeKind.ARRAY, false, "java.lang.String", "writeString", "readString"));
        addAccessor(new ObjectArrayFieldAccessorWriter(TypeKind.ARRAY, true, "java.lang.String", "writeString", "readString"));
        addAccessor(new ObjectArrayFieldAccessorWriter(TypeKind.ARRAY, false, "java.lang.Object", "writeObject", "readObject"));
        addAccessor(new ObjectArrayFieldAccessorWriter(TypeKind.ARRAY, true, "java.lang.Object", "writeObject", "readObject"));
    }

    public static void setTypeUtils(Types typeUtils) {
        FieldAccessorWriterManager.typeUtils = typeUtils;
    }

    public static boolean isWrapperType(TypeMirror typeMirror) {
        if (typeMirror.getKind() != TypeKind.DECLARED) {
            return false;
        }
        try {
            return typeMirror.toString().equals("java.lang.String") || typeUtils.unboxedType(typeMirror) != null;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
    public static FieldAccessorWriter getFieldAccessorWriter(TypeMirror type, boolean variable) {
        TypeKind kind = type.getKind();
        String typeName = null;
        if (kind == TypeKind.ARRAY) {
            ArrayType at = (ArrayType) type;
            TypeMirror componentType = at.getComponentType();
            if (componentType.getKind().isPrimitive())
                typeName = getTypeName(at);
            else {
                if (isWrapperType(componentType)) {
                    typeName = componentType.toString();
                } else {
                    typeName = "java.lang.Object";
                }
            }
        } else {
            typeName = getTypeName(type);
        }
        return ACCESSORS.get(new SimpleFieldAccessorWriter(kind, variable, typeName, null, null));
    }

    public static String getTypeName(TypeMirror type) {
        String typeString = type.toString();
        int annotationStart = typeString.lastIndexOf('@');
        if (annotationStart != -1) {
            int spcIdx = typeString.indexOf(' ', annotationStart);
            return typeString.substring(spcIdx + 1);
        }
        return typeString;
    }
}
