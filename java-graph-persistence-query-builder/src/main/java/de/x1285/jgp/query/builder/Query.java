package de.x1285.jgp.query.builder;

import de.x1285.jgp.element.GraphElement;
import de.x1285.jgp.element.GraphVertex;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@RequiredArgsConstructor
public abstract class Query<R> {

    @Getter
    @NonNull
    protected final GraphElement element;

    @Getter
    private String alias;

    @Getter
    @Setter
    protected R query;

    public boolean isVertex() {
        return element instanceof GraphVertex;
    }
}
