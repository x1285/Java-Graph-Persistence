package de.x1285.jgp.tests;

import de.x1285.jgp.api.annotation.Edge;
import de.x1285.jgp.api.annotation.Property;
import de.x1285.jgp.metamodel.MetaModel;
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
import org.junit.Assert;
import org.junit.Test;

import java.util.List;
import java.util.stream.Collectors;

public class MetaModelFactoryTest {

    @Test
    public void testPerson() {
        // Create test data
        MetaModel metaModel = MetaModelFactory.createMetaModel(Person.class);
        List<RelevantField<?, ?, ?>> relevantFields = metaModel.getRelevantFields();
        Assert.assertEquals(6, relevantFields.size());
        // test @Property fields
        List<RelevantField<?, ?, ?>> properties = relevantFields.stream().filter(x -> x instanceof PropertyField).collect(Collectors.toList());
        Assert.assertEquals(2, properties.size());
        Assert.assertTrue(properties.stream().allMatch(x -> x.getAnnotation() instanceof Property));
        // test @Edge fields
        List<RelevantField<?, ?, ?>> edges = relevantFields.stream().filter(x -> x instanceof EdgeField).collect(Collectors.toList());
        Assert.assertEquals(4, edges.size());
        Assert.assertTrue(edges.stream().allMatch(x -> x.getAnnotation() instanceof Edge));
        List<RelevantField<?, ?, ?>> edgeCollections = edges.stream().filter(x -> x instanceof EdgeCollectionField).collect(Collectors.toList());
        Assert.assertEquals(3, edgeCollections.size());
    }

    @Test
    public void testPlace() {
        // Create test data
        MetaModel metaModel = MetaModelFactory.createMetaModel(Place.class);
        List<RelevantField<?, ?, ?>> relevantFields = metaModel.getRelevantFields();
        Assert.assertEquals(1, relevantFields.size());
        // test @Property fields
        List<RelevantField<?, ?, ?>> properties = relevantFields.stream().filter(x -> x instanceof PropertyField).collect(Collectors.toList());
        Assert.assertEquals(1, properties.size());
        Assert.assertTrue(properties.stream().allMatch(x -> x.getAnnotation() instanceof Property));
        // test @Edge fields
        List<RelevantField<?, ?, ?>> edges = relevantFields.stream().filter(x -> x instanceof EdgeField).collect(Collectors.toList());
        Assert.assertEquals(0, edges.size());
        Assert.assertTrue(edges.stream().allMatch(x -> x.getAnnotation() instanceof Edge));
        List<RelevantField<?, ?, ?>> edgeCollections = edges.stream().filter(x -> x instanceof EdgeCollectionField).collect(Collectors.toList());
        Assert.assertEquals(0, edgeCollections.size());
    }

    @Test
    public void testSoftware() {
        // Create test data
        MetaModel metaModel = MetaModelFactory.createMetaModel(Software.class);
        List<RelevantField<?, ?, ?>> relevantFields = metaModel.getRelevantFields();
        Assert.assertEquals(2, relevantFields.size());
        // test @Property fields
        List<RelevantField<?, ?, ?>> properties = relevantFields.stream().filter(x -> x instanceof PropertyField).collect(Collectors.toList());
        Assert.assertEquals(2, properties.size());
        Assert.assertTrue(properties.stream().allMatch(x -> x.getAnnotation() instanceof Property));
        // test @Edge fields
        List<RelevantField<?, ?, ?>> edges = relevantFields.stream().filter(x -> x instanceof EdgeField).collect(Collectors.toList());
        Assert.assertEquals(0, edges.size());
        Assert.assertTrue(edges.stream().allMatch(x -> x.getAnnotation() instanceof Edge));
        List<RelevantField<?, ?, ?>> edgeCollections = edges.stream().filter(x -> x instanceof EdgeCollectionField).collect(Collectors.toList());
        Assert.assertEquals(0, edgeCollections.size());
    }

    @Test
    public void testKnows() {
        // Create test data
        MetaModel metaModel = MetaModelFactory.createMetaModel(Knows.class);
        List<RelevantField<?, ?, ?>> relevantFields = metaModel.getRelevantFields();
        Assert.assertEquals(1, relevantFields.size());
        // test @Property fields
        List<RelevantField<?, ?, ?>> properties = relevantFields.stream().filter(x -> x instanceof PropertyField).collect(Collectors.toList());
        Assert.assertEquals(1, properties.size());
        Assert.assertTrue(properties.stream().allMatch(x -> x.getAnnotation() instanceof Property));
        // test @Edge fields
        List<RelevantField<?, ?, ?>> edges = relevantFields.stream().filter(x -> x instanceof EdgeField).collect(Collectors.toList());
        Assert.assertEquals(0, edges.size());
        Assert.assertTrue(edges.stream().allMatch(x -> x.getAnnotation() instanceof Edge));
        List<RelevantField<?, ?, ?>> edgeCollections = edges.stream().filter(x -> x instanceof EdgeCollectionField).collect(Collectors.toList());
        Assert.assertEquals(0, edgeCollections.size());
    }

    @Test
    public void testCreated() {
        // Create test data
        MetaModel metaModel = MetaModelFactory.createMetaModel(Created.class);
        List<RelevantField<?, ?, ?>> relevantFields = metaModel.getRelevantFields();
        Assert.assertEquals(1, relevantFields.size());
        // test @Property fields
        List<RelevantField<?, ?, ?>> properties = relevantFields.stream().filter(x -> x instanceof PropertyField).collect(Collectors.toList());
        Assert.assertEquals(1, properties.size());
        Assert.assertTrue(properties.stream().allMatch(x -> x.getAnnotation() instanceof Property));
        // test @Edge fields
        List<RelevantField<?, ?, ?>> edges = relevantFields.stream().filter(x -> x instanceof EdgeField).collect(Collectors.toList());
        Assert.assertEquals(0, edges.size());
        Assert.assertTrue(edges.stream().allMatch(x -> x.getAnnotation() instanceof Edge));
        List<RelevantField<?, ?, ?>> edgeCollections = edges.stream().filter(x -> x instanceof EdgeCollectionField).collect(Collectors.toList());
        Assert.assertEquals(0, edgeCollections.size());
    }
}
