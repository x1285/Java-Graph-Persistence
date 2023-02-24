package de.x1285.jgp.query.builder.tinkerpop;

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal;
import org.apache.tinkerpop.gremlin.process.traversal.translator.GroovyTranslator;

public class GraphTraversalQueryTranslator {
    public static String translate(GraphTraversal<?, ?> traversal) {
        String translatedQuery = GroovyTranslator.of("g")
                                                 .translate(traversal)
                                                 .getScript();
        // @formatter:off
        return translatedQuery.replace("__.", "")
                              .replace("(int) ", "")
                              .replace("\"\"\"[\\\"", "'[\"")
                              .replace("\\\"]\"\"\"", "\"]'")
                              .replace("\\\"\"\"\"", "\"'")
                              .replace("\"\"\"\\\"", "'\"")
                              .replace(".property(VertexProperty.Cardinality.single,", ".property(single,")
                              .replace("L)", ")")
                              .replace("\\/", "/")
                              .replace(".select(Pop.all,", ".select(all,");
    }
}
