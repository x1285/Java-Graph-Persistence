package de.x1285.jpg.test.model;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class Knows extends WeightedRelationship<Person, Person> {

    public Knows(Person a, Person b) {
        this.outVertex = a;
        this.inVertex = b;
    }

}
