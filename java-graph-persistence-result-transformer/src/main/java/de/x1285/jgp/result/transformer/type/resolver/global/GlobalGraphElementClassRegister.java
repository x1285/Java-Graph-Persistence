package de.x1285.jgp.result.transformer.type.resolver.global;

import de.x1285.jgp.element.GraphElement;
import de.x1285.jgp.result.transformer.type.resolver.GraphElementClassResolverException;

import java.util.HashMap;
import java.util.Optional;

public class GlobalGraphElementClassRegister {

    private static final HashMap<Object, Class<? extends GraphElement>> REGISTERED_CLASSES = new HashMap<>();

    static Optional<Class<? extends GraphElement>> findByLabel(Object label) {
        return Optional.ofNullable(REGISTERED_CLASSES.get(label));
    }

    /**
     * Registers all {@link GraphElement} in the given array to the JVM global graph element register.
     *
     * @param clazzes {@link GraphElement} classes to register.
     * @throws GraphElementClassResolverException when there is already another class registered for one in the array,
     *                                            which uses the same label.
     */
    public static void registerClasses(Class<? extends GraphElement>... clazzes) {
        for (Class<? extends GraphElement> clazz : clazzes) {
            registerClass(clazz);
        }
    }

    /**
     * Registers a {@link GraphElement} class to the JVM global graph element register.
     *
     * @param clazz {@link GraphElement} class to register.
     * @throws GraphElementClassResolverException when there is already another class registered with the same label.
     */
    public static void registerClass(Class<? extends GraphElement> clazz) {
        try {
            final String label = clazz.newInstance().getLabel();
            Class<? extends GraphElement> registeredClass = REGISTERED_CLASSES.get(label);
            checkValidity(clazz, label, registeredClass);
            REGISTERED_CLASSES.put(label, clazz);
        } catch (InstantiationException | IllegalAccessException e) {
            throw new GraphElementClassResolverException("Could not initialize GraphElements class: " + clazz, e);
        }
    }

    /**
     * Resets the register: Removes all registeres {@link GraphElement} classes and brings the register back to it's
     * initial state.
     */
    public static void reset() {
        REGISTERED_CLASSES.clear();
    }

    private static void checkValidity(Class<? extends GraphElement> clazz,
                                      String label,
                                      Class<? extends GraphElement> registeredClass) {
        if (registeredClass != null && !clazz.equals(registeredClass)) {
            final String messageTemplate = "There was already another GraphElement class registered for label %s: %s." +
                    " Could not register new class: %s";
            final String message = String.format(messageTemplate, label, registeredClass, clazz);
            throw new GraphElementClassResolverException(message);
        }
    }
}
