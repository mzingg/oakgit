package oakgit.engine.commands;

import java.util.Collections;
import java.util.List;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import oakgit.engine.ContainerCommandResult;
import oakgit.engine.model.DatastoreMetaEntry;

@Getter
public class SelectDatastoreMetaByLastmodCommand
    extends AbstractContainerCommand<DatastoreMetaEntry> {

  private final long lastmod;

  @NonNull @Setter private List<String> resultFieldList = Collections.emptyList();

  public SelectDatastoreMetaByLastmodCommand(@NonNull String containerName, long lastmod) {
    super(containerName);
    this.lastmod = lastmod;
  }

  @SuppressWarnings("unchecked")
  public ContainerCommandResult<DatastoreMetaEntry> buildResult(@NonNull List<?> foundEntries) {
    return new MultipleEntriesResult<>(
        getContainerName(),
        getEntryType(),
        (List<DatastoreMetaEntry>) foundEntries,
        getResultFieldList());
  }
}
