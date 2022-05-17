package de.x1285.jgp.element;

import lombok.Getter;

public abstract class GraphEdge<O extends GraphVertex, I extends GraphVertex> extends GraphElement {

    @Getter
    protected O outVertex;
    @Getter
    protected I inVertex;

    @Override
    public String getLabel() {
        final String traversalLabel = this.getClass().getSimpleName();
        return traversalLabel.substring(0, 1).toLowerCase() + traversalLabel.substring(1);
    }

    @Override
    public String toString() {
        return "GraphEdge [outVertex="
                + (outVertex != null ? outVertex.getLabel() + "{id=" + outVertex.getId() + "}" : "")
                + ", inVertex=" + (inVertex != null ? inVertex.getLabel() + "{id=" + inVertex.getId() + "}" : "")
                + "]";
    }
}
