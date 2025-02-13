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
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Override the default behavior for arrays, collections or strings
 */
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.RECORD_COMPONENT})
@Retention(RetentionPolicy.CLASS)
public @interface CcArray {

    /**
     * Define a fixed size for an array, collection or string
     */
    int count() default -1;

    /**
     * If the array or the string is empty then it deserializes to null
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
     * Define a fixed size for the components an array or collection
     */
    int componentCount() default -1;

    /**
     * The collection implementation for collections. Default is Collection, Ccerial will determine automatically
     */
    String collectionImpl() default "java.util.Collection";

    /**
     * After reading turns the collection into an unmodifiable one
     */
    boolean unmodifiableCollection() default false;

}
