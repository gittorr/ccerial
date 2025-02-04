package org.gittorr.ccerial;

/**
 * The {@code SerializerFeature} enum represents configurable serialization features
 * that can be enabled or disabled to customize the serialization process.
 */
public enum SerializerFeature {

    /**
     * Forces the inclusion of headers in the serialized output.
     */
    FORCE_HEADERS,

    /**
     * Skips headers during serialization.
     */
    SKIP_HEADERS,

    /**
     * Forces all fields to be serialized with variable size.
     */
    FORCE_VARIABLE_SIZE,

    /**
     * Treats zero or empty values as null during serialization or deserialization.
     */
    FORCE_ZERO_OR_EMPTY_AS_NULL;
}
