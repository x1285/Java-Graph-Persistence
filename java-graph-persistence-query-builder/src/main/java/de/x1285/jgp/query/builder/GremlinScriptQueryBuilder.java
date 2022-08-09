package de.x1285.jgp.query.builder;

import de.x1285.jgp.element.GraphEdge;
import de.x1285.jgp.element.GraphElement;
import de.x1285.jgp.element.GraphVertex;
import de.x1285.jgp.metamodel.MetaModel;
import de.x1285.jgp.metamodel.field.EdgeCollectionField;
import de.x1285.jgp.metamodel.field.EdgeField;
import de.x1285.jgp.metamodel.field.PropertyField;
import de.x1285.jgp.metamodel.field.RelevantField;

import java.util.Collection;
import java.util.List;

import static de.x1285.jgp.metamodel.SupportedTypes.isSupportedType;

public class GremlinScriptQueryBuilder extends QueryBuilder<List<String>> {

    @Override
    public List<String> add(Collection<? extends GraphElement> elements) {
        GremlinScriptQueryBuilderContext context = new GremlinScriptQueryBuilderContext();
        for (GraphElement element : elements) {
            add(element, context);
        }
        return context.getResult();
    }

    @Override
    public List<String> add(GraphElement element) {
        GremlinScriptQueryBuilderContext context = new GremlinScriptQueryBuilderContext();
        add(element, context);
        return context.getResult();
    }

    private void add(GraphElement element, GremlinScriptQueryBuilderContext context) {
        if (!context.wasHandled(element)) {
            context.addHandled(element);
            final MetaModel metaModel = context.getMetaModel(element);
            if (element instanceof GraphVertex) {
                addVertex((GraphVertex) element, context, metaModel);
            } else if (element instanceof GraphEdge) {
                addEdge((GraphEdge) element, context, metaModel);
            }
        }
    }

    private void addVertex(GraphVertex vertex, GremlinScriptQueryBuilderContext context, MetaModel metaModel) {
        final StringBuilder addQuery = new StringBuilder("addV(\"").append(vertex.getLabel()).append("\")");

        String idStep = createIdStep(vertex);
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
            context.addToResult("g." + addQuery);
        } else {
            final String query = String.format("g.V(%s).fold().coalesce(unfold(), %s)", vertex.getId(), addQuery);
            context.addToResult(query);
        }
    }

    private void addEdge(GraphEdge edge, GremlinScriptQueryBuilderContext context, MetaModel metaModel) {
        final StringBuilder addQuery = new StringBuilder("addE(\"").append(edge.getLabel()).append("\")");

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

        // TODO: 08.08.2022 handle IN and OUT vertex

        if (edge.getId() == null) {
            context.addToResult("g." + addQuery);
        } else {
            final String query = String.format("g.E(%s).fold().coalesce(unfold(), %s)", edge.getId(), addQuery);
            context.addToResult(query);
        }
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
        String id = element.getId();
        if (id != null) {
            checkIdValueSupport(id, element);
            return String.format(".property(T.id, %s)", getValue(id));
        }
        return null;
    }

    @Override
    public List<String> update(Collection<? extends GraphElement> elements) {
        // TODO: 04.05.2022  
        return null;
    }

    @Override
    public List<String> update(GraphElement element) {
        // TODO: 04.05.2022  
        return null;
    }

    @Override
    public List<String> drop(Collection<? extends GraphElement> elements) {
        // TODO: 04.05.2022  
        return null;
    }

    @Override
    public List<String> drop(GraphElement element) {
        // TODO: 04.05.2022
        return null;
    }

    private String createPropertyStep(GraphElement element, RelevantField<? extends GraphElement, ?, ?> relevantField) {
        final String label = relevantField.getLabel();
        final Object value = getValue(element, relevantField);
        return String.format(".property(single, \"%s\", %s)", label, value);
    }

    private Object getValue(GraphElement element, RelevantField<? extends GraphElement, ?, ?> relevantField) {
        Object value = relevantField.getGetter().apply(element);
        checkValueSupport(value, element, relevantField);
        return getValue(value);
    }

    private Object getValue(Object value) {
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

    private void checkValueSupport(Object value, GraphElement element, RelevantField<? extends GraphElement, ?, ?> field) {
        if (value != null && !isSupportedType(value.getClass())) {
            final String message = String.format("Unsupported value type %s on field %s of element %s.",
                                                 value.getClass(), field, element.getClass());
            throw new QueryBuilderException(message);
        }
    }

    private void checkIdValueSupport(Object value, GraphElement element) {
        if (value != null && !isSupportedType(value.getClass())) {
            final String message = String.format("Unsupported value type %s on Id field of element %s.",
                                                 value.getClass(), element.getClass());
            throw new QueryBuilderException(message);
        }
    }
}
