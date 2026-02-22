package oakgit.engine.commands;

import java.util.Collections;
import java.util.List;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import oakgit.engine.ContainerCommandResult;
import oakgit.engine.model.DocumentEntry;

@Getter
public class SelectBySdtypeCommand extends AbstractContainerCommand<DocumentEntry> {

  @NonNull private final List<Integer> sdTypes;

  private final long sdMaxRevTime;

  private final int minVersion;

  @NonNull @Setter private List<String> resultFieldList = Collections.emptyList();

  public SelectBySdtypeCommand(
      @NonNull String containerName,
      @NonNull List<Integer> sdTypes,
      long sdMaxRevTime,
      int minVersion) {
    super(containerName);
    this.sdTypes = sdTypes;
    this.sdMaxRevTime = sdMaxRevTime;
    this.minVersion = minVersion;
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
