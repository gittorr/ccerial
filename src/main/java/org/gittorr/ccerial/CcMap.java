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
 * Override the default behavior for maps
 */
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.RECORD_COMPONENT})
@Retention(RetentionPolicy.CLASS)
public @interface CcMap {

    /**
     * If the map is empty then it deserializes to null
     */
    boolean nullIsEmpty() default false;

    /**
     * Change the charset for strings
     */
    String stringCharsetName() default "UTF-8";

    /**
     * Serialize a string as an array of chars. The difference is that it will use wide char instead of a defined charset.
     */
    boolean stringAsCharArray() default false;

    /**
     * Define a fixed size for the map
     */
    int count() default -1;

    /**
     * Define a fixed size for the keys of the map
     */
    int keyCount() default -1;

    /**
     * Define a fixed size for the values of the map
     */
    int valueCount() default -1;

    /**
     * The map implementation. Default is Map, Ccerial will determine automatically
     */
    String mapImpl() default "java.util.Map";

    /**
     * After reading turns the map into an unmodifiable one
     */
    boolean unmodifiableMap() default false;

}
