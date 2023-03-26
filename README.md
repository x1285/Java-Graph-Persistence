# Java-Graph-Persistence

Java OGM-Framework to define, write and read graph database elements

## Goals and status

This framework aims to simplify using graph databases which are accessible
with [Gremlin](https://tinkerpop.apache.org/gremlin.html) for Java applications. To achive that, annotations are
provided to define how POJOs are to be represented in the graph database and how they are connected. Thanks to that
definition, objects can be stored in and read from the database: An automatic query builder will create Gremlin queries
to add, update and delete graph elements. Custom gremlin queries can be executed and return the result as mapped POJOs
elements, which contain all information as requested by the query.

## Work in progress ⚠️

The implementation is currently in its early beginnings. See more details which features to expect at the Roadmap.

## Getting started

Define your entities and their relationships using the annotations provided by our java-graph-persistence-api module. 
To describe that a vertex entity is connected to another entity, simply annotate that field with ```@Edge```.
Notice that the framework needs public getter and setter methods to read and write the values of your entities. 

### First simple example

We define a ```Person``` as a vertex. 
A person can have a property called ```name``` as well as a birthplace.

```java
@Getter
@Setter
public class Person extends GraphVertex {

  @Property
  private String name;

  @Edge
  private Place birthPlace;

}
```

The birthplace is declared as ```Place``` vertex and holds a property ```name```.

```java
@Getter
@Setter
public class Place extends GraphVertex {

  @Property
  private String name;

}
```

#### Autogenerate gremlin queries for executions on a GraphTraversalSource
Instances of these entities can now be used to auto-generate gremlin statements to add these to your graph database.

```java
    // Prepare entities
    Person myPerson = new Person("Maik");
    Place myPlace = new Place("Cologne");
    myPerson.setBirthPlace(myPlace);

    // Generate queries
    GraphTraversalQueryBuilder queryBuilder = new GraphTraversalQueryBuilder();
    List<GraphTraversalQuery> traversalQueries = queryBuilder.add(myPerson);
    
    // use the auto-generated gremlin traversal queries, for example to store them
    try (final TinkerGraph graph = TinkerGraph.open()){
        for (GraphTraversalQuery query : traversalQueries){
            query.execute(graph.traversal());
        }
    }
```

#### Autogenerate string based gremlin queries
Alternatively the entities can also be used to auto-generate the gremlin statements as strings.

```java
    // Prepare entities
    Person myPerson = new Person("Maik");
    Place myPlace = new Place("Cologne");
    myPerson.setBirthPlace(myPlace);

    // Generate queries
    GremlinScriptQueryBuilder queryBuilder = new GremlinScriptQueryBuilder();
    List<GremlinScriptQuery> scriptQueries = queryBuilder.add(myPerson);
    
    // use the auto-generated gremlin script queries
    System.out.println(result.get(0).getQuery());
    // >> g.addV("Place").as(<UUID>).property(single, "name", "Cologne")
    System.out.println(result.get(1).getQuery()); 
    // >> g.addV("Person").as(<UUID>).property(single, "age", 0).property(single, "name", "Maik")
```

The resulting list will contain all needed gremlin scripts to insert ```myPerson```, including all properties, the insertion of ```myPlace``` and the connecting edge between both vertices.

### More advanced example

We are also able to define collections of edges.
A person for example has visited some places.
We define a relationship to a List of places using the same ```@Edge``` annotation.

```java
@Getter
@Setter
public class Person extends GraphVertex {

  @Edge
  private List<Place> visitedPlaces;

}
```

We could also imagine, that we do not just want to store the raw relationship between persons and places.
For example, we want to added informations, if the person ```liked``` the ```VisitedPlace```.
To be able to use edges which store their own properties, we introduced ```GraphEdge```.
The ```VisitedPlace``` is an edge which will have the property ```liked```.

```java
@Getter
@Setter
public class VisitedPlace extends GraphEdge<Person, Place> {

  @Property
  private boolean liked;

}
```

To use that edge, simply use it at the vertex of ```Person``:

```java
@Getter
@Setter
public class Person extends GraphVertex {

  @Edge
  private List<VisitedPlace> visitedPlaces;

}
```

## Roadmap

### Writing
- [x] Implement API inclusive Annotations to define how Java POJPs should be stored in the graph database.
- [x] Implement MetaModel and Factory to access during runtime how instances should be stored in the graph database.
- [ ] Implement first simple QueryBuilder which auto-generates Gremlin insert queries as string:
  - [x] Insert a vertex including all supported properties
  - [x] ... and all other referenced vertices
  - [ ] ... and all referenced edges including their properties
  - [ ] Support inserting a collection of vertices
- [x] Implement first simple QueryBuilder which auto-generates Gremlin insert queries for GraphTraversals:
  - [x] Insert a vertex including all supported properties
  - [x] ... and all other referenced vertices
  - [x] ... and all referenced edges including their properties
  - [ ] Support inserting a collection of vertices

### Reading
- [ ] Implement first simple mapping from TinkerPop Gremlin Result objects to our Java POJO.
  - [ ] Fetching a vertex including all supported properties
  - [ ] ... and all others fetched vertices including their properties
  - [ ] ... and all edges including their properties
  - [ ] ... and set all instance references

Further:
- [ ] Add support for complex property types (using a customizable (de-)serializer)
- [ ] Implement an entity manager