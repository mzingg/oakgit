package oakgit.engine.commands;

import lombok.Getter;
import lombok.NonNull;
import oakgit.engine.ContainerCommand;
import oakgit.engine.model.ContainerEntry;
import oakgit.engine.model.DatastoreDataEntry;
import oakgit.engine.model.DatastoreMetaEntry;
import oakgit.engine.model.DocumentEntry;

import java.util.Map;

@Getter
public abstract class AbstractContainerCommand<T extends ContainerEntry<T>> implements ContainerCommand<T> {

  private static final Map<String, Class<? extends ContainerEntry<?>>> TYPE_MAP = Map.of(
    "CLUSTERNODES", DocumentEntry.class,
    "JOURNAL", DocumentEntry.class,
    "NODES", DocumentEntry.class,
    "SETTINGS", DocumentEntry.class,
    "DATASTORE_DATA", DatastoreDataEntry.class,
    "DATASTORE_META", DatastoreMetaEntry.class
  );

  @NonNull
  private final String containerName;

  @NonNull
  private final Class<T> entryType;

  @SuppressWarnings("unchecked")
  public AbstractContainerCommand(@NonNull String containerName) {
    this.containerName = containerName;
    if (!TYPE_MAP.containsKey(containerName.toUpperCase())) {
      throw new IllegalArgumentException("unknown container");
    }
    this.entryType = (Class<T>) TYPE_MAP.get(containerName.toUpperCase());
  }

}
