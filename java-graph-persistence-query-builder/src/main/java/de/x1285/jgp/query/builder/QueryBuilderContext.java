package de.x1285.jgp.query.builder;

import de.x1285.jgp.element.GraphElement;
import de.x1285.jgp.element.SimpleGraphEdge;
import de.x1285.jgp.metamodel.MetaModel;
import de.x1285.jgp.metamodel.MetaModelFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

public abstract class QueryBuilderContext<Q extends Query> {

    private final AliasGenerator aliasGenerator = new AliasGenerator();
    protected final List<Q> result = new ArrayList<>();
    protected final HashMap<Class<? extends GraphElement>, MetaModel> metaModelCache = new HashMap<>();

    public List<Q> getResult() {
        return result;
    }

    public void addToResult(Q query) {
        if (query.isVertex()) {
            result.add(0, query);
        } else {
            result.add(query);
        }
    }

    public Optional<Q> getResultFor(GraphElement element) {
        return result.stream().filter(q -> {
            if (element instanceof SimpleGraphEdge && q.getElement() instanceof SimpleGraphEdge) {
                SimpleGraphEdge<?,?> graphEdge = (SimpleGraphEdge<?,?>) element;
                SimpleGraphEdge<?,?> knownGraphEdge = (SimpleGraphEdge<?,?>) q.getElement();
                return graphEdge.getOutVertex() == knownGraphEdge.getOutVertex()
                        && graphEdge.getInVertex() == knownGraphEdge.getInVertex()
                        && graphEdge.getLabel().equals(knownGraphEdge.getLabel());
            }
            return q.getElement() == element;
        }).findFirst();
    }

    public boolean wasHandled(GraphElement graphElement) {
        return getResultFor(graphElement).isPresent();
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

    public String generateAlias() {
        return aliasGenerator.generateAlias();
    }
}
