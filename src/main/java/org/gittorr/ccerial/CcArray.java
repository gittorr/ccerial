package org.gittorr.ccerial;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * Override the default behavior for arrays, collections or strings
 */
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.RECORD_COMPONENT})
@Retention(RetentionPolicy.CLASS)
public @interface CcArray {

    /**
     * Define a fixed size for an array, collection or string
     */
    int count() default -1;

    boolean nullIsEmpty() default true;

    /**
     * Change the charset for strings
     */
    String stringCharsetName() default "UTF-8";

    /**
     * Serialize a string as an array of chars. The difference is that it will use wide char instead of a defined charset.
     */
    boolean stringAsCharArray() default false;

}
