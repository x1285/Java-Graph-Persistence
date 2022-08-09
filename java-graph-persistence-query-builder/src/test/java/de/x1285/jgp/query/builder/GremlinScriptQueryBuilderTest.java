package de.x1285.jgp.query.builder;

import de.x1285.jgp.api.annotation.Property;
import de.x1285.jgp.element.GraphVertex;
import de.x1285.jgp.metamodel.MetaModelException;
import de.x1285.jpg.test.data.TestData;
import de.x1285.jpg.test.data.TestDataGenerator;
import de.x1285.jpg.test.model.Person;
import lombok.Getter;
import lombok.Setter;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

public class GremlinScriptQueryBuilderTest {

    @Test
    public void testAddAllElements() {
        TestData testData = TestDataGenerator.generateTestData();

        final GremlinScriptQueryBuilder queryBuilder = new GremlinScriptQueryBuilder();
        final List<String> result = queryBuilder.add(testData.getAllVertices());

        assertNotNull(result);
        assertEquals(15, result.size());
        Assert.assertEquals(4, result.stream().filter(x -> x.contains("addV(\"Person\")")).count());
        Assert.assertEquals(3, result.stream().filter(x -> x.contains("addV(\"Place\")")).count());
        Assert.assertEquals(2, result.stream().filter(x -> x.contains("addV(\"Software\")")).count());
        Assert.assertEquals(4, result.stream().filter(x -> x.contains("addE(\"created\")")).count());
        Assert.assertEquals(2, result.stream().filter(x -> x.contains("addE(\"knows\")")).count());
    }

    @Test
    public void testAddElementWithoutId() {
        TestData testData = TestDataGenerator.generateTestData();
        final GraphVertex testElement = testData.getMarko();

        final GremlinScriptQueryBuilder queryBuilder = new GremlinScriptQueryBuilder();
        final List<String> result = queryBuilder.add(testElement);

        assertNotNull(result);
        assertEquals(5, result.size());
        assertFalse(result.get(4).contains(".coalesce("));
        assertTrue(result.get(4).contains("\"name\", \"Marko\""));
        assertTrue(result.get(4).contains("\"age\", 29"));
    }

    @Test
    public void testAddElementWithId() {
        TestData testData = TestDataGenerator.generateTestData();
        final GraphVertex testElement = testData.getMarko();
        final String id = UUID.randomUUID().toString();
        testElement.setId(id);

        final GremlinScriptQueryBuilder queryBuilder = new GremlinScriptQueryBuilder();
        final List<String> result = queryBuilder.add(testElement);

        assertNotNull(result);
        assertEquals(5, result.size());
        assertTrue(result.get(4).contains(".coalesce("));
        assertTrue(result.get(4).contains("T.id, \"" + id + "\""));
        assertTrue(result.get(4).contains("\"name\", \"Marko\""));
        assertTrue(result.get(4).contains("\"age\", 29"));
    }

    @Test
    public void testNullPropertyDoesNotThrow() {
        // create vertex with a null property: Person.name = null.
        final Person person = Person.builder().age(12).build();
        person.setName(null);

        final GremlinScriptQueryBuilder queryBuilder = new GremlinScriptQueryBuilder();
        final List<String> result = queryBuilder.add(person);

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