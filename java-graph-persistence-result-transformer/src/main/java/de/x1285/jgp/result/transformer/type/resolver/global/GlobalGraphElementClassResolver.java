package de.x1285.jgp.result.transformer.type.resolver.global;

import de.x1285.jgp.element.GraphElement;
import de.x1285.jgp.result.transformer.type.resolver.GraphElementClassResolverException;
import de.x1285.jgp.result.transformer.type.resolver.GraphElementLabelToClassResolver;

import java.util.Optional;

public class GlobalGraphElementClassResolver implements GraphElementLabelToClassResolver {

    @Override
    public Class<? extends GraphElement> resolveClass(final Object label) {
        Optional<Class<? extends GraphElement>> classForLabel = GlobalGraphElementClassRegister.findByLabel(label);
        if (!classForLabel.isPresent()) {
            throw new GraphElementClassResolverException("Could not find correspondig class for graph elements label: "
                                                                 + label);
        }
        return classForLabel.get();
    }
}
