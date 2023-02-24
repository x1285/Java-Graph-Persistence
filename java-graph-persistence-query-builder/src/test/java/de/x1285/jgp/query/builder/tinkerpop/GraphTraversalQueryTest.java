package de.x1285.jgp.query.builder.tinkerpop;

import de.x1285.jpg.test.data.TestData;
import de.x1285.jpg.test.data.TestDataGenerator;
import org.apache.tinkerpop.gremlin.structure.Graph;
import org.apache.tinkerpop.gremlin.tinkergraph.structure.TinkerGraph;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GraphTraversalQueryTest {

    @Test
    public void testExecuteAllVertices() throws Exception {
        final TestData testData = TestDataGenerator.generateTestData();

        final GraphTraversalQueryBuilder queryBuilder = new GraphTraversalQueryBuilder();
        final List<GraphTraversalQuery> queries = queryBuilder.add(testData.getAllVertices());

        try (final Graph graph = TinkerGraph.open()) {
            for (GraphTraversalQuery query : queries) {
                query.execute(graph.traversal()).iterate();
            }

            assertEquals(9, graph.traversal().V().count().next());
            assertEquals(2, graph.traversal().V().hasLabel("Software").count().next());
            assertEquals(3, graph.traversal().V().hasLabel("Place").count().next());
            assertEquals(4, graph.traversal().V().hasLabel("Person").count().next());
        }
    }

}