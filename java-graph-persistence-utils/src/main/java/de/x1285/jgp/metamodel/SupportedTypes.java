package de.x1285.jgp.metamodel;

import java.util.Arrays;
import java.util.List;

public class SupportedTypes {

    public static final List<Class<?>> SUPPORTED_PROPERTY_TYPES = Arrays.asList(
            String.class, char.class, Character.class, short.class, Short.class, int.class, Integer.class,
            long.class, Long.class, boolean.class, Boolean.class, float.class, Float.class, double.class, Double.class);

    public static boolean isSupportedType(Class<?> type) {
        return SUPPORTED_PROPERTY_TYPES.contains(type) || type.isEnum();
    }

}
