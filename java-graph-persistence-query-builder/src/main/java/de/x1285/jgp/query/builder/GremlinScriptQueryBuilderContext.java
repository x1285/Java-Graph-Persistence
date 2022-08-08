package de.x1285.jgp.query.builder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class GremlinScriptQueryBuilderContext extends QueryBuilderContext {

    private final List<String> result = new ArrayList<>();

    public List<String> getResult() {
        return result;
    }

    public void addToResult(String gremlinScript) {
        result.add(gremlinScript);
    }

    public void addToResult(Collection<String> gremlinScripts) {
        result.addAll(gremlinScripts);
    }
}
