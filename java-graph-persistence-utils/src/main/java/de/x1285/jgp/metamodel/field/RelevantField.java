package de.x1285.jgp.metamodel.field;

import de.x1285.jgp.api.annotation.Edge;
import de.x1285.jgp.api.annotation.Property;
import de.x1285.jgp.element.GraphElement;
import de.x1285.jgp.metamodel.MetaModelException;
import lombok.Getter;
import lombok.Setter;

import java.util.function.BiConsumer;
import java.util.function.Function;

@Getter
@Setter
public class RelevantField<E extends GraphElement, T, A> {

    protected A annotation;
    protected String fieldName;
    protected Function<GraphElement, T> getter;
    protected BiConsumer<E, T> setter;

    public String getLabel() {
        if (annotation instanceof Property) {
            final String label = ((Property) annotation).label();
            return isEmpty(label) ? fieldName : label;
        } else if (annotation instanceof Edge) {
            final String label = ((Edge) annotation).label();
            return isEmpty(label) ? fieldName : label;
        } else {
            final String message = String.format("Unknown annotation type %s at field %s." + annotation.getClass(), fieldName);
            throw new MetaModelException(message);
        }
    }

    @Override
    public String toString() {
        return annotation.toString();
    }

    private boolean isEmpty(String string) {
        return string == null || string.trim().isEmpty();
    }
}
