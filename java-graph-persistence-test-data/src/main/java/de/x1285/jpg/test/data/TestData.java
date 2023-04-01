package de.x1285.jpg.test.data;

import de.x1285.jgp.element.GraphElement;
import de.x1285.jgp.element.GraphVertex;
import de.x1285.jpg.test.model.Created;
import de.x1285.jpg.test.model.Knows;
import de.x1285.jpg.test.model.Person;
import de.x1285.jpg.test.model.Place;
import de.x1285.jpg.test.model.Software;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Arrays;
import java.util.List;

@Builder
@Getter
@Setter
public class TestData {

    private final Person marko;
    private final Person josh;
    private final Person vadas;
    private final Person peter;

    private final Software lop;
    private final Software ripple;

    private final Place berlin;
    private final Place hamburg;
    private final Place duesseldorf;

    public List<GraphVertex> getAllPersons() {
        return Arrays.asList(marko, josh, vadas, peter);
    }

    public List<GraphVertex> getAllVertices() {
        return Arrays.asList(marko, josh, vadas, peter, lop, ripple, berlin, hamburg, duesseldorf);
    }

    public List<Class<? extends GraphElement>> getAllGraphElementClasses() {
        return Arrays.asList(Created.class, Knows.class, Person.class, Place.class, Software.class);
    }
}
