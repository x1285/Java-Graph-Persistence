package de.x1285.jgp.metamodel.field;

import de.x1285.jgp.element.GraphVertex;
import lombok.Getter;
import lombok.Setter;

import java.util.Collection;

@Getter
@Setter
public class EdgeCollectionField<E extends GraphVertex, T extends Collection<G>, G> extends EdgeField<E, T> {

    private Class<G> genericType;

}
