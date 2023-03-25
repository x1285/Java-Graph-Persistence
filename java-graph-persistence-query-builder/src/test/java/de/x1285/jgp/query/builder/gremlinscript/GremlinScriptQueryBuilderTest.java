package de.x1285.jgp.query.builder.gremlinscript;

import de.x1285.jgp.api.annotation.Property;
import de.x1285.jgp.element.GraphVertex;
import de.x1285.jgp.metamodel.MetaModelException;
import de.x1285.jpg.test.data.TestData;
import de.x1285.jpg.test.data.TestDataGenerator;
import de.x1285.jpg.test.model.Person;
import lombok.Getter;
import lombok.Setter;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class GremlinScriptQueryBuilderTest {

    @Test
    public void testAddAllElements() {
        TestData testData = TestDataGenerator.generateTestData();

        final GremlinScriptQueryBuilder queryBuilder = new GremlinScriptQueryBuilder();
        final List<GremlinScriptQuery> result = queryBuilder.add(testData.getAllVertices());

        assertNotNull(result);
        assertEquals(15, result.size());

        final List<String> queries = result.stream().map(GremlinScriptQuery::getQuery).collect(Collectors.toList());
        assertEquals(4, queries.stream().filter(x -> x.contains("addV(\"Person\")")).count());
        assertEquals(3, queries.stream().filter(x -> x.contains("addV(\"Place\")")).count());
        assertEquals(2, queries.stream().filter(x -> x.contains("addV(\"Software\")")).count());
        assertEquals(4, queries.stream().filter(x -> x.contains("addE(\"created\")")).count());
        assertEquals(2, queries.stream().filter(x -> x.contains("addE(\"knows\")")).count());
    }

    @Test
    public void testAddElementWithoutId() {
        TestData testData = TestDataGenerator.generateTestData();
        final GraphVertex testElementMarko = testData.getMarko();

        final GremlinScriptQueryBuilder queryBuilder = new GremlinScriptQueryBuilder();
        final List<GremlinScriptQuery> result = queryBuilder.add(testElementMarko);

        assertNotNull(result);
        assertEquals(12, result.size());

        final Optional<String> addQueryMarko = result.stream()
                                                     .filter(query -> query.getElement() == testElementMarko)
                                                     .map(GremlinScriptQuery::getQuery)
                                                     .findFirst();
        assertTrue(addQueryMarko.isPresent());
        assertFalse(addQueryMarko.get().contains(".coalesce("));
        assertTrue(addQueryMarko.get().contains("\"name\", \"Marko\""));
        assertTrue(addQueryMarko.get().contains("\"age\", 29"));
    }

    @Test
    public void testAddElementWithId() {
        TestData testData = TestDataGenerator.generateTestData();
        final GraphVertex testElementMarko = testData.getMarko();
        final String id = UUID.randomUUID().toString();
        testElementMarko.setId(id);

        final GremlinScriptQueryBuilder queryBuilder = new GremlinScriptQueryBuilder();
        final List<GremlinScriptQuery> result = queryBuilder.add(testElementMarko);

        assertNotNull(result);
        assertEquals(12, result.size());

        final Optional<String> addQueryMarko = result.stream()
                                                     .filter(query -> query.getElement() == testElementMarko)
                                                     .map(GremlinScriptQuery::getQuery)
                                                     .findFirst();
        assertTrue(addQueryMarko.isPresent());
        assertTrue(addQueryMarko.get().contains(".coalesce("));
        assertTrue(addQueryMarko.get().contains("T.id, \"" + id + "\""));
        assertTrue(addQueryMarko.get().contains("\"name\", \"Marko\""));
        assertTrue(addQueryMarko.get().contains("\"age\", 29"));
    }

    @Test
    public void testNullPropertyDoesNotThrow() {
        // create vertex with a null property: Person.name = null.
        final Person person = Person.builder().age(12).build();
        person.setName(null);

        final GremlinScriptQueryBuilder queryBuilder = new GremlinScriptQueryBuilder();
        final List<GremlinScriptQuery> result = queryBuilder.add(person);

        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    @Test
    public void testAddUnsupportedPropertyDoesThrow() {
        InvalidVertex invalidVertex = new InvalidVertex();
        invalidVertex.setNotAValidProperty(new InvalidVertex());

        final GremlinScriptQueryBuilder queryBuilder = new GremlinScriptQueryBuilder();
        assertThrows(MetaModelException.class, () -> queryBuilder.add(invalidVertex));
    }

    @Getter
    @Setter
    public static class InvalidVertex extends GraphVertex {
        @Property
        private Object notAValidProperty;
    }

}