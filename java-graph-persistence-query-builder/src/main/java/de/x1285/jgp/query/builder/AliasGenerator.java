package de.x1285.jgp.query.builder;

import java.util.UUID;

public class AliasGenerator {

    public String generateAlias() {
        return UUID.randomUUID().toString();
    }

}
