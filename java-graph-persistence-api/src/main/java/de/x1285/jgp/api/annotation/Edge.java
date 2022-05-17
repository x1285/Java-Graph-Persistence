package de.x1285.jgp.api.annotation;

import de.x1285.jgp.element.GraphEdge;
import de.x1285.jgp.element.GraphVertex;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Defines a field as a that should be interpreted as Edge. The fields type needs to be a subtype of
 * {@link GraphEdge}, a {@link GraphVertex} or a {@link java.util.Collection} of one of these.
 *
 * <pre>
 *   Example:
 *
 *   &#064;Edge
 *   private Relationship relationshipToBestFriend;
 * </pre>
 */
@Target(FIELD)
@Retention(RUNTIME)
public @interface Edge {

    /**
     * (Optional) The label of the edge.
     * <p>
     * Defaults to the field name.
     * </p>
     */
    String label() default "";

    /**
     * (Optional) The direction of the edge.
     * This only takes effect on annotated field which types are a subtype of {@link GraphVertex} or a
     * {@link java.util.Collection} of those.
     * <p>
     * Defaults to {@link EdgeDirection#OUT}.
     * </p>
     */
    EdgeDirection direction() default EdgeDirection.OUT;
}
