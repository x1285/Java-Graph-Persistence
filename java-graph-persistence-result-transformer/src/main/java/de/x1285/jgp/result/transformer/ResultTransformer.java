package de.x1285.jgp.result.transformer;

import de.x1285.jgp.element.GraphElement;
import de.x1285.jgp.result.transformer.type.resolver.global.GlobalGraphElementClassResolver;
import org.apache.tinkerpop.gremlin.process.traversal.Path;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal;
import org.apache.tinkerpop.gremlin.structure.Graph;

import java.util.List;
import java.util.Map;

public class ResultTransformer {

    public List<GraphElement> toBeans(Graph graph, GraphTraversal<?, ?> traversal) {
        final ResultTransformation transformation = newResultTransformation(graph);
        while (traversal.hasNext()) {
            Object rawResult = traversal.next();
            if (rawResult instanceof Map) {
                transformation.readMap((Map<Object, Object>) rawResult);
            } else if (rawResult instanceof Path) {
                final Path path = (Path) rawResult;
                path.objects().forEach(transformation::readObject);
            } else {
                transformation.readObject(rawResult);
            }
        }
        return transformation.collectElementsAsList();
    }

    private ResultTransformation newResultTransformation(Graph graph) {
        return new ResultTransformation(graph, new GlobalGraphElementClassResolver());
    }
}
