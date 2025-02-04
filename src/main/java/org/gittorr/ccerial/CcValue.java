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
 * Customizes the serialization behavior for specific fields.
 * This annotation provides options to control how individual fields are serialized
 * and deserialized within an object.
 */
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.RECORD_COMPONENT})
@Retention(RetentionPolicy.CLASS)
public @interface CcValue {

    /**
     * Specifies whether the field should have a variable size during serialization.
     * This flag is ignored for object types.
     *
     * @return {@code true} if the field should be serialized with a variable size, {@code false} otherwise.
     */
    boolean variableSize() default false;

    /**
     * Determines if zero or empty values should be treated as {@code null} during deserialization.
     * This flag is ignored for object types.
     *
     * @return {@code true} to replace zero or empty values with {@code null}, {@code false} otherwise.
     */
    boolean nullIsZeroOrEmpty() default true;

    /**
     * Specifies the implementing class name when the field type is an abstract class or an interface.
     * If left empty, the default behavior will be used.
     *
     * @return the fully qualified class name of the concrete implementation.
     */
    String className() default "";
}
