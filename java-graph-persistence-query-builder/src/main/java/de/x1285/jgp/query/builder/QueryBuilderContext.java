package de.x1285.jgp.query.builder;

import de.x1285.jgp.element.GraphElement;
import de.x1285.jgp.metamodel.MetaModel;
import de.x1285.jgp.metamodel.MetaModelFactory;

import java.util.HashMap;
import java.util.HashSet;

public abstract class QueryBuilderContext {

    protected final HashSet<GraphElement> stack = new HashSet<>();
    protected final HashMap<Class<? extends GraphElement>, MetaModel> metaModelCache = new HashMap<>();

    public void addHandled(GraphElement graphElement) {
        stack.add(graphElement);
    }

    public boolean wasHandled(GraphElement graphElement) {
        return stack.contains(graphElement);
    }

    public MetaModel getMetaModel(GraphElement element) {
        MetaModel cachedMetaModel = metaModelCache.get(element.getClass());
        if (cachedMetaModel != null) {
            return cachedMetaModel;
        } else {
            MetaModel metaModel = MetaModelFactory.createMetaModel(element);
            metaModelCache.put(element.getClass(), metaModel);
            return metaModel;
        }
    }
}
