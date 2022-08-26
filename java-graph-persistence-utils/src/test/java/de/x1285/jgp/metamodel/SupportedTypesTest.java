package de.x1285.jgp.metamodel;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SupportedTypesTest {

    @Test
    public void enumValuesAreSupported() {
        assertTrue(SupportedTypes.isSupportedType(TestEnum.TEST_ENUM.getClass()));
    }

    @Test
    public void booleanValuesAreSupported() {
        assertTrue(SupportedTypes.isSupportedType(boolean.class));
    }

    @Test
    public void booleanClassValuesAreSupported() {
        assertTrue(SupportedTypes.isSupportedType(Boolean.class));
    }

    @Test
    public void charValuesAreSupported() {
        assertTrue(SupportedTypes.isSupportedType(char.class));
    }

    @Test
    public void characterClassValuesAreSupported() {
        assertTrue(SupportedTypes.isSupportedType(Character.class));
    }

    @Test
    public void floatValuesAreSupported() {
        assertTrue(SupportedTypes.isSupportedType(float.class));
    }

    @Test
    public void floatClassValuesAreSupported() {
        assertTrue(SupportedTypes.isSupportedType(Float.class));
    }

    @Test
    public void intValuesAreSupported() {
        assertTrue(SupportedTypes.isSupportedType(int.class));
    }

    @Test
    public void integerValuesAreSupported() {
        assertTrue(SupportedTypes.isSupportedType(Integer.class));
    }

    @Test
    public void longValuesAreSupported() {
        assertTrue(SupportedTypes.isSupportedType(long.class));
    }

    @Test
    public void longClassValuesAreSupported() {
        assertTrue(SupportedTypes.isSupportedType(Long.class));
    }

    @Test
    public void doubleValuesAreSupported() {
        assertTrue(SupportedTypes.isSupportedType(double.class));
    }

    @Test
    public void doubleClassValuesAreSupported() {
        assertTrue(SupportedTypes.isSupportedType(Double.class));
    }

    @Test
    public void shortValuesAreSupported() {
        assertTrue(SupportedTypes.isSupportedType(short.class));
    }

    @Test
    public void shortClassValuesAreSupported() {
        assertTrue(SupportedTypes.isSupportedType(Short.class));
    }

    @Test
    public void testNotSupportedClass() {
        assertFalse(SupportedTypes.isSupportedType(TestClass.class));
    }

    private enum TestEnum {
        TEST_ENUM
    }

    private static class TestClass {

    }


}