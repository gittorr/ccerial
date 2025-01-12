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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * The {@code Serializer} interface defines the contract for serializing and deserializing
 * objects to and from binary streams in the Ccerial framework.
 * <p>
 * Implementations of this interface are responsible for converting Java objects into a
 * compact binary format and reconstructing objects from that format.
 * </p>
 *
 * @param <E> the type of object that this serializer handles
 */
public interface Serializer<E> {

    /**
     * Serializes the specified entity into a binary format and writes it to the provided {@link OutputStream}.
     *
     * @param out the {@link OutputStream} where the serialized data will be written
     * @param entity the entity to serialize
     * @throws IOException if an I/O error occurs during serialization
     */
    void serialize(OutputStream out, E entity) throws IOException;

    /**
     * Deserializes data from the provided {@link InputStream} and reconstructs an instance of the object.
     *
     * @param in the {@link InputStream} containing the binary data to deserialize
     * @return the reconstructed object of type {@code E}
     * @throws IOException if an I/O error occurs during deserialization
     */
    E deserialize(InputStream in) throws IOException;

}
