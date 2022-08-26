package de.x1285.jgp.element.explore;

import de.x1285.jgp.element.GraphElement;
import de.x1285.jgp.element.GraphVertex;
import de.x1285.jpg.test.data.TestData;
import de.x1285.jpg.test.data.TestDataGenerator;
import de.x1285.jpg.test.model.Created;
import de.x1285.jpg.test.model.Knows;
import de.x1285.jpg.test.model.Person;
import de.x1285.jpg.test.model.Place;
import de.x1285.jpg.test.model.Software;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ElementExplorerTest {

    @Test
    void testExplorerOnAllTestVertices() {
        final List<GraphVertex> testData = TestDataGenerator.generateTestData().getAllVertices();
        final Set<? extends GraphElement> elements = ElementExplorer.collectAllElements(testData);

        assertEquals(15, elements.size());
        assertEquals(4, elements.stream().filter(x -> x instanceof Person).count());
        assertEquals(3, elements.stream().filter(x -> x instanceof Place).count());
        assertEquals(2, elements.stream().filter(x -> x instanceof Software).count());
        assertEquals(4, elements.stream().filter(x -> x instanceof Created).count());
        assertEquals(2, elements.stream().filter(x -> x instanceof Knows).count());
    }

    @Test
    void testExplorerOnAllPersonVertices() {
        final List<GraphVertex> testData = TestDataGenerator.generateTestData().getAllPersons();
        final Set<? extends GraphElement> elements = ElementExplorer.collectAllElements(testData);

        assertEquals(15, elements.size());
        assertEquals(4, elements.stream().filter(x -> x instanceof Person).count());
        assertEquals(3, elements.stream().filter(x -> x instanceof Place).count());
        assertEquals(2, elements.stream().filter(x -> x instanceof Software).count());
        assertEquals(4, elements.stream().filter(x -> x instanceof Created).count());
        assertEquals(2, elements.stream().filter(x -> x instanceof Knows).count());
    }

    @Test
    void testExplorerOnMarkoTestVertex() {
        final TestData testData = TestDataGenerator.generateTestData();
        final Person marko = testData.getMarko();
        final Set<? extends GraphElement> elements = ElementExplorer.collectAllElements(marko);

        assertEquals(12, elements.size());
        assertEquals(3, elements.stream().filter(x -> x instanceof Person).count());
        assertEquals(2, elements.stream().filter(x -> x instanceof Place).count());
        assertEquals(2, elements.stream().filter(x -> x instanceof Software).count());
        assertEquals(3, elements.stream().filter(x -> x instanceof Created).count());
        assertEquals(2, elements.stream().filter(x -> x instanceof Knows).count());
    }

    @Test
    void testExplorerOnMarkoAndPeterTestVertices() {
        final TestData testData = TestDataGenerator.generateTestData();
        final Set<? extends GraphElement> elements = ElementExplorer.collectAllElements(testData.getMarko(),
                                                                                        testData.getPeter());

        assertEquals(15, elements.size());
        assertEquals(4, elements.stream().filter(x -> x instanceof Person).count());
        assertEquals(3, elements.stream().filter(x -> x instanceof Place).count());
        assertEquals(2, elements.stream().filter(x -> x instanceof Software).count());
        assertEquals(4, elements.stream().filter(x -> x instanceof Created).count());
        assertEquals(2, elements.stream().filter(x -> x instanceof Knows).count());
    }

}