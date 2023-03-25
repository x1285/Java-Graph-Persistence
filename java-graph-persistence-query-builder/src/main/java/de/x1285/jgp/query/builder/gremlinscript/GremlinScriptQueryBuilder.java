package de.x1285.jgp.query.builder.gremlinscript;

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
            final MetaModel metaModel = context.getMetaModel(element);
            if (element instanceof GraphVertex) {
                addVertex((GraphVertex) element, context, metaModel);
            } else if (element instanceof GraphEdge) {
                addEdge((GraphEdge<?, ?>) element, context, metaModel);
            }
        }
    }

    private void addVertex(GraphVertex vertex, GremlinScriptQueryBuilderContext context, MetaModel metaModel) {
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

        for (RelevantField<? extends GraphElement, ?, ?> relevantField : metaModel.getRelevantFields()) {
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

    private void addEdge(GraphEdge<?, ?> edge, GremlinScriptQueryBuilderContext context, MetaModel metaModel) {
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

        for (RelevantField<? extends GraphElement, ?, ?> relevantField : metaModel.getRelevantFields()) {
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

    private void addEdgeCollectionSteps(GraphElement element,
                                        EdgeCollectionField<?, ?, ?> edgeCollectionField,
                                        GremlinScriptQueryBuilderContext context) {
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
                              GremlinScriptQueryBuilderContext context) {
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

    private String createIdStep(GraphElement element) {
        Object id = element.getId();
        if (id != null) {
            checkIdValueSupport(id, element);
            return String.format(".property(T.id, %s)", getValue(id));
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
}
