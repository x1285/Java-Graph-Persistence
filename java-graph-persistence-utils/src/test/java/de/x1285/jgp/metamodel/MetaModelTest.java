package de.x1285.jgp.metamodel;

import de.x1285.jgp.api.annotation.Property;
import de.x1285.jgp.metamodel.field.EdgeField;
import de.x1285.jgp.metamodel.field.PropertyField;
import de.x1285.jgp.metamodel.field.RelevantField;
import de.x1285.jpg.test.model.Person;
import de.x1285.jpg.test.model.Software;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MetaModelTest {

    @Test
    void createsExpectedMetaModelForGraphVertexPerson() {
        final MetaModel<Person> personMetaModel = MetaModelFactory.createMetaModel(Person.class);
        assertEquals(personMetaModel.getElementClass(), Person.class);
        final List<RelevantField<Person, ?, ?>> relevantFields = personMetaModel.getRelevantFields();
        assertEquals(relevantFields.size(), 6);
        assertEquals(relevantFields.stream().filter(f -> f instanceof PropertyField).count(), 2);
        assertEquals(relevantFields.stream().filter(f -> f instanceof EdgeField).count(), 4);
    }

    @Test
    void findRelevantFieldByLabelGivesExpectedField() {
        final MetaModel<Software> softwareMetaModel = MetaModelFactory.createMetaModel(Software.class);
        Optional<RelevantField<Software, ?, ?>> languageField = softwareMetaModel.findRelevantFieldByLabel("language");
        assertTrue(languageField.isPresent());
        assertEquals(languageField.get().getFieldName(), "lang");
        assertTrue(languageField.get() instanceof PropertyField);
        assertTrue(languageField.get().getAnnotation() instanceof Property);
    }

}