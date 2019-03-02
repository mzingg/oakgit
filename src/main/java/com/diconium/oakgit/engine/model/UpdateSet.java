package com.diconium.oakgit.engine.model;

import com.diconium.oakgit.jdbc.OakGitResultSet;
import lombok.NonNull;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class UpdateSet implements ContainerEntry<UpdateSet> {

    private Map<String, Object> updatedValues = new HashMap<>();

    public UpdateSet withValue(@NonNull String name, Object value) {
        updatedValues.put(name, value);
        return this;
    }

    @Override
    public String getId() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Consumer<OakGitResultSet> getResultSetModifier() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String toString() {
        return "UpdateSet{" +
                "updatedValues=" + updatedValues +
                '}';
    }
}
