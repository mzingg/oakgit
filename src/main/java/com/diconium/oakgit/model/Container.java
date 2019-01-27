package com.diconium.oakgit.model;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

@RequiredArgsConstructor
public class Container {

    @NonNull
    @Getter
    private final String name;

    private final Map<String, ContainerEntry> entries = new HashMap<>();

    public Container setEntry(@NonNull ContainerEntry entry) {
        entries.put(entry.getID(), entry);
        return this;
    }

    public Optional<ContainerEntry> findById(String id) {
        if (entries.containsKey(id)) {
            return Optional.of(entries.get(id));
        }
        return Optional.empty();
    }

    public List<ContainerEntry> findByIdRange(String idMin, String idMax) {
        ArrayList<ContainerEntry> result = new ArrayList<>();

        try {
            int min = Integer.parseInt(idMin);
            int max = Integer.parseInt(idMax);
            for (String key : entries.keySet()) {
                try {
                    int k = Integer.parseInt(key);
                    if (k > min && k < max) {
                        result.add(entries.get(key));
                    }
                } catch (NumberFormatException skipped) {
                    // skip this entry
                }
            }
        } catch (NumberFormatException ignored) {
            // fall through to empty result
        }

        return result;
    }
}
