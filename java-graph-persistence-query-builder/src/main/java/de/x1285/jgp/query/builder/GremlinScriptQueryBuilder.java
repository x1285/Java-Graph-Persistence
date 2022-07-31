package de.x1285.jgp.query.builder;

import de.x1285.jgp.element.GraphElement;
import de.x1285.jgp.metamodel.MetaModel;
import de.x1285.jgp.metamodel.MetaModelFactory;
import de.x1285.jgp.metamodel.field.EdgeCollectionField;
import de.x1285.jgp.metamodel.field.EdgeField;
import de.x1285.jgp.metamodel.field.PropertyField;
import de.x1285.jgp.metamodel.field.RelevantField;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static de.x1285.jgp.metamodel.SupportedTypes.isSupportedType;

public class GremlinScriptQueryBuilder extends QueryBuilder<List<String>> {

    @Override
    public List<String> add(Collection<? extends GraphElement> elements) {
        // TODO: 04.05.2022  
        return null;
    }

    @Override
    public List<String> add(GraphElement element) {
        final ArrayList<String> result = new ArrayList<>();
        final MetaModel metaModel = MetaModelFactory.createMetaModel(element);

        final StringBuilder addQuery = new StringBuilder("addV(\"").append(element.getLabel()).append("\")");

        String idStep = createIdStep(element);
        if (idStep != null) {
            addQuery.append(idStep);
        }

        for (RelevantField<? extends GraphElement, ?, ?> relevantField : metaModel.getRelevantFields()) {
            if (relevantField instanceof PropertyField) {
                final String propertyStep = createPropertyStep(element, relevantField);
                addQuery.append(propertyStep);
            } else if (relevantField instanceof EdgeCollectionField) {
                // TODO: 31.05.2022
            } else if (relevantField instanceof EdgeField) {
                // TODO: 31.05.2022
            }
        }

        if (element.getId() == null) {
            result.add("g." + addQuery);
        } else {
            final String query = String.format("g.V(%s).fold().coalesce(unfold(), %s)", element.getId(), addQuery);
            result.add(query);
        }
        return result;
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
