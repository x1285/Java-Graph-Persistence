package de.x1285.jgp.result.transformer.type.resolver;

public class GraphElementClassResolverException extends RuntimeException {

    public GraphElementClassResolverException(String message) {
        super(message);
    }

    public GraphElementClassResolverException(String message, Exception e) {
        super(message, e);
    }

}
