package de.x1285.jgp.metamodel;

import de.x1285.jgp.element.GraphElement;
import de.x1285.jgp.metamodel.field.RelevantField;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@EqualsAndHashCode(of = "elementClass")
public class MetaModel<E extends GraphElement> {

    private final List<RelevantField<E, ?, ?>> relevantFields;
    private final Class<? extends GraphElement> elementClass;

    public MetaModel(Class<E> elementClass, List<RelevantField<E, ?, ?>> relevantFields) {
        this.elementClass = elementClass;
        this.relevantFields = relevantFields;
    }

}
