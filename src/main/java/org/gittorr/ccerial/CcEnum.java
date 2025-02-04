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
package org.gittorr.ccerial;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Specifies the serialization behavior for enum fields in the Ccerial framework.
 * This annotation allows customization of how enums are serialized and deserialized,
 * including whether they should be represented by their ordinal values or names.
 */
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.RECORD_COMPONENT})
@Retention(RetentionPolicy.CLASS)
public @interface CcEnum {

    /**
     * Defines how the enum should be serialized.
     *
     * @return the selected {@link EnumType}, which determines whether the enum is stored as an ordinal or a string.
     */
    EnumType value() default EnumType.ORDINAL;

    /**
     * Specifies the fixed size for the enum representation.
     * If set to a positive value, it ensures a fixed-length serialization.
     *
     * @return the fixed size for serialization, or -1 for variable size.
     */
    int count() default -1;

    /**
     * Defines the character encoding to be used when serializing the enum as a string.
     * This applies when the enum is stored as a string representation.
     *
     * @return the name of the character set used for encoding.
     */
    String stringCharsetName() default "UTF-8";

    /**
     * Specifies whether the enum should be serialized as an array of characters instead of a standard string.
     * When enabled, it stores the enum as a sequence of wide characters, preserving direct character encoding.
     *
     * @return {@code true} if the enum should be stored as a char array, {@code false} otherwise.
     */
    boolean stringAsCharArray() default false;
}
