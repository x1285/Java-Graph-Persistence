package de.x1285.jgp.query.builder.tinkerpop;

import de.x1285.jgp.api.annotation.EdgeDirection;
import de.x1285.jgp.element.GraphEdge;
import de.x1285.jgp.element.GraphElement;
import de.x1285.jgp.element.GraphVertex;
import de.x1285.jgp.element.SimpleGraphEdge;
import de.x1285.jgp.metamodel.MetaModel;
import de.x1285.jgp.metamodel.field.EdgeCollectionField;
import de.x1285.jgp.metamodel.field.EdgeField;
import de.x1285.jgp.metamodel.field.PropertyField;
import de.x1285.jgp.metamodel.field.RelevantField;
import de.x1285.jgp.query.builder.QueryBuilder;
import de.x1285.jgp.query.builder.QueryBuilderException;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__;
import org.apache.tinkerpop.gremlin.structure.T;

import java.util.Collection;
import java.util.List;
import java.util.function.Function;

import static org.apache.tinkerpop.gremlin.structure.VertexProperty.Cardinality.single;

public class GraphTraversalQueryBuilder extends QueryBuilder<List<GraphTraversalQuery>> {

    @Override
    public List<GraphTraversalQuery> add(Collection<? extends GraphElement> elements) {
        GraphTraversalQueryBuilderContext context = new GraphTraversalQueryBuilderContext();
        for (GraphElement element : elements) {
            add(element, context);
        }
        return context.getResult();
    }

    @Override
    public List<GraphTraversalQuery> add(GraphElement element) {
        GraphTraversalQueryBuilderContext context = new GraphTraversalQueryBuilderContext();
        add(element, context);
        return context.getResult();
    }

    private void add(GraphElement element, GraphTraversalQueryBuilderContext context) {
        if (context.wasHandled(element)) {
            return;
        } else if (element instanceof GraphVertex) {
            addVertex((GraphVertex) element, context);
        } else if (element instanceof GraphEdge) {
            addEdge((GraphEdge<?, ?>) element, context);
        }
    }

    private void addVertex(GraphVertex vertex, GraphTraversalQueryBuilderContext context) {
        final MetaModel metaModel = context.getMetaModel(vertex);
        final String alias = context.generateAlias();
        final GraphTraversalQuery graphTraversalQuery = GraphTraversalQuery.of(vertex, alias);
        context.addToResult(graphTraversalQuery);

        Function<GraphTraversalSource, GraphTraversal<?, ?>> query;

        if (vertex.getId() == null) {
            query = gts -> addVertexAndProperties(vertex, context).apply(gts::addV);
        } else {
            Function<Function<String, GraphTraversal<?, ?>>, GraphTraversal<?, ?>> addVQuery = addVertexAndProperties(vertex, context);
            query = g -> g.V(vertex.getId()).fold().coalesce(__.unfold(), addVQuery.apply(__.start()::addV));
            query = query.andThen(g -> g.property(T.id, vertex.getId()));
        }
        query = query.andThen(GraphTraversal::id);
        graphTraversalQuery.setQuery(query);

        for (RelevantField<? extends GraphElement, ?, ?> relevantField : metaModel.getRelevantFields()) {
            if (relevantField instanceof EdgeCollectionField) {
                addEdgeCollectionSteps(vertex, (EdgeCollectionField<?, ?, ?>) relevantField, context);
            } else if (relevantField instanceof EdgeField) {
                addEdgeSteps(vertex, (EdgeField<?, ?>) relevantField, context);
            }
        }
    }

    private Function<Function<String, GraphTraversal<?, ?>>, GraphTraversal<?, ?>>
    addVertexAndProperties(GraphVertex vertex, GraphTraversalQueryBuilderContext context) {
        final MetaModel metaModel = context.getMetaModel(vertex);
        Function<Function<String, GraphTraversal<?, ?>>, GraphTraversal<?, ?>> addVQuery = g -> g.apply(vertex.getLabel());
        for (RelevantField<? extends GraphElement, ?, ?> relevantField : metaModel.getRelevantFields()) {
            if (relevantField instanceof PropertyField) {
                addVQuery = addVQuery.andThen(createPropertyStep(vertex, relevantField));
            }
        }
        return addVQuery;
    }

    private void addEdge(GraphEdge<?, ?> edge, GraphTraversalQueryBuilderContext context) {
        final GraphTraversalQuery graphTraversalQuery = GraphTraversalQuery.of(edge);
        context.addToResult(graphTraversalQuery);

        prepareAddOutAndInVerticesForEdges(edge, context);

        Function<GraphTraversalSource, GraphTraversal<?, ?>> query;
        if (edge.getId() == null) {
            query = gts -> addEdgeAndProperties(edge, context).apply(gts);
        } else {
            Function<GraphTraversalSource, GraphTraversal<?, ?>> addEQuery = addEdgeAndProperties(edge, context);
            query = g -> g.E(edge.getId()).fold().coalesce(__.unfold(), addEQuery.apply(g));
        }
        query = query.andThen(GraphTraversal::id);
        graphTraversalQuery.setQuery(query);
    }

    private Function<GraphTraversalSource, GraphTraversal<?, ?>>
    addEdgeAndProperties(GraphEdge<?, ?> edge, GraphTraversalQueryBuilderContext context) {
        final GraphElement outVertex = getQueryOrThrow(edge.getOutVertex(), context).getElement();
        final GraphElement inVertex = getQueryOrThrow(edge.getInVertex(), context).getElement();

        Function<GraphTraversalSource, GraphTraversal<?, ?>> addEQuery =
                g -> g.addE(edge.getLabel())
                      .from(__.V(outVertex.getId()))
                      .to(__.V(inVertex.getId()));
        final MetaModel metaModel = context.getMetaModel(edge);
        for (RelevantField<? extends GraphElement, ?, ?> relevantField : metaModel.getRelevantFields()) {
            if (relevantField instanceof PropertyField) {
                addEQuery = addEQuery.andThen(createPropertyStep(edge, relevantField));
            } else {
                final String message = String.format("Unsupported edge annotation type %s at class %s (field: %s)",
                                                     relevantField.getAnnotation().getClass(),
                                                     edge.getClass(),
                                                     relevantField.getFieldName());
                throw new QueryBuilderException(message);
            }
        }
        return addEQuery;
    }

    private void prepareAddOutAndInVerticesForEdges(GraphEdge<?, ?> edge, GraphTraversalQueryBuilderContext context) {
        add(edge.getOutVertex(), context);
        add(edge.getInVertex(), context);
    }

    private GraphTraversalQuery getQueryOrThrow(GraphElement element, GraphTraversalQueryBuilderContext context)
            throws QueryBuilderException {
        return context.getResultFor(element).orElseThrow(
                () -> new QueryBuilderException("Internal error: Required parental query for element not found."));
    }

    private void addEdgeCollectionSteps(GraphElement element,
                                        EdgeCollectionField<?, ?, ?> edgeCollectionField,
                                        GraphTraversalQueryBuilderContext context) {
        Collection<?> values = edgeCollectionField.getGetter().apply(element);
        if (values != null) {
            for (Object value : values) {
                if (value != null) {
                    if (value instanceof GraphElement) {
                        add((GraphElement) value, context);
                    } else {
                        String message = String.format("Unsupported type %s declared in edge collection at element %s (field %s)",
                                                       value.getClass(), element.getClass(), edgeCollectionField.getFieldName());
                        throw new QueryBuilderException(message);
                    }
                }
            }
        }
    }

    private void addEdgeSteps(GraphVertex vertex,
                              EdgeField<?, ?> edgeField,
                              GraphTraversalQueryBuilderContext context) {
        Object value = edgeField.getGetter().apply(vertex);
        if (value != null) {
            if (value instanceof GraphVertex) {
                GraphVertex oppositeElement = (GraphVertex) value;
                add(oppositeElement, context);

                GraphVertex outVertex = edgeField.getDirection() == EdgeDirection.OUT ? vertex : oppositeElement;
                GraphVertex inVertex = edgeField.getDirection() == EdgeDirection.OUT ? oppositeElement : vertex;
                SimpleGraphEdge<?, ?> edgeElement = new SimpleGraphEdge<>(edgeField.getLabel(), outVertex, inVertex);
                add(edgeElement, context);
            } else if (value instanceof GraphEdge) {
                add((GraphEdge<?, ?>) value, context);
            } else {
                String message = String.format("Unsupported type %s declared as edge at vertex %s (field %s)",
                                               value.getClass(), vertex.getClass(), edgeField.getFieldName());
                throw new QueryBuilderException(message);
            }
        }
    }

    @Override
    public List<GraphTraversalQuery> update(Collection<? extends GraphElement> elements) {
        // TODO: 04.05.2022  
        return null;
    }

    @Override
    public List<GraphTraversalQuery> update(GraphElement element) {
        // TODO: 04.05.2022  
        return null;
    }

    @Override
    public List<GraphTraversalQuery> drop(Collection<? extends GraphElement> elements) {
        // TODO: 04.05.2022  
        return null;
    }

    @Override
    public List<GraphTraversalQuery> drop(GraphElement element) {
        // TODO: 04.05.2022
        return null;
    }

    private Function<GraphTraversal<?, ?>, GraphTraversal<?, ?>> createPropertyStep(GraphElement element,
                                                                                    RelevantField<? extends GraphElement, ?, ?> relevantField) {
        return g -> {
            final String label = relevantField.getLabel();
            final Object value = getValue(element, relevantField);
            return g.property(single, label, value);
        };
    }
}
