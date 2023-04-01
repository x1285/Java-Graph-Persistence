package de.x1285.jpg.test.model;

import de.x1285.jgp.api.annotation.Edge;
import de.x1285.jgp.api.annotation.Property;
import de.x1285.jgp.element.GraphVertex;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class Person extends GraphVertex {

    @Property
    private String name;

    @Property
    private int age;

    @Edge
    private List<Knows> knows;

    @Edge
    private List<Created> created;

    @Edge
    private List<Place> visitedPlaces;

    @Edge
    private Place birthPlace;

    public Knows knows(Person person) {
        final Knows knows = new Knows(this, person);
        if (getKnows() == null) {
            setKnows(new ArrayList<>());
        }
        getKnows().add(knows);
        return knows;
    }

    public Created created(Software software) {
        final Created created = new Created(this, software);
        if (getCreated() == null) {
            setCreated(new ArrayList<>());
        }
        getCreated().add(created);
        return created;
    }

    public void visitedPlace(Place place) {
        if (getVisitedPlaces() == null) {
            setVisitedPlaces(new ArrayList<>());
        }
        getVisitedPlaces().add(place);
    }

}
