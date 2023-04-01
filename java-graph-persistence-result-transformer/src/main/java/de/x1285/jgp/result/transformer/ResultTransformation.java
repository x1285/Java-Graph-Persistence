package de.x1285.jgp.result.transformer;

import de.x1285.jgp.element.GraphEdge;
import de.x1285.jgp.element.GraphElement;
import de.x1285.jgp.element.GraphVertex;
import de.x1285.jgp.result.transformer.type.resolver.GraphElementLabelToClassResolver;
import lombok.RequiredArgsConstructor;
import org.apache.tinkerpop.gremlin.structure.Edge;
import org.apache.tinkerpop.gremlin.structure.Graph;
import org.apache.tinkerpop.gremlin.structure.T;
import org.apache.tinkerpop.gremlin.structure.Vertex;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
class ResultTransformation {

    private final Graph graph;
    private final GraphElementLabelToClassResolver classResolver;
    private final Map<Object, GraphVertex> verticesCache = new LinkedHashMap<>();
    private final Map<Object, GraphEdge<?,?>> edgesCache = new LinkedHashMap<>();

    List<GraphElement> collectElementsAsList() {
        ArrayList<GraphElement> elementList = new ArrayList<>();
        elementList.addAll(verticesCache.values());
        elementList.addAll(edgesCache.values());
        // TODO: 30.03.2023 connect vertices for edges and edges to vertices
        return elementList;
    }

    void readObject(Object object) {
        if (object instanceof Vertex) {
            Vertex vertex = (Vertex) object;
            final Map<Object, Object> elementMap = graph.traversal().V(vertex.id()).elementMap().next();
            readMap(elementMap);
        } else if (object instanceof Edge) {
            Edge edge = (Edge) object;
            final Map<Object, Object> elementMap = graph.traversal().E(edge.id()).elementMap().next();
            readMap(elementMap);
        } else {
            throw new ResultTransformationException("Unsupported object class " + object.getClass());
        }
    }

    void readMap(Map<Object, Object> rawResult) {
        GraphElement graphElement = createGraphElement(rawResult);
        putElement(graphElement);
    }

    private GraphElement createGraphElement(Map<Object, Object> rawResult) {
        final Object label = rawResult.get(T.label);
        Class<? extends GraphElement> graphElementClass = classResolver.resolveClass(label);
        try {
            GraphElement graphElement = graphElementClass.newInstance();
            final Object id = rawResult.get(T.id);
            graphElement.setId(id);
            return graphElement;
        } catch (InstantiationException | IllegalAccessException e) {
            throw new ResultTransformationException("Could not instantiate " + graphElementClass
                                                            + ". No-arg constructor needed for de-/serialization.");
        }
    }

    private void putElement(GraphElement graphElement) {
        if (graphElement instanceof GraphVertex) {
            verticesCache.put(graphElement.getId(), (GraphVertex) graphElement);
        } else if (graphElement instanceof GraphEdge) {
            edgesCache.put(graphElement.getId(), (GraphEdge<?, ?>) graphElement);
        }
    }
}
