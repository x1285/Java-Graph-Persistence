package de.x1285.jgp.query.builder.tinkerpop;

import de.x1285.jgp.element.GraphEdge;
import de.x1285.jgp.element.GraphElement;
import de.x1285.jgp.element.GraphVertex;
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
        if (!context.wasHandled(element)) {
            context.addHandled(element);
            final MetaModel metaModel = context.getMetaModel(element);
            if (element instanceof GraphVertex) {
                addVertex((GraphVertex) element, context, metaModel);
            } else if (element instanceof GraphEdge) {
                addEdge((GraphEdge<?, ?>) element, context, metaModel);
            }
        }
    }

    private void addVertex(GraphVertex vertex, GraphTraversalQueryBuilderContext context, MetaModel metaModel) {
        final GraphTraversalQuery graphTraversalQuery = GraphTraversalQuery.of(vertex);
        context.addToResult(graphTraversalQuery);

        Function<GraphTraversalSource, GraphTraversal<?, ?>> query;

        if (vertex.getId() == null) {
            query = gts -> addVertexAndProperties(vertex, metaModel).apply(gts::addV);
        } else {
            Function<Function<String, GraphTraversal<?, ?>>, GraphTraversal<?, ?>> addVQuery = addVertexAndProperties(vertex, metaModel);
            query = g -> g.V(vertex.getId()).fold().coalesce(__.unfold(), addVQuery.apply(__.start()::addV));
            query = query.andThen(g -> g.property(T.id, vertex.getId()));
        }
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
        addVertexAndProperties(GraphVertex vertex, MetaModel metaModel) {
        Function<Function<String, GraphTraversal<?, ?>>, GraphTraversal<?, ?>> addVQuery = g -> g.apply(vertex.getLabel());
        for (RelevantField<? extends GraphElement, ?, ?> relevantField : metaModel.getRelevantFields()) {
            if (relevantField instanceof PropertyField) {
                addVQuery = addVQuery.andThen(createPropertyStep(vertex, relevantField));
            }
        }
        return addVQuery;
    }

    private void addEdge(GraphEdge<?, ?> edge, GraphTraversalQueryBuilderContext context, MetaModel metaModel) {
       new QueryBuilderException("Not implemented yet");
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

    private void addEdgeSteps(GraphElement element,
                              EdgeField<?, ?> edgeField,
                              GraphTraversalQueryBuilderContext context) {
        Object value = edgeField.getGetter().apply(element);
        if (value != null) {
            if (value instanceof GraphVertex) {
                GraphVertex oppositeElement = (GraphVertex) value;
                add(oppositeElement, context);
                // TODO: 02.08.2022 add edge and properties
            } else if (value instanceof GraphEdge) {
                // TODO: 02.08.2022 handle rich edge: add edge, vertices and properties
            } else {
                String message = String.format("Unsupported type %s declared as edge at element %s (field %s)",
                                               value.getClass(), element.getClass(), edgeField.getFieldName());
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
