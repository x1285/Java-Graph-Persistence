package de.x1285.jgp.api.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Defines a field as a property to store and read from the graph database.
 *
 * <pre>
 *   Example:
 *
 *   &#064;Property
 *   private String firstName;
 * </pre>
 */
@Target(FIELD)
@Retention(RUNTIME)
public @interface Property {

    /**
     * (Optional) The label of the property.
     * <p>
     * Defaults to the field name.
     * </p>
     */
    String label() default "";
}
