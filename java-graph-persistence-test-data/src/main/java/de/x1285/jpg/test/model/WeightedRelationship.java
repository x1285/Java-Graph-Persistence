package de.x1285.jpg.test.model;

import de.x1285.jgp.api.annotation.Property;
import de.x1285.jgp.element.GraphEdge;
import de.x1285.jgp.element.GraphVertex;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class WeightedRelationship<O extends GraphVertex, I extends GraphVertex> extends GraphEdge<O, I> {

    @Property
    private double weight;

}
