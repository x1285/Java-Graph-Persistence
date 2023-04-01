package de.x1285.jgp.result.transformer;

import de.x1285.jgp.element.GraphElement;
import de.x1285.jgp.query.builder.tinkerpop.GraphTraversalQuery;
import de.x1285.jgp.query.builder.tinkerpop.GraphTraversalQueryBuilder;
import de.x1285.jgp.result.transformer.type.resolver.global.GlobalGraphElementClassRegister;
import de.x1285.jpg.test.data.TestData;
import de.x1285.jpg.test.data.TestDataGenerator;
import de.x1285.jpg.test.model.Language;
import de.x1285.jpg.test.model.Person;
import de.x1285.jpg.test.model.Place;
import de.x1285.jpg.test.model.Software;
import org.apache.tinkerpop.gremlin.process.traversal.Path;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.apache.tinkerpop.gremlin.tinkergraph.structure.TinkerGraph;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
            assertEquals(beans.size(), 9);

            // PERSONS

            List<Person> persons = beans.stream()
                                        .filter(b -> b instanceof Person)
                                        .map(p -> (Person) p)
                                        .collect(Collectors.toList());
            assertEquals(persons.size(), 4);

            Optional<Person> marko = persons.stream().filter(p -> "Marko".equals(p.getName())).findFirst();
            assertTrue(marko.isPresent());
            assertEquals(marko.get().getAge(), 29);

            Optional<Person> josh = persons.stream().filter(p -> "Josh".equals(p.getName())).findFirst();
            assertTrue(josh.isPresent());
            assertEquals(josh.get().getAge(), 32);

            Optional<Person> vadas = persons.stream().filter(p -> "Vadas".equals(p.getName())).findFirst();
            assertTrue(vadas.isPresent());
            assertEquals(vadas.get().getAge(), 27);

            Optional<Person> peter = persons.stream().filter(p -> "Peter".equals(p.getName())).findFirst();
            assertTrue(peter.isPresent());
            assertEquals(peter.get().getAge(), 35);

            // SOFTWARE

            List<Software> software = beans.stream()
                                           .filter(b -> b instanceof Software)
                                           .map(s -> (Software) s)
                                           .collect(Collectors.toList());;
            assertEquals(software.size(), 2);

            Optional<Software> lop = software.stream().filter(p -> "lop".equals(p.getName())).findFirst();
            assertTrue(lop.isPresent());
            assertEquals(lop.get().getLang(), Language.JAVA);

            Optional<Software> ripple = software.stream().filter(p -> "ripple".equals(p.getName())).findFirst();
            assertTrue(ripple.isPresent());
            assertEquals(ripple.get().getLang(), Language.PYTHON);

            // PLACES

            List<Place> places = beans.stream()
                                      .filter(b -> b instanceof Place)
                                      .map(p -> (Place) p)
                                      .collect(Collectors.toList());;
            assertEquals(places.size(), 3);

            Optional<Place> duesseldorf = places.stream().filter(p -> "Düsseldorf".equals(p.getName())).findFirst();
            assertTrue(duesseldorf.isPresent());

            Optional<Place> hamburg = places.stream().filter(p -> "Hamburg".equals(p.getName())).findFirst();
            assertTrue(hamburg.isPresent());

            Optional<Place> berlin = places.stream().filter(p -> "Berlin".equals(p.getName())).findFirst();
            assertTrue(berlin.isPresent());
        }
    }

    @Test
    void transformQueriedVertexGivesExpectedGraphVertexBean() {
        final Person maik = Person.builder().name("Maik").age(28).build();
        try (final TinkerGraph graph = TinkerGraph.open()) {
            final GraphTraversalQueryBuilder queryBuilder = new GraphTraversalQueryBuilder();
            final List<GraphTraversalQuery> queries = queryBuilder.add(maik);
            queries.forEach(query -> query.execute(graph.traversal()));

            GraphTraversal<?, Vertex> rawResult = graph.traversal().V();
            List<GraphElement> beans = resultTransformer.toBeans(graph, rawResult);
            assertEquals(beans.size(), 1);
            assertEquals(beans.stream().filter(b -> b instanceof Person).count(), 1);
            final Person person = (Person) beans.get(0);
            assertEquals(person.getName(), "Maik");
            assertEquals(person.getAge(), 28);
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
