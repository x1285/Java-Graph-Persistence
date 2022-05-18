package de.x1285.jgp.metamodel.field;

import de.x1285.jgp.api.annotation.Edge;
import de.x1285.jgp.api.annotation.EdgeDirection;
import de.x1285.jgp.element.GraphVertex;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EdgeField<E extends GraphVertex, T> extends RelevantField<E, T, Edge> {

    private Class<E> elementClass;
    private Class<T> type;

    public Class<?> getOutType() {
        return getAnnotation().direction() == EdgeDirection.OUT ? elementClass : type;
    }

    public Class<?> getInType() {
        return getAnnotation().direction() == EdgeDirection.IN ? elementClass : type;
    }

}
