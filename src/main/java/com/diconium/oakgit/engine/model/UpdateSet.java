package com.diconium.oakgit.engine.model;

import com.diconium.oakgit.jdbc.OakGitResultSet;
import lombok.NonNull;
import lombok.ToString;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

@ToString
public class UpdateSet implements ContainerEntry<UpdateSet> {

    private final Map<String, Object> updatedValues = new HashMap<>();

    public UpdateSet withValue(@NonNull String name, Object value) {
        updatedValues.put(name, value);
        return this;
    }

    @SuppressWarnings("unchecked")
    public <T> UpdateSet whenHasValue(@NonNull String name, Class<T> targetType, Consumer<T> consumer) {
        if (updatedValues.containsKey(name)) {
            Object value = updatedValues.get(name);
            if (value == null || targetType.isAssignableFrom(value.getClass())) {
                consumer.accept((T) value);
            } else {
                System.out.println("value " + name + " of type " + targetType.getName() + " not accepted: value has type " + value.getClass().getName());
            }
        } else {
            System.out.println("value " + name + " of type " + targetType.getName() + " does not exist");
        }
        return this;
    }

    @SuppressWarnings("unchecked")
    public <T> Optional<T> getValue(@NonNull String name, Class<T> targetType) {
        if (updatedValues.containsKey(name)) {
            Object value = updatedValues.get(name);
            if (value != null && targetType.isAssignableFrom(value.getClass())) {
                return Optional.of((T) value);
            }
        }

        throw new IllegalArgumentException("value " + name + " of type " + targetType.getName() + " not found");
    }

    @Override
    public String getId() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Long getModCount() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Consumer<OakGitResultSet> getResultSetTypeModifier() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Consumer<OakGitResultSet> getResultSetModifier() {
        throw new UnsupportedOperationException();
    }

}
