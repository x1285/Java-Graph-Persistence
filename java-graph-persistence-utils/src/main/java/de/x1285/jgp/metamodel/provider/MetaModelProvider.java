package de.x1285.jgp.metamodel.provider;

import de.x1285.jgp.element.GraphElement;
import de.x1285.jgp.metamodel.MetaModel;

public interface MetaModelProvider {

    <E extends GraphElement> MetaModel<E> getMetaModel(Class<E> elementClass);

}
