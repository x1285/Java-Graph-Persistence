package de.x1285.jgp.metamodel.provider;

import de.x1285.jgp.element.GraphElement;
import de.x1285.jgp.metamodel.MetaModel;
import de.x1285.jgp.metamodel.MetaModelFactory;

import java.util.HashSet;

public class CachingMetaModelProvider implements MetaModelProvider {

    private final HashSet<MetaModel<?>> META_MODEL_CACHE = new HashSet<>();

    @Override
    public <E extends GraphElement> MetaModel<E> getMetaModel(Class<E> elementClass) {
        for (MetaModel<?> metaModel : META_MODEL_CACHE) {
            if (metaModel.getElementClass() == elementClass) {
                return (MetaModel<E>) metaModel;
            }
        }
        final MetaModel<E> metaModel = MetaModelFactory.createMetaModel(elementClass);
        META_MODEL_CACHE.add(metaModel);
        return metaModel;
    }
}
