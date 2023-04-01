package de.x1285.jpg.test.model;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class Created extends WeightedRelationship<Person, Software> {

    public Created(Person person, Software software) {
        this.outVertex = person;
        this.inVertex = software;
    }

}
