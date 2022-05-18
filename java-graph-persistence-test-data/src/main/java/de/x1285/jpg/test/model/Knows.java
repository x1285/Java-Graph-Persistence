package de.x1285.jpg.test.model;

public class Knows extends WeightedRelationship<Person, Person> {

    public Knows(Person a, Person b) {
        this.outVertex = a;
        this.inVertex = b;
    }

}
