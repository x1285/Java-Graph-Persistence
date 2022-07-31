package de.x1285.jgp.query.builder;

import de.x1285.jgp.element.GraphElement;

import java.util.Collection;

public abstract class QueryBuilder<R> {

    /**
     * @param elements the collection of all elements for which an add query should be generated.
     *                 Elements can be vertices, as well as edges.
     *                 <p>
     *                 The resulting query will add:
     *                 - all vertices* without an id.
     *                 - all vertices* with an id, when no vertex exists with the same id.
     *                 - all edges* without an id, which relate to a vertex that will be added.
     *                 - all edges* without an id, where no edge with the same label exists between both vertices.
     *                 - all edges* with an id, when no edge exists with the same id.
     *                 <p>
     *                 *) including all elements that are referenced by a relation and may are not directly contained
     *                 in the collection.
     * @return a collection where each entry represents a result for one element.
     * The exact type and meaning of the result depends on the query builder implementation.
     */
    public abstract R add(Collection<? extends GraphElement> elements);

    /**
     * @param element for which an add query should be generated. The element can be a vertex, as well as an edge.
     *                <p>
     *                The resulting query will add:
     *                - all vertices* without an id.
     *                - all vertices* with an id, when no vertex exists with the same id.
     *                - all edges* without an id, which relate to a vertex that will be added.
     *                - all edges* without an id, where no edge with the same label exists between both vertices.
     *                - all edges* with an id, when no edge exists with the same id.
     *                <p>
     *                *) including all elements that are referenced by a relation and may are not directly contained
     *                in the collection.
     * @return an entry which represent the result for the element.
     * The exact type and meaning of the result depends on the query builder implementation.
     */
    public abstract R add(GraphElement element);

    public abstract R update(Collection<? extends GraphElement> elements);

    public abstract R update(GraphElement element);

    public abstract R drop(Collection<? extends GraphElement> elements);

    public abstract R drop(GraphElement element);

}