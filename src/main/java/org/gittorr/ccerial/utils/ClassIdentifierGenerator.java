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

/**
 * The {@code ClassIdentifierGenerator} interface defines a contract for generating unique
 * integer identifiers for class names. This is typically used in the Ccerial framework
 * to associate classes with their respective binary serialization identifiers.
 *
 * <p>It includes a default implementation, {@link FNV1aClassIdentifierGenerator}, which uses
 * the FNV-1a hash algorithm to generate compact and consistent identifiers.</p>
 */
public interface ClassIdentifierGenerator {

    /**
     * Generates an integer identifier for the given class name.
     *
     * @param className the fully qualified name of the class
     * @return a unique integer identifier for the class name
     */
    int generateIdentifier(String className);

    // Default instance of ClassIdentifierGenerator using the FNV-1a hashing algorithm.
    final ClassIdentifierGenerator INSTANCE = new FNV1aClassIdentifierGenerator();

    /*
     * A default implementation of ClassIdentifierGenerator that uses the
     * FNV-1a hashing algorithm to generate unique identifiers for class names.
     */
    class FNV1aClassIdentifierGenerator implements ClassIdentifierGenerator {
        private static final int FNV_OFFSET_BASIS = 0x811C9DC5;
        private static final int FNV_PRIME = 0x01000193;

        public int generateIdentifier(String className) {
            int hash = FNV_OFFSET_BASIS;
            for (char c : className.toCharArray()) {
                hash ^= c;
                hash *= FNV_PRIME;
            }
            return hash;
        }
    }

}