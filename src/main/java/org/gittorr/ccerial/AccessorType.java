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

/**
 * The {@code AccessorType} enum defines the methods by which fields of a class
 * annotated with {@link org.gittorr.ccerial.CcSerializable} are accessed during serialization
 * and deserialization. This allows fine-grained control over how object data is read and written.
 *
 * <h2>Usage:</h2>
 * <ul>
 *     <li>{@link #SETTER}: Uses setter methods to assign values during deserialization.</li>
 *     <li>{@link #FIELD}: Directly accesses fields, bypassing getter/setter methods.</li>
 *     <li>{@link #CONSTRUCTOR}: Uses a constructor to set fields during deserialization.</li>
 * </ul>
 *
 * <h2>Examples:</h2>
 * <pre>{@code
 * @CcSerializable(accessorType = AccessorType.SETTER)
 * public class User {
 *     private String name;
 *     private int age;
 *
 *     public void setName(String name) {
 *         this.name = name;
 *     }
 *     public void setAge(int age) {
 *         this.age = age;
 *     }
 * }
 *
 * @CcSerializable(accessorType = AccessorType.CONSTRUCTOR)
 * public record UserRecord(String name, int age) {}
 * }</pre>
 */
public enum AccessorType {

    /**
     * Access fields using setter methods.
     * This option requires that setter methods are available for all fields.
     */
    SETTER,

    /**
     * Access fields directly, bypassing getter and setter methods.
     * This may require that fields are accessible (e.g., non-private).
     */
    FIELD,

    /**
     * Use the constructor to set all fields during deserialization.
     * This option requires that a compatible constructor is available.
     */
    CONSTRUCTOR;

}

