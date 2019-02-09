package com.diconium.oakgit.model;

import com.diconium.oakgit.jdbc.OakGitResultSet;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.InvocationTargetException;
import java.util.function.Consumer;

public interface ContainerEntry<T extends ContainerEntry>  {

    String EMPTY_ID_VALUE = "EMPTY";

    /**
     * Returns an instance of an empty container typed to the given class.
     * Always returns a new object instance. Calls the ctor(String) of the given type class.
     * Use {@link ContainerEntry#isEmpty(ContainerEntry)} to test if a given object is empty.
     * @param entryClass
     * @param <T>
     * @return ContainerEntry
     */
    static <T extends ContainerEntry<T>> ContainerEntry<T> emptyOf(Class<T> entryClass) {
        try {
            return entryClass.getConstructor(String.class).newInstance(EMPTY_ID_VALUE);
        } catch (InstantiationException | NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            throw new IllegalArgumentException("no ctor with id string found for ContainerEntry implementation");
        }
    }

    /**
     * Tests if a given containerEntry object is of the empty type.
     * @param containerEntry
     * @return boolean
     */
    static boolean isEmpty(ContainerEntry<?> containerEntry) {
        return containerEntry == null || EMPTY_ID_VALUE.equals(containerEntry.getId());
    }

    /**
     * Returns an instance of an invalid container typed to the given class.
     * Always returns a new object instance. Use {@link ContainerEntry#isInvalid(ContainerEntry)}
     * to test if a given object is invalid.
     * @param entryClass
     * @param <T>
     * @return ContainerEntry
     */
    static <T extends ContainerEntry<T>> ContainerEntry<T> invalidOf(Class<T> entryClass) {
        return new InvalidContainerEntry<T>();
    }

    /**
     * Tests if a given containerEntry object is of the invalid type.
     * @param containerEntry
     * @return boolean
     */
    static boolean isInvalid(ContainerEntry<?> containerEntry) {
        return containerEntry instanceof InvalidContainerEntry;
    }

    /**
     * Combines !{@link ContainerEntry#isEmpty(ContainerEntry)} and !{@link ContainerEntry#isInvalid(ContainerEntry)}
     * @param containerEntry
     * @return
     */
    static boolean isValidAndNotEmpty(ContainerEntry<?> containerEntry) {
        return !isInvalid(containerEntry) && !isEmpty(containerEntry);
    }

    /**
     * A unique id for this entry.
     *
     * @return String
     */
    String getId();

    Consumer<OakGitResultSet> getResultSetModifier();

    /**
     * NULL object type to indicate an empty entry.
     * @param <T>
     */
    class EmptyContainerEntry<T extends ContainerEntry<T>>  implements ContainerEntry<T> {

        @Override
        public String getId() {
            return StringUtils.EMPTY;
        }

        @Override
        public Consumer<OakGitResultSet> getResultSetModifier() {
            return (resultSet) -> {};
        }

    }

    /**
     * NULL object type to indicate an invalid entry.
     * @param <T>
     */
    class InvalidContainerEntry<T extends ContainerEntry<T>>  implements ContainerEntry<T> {

        @Override
        public String getId() {
            return "INVALID";
        }

        @Override
        public Consumer<OakGitResultSet> getResultSetModifier() {
            throw new IllegalArgumentException("trying to apply invalid entry to a result set");
        }

    }
}
