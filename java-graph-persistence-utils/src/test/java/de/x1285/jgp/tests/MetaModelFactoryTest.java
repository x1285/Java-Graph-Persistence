package de.x1285.jgp.tests;

import de.x1285.jgp.api.annotation.Edge;
import de.x1285.jgp.api.annotation.Property;
import de.x1285.jgp.element.GraphEdge;
import de.x1285.jgp.element.GraphVertex;
import de.x1285.jgp.metamodel.MetaModel;
import de.x1285.jgp.metamodel.MetaModelException;
import de.x1285.jgp.metamodel.MetaModelFactory;
import de.x1285.jgp.metamodel.field.EdgeCollectionField;
import de.x1285.jgp.metamodel.field.EdgeField;
import de.x1285.jgp.metamodel.field.PropertyField;
import de.x1285.jgp.metamodel.field.RelevantField;
import de.x1285.jpg.test.model.Created;
import de.x1285.jpg.test.model.Knows;
import de.x1285.jpg.test.model.Person;
import de.x1285.jpg.test.model.Place;
import de.x1285.jpg.test.model.Software;
import lombok.Getter;
import lombok.Setter;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;


public class MetaModelFactoryTest {

    @Test
    public void testPerson() {
        // Create test data
        MetaModel metaModel = MetaModelFactory.createMetaModel(Person.class);
        List<RelevantField<?, ?, ?>> relevantFields = metaModel.getRelevantFields();
        assertEquals(6, relevantFields.size());
        // test @Property fields
        List<RelevantField<?, ?, ?>> properties = relevantFields.stream().filter(x -> x instanceof PropertyField).collect(Collectors.toList());
        assertEquals(2, properties.size());
        assertTrue(properties.stream().allMatch(x -> x.getAnnotation() instanceof Property));
        // test @Edge fields
        List<RelevantField<?, ?, ?>> edges = relevantFields.stream().filter(x -> x instanceof EdgeField).collect(Collectors.toList());
        assertEquals(4, edges.size());
        assertTrue(edges.stream().allMatch(x -> x.getAnnotation() instanceof Edge));
        List<RelevantField<?, ?, ?>> edgeCollections = edges.stream().filter(x -> x instanceof EdgeCollectionField).collect(Collectors.toList());
        assertEquals(3, edgeCollections.size());
    }

    @Test
    public void testPlace() {
        // Create test data
        MetaModel metaModel = MetaModelFactory.createMetaModel(Place.class);
        List<RelevantField<?, ?, ?>> relevantFields = metaModel.getRelevantFields();
        assertEquals(1, relevantFields.size());
        // test @Property fields
        List<RelevantField<?, ?, ?>> properties = relevantFields.stream().filter(x -> x instanceof PropertyField).collect(Collectors.toList());
        assertEquals(1, properties.size());
        assertTrue(properties.stream().allMatch(x -> x.getAnnotation() instanceof Property));
        // test @Edge fields
        List<RelevantField<?, ?, ?>> edges = relevantFields.stream().filter(x -> x instanceof EdgeField).collect(Collectors.toList());
        assertEquals(0, edges.size());
        assertTrue(edges.stream().allMatch(x -> x.getAnnotation() instanceof Edge));
        List<RelevantField<?, ?, ?>> edgeCollections = edges.stream().filter(x -> x instanceof EdgeCollectionField).collect(Collectors.toList());
        assertEquals(0, edgeCollections.size());
    }

    @Test
    public void testSoftware() {
        // Create test data
        MetaModel metaModel = MetaModelFactory.createMetaModel(Software.class);
        List<RelevantField<?, ?, ?>> relevantFields = metaModel.getRelevantFields();
        assertEquals(2, relevantFields.size());
        // test @Property fields
        List<RelevantField<?, ?, ?>> properties = relevantFields.stream().filter(x -> x instanceof PropertyField).collect(Collectors.toList());
        assertEquals(2, properties.size());
        assertTrue(properties.stream().allMatch(x -> x.getAnnotation() instanceof Property));
        // test @Edge fields
        List<RelevantField<?, ?, ?>> edges = relevantFields.stream().filter(x -> x instanceof EdgeField).collect(Collectors.toList());
        assertEquals(0, edges.size());
        assertTrue(edges.stream().allMatch(x -> x.getAnnotation() instanceof Edge));
        List<RelevantField<?, ?, ?>> edgeCollections = edges.stream().filter(x -> x instanceof EdgeCollectionField).collect(Collectors.toList());
        assertEquals(0, edgeCollections.size());
    }

    @Test
    public void testKnows() {
        // Create test data
        MetaModel metaModel = MetaModelFactory.createMetaModel(Knows.class);
        List<RelevantField<?, ?, ?>> relevantFields = metaModel.getRelevantFields();
        assertEquals(1, relevantFields.size());
        // test @Property fields
        List<RelevantField<?, ?, ?>> properties = relevantFields.stream().filter(x -> x instanceof PropertyField).collect(Collectors.toList());
        assertEquals(1, properties.size());
        assertTrue(properties.stream().allMatch(x -> x.getAnnotation() instanceof Property));
        // test @Edge fields
        List<RelevantField<?, ?, ?>> edges = relevantFields.stream().filter(x -> x instanceof EdgeField).collect(Collectors.toList());
        assertEquals(0, edges.size());
        assertTrue(edges.stream().allMatch(x -> x.getAnnotation() instanceof Edge));
        List<RelevantField<?, ?, ?>> edgeCollections = edges.stream().filter(x -> x instanceof EdgeCollectionField).collect(Collectors.toList());
        assertEquals(0, edgeCollections.size());
    }

    @Test
    public void testCreated() {
        // Create test data
        MetaModel metaModel = MetaModelFactory.createMetaModel(Created.class);
        List<RelevantField<?, ?, ?>> relevantFields = metaModel.getRelevantFields();
        assertEquals(1, relevantFields.size());
        // test @Property fields
        List<RelevantField<?, ?, ?>> properties = relevantFields.stream().filter(x -> x instanceof PropertyField).collect(Collectors.toList());
        assertEquals(1, properties.size());
        assertTrue(properties.stream().allMatch(x -> x.getAnnotation() instanceof Property));
        // test @Edge fields
        List<RelevantField<?, ?, ?>> edges = relevantFields.stream().filter(x -> x instanceof EdgeField).collect(Collectors.toList());
        assertEquals(0, edges.size());
        assertTrue(edges.stream().allMatch(x -> x.getAnnotation() instanceof Edge));
        List<RelevantField<?, ?, ?>> edgeCollections = edges.stream().filter(x -> x instanceof EdgeCollectionField).collect(Collectors.toList());
        assertEquals(0, edgeCollections.size());
    }

    @Test
    public void testInvalidVertexWithVertexPropertyClass() {
        InvalidVertexWithVertexProperty invalidVertex = new InvalidVertexWithVertexProperty();
        assertThrows(MetaModelException.class, () -> MetaModelFactory.createMetaModel(invalidVertex));
    }

    @Test
    public void testInvalidVertexWithEdgePropertyClass() {
        InvalidVertexWithEdgeProperty invalidVertex = new InvalidVertexWithEdgeProperty();
        assertThrows(MetaModelException.class, () -> MetaModelFactory.createMetaModel(invalidVertex));
    }

    @Test
    public void testInvalidEdgeWithEdgeFieldClass() {
        InvalidEdgeWithEdgeField invalidEdge = new InvalidEdgeWithEdgeField();
        assertThrows(MetaModelException.class, () -> MetaModelFactory.createMetaModel(invalidEdge));
    }

    @Test
    public void testInvalidEdgeWithVertexFieldClass() {
        InvalidEdgeWithVertexField invalidEdge = new InvalidEdgeWithVertexField();
        assertThrows(MetaModelException.class, () -> MetaModelFactory.createMetaModel(invalidEdge));
    }

    @Test
    public void testInvalidVertexWithNonGraphElementEdgeClass() {
        InvalidVertexWithNonGraphElementEdge invalidVertex = new InvalidVertexWithNonGraphElementEdge();
        assertThrows(MetaModelException.class, () -> MetaModelFactory.createMetaModel(invalidVertex));
    }

    @Getter
    @Setter
    private static class InvalidVertexWithVertexProperty extends GraphVertex {
        @Property // INVALID: A property can not be a vertex
        private GraphVertex notAValidProperty;
    }

    @Getter
    @Setter
    private static class InvalidVertexWithEdgeProperty extends GraphVertex {
        @Property // INVALID: A property can not be an edge
        private GraphEdge<?, ?> notAValidProperty;
    }

    @Getter
    @Setter
    private static class InvalidVertex extends GraphVertex {
        @Property // INVALID: A property can not be a vertex
        private InvalidVertex notAValidProperty;
    }

    @Getter
    @Setter
    private static class InvalidEdgeWithEdgeField extends GraphEdge<InvalidVertex, InvalidVertex> {
        @Edge // INVALID: An edge can not have an edge
        private GraphEdge<?, ?> notAValidEdge;
    }

    @Getter
    @Setter
    private static class InvalidEdgeWithVertexField extends GraphEdge<InvalidVertex, InvalidVertex> {
        @Edge // INVALID: An edge can not have an edge
        private GraphVertex notAValidEdge;
    }

    @Getter
    @Setter
    private static class InvalidVertexWithNonGraphElementEdge extends GraphVertex {
        @Edge // INVALID: An edge needs to be instanceof GraphEdge, GraphVertex or a collection of these
        private String notAValidEdge;
    }
}
