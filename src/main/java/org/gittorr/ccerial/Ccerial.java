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

import org.gittorr.ccerial.utils.ClassIdentifierGenerator;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Enumeration;
import java.util.Properties;
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

    private static final Map<Class<?>, Constructor<Serializer<?>>> serializersCtorCache = new ConcurrentHashMap<>();
    private static final Map<Integer, Constructor<Serializer<?>>> serializersCtorCacheByOid = new ConcurrentHashMap<>();

    static {
        try {
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            Enumeration<URL> resources = classLoader.getResources("cerial/serializers.properties");
            resources.asIterator().forEachRemaining(url -> {
                Properties props = new Properties();
                try {
                    props.load(new FileInputStream(new File(url.toURI())));
                } catch (IOException | URISyntaxException e) {
                    throw new RuntimeException(e);
                }
                props.forEach((className, serializerClassName) -> {
                    Integer oid = ClassIdentifierGenerator.INSTANCE.generateIdentifier(className.toString());
                    try {
                        Class<?> entityClass = Class.forName(className.toString());
                        @SuppressWarnings("unchecked")
                        Class<Serializer<?>> serializerClass = (Class<Serializer<?>>) Class.forName(serializerClassName.toString());
                        Constructor<Serializer<?>> constructor = serializerClass.getDeclaredConstructor();
                        serializersCtorCache.put(entityClass, constructor);
                        serializersCtorCacheByOid.put(oid, constructor);
                    } catch (NoSuchMethodException | ClassNotFoundException e) {
                        throw new RuntimeException(e);
                    }
                });
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

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

        Constructor<Serializer<?>> constructor = serializersCtorCache.get(forClass);

        try {
            if (constructor == null) {
                // Build the qualified name for the serializer class
                String originalClassName = forClass.getName();
                String serializerClassName = "ccerial." + originalClassName + "_CcerialSerializer";
                // Load the serializer class
                Class<Serializer<?>> serializerClass = (Class<Serializer<?>>) Class.forName(serializerClassName);
                constructor = serializerClass.getDeclaredConstructor();
                serializersCtorCache.put(forClass, constructor);
            }

            return (Serializer<E>) constructor.newInstance();

        } catch (ClassNotFoundException e) {
            String originalClassName = forClass.getName();
            throw new IllegalArgumentException("Serializer class not found for " + originalClassName, e);
        } catch (Exception e) {
            String originalClassName = forClass.getName();
            throw new RuntimeException("Failed to instantiate serializer for " + originalClassName, e);
        }
    }

    public static <E> Serializer<E> getSerializer(int objectId) {
        Constructor<Serializer<?>> constructor = serializersCtorCacheByOid.get(objectId);
        if (constructor == null)
            throw new IllegalArgumentException("Serializer class not found for object ID " + objectId);
        try {
            @SuppressWarnings("unchecked")
            Serializer<E> serializer = (Serializer<E>) constructor.newInstance();
            return serializer;
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new IllegalArgumentException("Failed to instantiate serializer for object ID " + objectId, e);
        }
    }
}
