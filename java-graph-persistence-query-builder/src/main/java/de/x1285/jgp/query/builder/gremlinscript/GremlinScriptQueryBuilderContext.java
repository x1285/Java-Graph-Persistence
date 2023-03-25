package de.x1285.jgp.query.builder.gremlinscript;

import de.x1285.jgp.element.GraphElement;
import de.x1285.jgp.query.builder.QueryBuilderContext;

import java.util.Optional;

public class GremlinScriptQueryBuilderContext extends QueryBuilderContext<GremlinScriptQuery> {

    public void addToResult(GremlinScriptQuery gremlinScriptQuery) {
        if (gremlinScriptQuery.isVertex()) {
            result.add(0, gremlinScriptQuery);
        } else {
            result.add(gremlinScriptQuery);
        }
    }

    public Optional<GremlinScriptQuery> getResultFor(GraphElement element) {
        return this.result.stream().filter(q -> q.getElement() == element).findFirst();
    }

}
