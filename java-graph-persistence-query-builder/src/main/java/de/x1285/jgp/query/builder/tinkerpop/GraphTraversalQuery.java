package de.x1285.jgp.query.builder.tinkerpop;

import de.x1285.jgp.element.GraphEdge;
import de.x1285.jgp.element.GraphVertex;
import de.x1285.jgp.query.builder.Query;
import lombok.experimental.SuperBuilder;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.structure.util.empty.EmptyGraph;

import java.util.function.Function;

@SuperBuilder
public class GraphTraversalQuery extends Query<Function<GraphTraversalSource, GraphTraversal<?, ?>>> {

    public GraphTraversal<?, ?> execute(GraphTraversalSource g) {
        return query.apply(g);
    }

    public static GraphTraversalQuery of(GraphVertex vertex, String alias) {
        return GraphTraversalQuery.builder()
                                  .element(vertex)
                                  .alias(alias)
                                  .build();
    }

    public static GraphTraversalQuery of(GraphEdge<?, ?> edge) {
        return GraphTraversalQuery.builder()
                                  .element(edge)
                                  .build();
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
