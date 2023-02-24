package de.x1285.jgp.query.builder.tinkerpop;

import de.x1285.jgp.element.GraphElement;
import de.x1285.jgp.query.builder.Query;
import lombok.NonNull;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.structure.util.empty.EmptyGraph;

import java.util.function.Function;

public class GraphTraversalQuery extends Query<Function<GraphTraversalSource, GraphTraversal<?, ?>>> {

    public GraphTraversalQuery(@NonNull GraphElement element) {
        super(element);
    }

    public GraphTraversal<?, ?> execute(GraphTraversalSource g) {
        return query.apply(g);
    }

    public static GraphTraversalQuery of(GraphElement element) {
        return new GraphTraversalQuery(element);
    }

    public String toGremlinScript() {
        final GraphTraversal<?, ?> g = query.apply(new GraphTraversalSource(EmptyGraph.instance()));
        return GraphTraversalQueryTranslator.translate(g);
    }

    @Override
    public String toString() {
        return "GraphTraversalQuery{element.label='" + element.getLabel() + "', query='" + toGremlinScript() + "'}";
    }
}
