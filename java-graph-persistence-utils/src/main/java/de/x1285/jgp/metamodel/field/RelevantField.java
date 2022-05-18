package de.x1285.jgp.metamodel.field;

import de.x1285.jgp.element.GraphElement;
import lombok.Getter;
import lombok.Setter;

import java.util.function.BiConsumer;
import java.util.function.Function;

@Getter
@Setter
public class RelevantField<E extends GraphElement, T, A> {

    protected A annotation;
    protected Function<E, T> getter;
    protected BiConsumer<E, T> setter;

}
