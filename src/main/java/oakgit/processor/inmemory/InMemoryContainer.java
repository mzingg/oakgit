package oakgit.processor.inmemory;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import oakgit.engine.model.ContainerEntry;

import java.util.*;

@RequiredArgsConstructor
public class InMemoryContainer {

    @NonNull
    @Getter
    private final String name;

    private final Map<String, ContainerEntry<?>> entries = new HashMap<>();

    public <T extends ContainerEntry<T>> InMemoryContainer setEntry(@NonNull ContainerEntry<T> entry) {
        entries.put(entry.getId(), entry);
        return this;
    }

    @SuppressWarnings("unchecked")
    public <T extends ContainerEntry<T>> Optional<T> findById(String id, Class<T> resultType) {
        if (entries.containsKey(id)) {
            ContainerEntry<?> entry = entries.get(id);
            if (resultType.isAssignableFrom(entry.getClass())) {
                return Optional.of((T) entry);
            }
        }
        return Optional.empty();
    }

    @SuppressWarnings("unchecked")
    public <T extends ContainerEntry<T>> Optional<T> findByIdAndModCount(String id, long modCount, Class<T> resultType) {
        if (entries.containsKey(id)) {
            ContainerEntry<?> entry = entries.get(id);
            if (resultType.isAssignableFrom(entry.getClass())) {
                return Optional.of((T) entry);
            }
        }
        return Optional.empty();
    }

    @SuppressWarnings("unchecked")
    public <T extends ContainerEntry<T>> List<T> findByIdRange(String idMin, String idMax, Class<T> resultType) {
        ArrayList<T> result = new ArrayList<>();

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
    public <T extends ContainerEntry<T>> List<T> findByIds(List<String> ids, Class<T> resultType) {
        ArrayList<T> result = new ArrayList<>();

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
