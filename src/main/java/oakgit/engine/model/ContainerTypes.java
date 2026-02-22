package oakgit.engine.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ContainerTypes {
  CLUSTERNODES(DocumentEntry.class),
  JOURNAL(DocumentEntry.class),
  NODES(DocumentEntry.class),
  SETTINGS(DocumentEntry.class),
  DATASTORE_DATA(DatastoreDataEntry.class),
  DATASTORE_META(DatastoreMetaEntry.class);

  private final Class<? extends ContainerEntry<?>> entryType;

  public static ContainerTypes fromName(String name) {
    return valueOf(name.toUpperCase());
  }
}
