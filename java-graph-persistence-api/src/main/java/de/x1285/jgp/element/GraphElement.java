package de.x1285.jgp.element;

import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

public abstract class GraphElement {

    @Getter
    @Setter
    private String id;

    public String getLabel() {
        return this.getClass().getSimpleName();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        GraphElement that = (GraphElement) o;
        if (id != null && that.id != null) {
            return Objects.equals(id, that.id);
        }
        return false;
    }

    @Override
    public int hashCode() {
        if (id != null) {
            return Objects.hash(id);
        }
        return super.hashCode();
    }

    public static <O extends GraphElement> String getLabel(final Class<O> beanClass) {
        try {
            return beanClass.newInstance().getLabel();
        } catch (InstantiationException | IllegalAccessException e) {
            final String message = "Could not instantiate " + beanClass
                    + ". No-arg constructor needed for de-/serialization.";
            throw new IllegalStateException(message);
        }
    }

    @Override
    public String toString() {
        return getLabel() + " {id=" + getId() + "}";
    }
}
