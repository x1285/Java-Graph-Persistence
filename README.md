# Java-Graph-Persistence
Java OGM-Framework to define, write and read graph database elements

## Goals and status
This framework aims to simplify using graph databases which are accessible with [Gremlin](https://tinkerpop.apache.org/gremlin.html) for Java applications. To achive that, annotations are provided to define how POJOs are to be represented in the graph database and how they are connected. Thanks to that definition, objects can be stored in and read from the database: An automatic query builder will create Gremlin queries to add, update and delete graph elements. Custom gremlin queries can be executed and return the result as mapped POJOs elements, which contain all information as requested by the query.

## Work in progress ⚠️
The implementation is currently in its early beginnings.
