package de.x1285.jgp.element.explore;

import de.x1285.jgp.element.GraphElement;
import de.x1285.jgp.element.GraphVertex;
import de.x1285.jpg.test.data.TestDataGenerator;
import de.x1285.jpg.test.model.Created;
import de.x1285.jpg.test.model.Knows;
import de.x1285.jpg.test.model.Person;
import de.x1285.jpg.test.model.Place;
import de.x1285.jpg.test.model.Software;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class ElementExplorerTest {

    @Test
    public void testExplorerOnAllTestVertices() {
        final List<GraphVertex> testData = TestDataGenerator.generateTestData();
        final Set<? extends GraphElement> elements = ElementExplorer.collectAllElements(testData);

        Assert.assertEquals(15, elements.size());
        Assert.assertEquals(4, elements.stream().filter(x -> x instanceof Person).count());
        Assert.assertEquals(3, elements.stream().filter(x -> x instanceof Place).count());
        Assert.assertEquals(2, elements.stream().filter(x -> x instanceof Software).count());
        Assert.assertEquals(4, elements.stream().filter(x -> x instanceof Created).count());
        Assert.assertEquals(2, elements.stream().filter(x -> x instanceof Knows).count());
    }

    @Test
    public void testExplorerOnMarkoTestVertex() {
        final List<GraphVertex> testData = TestDataGenerator.generateTestData();
        final Person marko = testData.stream()
                                     .filter(x -> x instanceof Person)
                                     .map(x -> (Person) x)
                                     .filter(p -> "Marko".equals(p.getName()))
                                     .findFirst()
                                     .get();
        final Set<? extends GraphElement> elements = ElementExplorer.collectAllElements(marko);

        Assert.assertEquals(12, elements.size());
        Assert.assertEquals(3, elements.stream().filter(x -> x instanceof Person).count());
        Assert.assertEquals(2, elements.stream().filter(x -> x instanceof Place).count());
        Assert.assertEquals(2, elements.stream().filter(x -> x instanceof Software).count());
        Assert.assertEquals(3, elements.stream().filter(x -> x instanceof Created).count());
        Assert.assertEquals(2, elements.stream().filter(x -> x instanceof Knows).count());
    }

    @Test
    public void testExplorerOnMarkoAndPeterTestVertices() {
        final List<GraphVertex> testData = TestDataGenerator.generateTestData();
        final List<Person> list = testData.stream()
                                          .filter(x -> x instanceof Person)
                                          .map(x -> (Person) x)
                                          .filter(p -> "Marko".equals(p.getName()) || "Peter".equals(p.getName()))
                                          .collect(Collectors.toList());
        final Set<? extends GraphElement> elements = ElementExplorer.collectAllElements(list);

        Assert.assertEquals(15, elements.size());
        Assert.assertEquals(4, elements.stream().filter(x -> x instanceof Person).count());
        Assert.assertEquals(3, elements.stream().filter(x -> x instanceof Place).count());
        Assert.assertEquals(2, elements.stream().filter(x -> x instanceof Software).count());
        Assert.assertEquals(4, elements.stream().filter(x -> x instanceof Created).count());
        Assert.assertEquals(2, elements.stream().filter(x -> x instanceof Knows).count());
    }

}