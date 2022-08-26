package de.x1285.jgp.query.builder.gremlinscript;

import de.x1285.jgp.element.GraphEdge;
import de.x1285.jgp.element.GraphElement;
import de.x1285.jgp.element.GraphVertex;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

@Builder
public class GremlinScriptQuery {

    @Getter
    @NonNull
    private final GraphElement element;

    @Getter
    private final String alias;

    @Getter
    @Setter
    private String query;

    public static GremlinScriptQuery of(GraphElement element, String query, String alias) {
        return GremlinScriptQuery.builder()
                                 .query(query)
                                 .element(element)
                                 .alias(alias)
                                 .build();
    }

    public static GremlinScriptQuery of(GraphElement element, String alias) {
        return GremlinScriptQuery.builder()
                                 .element(element)
                                 .alias(alias)
                                 .build();
    }

    public static GremlinScriptQuery ofEdge(GraphEdge<?, ?> edge, String query) {
        return GremlinScriptQuery.builder()
                                 .query(query)
                                 .element(edge)
                                 .build();
    }

    public boolean isVertex() {
        return element instanceof GraphVertex;
    }

    @Override
    public String toString() {
        return "GremlinScriptQuery{element.label='" + element.getLabel() + '\'' + ", query=" + query + '}';
    }
}
