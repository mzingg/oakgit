package com.diconium.oakgit.model;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.*;

@RequiredArgsConstructor
public class Container {

    @NonNull
    @Getter
    private final String name;

    private final Map<String, ContainerEntry<?>> entries = new HashMap<>();

    public Container setEntry(@NonNull ContainerEntry entry) {
        entries.put(entry.getId(), entry);
        return this;
    }

    @SuppressWarnings("unchecked")
    public <T extends ContainerEntry<T>> Optional<ContainerEntry<T>> findById(String id, Class<T> resultType) {
        if (entries.containsKey(id)) {
            ContainerEntry<?> containerEntry = entries.get(id);
            if (resultType.isAssignableFrom(containerEntry.getClass())) {
                return Optional.of((T) containerEntry);
            }
        }
        return Optional.empty();
    }

    @SuppressWarnings("unchecked")
    public <T extends ContainerEntry<T>> List<ContainerEntry<T>> findByIdRange(String idMin, String idMax, Class<T> resultType) {
        ArrayList<ContainerEntry<T>> result = new ArrayList<>();

        try {
            int min = Integer.parseInt(idMin);
            int max = Integer.parseInt(idMax);
            for (String key : entries.keySet()) {
                ContainerEntry<?> containerEntry = entries.get(key);
                if (resultType.isAssignableFrom(containerEntry.getClass())) {
                    try {
                        int k = Integer.parseInt(key);
                        if (k > min && k < max) {

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
}
