package de.x1285.jgp.result.transformer.type.resolver.global;

import de.x1285.jgp.element.GraphElement;
import de.x1285.jgp.result.transformer.type.resolver.GraphElementClassResolverException;
import de.x1285.jpg.test.model.Person;
import lombok.NoArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class GlobalGraphElementClassRegisterTest {

    @BeforeEach
    void reset() {
        GlobalGraphElementClassRegister.reset();
    }

    @Test
    void registeredClassIsGloballyReturned() {
        final String elementsLabel = Person.builder().build().getLabel();
        GlobalGraphElementClassRegister.registerClass(Person.class);

        GlobalGraphElementClassResolver resolverA = new GlobalGraphElementClassResolver();
        Class<? extends GraphElement> resolvedClass = resolverA.resolveClass(elementsLabel);

        assertEquals(Person.class, resolvedClass);
    }

    @Test
    void registeringClassTwiceIsAllowed() {
        GlobalGraphElementClassRegister.registerClass(Person.class);
        assertDoesNotThrow(() -> GlobalGraphElementClassRegister.registerClass(Person.class));
    }

    @Test
    void registeringClassesTwiceIsAllowed() {
        assertDoesNotThrow(() -> GlobalGraphElementClassRegister.registerClasses(Person.class, Person.class));
    }

    @Test
    void registeringClassWithSameLabelThrowsException() {
        GlobalGraphElementClassRegister.registerClass(GraphElementTypeA_WithLabelXY.class);
        assertThrows(GraphElementClassResolverException.class,
                     () -> GlobalGraphElementClassRegister.registerClass(GraphElementTypeB_WithLabelXY.class));
    }

    @Test
    void registeringClassesWithSameLabelThrowsException() {
        assertThrows(GraphElementClassResolverException.class,
                     () -> GlobalGraphElementClassRegister.registerClasses(GraphElementTypeA_WithLabelXY.class,
                                                                           GraphElementTypeB_WithLabelXY.class));
    }

    @Test
    void resetAllowsReregistration() {
        final String elementsLabel = Person.builder().build().getLabel();
        GlobalGraphElementClassRegister.registerClass(Person.class);

        GlobalGraphElementClassResolver resolver = new GlobalGraphElementClassResolver();
        Class<? extends GraphElement> resolvedClass = resolver.resolveClass(elementsLabel);
        assertEquals(Person.class, resolvedClass);

        GlobalGraphElementClassRegister.reset();
        assertThrows(GraphElementClassResolverException.class, () -> resolver.resolveClass(elementsLabel));

        GlobalGraphElementClassRegister.registerClass(Person.class);
        assertEquals(Person.class, resolvedClass);
    }

    @NoArgsConstructor
    private static class GraphElementTypeA_WithLabelXY extends GraphElement {
        @Override
        public String getLabel() {
            return "XY";
        }
    }

    @NoArgsConstructor
    private static class GraphElementTypeB_WithLabelXY extends GraphElement {
        @Override
        public String getLabel() {
            return "XY";
        }
    }

}