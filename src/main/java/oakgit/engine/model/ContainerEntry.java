package oakgit.engine.model;

import lombok.Data;
import lombok.NonNull;
import oakgit.jdbc.OakGitResultSet;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.function.Consumer;

public interface ContainerEntry<T extends ContainerEntry> {

    /**
     * Returns an instance of an empty container typed to the given class.
     * Always returns a new object instance. Calls the ctor(String) of the given type class.
     * Use {@link ContainerEntry#isEmpty(ContainerEntry)} to test if a given object is empty.
     *
     * @param entryClass
     * @param <T>
     * @return ContainerEntry
     */
    static <T extends ContainerEntry<T>> T emptyOf(Class<T> entryClass) {
        try {
            return entryClass.getConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            throw new IllegalArgumentException("no empty ctor found for ContainerEntry implementation");
        }
    }

    /**
     * Tests if a given containerEntry object is of the empty type.
     *
     * @param containerEntry
     * @return boolean
     */
    static boolean isEmpty(ContainerEntry<?> containerEntry) {
        return containerEntry == null || "".equals(containerEntry.getId());
    }

    /**
     * Returns an instance of an invalid container typed to the given class.
     * Always returns a new object instance. Use {@link ContainerEntry#isInvalid(ContainerEntry)}
     * to test if a given object is invalid.
     *
     * @param entryClass
     * @param <T>
     * @return ContainerEntry
     */
    static <T extends ContainerEntry<T>> ContainerEntry<T> invalidOf(Class<T> entryClass) {
        return new InvalidContainerEntry<T>();
    }

    /**
     * Tests if a given containerEntry object is of the invalid type.
     *
     * @param containerEntry
     * @return boolean
     */
    static boolean isInvalid(ContainerEntry<?> containerEntry) {
        return containerEntry instanceof InvalidContainerEntry;
    }

    /**
     * Combines !{@link ContainerEntry#isEmpty(ContainerEntry)} and !{@link ContainerEntry#isInvalid(ContainerEntry)}
     *
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

    Map<String, OakGitResultSet.Column> getAvailableColumnsByName();

    default Consumer<OakGitResultSet> getResultSetTypeModifier(@NonNull List<String> fieldList) {
        return result -> {
            List<String> fields = expandOrReturnFieldList(fieldList);
            for (String specialFieldName : fields) {
                String fieldName = typeGetter(specialFieldName)
                        .orElseThrow(() -> new IllegalStateException("could not assign column type to fieldName: " + specialFieldName));
                result.addColumn(getAvailableColumnsByName().get(fieldName).copy());
            }
        };
    }

    default List<String> expandOrReturnFieldList(@NonNull List<String> fieldList) {
        List<String> result = new ArrayList<>(fieldList);
        if (result.isEmpty()) {
            result.addAll(getAvailableColumnsByName().keySet());
        }
        return result;
    }

    default Optional<String> typeGetter(String fieldName) {
        if (getAvailableColumnsByName().containsKey(fieldName)) {
            return Optional.of(fieldName);
        }
        return Optional.empty();
    }

    default Consumer<OakGitResultSet> getResultSetModifier(@NonNull List<String> fieldList) {
        return result -> {
            List<String> fields = expandOrReturnFieldList(fieldList);
            for (String specialFieldName : fields) {
                ColumnGetterResult getterResult = entryGetter(specialFieldName)
                        .orElseThrow(() -> new IllegalStateException("could not assign entry to fieldName: " + specialFieldName));
                result.addValue(getterResult.getFieldName(), getterResult.getValue());
            }
        };
    }

    Optional<ColumnGetterResult> entryGetter(String fieldName);


    @Data
    static class ColumnGetterResult {
        @NonNull
        private final String fieldName;

        private final Object value;
    }

    /**
     * NULL object type to indicate an invalid entry.
     *
     * @param <T>
     */
    class InvalidContainerEntry<T extends ContainerEntry<T>> implements ContainerEntry<T> {

        @Override
        public String getId() {
            throw new UnsupportedOperationException("trying to get id from an invalid entry");
        }

        @Override
        public Map<String, OakGitResultSet.Column> getAvailableColumnsByName() {
            throw new UnsupportedOperationException("trying to get columns from an invalid entry");
        }

        @Override
        public Optional<ColumnGetterResult> entryGetter(String fieldName) {
            throw new UnsupportedOperationException("trying to apply invalid entry to a result set");
        }

    }
}
