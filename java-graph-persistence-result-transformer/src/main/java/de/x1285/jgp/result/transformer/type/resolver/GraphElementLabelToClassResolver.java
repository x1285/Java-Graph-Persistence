package de.x1285.jgp.result.transformer.type.resolver;

import de.x1285.jgp.element.GraphElement;

public interface GraphElementLabelToClassResolver {

    Class<? extends GraphElement> resolveClass(Object label);

}
