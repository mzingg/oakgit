package oakgit.processor.inmemory;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import oakgit.engine.model.ContainerEntry;
import oakgit.engine.model.ModCountSupport;

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
        return Optional.of((T) entry.copy());
      }
    }
    return Optional.empty();
  }

  @SuppressWarnings("unchecked")
  public <T extends ContainerEntry<T>> Optional<T> findByIdAndModCount(String id, long modCount, Class<T> resultType) {
    if (entries.containsKey(id)) {
      ContainerEntry<?> entry = entries.get(id);
      if (resultType.isAssignableFrom(entry.getClass())) {
        if (entry instanceof ModCountSupport) {
          if (((ModCountSupport) entry).getModCount() == modCount) {
            return Optional.of((T) entry.copy());
          }
        } else {
          return Optional.of((T) entry.copy());
        }
      }
    }
    return Optional.empty();
  }

  @SuppressWarnings("unchecked")
  public <T extends ContainerEntry<T>> List<T> findByIdRange(String idMin, String idMax, Class<T> resultType) {
    ArrayList<T> result = new ArrayList<>();

    NaturalOrderComparator comparator = new NaturalOrderComparator();
    for (String key : entries.keySet()) {
      ContainerEntry<?> containerEntry = entries.get(key);
      if (resultType.isAssignableFrom(containerEntry.getClass())) {
        String entryId = containerEntry.getId();
        if (comparator.compare(entryId, idMin) >= 0 && comparator.compare(entryId, idMax) <= 0) {
          result.add((T) containerEntry.copy());
        }
      }
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
          result.add((T) containerEntry.copy());
        }
      }
    }

    return result;
  }
}
