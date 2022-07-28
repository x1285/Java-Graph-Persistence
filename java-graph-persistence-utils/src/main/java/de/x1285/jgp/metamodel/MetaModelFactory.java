package de.x1285.jgp.metamodel;

import de.x1285.jgp.api.annotation.Edge;
import de.x1285.jgp.api.annotation.Property;
import de.x1285.jgp.element.GraphEdge;
import de.x1285.jgp.element.GraphElement;
import de.x1285.jgp.element.GraphVertex;
import de.x1285.jgp.metamodel.field.EdgeCollectionField;
import de.x1285.jgp.metamodel.field.EdgeField;
import de.x1285.jgp.metamodel.field.PropertyField;
import de.x1285.jgp.metamodel.field.RelevantField;
import sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;

public class MetaModelFactory {

    public static final List<Class<?>> SUPPORTED_PROPERTY_TYPES = Arrays.asList(String.class, Character.class, Short.class,
                                                                                Integer.class, Long.class, Boolean.class, Float.class, Double.class);

    private static final HashMap<Class<? extends GraphElement>, List<Field>> CLASS_FIELDS = new HashMap<>();

    public static MetaModel createMetaModel(GraphElement element) {
        return createMetaModel(element.getClass());
    }

    public static MetaModel createMetaModel(Class<? extends GraphElement> elementClass) {
        List<PropertyDescriptor> pds = preparePropertyDescriptors(elementClass);
        List<RelevantField<?, ?, ?>> relevantFields = createFields(elementClass, pds);
        return new MetaModel(elementClass, relevantFields);
    }

    protected static List<RelevantField<?, ?, ?>> createFields(Class<? extends GraphElement> elementClass, List<PropertyDescriptor> pds) {
        List<RelevantField<?, ?, ?>> relevantFields = new ArrayList<>();
        for (PropertyDescriptor pd : pds) {
            final Class<?> type = pd.getPropertyType();
            RelevantField<?, ?, ?> relevantField = createField(pd, type, elementClass);
            if (relevantField != null) {
                relevantFields.add(relevantField);
            }
        }
        return relevantFields;
    }

    private static <E extends GraphElement, T> RelevantField<E, T, ? extends Annotation>
    createField(PropertyDescriptor pd, Class<T> type, Class<E> elementClass) {
        final Field field = getField(pd.getName(), elementClass);
        final Property property = field.getAnnotation(Property.class);
        final Edge edge = field.getAnnotation(Edge.class);
        if (edge != null && property != null) {
            throw new MetaModelException("Both annotations ('Property' and 'Edge') found at class "
                                                 + elementClass.getSimpleName() + " on field " + field.getName());
        } else if (property != null) {
            return createPropertyField(pd, field, elementClass);
        } else if (edge != null) {
            if (GraphVertex.class.isAssignableFrom(elementClass)) {
                final Class<? extends GraphVertex> vertexClass = (Class<? extends GraphVertex>) elementClass;
                if (Collection.class.isAssignableFrom(type)) {
                    final Class<? extends Collection> collectionType = (Class<? extends Collection>) type;
                    final Class<?> genericType = getFieldsGenericType(field, 0);
                    return (RelevantField<E, T, Edge>) createEdgeForCollectionField(pd, edge, collectionType, genericType, vertexClass);
                } else if (GraphEdge.class.isAssignableFrom(type)) {
                    return (RelevantField<E, T, Edge>) createEdgeField(pd, edge, type, vertexClass);
                } else if (GraphVertex.class.isAssignableFrom(type)) {
                    return (RelevantField<E, T, Edge>) createEdgeForVertexField(pd, edge, type, vertexClass);
                }
            }
            final String message = "Edge field found on non GraphVertex class: " + elementClass.getSimpleName();
            throw new MetaModelException(message);
        }
        return null;
    }

    private static <E extends GraphVertex, T extends Collection<G>, G>
    EdgeCollectionField<E, T, G> createEdgeForCollectionField(PropertyDescriptor pd, Edge annotation,
                                                              Class<T> collectionType, Class<G> genericType,
                                                              Class<E> vertexClass) {
        EdgeCollectionField<E, T, G> edgeField = new EdgeCollectionField<>();
        edgeField.setFieldName(pd.getName());
        edgeField.setAnnotation(annotation);
        edgeField.setType(collectionType);
        edgeField.setElementClass(vertexClass);
        edgeField.setGenericType(genericType);
        edgeField.setSetter(createSetter(pd, vertexClass));
        edgeField.setGetter(createGetter(pd, vertexClass));
        return edgeField;
    }

    private static <E extends GraphVertex, T>
    EdgeField<E, T> createEdgeForVertexField(PropertyDescriptor pd, Edge annotation, Class<T> type, Class<E> vertexClass) {
        EdgeField<E, T> edgeField = new EdgeField<>();
        edgeField.setFieldName(pd.getName());
        edgeField.setAnnotation(annotation);
        edgeField.setType(type);
        edgeField.setElementClass(vertexClass);
        edgeField.setSetter(createSetter(pd, vertexClass));
        edgeField.setGetter(createGetter(pd, vertexClass));
        return edgeField;
    }

    private static <E extends GraphVertex, T>
    EdgeField<E, T> createEdgeField(PropertyDescriptor pd, Edge annotation, Class<T> type, Class<E> vertexClass) {
        // the type parameter is needed for the generics!
        EdgeField<E, T> edgeField = new EdgeField<>();
        edgeField.setFieldName(pd.getName());
        edgeField.setAnnotation(annotation);
        edgeField.setType(type);
        edgeField.setElementClass(vertexClass);
        edgeField.setSetter(createSetter(pd, vertexClass));
        edgeField.setGetter(createGetter(pd, vertexClass));
        // TODO: 28.04.2022 check if annotation contains direction ? -> FAIL or LOG ?
        // TODO: 28.04.2022 handle edge properties ?
        return edgeField;
    }

    // the type parameter is needed for the generics!
    private static <E extends GraphElement, T> PropertyField<E, T>
    createPropertyField(PropertyDescriptor pd, Field field, Class<E> elementClass) {
        checkPropertySupport(field, elementClass);

        PropertyField<E, T> propertyField = new PropertyField<>();
        Property annotation = field.getAnnotation(Property.class);
        // TODO: 03.05.2022 check that annotation not null
        propertyField.setFieldName(pd.getName());
        propertyField.setAnnotation(annotation);
        propertyField.setSetter(createSetter(pd, elementClass));
        propertyField.setGetter(createGetter(pd, elementClass));
        return propertyField;
    }

    private static void checkPropertySupport(Field field, Class<?> elementClass) {
        if (!SUPPORTED_PROPERTY_TYPES.contains(field.getType())) {
            final String message = String.format("Unsupported value type %s on field %s of element %s.",
                                                 field.getType(), field.getName(), elementClass);
            throw new MetaModelException(message);
        }
    }

    private static Class<?> getFieldsGenericType(Field field, int index) {
        final ParameterizedType generic = (ParameterizedType) field.getGenericType();
        final Type genericType = generic.getActualTypeArguments()[index];
        if (genericType instanceof ParameterizedTypeImpl) {
            return ((ParameterizedTypeImpl) genericType).getRawType();
        } else {
            return (Class<?>) genericType;
        }
    }

    private static <E extends GraphElement, T> BiConsumer<E, T> createSetter(PropertyDescriptor pd,
                                                                             Class<? extends GraphElement> elementClass) {
        return (element, value) -> {
            try {
                pd.getWriteMethod().invoke(element, value);
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new MetaModelException("Could not call write method of field " + pd.getName() + " at class "
                                                     + elementClass.getSimpleName(), e);
            }
        };
    }

    private static <E extends GraphElement, T> Function<E, T> createGetter(PropertyDescriptor pd,
                                                                           Class<? extends GraphElement> elementClass) {
        return (element) -> {
            try {
                return (T) pd.getReadMethod().invoke(element);
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new MetaModelException("Could not call read method of field " + pd.getName() + " at class "
                                                     + elementClass.getSimpleName(), e);
            }
        };
    }

    protected static List<PropertyDescriptor> preparePropertyDescriptors(final Class<?> type) {
        List<PropertyDescriptor> result = new ArrayList<>();
        try {
            PropertyDescriptor[] descriptors = Introspector.getBeanInfo(type, Object.class).getPropertyDescriptors();
            for (PropertyDescriptor pd : descriptors) {
                if (pd.getReadMethod() != null && pd.getWriteMethod() != null) {
                    result.add(pd);
                }
            }
            return result;
        } catch (IntrospectionException e) {
            throw new MetaModelException(e);
        }
    }

    private static <T extends GraphElement> Field getField(String name, Class<T> type) {
        List<Field> fields = getClassFields(type);
        final List<Field> namedField = fields.stream().filter(f -> name.equals(f.getName())).collect(Collectors.toList());
        if (namedField.size() == 1) {
            return namedField.get(0);
        }
        throw new MetaModelException("Could not find field " + name + " at class " + type.getSimpleName());
    }

    private static <T extends GraphElement> List<Field> getClassFields(Class<T> type) {
        if (CLASS_FIELDS.containsKey(type)) {
            return CLASS_FIELDS.get(type);
        }
        List<Field> fields = getClassFields(type, GraphElement.class);
        CLASS_FIELDS.put(type, fields);
        return fields;
    }

    private static <T extends GraphElement> List<Field> getClassFields(Class<T> type, Class<GraphElement> untilClass) {
        List<Field> fields = new ArrayList<>();
        while (type != null) {
            if (CLASS_FIELDS.containsKey(type)) {
                fields.addAll(CLASS_FIELDS.get(type));
            } else {
                List<Field> typeFields = Arrays.asList(type.getDeclaredFields());
                CLASS_FIELDS.put(type, typeFields);
                fields.addAll(typeFields);
            }
            Class<? super T> superclass = type.getSuperclass();
            if (untilClass.isAssignableFrom(superclass)) {
                type = (Class<T>) superclass;
            } else {
                type = null;
            }
        }
        return fields;
    }
}
