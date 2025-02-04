package org.gittorr.ccerial;

/**
 * Defines the possible serialization strategies for enums in the Ccerial framework.
 * This enumeration specifies whether an enum should be stored using its ordinal value or its name.
 */
public enum EnumType {

    /**
     * Serializes the enum using its ordinal value (zero-based index in the enum declaration).
     * This method is compact but may lead to compatibility issues if enum values are reordered.
     */
    ORDINAL,

    /**
     * Serializes the enum using its name as a string.
     * This method ensures compatibility even if the order of the enum values changes,
     * but it results in a larger serialized representation.
     */
    STRING;
}