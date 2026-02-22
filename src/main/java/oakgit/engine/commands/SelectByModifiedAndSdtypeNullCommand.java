package oakgit.engine.commands;

import java.util.Collections;
import java.util.List;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import oakgit.engine.ContainerCommandResult;
import oakgit.engine.model.DocumentEntry;

@Getter
public class SelectByModifiedAndSdtypeNullCommand extends AbstractContainerCommand<DocumentEntry> {

  private final long minModified;

  @NonNull @Setter private List<String> resultFieldList = Collections.emptyList();

  public SelectByModifiedAndSdtypeNullCommand(@NonNull String containerName, long minModified) {
    super(containerName);
    this.minModified = minModified;
  }

  @SuppressWarnings("unchecked")
  public ContainerCommandResult<DocumentEntry> buildResult(@NonNull List<?> foundEntries) {
    return new MultipleEntriesResult<>(
        getContainerName(),
        getEntryType(),
        (List<DocumentEntry>) foundEntries,
        getResultFieldList());
  }
}
