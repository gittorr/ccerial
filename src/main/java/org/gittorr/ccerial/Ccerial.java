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

import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

/**
 * The {@code Ccerial} class is the main entry point for the Ccerial serialization framework.
 * This utility provides methods to obtain serializers for classes annotated with {@link org.gittorr.ccerial.CcSerializable}.
 *
 * <h2>Usage:</h2>
 * <pre>{@code
 * // Example of obtaining a serializer and serializing an object
 * User user = new User();
 * user.setName("John");
 * user.setAge(30);
 *
 * ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
 * Ccerial.getSerializer(User.class).serialize(user, outputStream);
 *
 * byte[] serializedData = outputStream.toByteArray();
 *
 * ByteArrayInputStream inputStream = new ByteArrayInputStream(serializedData);
 * User deserializedUser = Ccerial.getSerializer(User.class).deserialize(inputStream);
 * }</pre>
 *
 * <h2>See Also:</h2>
 * <ul>
 *     <li>{@link org.gittorr.ccerial.CcSerializable}</li>
 *     <li>{@link org.gittorr.ccerial.CcArray}</li>
 *     <li>{@link org.gittorr.ccerial.CcValue}</li>
 * </ul>
 */
public final class Ccerial {

    private static final Map<Class<?>, Serializer<?>> serializersCache = new ConcurrentHashMap<>();

    /**
     * Retrieves the serializer for the specified class.
     * Classes must be annotated with {@link org.gittorr.ccerial.CcSerializable} to have serializers generated.
     *
     * @param forClass the class to retrieve the serializer for
     * @param <E>      the type of the class being serialized
     * @return the {@link Serializer} instance for the specified class
     * @throws IllegalArgumentException if no serializer is found for the class
     */
    @SuppressWarnings("unchecked")
    public static <E> Serializer<E> getSerializer(Class<E> forClass) {
        // checks if the serializer is in the cache
        Serializer<E> serializer = (Serializer<E>) serializersCache.get(forClass);
        if (serializer != null) {
            return serializer;
        }

        // Build the qualified name for the serializer class
        String originalClassName = forClass.getName();
        String serializerClassName = "ccerial." + originalClassName + "_CcerialSerializer";

        try {
            // Load the serializer class
            Class<?> serializerClass = Class.forName(serializerClassName);

            // Try to access a singleton
            try {
                serializer = (Serializer<E>) serializerClass.getField("INSTANCE").get(null);
            } catch (NoSuchFieldException e) {
                // if the singleton doesn't exist, instantiate one
                serializer = (Serializer<E>) serializerClass.getDeclaredConstructor().newInstance();
            }

            // Store the serializer in the cache
            serializersCache.put(forClass, serializer);

            return serializer;
        } catch (ClassNotFoundException e) {
            throw new IllegalArgumentException("Serializer class not found for " + originalClassName, e);
        } catch (Exception e) {
            throw new RuntimeException("Failed to instantiate serializer for " + originalClassName, e);
        }
    }
}
