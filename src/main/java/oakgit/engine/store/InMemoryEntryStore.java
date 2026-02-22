package oakgit.engine.store;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class InMemoryEntryStore implements EntryStore {

  private final Map<String, Map<String, StorageDocument>> containers = new HashMap<>();

  @Override
  public void createContainer(String name) {
    containers.putIfAbsent(name, new HashMap<>());
  }

  @Override
  public boolean hasContainer(String name) {
    return containers.containsKey(name);
  }

  @Override
  public boolean containsKey(String container, String id) {
    var entries = containers.get(container);
    return entries != null && entries.containsKey(id);
  }

  @Override
  public void put(String container, StorageDocument doc) {
    containers.get(container).put(doc.getId(), doc);
  }

  @Override
  public Optional<StorageDocument> get(String container, String id) {
    var entries = containers.get(container);
    if (entries == null) {
      return Optional.empty();
    }
    return Optional.ofNullable(entries.get(id));
  }

  @Override
  public List<StorageDocument> getAll(String container) {
    var entries = containers.get(container);
    if (entries == null) {
      return List.of();
    }
    return List.copyOf(entries.values());
  }

  @Override
  public boolean remove(String container, String id) {
    var entries = containers.get(container);
    if (entries == null) {
      return false;
    }
    return entries.remove(id) != null;
  }

  @Override
  public void removeAll(String container, List<String> ids) {
    var entries = containers.get(container);
    if (entries != null) {
      ids.forEach(entries::remove);
    }
  }
}
