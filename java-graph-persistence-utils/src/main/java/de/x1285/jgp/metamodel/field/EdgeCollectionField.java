package de.x1285.jgp.metamodel.field;

import de.x1285.jgp.api.annotation.EdgeDirection;
import de.x1285.jgp.element.GraphVertex;
import lombok.Getter;
import lombok.Setter;

import java.util.Collection;

@Getter
@Setter
public class EdgeCollectionField<E extends GraphVertex, T extends Collection<G>, G> extends EdgeField<E, T> {

    private Class<G> genericType;

    @Override
    public Class<?> getOutType() {
        return getDirection() == EdgeDirection.OUT ? getElementClass() : genericType;
    }

    @Override
    public Class<?> getInType() {
        return getDirection() == EdgeDirection.IN ? getElementClass() : genericType;
    }

}
