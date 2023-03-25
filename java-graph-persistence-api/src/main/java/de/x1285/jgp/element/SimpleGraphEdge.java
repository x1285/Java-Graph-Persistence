package de.x1285.jgp.element;

public class SimpleGraphEdge<O extends GraphVertex, I extends GraphVertex> extends GraphEdge<O, I> {

    private final String label;

    public SimpleGraphEdge(String label, O outVertex, I inVertex) {
        if (label == null) {
            throw new IllegalArgumentException("A SimpleGraphEdge requires a label, but null was given.");
        }
        this.label = label;
        this.outVertex = outVertex;
        this.inVertex = inVertex;
    }

    @Override
    public String getLabel() {
        return label;
    }
}
