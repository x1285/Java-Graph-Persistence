package de.x1285.jgp.result.transformer;

import de.x1285.jgp.element.GraphElement;
import de.x1285.jgp.query.builder.tinkerpop.GraphTraversalQuery;
import de.x1285.jgp.query.builder.tinkerpop.GraphTraversalQueryBuilder;
import de.x1285.jgp.result.transformer.type.resolver.global.GlobalGraphElementClassRegister;
import de.x1285.jpg.test.data.TestData;
import de.x1285.jpg.test.data.TestDataGenerator;
import de.x1285.jpg.test.model.Person;
import de.x1285.jpg.test.model.Place;
import de.x1285.jpg.test.model.Software;
import org.apache.tinkerpop.gremlin.process.traversal.Path;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.apache.tinkerpop.gremlin.tinkergraph.structure.TinkerGraph;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

public class ResultTransfomerTest {

    private ResultTransformer resultTransformer;

    private TestData testData;

    @BeforeEach
    void init() {
        resultTransformer = new ResultTransformer();
        testData = TestDataGenerator.generateTestData();
        registerTestGraphElementClasses(testData);
    }

    @Test
    void transformAllQueriedVerticesGivesExpectedGraphVertexBeans() {
        try (final TinkerGraph graph = TinkerGraph.open()) {
            addTestdataToGraph(graph);

            GraphTraversal<?, Path> rawResult = graph.traversal().V().path();
            List<GraphElement> beans = resultTransformer.toBeans(graph, rawResult);
            Assertions.assertEquals(beans.size(), 9);
            Assertions.assertEquals(beans.stream().filter(b -> b instanceof Person).count(), 4);
            Assertions.assertEquals(beans.stream().filter(b -> b instanceof Place).count(), 3);
            Assertions.assertEquals(beans.stream().filter(b -> b instanceof Software).count(), 2);
        }
    }

    @Test
    void transformQueriedVertexGivesExpectedGraphVertexBean() {
        try (final TinkerGraph graph = TinkerGraph.open()) {
            final GraphTraversalQueryBuilder queryBuilder = new GraphTraversalQueryBuilder();
            final List<GraphTraversalQuery> queries = queryBuilder.add(testData.getHamburg());
            queries.forEach(query -> query.execute(graph.traversal()));

            GraphTraversal<?, Vertex> rawResult = graph.traversal().V();
            List<GraphElement> beans = resultTransformer.toBeans(graph, rawResult);
            Assertions.assertEquals(beans.size(), 1);
            Assertions.assertEquals(beans.stream().filter(b -> b instanceof Place).count(), 1);
        }
    }

    private void registerTestGraphElementClasses(TestData testData) {
        for (Class<? extends GraphElement> clazz : testData.getAllGraphElementClasses()){
            GlobalGraphElementClassRegister.registerClass(clazz);
        }
    }

    private void addTestdataToGraph(final TinkerGraph graph) {
        final GraphTraversalQueryBuilder queryBuilder = new GraphTraversalQueryBuilder();
        final List<GraphTraversalQuery> queries = queryBuilder.add(testData.getAllVertices());
        queries.forEach(query -> query.execute(graph.traversal()));
    }

}
