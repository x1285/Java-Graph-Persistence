package de.x1285.jgp.api.annotation;

public enum EdgeDirection {

    OUT,
    IN;

    public EdgeDirection invert() {
        if (this == IN) {
            return OUT;
        }
        if (this == OUT) {
            return IN;
        } else {
            throw new IllegalArgumentException();
        }
    }
}
