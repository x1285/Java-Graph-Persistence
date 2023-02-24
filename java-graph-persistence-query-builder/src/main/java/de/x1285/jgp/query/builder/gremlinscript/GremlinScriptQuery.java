package de.x1285.jgp.query.builder.gremlinscript;

import de.x1285.jgp.element.GraphEdge;
import de.x1285.jgp.element.GraphElement;
import de.x1285.jgp.query.builder.Query;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@SuperBuilder
public class GremlinScriptQuery extends Query<String> {

    @Getter
    private final String alias;

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

    @Override
    public String toString() {
        return "GremlinScriptQuery{element.label='" + element.getLabel() + "', query='" + query + "'}";
    }
}
