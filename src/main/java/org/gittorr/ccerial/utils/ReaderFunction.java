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

import java.io.IOException;
import java.io.InputStream;

/**
 * Internal class
 * @param <T> the parameter
 */
public interface ReaderFunction<T> {

    /**
     * Read e value from the input stream
     * @param in the input stream
     * @return return the value
     * @throws IOException if any exception happens
     */
    T read(InputStream in) throws IOException;

}
