package de.x1285.jgp.element.explore;

import de.x1285.jgp.element.GraphEdge;
import de.x1285.jgp.element.GraphElement;
import de.x1285.jgp.element.GraphVertex;
import de.x1285.jgp.metamodel.MetaModel;
import de.x1285.jgp.metamodel.MetaModelFactory;
import de.x1285.jgp.metamodel.field.EdgeField;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class ElementExplorer {

    private static final HashSet<MetaModel> META_MODEL_CACHE = new HashSet<>();

    public static Set<? extends GraphElement> collectAllElements(GraphElement element) {
        return collectAllElements(Collections.singleton(element));
    }

    public static Set<? extends GraphElement> collectAllElements(Collection<? extends GraphElement> elements) {
        final HashSet<GraphElement> result = new HashSet<>(elements);
        collectAllElements(elements, result);
        return result;
    }

    protected static void collectAllElements(Collection<? extends GraphElement> elements, HashSet<GraphElement> result) {
        for (GraphElement element : elements) {
            collectAllElements(element, result);
        }
    }

    protected static void collectAllElements(GraphElement element, HashSet<GraphElement> result) {
        if (element instanceof GraphEdge) {
            collectAllElements((GraphEdge<?, ?>) element, result);
        } else if (element instanceof GraphVertex) {
            final MetaModel metaModel = getMetaModel(element.getClass());
            final GraphVertex vertex = (GraphVertex) element;
            metaModel.getRelevantFields()
                     .stream()
                     .filter(x -> x instanceof EdgeField)
                     .map(x -> (EdgeField<GraphVertex, ?>) x)
                     .forEach(edge -> {
                         Object fieldValue = edge.getGetter().apply(vertex);
                         collectAllElements(fieldValue, result);
                     });
            // TODO: 04.05.2022
        } else {
            throw new IllegalStateException("Unknown GraphElement type " + element.getClass() + " not supported during meta model creation.");
        }
    }

    private static void collectAllElements(Object fieldValue, HashSet<GraphElement> result) {
        if (fieldValue instanceof Collection) {
            for (Object collectionEntry : ((Collection<?>) fieldValue)) {
                collectAllElements(collectionEntry, result);
            }
        } else if (fieldValue instanceof GraphElement) {
            if (result.add((GraphElement) fieldValue)) {
                collectAllElements((GraphElement) fieldValue, result);
            }
        }
    }

    protected static void collectAllElements(GraphEdge<?, ?> edge, HashSet<GraphElement> result) {
        final GraphVertex outVertex = edge.getOutVertex();
        if (result.add(outVertex)) {
            collectAllElements(outVertex, result);
        }
        final GraphVertex inVertex = edge.getInVertex();
        if (result.add(inVertex)) {
            collectAllElements(inVertex, result);
        }
    }

    private static MetaModel getMetaModel(Class<? extends GraphElement> elementClass) {
        for (MetaModel metaModel : META_MODEL_CACHE) {
            if (metaModel.getElementClass() == elementClass) {
                return metaModel;
            }
        }
        final MetaModel metaModel = MetaModelFactory.createMetaModel(elementClass);
        META_MODEL_CACHE.add(metaModel);
        return metaModel;
    }
}
