package de.x1285.jgp.metamodel;

public class MetaModelException extends RuntimeException {
    public MetaModelException(String message) {
        super(message);
    }

    public MetaModelException(String message, Throwable cause) {
        super(message, cause);
    }

    public MetaModelException(Throwable cause) {
        super(cause);
    }
}
