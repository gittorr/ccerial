package org.gittorr.ccerial;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation marks a class as a Ccerializable, it means that the annotated class can be serialized with Ccerial, and it will generate serializers for it.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.CLASS)
public @interface CcSerializable {

    /**
     * The accessor type defines if the fields will be accessed through  SETTER, FIELD, CONSTRUCTOR
     */
    AccessorType accessorType() default AccessorType.CONSTRUCTOR;

    /**
     * The fields described in this field will be used to serialize including their order. * for all and a comma separated list to define the fields.
     */
    String properties() default "*";

    /**
     * An identifier for the class. Used for complex objects.
     */
    int classIdentifier() default 0;

    /**
     * The fields can be serialized as variable size.
     */
    boolean variableSize() default true;

    /**
     * When serializing a null or empty become a zero.
     */
    boolean nullIsZeroOrEmpty() default true;

    /**
     * Include some headers if necessary
     */
    boolean includeHeader() default false;

}
