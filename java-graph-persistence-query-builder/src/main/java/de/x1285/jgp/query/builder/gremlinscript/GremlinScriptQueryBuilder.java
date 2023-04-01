package de.x1285.jgp.query.builder.gremlinscript;

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

import java.util.Collection;
import java.util.List;

public class GremlinScriptQueryBuilder extends QueryBuilder<List<GremlinScriptQuery>> {

    @Override
    public List<GremlinScriptQuery> add(Collection<? extends GraphElement> elements) {
        GremlinScriptQueryBuilderContext context = new GremlinScriptQueryBuilderContext();
        for (GraphElement element : elements) {
            add(element, context);
        }
        return context.getResult();
    }

    @Override
    public List<GremlinScriptQuery> add(GraphElement element) {
        GremlinScriptQueryBuilderContext context = new GremlinScriptQueryBuilderContext();
        add(element, context);
        return context.getResult();
    }

    private void add(GraphElement element, GremlinScriptQueryBuilderContext context) {
        if (!context.wasHandled(element)) {
            if (element instanceof GraphVertex) {
                addVertex((GraphVertex) element, context);
            } else if (element instanceof GraphEdge) {
                addEdge((GraphEdge<?, ?>) element, context);
            }
        }
    }

    private <G extends GraphVertex> void addVertex(G vertex, GremlinScriptQueryBuilderContext context) {
        final String alias = context.generateAlias();
        final GremlinScriptQuery gremlinScriptQuery = GremlinScriptQuery.of(vertex, alias);
        context.addToResult(gremlinScriptQuery);

        final StringBuilder addQuery = new StringBuilder("addV(\"").append(vertex.getLabel()).append("\")");
        addQuery.append(".as(\"")
                .append(gremlinScriptQuery.getAlias())
                .append("\")");

        final String idStep = createIdStep(vertex);
        if (idStep != null) {
            addQuery.append(idStep);
        }

        final MetaModel<G> metaModel = (MetaModel<G>) context.getMetaModel(vertex);
        for (RelevantField<G, ?, ?> relevantField : metaModel.getRelevantFields()) {
            if (relevantField instanceof PropertyField) {
                final String propertyStep = createPropertyStep(vertex, relevantField);
                addQuery.append(propertyStep);
            } else if (relevantField instanceof EdgeCollectionField) {
                addEdgeCollectionSteps(vertex, (EdgeCollectionField<?, ?, ?>) relevantField, context);
            } else if (relevantField instanceof EdgeField) {
                addEdgeSteps(vertex, (EdgeField<?, ?>) relevantField, context);
            }
        }

        if (vertex.getId() == null) {
            gremlinScriptQuery.setQuery("g." + addQuery);
        } else {
            final String query = String.format("g.V(%s).fold().coalesce(unfold(), %s).as(\"%s\")",
                                               vertex.getId(), addQuery, alias);
            gremlinScriptQuery.setQuery(query);
        }
    }

    private <G extends GraphEdge<?, ?>> void addEdge(G edge, GremlinScriptQueryBuilderContext context) {
        final StringBuilder addQuery = new StringBuilder("addE(\"").append(edge.getLabel()).append("\")");

        // handle .from and .to
        prepareAddOutAndInVerticesForEdges(edge, context);
        final String outVertexAlias = getQueryOrThrow(edge.getOutVertex(), context).getAlias();
        final String inVertexAlias = getQueryOrThrow(edge.getInVertex(), context).getAlias();
        addQuery.append(".from(\"")
                .append(outVertexAlias)
                .append("\").to(\"")
                .append(inVertexAlias)
                .append("\")");

        String idStep = createIdStep(edge);
        if (idStep != null) {
            addQuery.append(idStep);
        }

        final MetaModel<G> metaModel = (MetaModel<G>) context.getMetaModel(edge);
        for (RelevantField<G, ?, ?> relevantField : metaModel.getRelevantFields()) {
            if (relevantField instanceof PropertyField) {
                final String propertyStep = createPropertyStep(edge, relevantField);
                addQuery.append(propertyStep);
            } else {
                final String message = String.format("Unsupported edge annotation type %s at class %s (field: %s)",
                                                     relevantField.getAnnotation().getClass(),
                                                     edge.getClass(),
                                                     relevantField.getFieldName());
                throw new QueryBuilderException(message);
            }
        }

        if (edge.getId() == null) {
            final GremlinScriptQuery gremlinScriptQuery = GremlinScriptQuery.ofEdge(edge, "g." + addQuery);
            context.addToResult(gremlinScriptQuery);
        } else {
            final String query = String.format("g.E(%s).fold().coalesce(unfold(), %s)", edge.getId(), addQuery);
            final GremlinScriptQuery gremlinScriptQuery = GremlinScriptQuery.ofEdge(edge, query);
            context.addToResult(gremlinScriptQuery);
        }
    }

    private void prepareAddOutAndInVerticesForEdges(GraphEdge<?, ?> edge, GremlinScriptQueryBuilderContext context) {
        add(edge.getOutVertex(), context);
        add(edge.getInVertex(), context);
    }

    private GremlinScriptQuery getQueryOrThrow(GraphElement element, GremlinScriptQueryBuilderContext context)
            throws QueryBuilderException {
        return context.getResultFor(element).orElseThrow(() -> new QueryBuilderException(""));
    }

    private void addEdgeCollectionSteps(GraphVertex vertex,
                                        EdgeCollectionField<?, ?, ?> edgeCollectionField,
                                        GremlinScriptQueryBuilderContext context) {
        Collection<?> graphElements = edgeCollectionField.getGetter().apply(vertex);
        if (graphElements != null) {
            for (Object graphElement : graphElements) {
                if (graphElement != null) {
                    if (graphElement instanceof GraphElement) {
                        addEdgeOfVertexToGraphElement(vertex, (GraphElement) graphElement, edgeCollectionField, context);
                    } else {
                        String message = String.format("Unsupported type %s declared in edge collection at element %s (field %s)",
                                                       graphElement.getClass(),
                                                       edgeCollectionField.getClass(),
                                                       edgeCollectionField.getFieldName());
                        throw new QueryBuilderException(message);
                    }
                }
            }
        }
    }

    protected void addEdgeSteps(GraphVertex vertex,
                                EdgeField<?, ?> edgeField,
                                GremlinScriptQueryBuilderContext context) {
        Object graphElement = edgeField.getGetter().apply(vertex);
        if (graphElement instanceof GraphElement) {
            addEdgeOfVertexToGraphElement(vertex, (GraphElement) graphElement, edgeField, context);
        } else if (graphElement != null) {
            String message = String.format("Unsupported type %s declared as edge at element %s (field %s)",
                                           graphElement.getClass(), vertex.getClass(), edgeField.getFieldName());
            throw new QueryBuilderException(message);
        }
    }

    private void addEdgeOfVertexToGraphElement(GraphVertex vertex, GraphElement graphElement, EdgeField<?, ?> edgeField, GremlinScriptQueryBuilderContext context) {
        if (graphElement instanceof GraphVertex) {
            GraphVertex oppositeElement = (GraphVertex) graphElement;
            add(oppositeElement, context);

            GraphVertex outVertex = edgeField.getDirection() == EdgeDirection.OUT ? vertex : oppositeElement;
            GraphVertex inVertex = edgeField.getDirection() == EdgeDirection.OUT ? oppositeElement : vertex;
            SimpleGraphEdge<?, ?> edgeElement = new SimpleGraphEdge<>(edgeField.getLabel(), outVertex, inVertex);
            add(edgeElement, context);
        } else if (graphElement instanceof GraphEdge) {
            add(graphElement, context);
        } else if (graphElement != null) {
            String message = String.format("Unsupported type %s declared as edge at vertex %s (field %s)",
                                           graphElement.getClass(), vertex.getClass(), edgeField.getFieldName());
            throw new QueryBuilderException(message);
        }
    }

    private String createIdStep(GraphElement element) {
        Object id = element.getId();
        if (id != null) {
            checkIdValueSupport(id, element);
            return String.format(".property(T.id, %s)", transformValue(id));
        }
        return null;
    }

    @Override
    public List<GremlinScriptQuery> update(Collection<? extends GraphElement> elements) {
        // TODO: 04.05.2022  
        return null;
    }

    @Override
    public List<GremlinScriptQuery> update(GraphElement element) {
        // TODO: 04.05.2022  
        return null;
    }

    @Override
    public List<GremlinScriptQuery> drop(Collection<? extends GraphElement> elements) {
        // TODO: 04.05.2022  
        return null;
    }

    @Override
    public List<GremlinScriptQuery> drop(GraphElement element) {
        // TODO: 04.05.2022
        return null;
    }

    private String createPropertyStep(GraphElement element, RelevantField<? extends GraphElement, ?, ?> relevantField) {
        final String label = relevantField.getLabel();
        final Object value = getValue(element, relevantField);
        return String.format(".property(single, \"%s\", %s)", label, value);
    }

    @Override
    protected Object transformValue(Object value) {
        if (value instanceof String) {
            value = "\"" + ((String) value).replace("\"", "\\\"") + "\"";
        } else if (value instanceof Double) {
            value += "d";
        } else if (value instanceof Long) {
            value += "L";
        } else if (value instanceof Enum) {
            value = "\"" + ((Enum<?>) value).name() + "\"";
        }
        return value;
    }
}
