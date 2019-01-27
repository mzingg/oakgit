package com.diconium.oakgit.engine.model;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RequiredArgsConstructor
public class Container {

    @NonNull
    @Getter
    private final String name;

    private final Map<String, ContainerEntry<?>> entries = new HashMap<>();

    public <T extends ContainerEntry<T>> Container setEntry(@NonNull ContainerEntry<T> entry) {
        entries.put(entry.getId(), entry);
        return this;
    }

    @SuppressWarnings("unchecked")
    public <T extends ContainerEntry<T>> Optional<ContainerEntry<T>> findById(String id, Class<T> resultType) {
        if (entries.containsKey(id)) {
            ContainerEntry<?> entry = entries.get(id);
            if (resultType.isAssignableFrom(entry.getClass())) {
                return Optional.of((T) entry);
            }
        }
        return Optional.empty();
    }

    @SuppressWarnings("unchecked")
    public <T extends ContainerEntry<T>> Optional<ContainerEntry<T>> findByIdAndModCount(String id, long modCount, Class<T> resultType) {
        if (entries.containsKey(id)) {
            ContainerEntry<?> entry = entries.get(id);
            if (resultType.isAssignableFrom(entry.getClass()) && (entry.getModCount() == null || entry.getModCount() == modCount)) {
                return Optional.of((T) entry);
            }
        }
        return Optional.empty();
    }

    @SuppressWarnings("unchecked")
    public <T extends ContainerEntry<T>> List<ContainerEntry<T>> findByIdRange(String idMin, String idMax, Class<T> resultType) {
        ArrayList<ContainerEntry<T>> result = new ArrayList<>();

        try {
            for (String key : entries.keySet()) {
                ContainerEntry<?> containerEntry = entries.get(key);
                if (resultType.isAssignableFrom(containerEntry.getClass())) {
                    try {
                        if (containerEntry.getId().compareTo(idMin) >= 0 && containerEntry.getId().compareTo(idMax) <= 0) {
                            result.add((T) containerEntry);
                        }
                    } catch (NumberFormatException skipped) {
                        // skip this entry
                    }
                }
            }
        } catch (NumberFormatException ignored) {
            // fall through to empty result
        }

        return result;
    }

    @SuppressWarnings("unchecked")
    public <T extends ContainerEntry<T>> List<ContainerEntry<T>> findByIds(List<String> ids, Class<T> resultType) {
        ArrayList<ContainerEntry<T>> result = new ArrayList<>();

        for (String id : ids) {
            if (entries.containsKey(id)) {
                ContainerEntry<?> containerEntry = entries.get(id);
                if (resultType.isAssignableFrom(containerEntry.getClass())) {
                    result.add((T) containerEntry);
                }
            }
        }

        return result;
    }
}
