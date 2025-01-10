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

