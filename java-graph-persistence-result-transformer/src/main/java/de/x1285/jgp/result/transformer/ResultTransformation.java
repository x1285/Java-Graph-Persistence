package de.x1285.jgp.result.transformer;

import de.x1285.jgp.element.GraphEdge;
import de.x1285.jgp.element.GraphElement;
import de.x1285.jgp.element.GraphVertex;
import de.x1285.jgp.metamodel.MetaModel;
import de.x1285.jgp.metamodel.field.EdgeCollectionField;
import de.x1285.jgp.metamodel.field.EdgeField;
import de.x1285.jgp.metamodel.field.PropertyField;
import de.x1285.jgp.metamodel.field.RelevantField;
import de.x1285.jgp.metamodel.provider.CachingMetaModelProvider;
import de.x1285.jgp.metamodel.provider.MetaModelProvider;
import de.x1285.jgp.result.transformer.type.resolver.GraphElementLabelToClassResolver;
import lombok.RequiredArgsConstructor;
import org.apache.tinkerpop.gremlin.structure.Direction;
import org.apache.tinkerpop.gremlin.structure.Edge;
import org.apache.tinkerpop.gremlin.structure.Graph;
import org.apache.tinkerpop.gremlin.structure.T;
import org.apache.tinkerpop.gremlin.structure.Vertex;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RequiredArgsConstructor
class ResultTransformation {

    private final Graph graph;
    private final GraphElementLabelToClassResolver classResolver;
    private final MetaModelProvider metaModelProvider = new CachingMetaModelProvider();
    private final Map<Object, GraphVertex> verticesCache = new LinkedHashMap<>();
    private final Map<Object, GraphEdge<?, ?>> edgesCache = new LinkedHashMap<>();

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
            // TODO: 02.04.2023 access OUT info + access GraphElement class, get RelevantField + create GraphElement + set RelevantField
            // TODO: 02.04.2023 if OUT info not suitable: try IN info
            readMap(elementMap);
        } else {
            throw new ResultTransformationException("Unsupported object class " + object.getClass());
        }
    }

    void readMap(Map<Object, Object> rawResult) {
        GraphElement graphElement = createGraphElement(rawResult);
        putElement(graphElement);
    }

    private void putElement(GraphElement graphElement) {
        if (graphElement instanceof GraphVertex) {
            verticesCache.put(graphElement.getId(), (GraphVertex) graphElement);
        } else if (graphElement instanceof GraphEdge) {
            edgesCache.put(graphElement.getId(), (GraphEdge<?, ?>) graphElement);
        }
    }

    private GraphElement createGraphElement(Map<Object, Object> rawResult) {
        Class<? extends GraphElement> graphElementClass = resolveGraphElementClass(rawResult);
        try {
            GraphElement graphElement = graphElementClass.newInstance();
            setBeanFields(graphElement, rawResult);
            return graphElement;
        } catch (InstantiationException | IllegalAccessException e) {
            throw new ResultTransformationException("Could not instantiate " + graphElementClass
                                                            + ". No-arg constructor needed for de-/serialization.");
        }
    }

    private Class<? extends GraphElement> resolveGraphElementClass(Map<Object, Object> rawResult) {
        Class<? extends GraphElement> graphElementClass;
        if (rawResult.containsKey(Direction.OUT) || rawResult.containsKey(Direction.IN)) {
            graphElementClass = findFieldOfEdgeMapByDirection(rawResult, Direction.OUT)
                    .orElseGet(() -> findFieldOfEdgeMapByDirection(rawResult, Direction.IN)
                            .orElseThrow(() -> {
                                final Object label = rawResult.get(T.label);
                                return new ResultTransformationException("Field not found for edge " + label
                                                                                + " for raw result: " + rawResult);
                            }));
        } else {
            final Object label = rawResult.get(T.label);
            graphElementClass = classResolver.resolveClass(label);
        }
        return graphElementClass;
    }

    private Optional<Class<? extends GraphElement>> findFieldOfEdgeMapByDirection(Map<Object, Object> edgeMap,
                                                                                  Direction direction) {
        // get details of the out or in vertex (direction) and search for the field by the label of the edge
        final Map<Object, Object> vertexMap = (Map<Object, Object>) edgeMap.get(direction);
        Class<? extends GraphElement> vertexClass = classResolver.resolveClass(vertexMap.get(T.label));
        MetaModel<? extends GraphElement> vertexMetaModel = metaModelProvider.getMetaModel(vertexClass);
        final Object label = edgeMap.get(T.label);
        return vertexMetaModel.findRelevantFieldByLabel(label)
                              .map(edgeField -> {
                                  if (edgeField instanceof EdgeCollectionField) {
                                      return ((EdgeCollectionField<?,?,?>) edgeField).getGenericType();
                                  } else {
                                      return ((EdgeField<?,?>) edgeField).getType();
                                  }
                              })
                              .map(clazz -> (Class<? extends GraphElement>) clazz);
    }

    private <E extends GraphElement> void setBeanFields(E graphElement, Map<Object, Object> rawResult) {
        final Object id = rawResult.get(T.id);
        graphElement.setId(id);

        final MetaModel<E> metaModel = (MetaModel<E>) metaModelProvider.getMetaModel(graphElement.getClass());
        for (RelevantField<E, ?, ?> field : metaModel.getRelevantFields()) {
            if (field instanceof PropertyField) {
                final String label = field.getLabel();
                final Object value = rawResult.get(label);
                setField(field, graphElement, value);
            }
        }
    }

    private <E extends GraphElement, T> void setField(RelevantField<E, T, ?> field,
                                                      E graphElement,
                                                      Object value) {
        field.getSetter().accept(graphElement, (T) value);
    }

}