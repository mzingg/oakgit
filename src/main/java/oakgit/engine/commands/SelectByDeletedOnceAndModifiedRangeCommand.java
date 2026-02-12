package oakgit.engine.commands;

import java.util.Collections;
import java.util.List;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import oakgit.engine.ContainerCommandResult;
import oakgit.engine.model.DocumentEntry;

@Getter
public class SelectByDeletedOnceAndModifiedRangeCommand
    extends AbstractContainerCommand<DocumentEntry> {

  private final int deletedOnce;

  private final long modifiedLowerBound;

  private final long modifiedUpperBound;

  @NonNull @Setter private List<String> resultFieldList = Collections.emptyList();

  public SelectByDeletedOnceAndModifiedRangeCommand(
      @NonNull String containerName,
      int deletedOnce,
      long modifiedUpperBound,
      long modifiedLowerBound) {
    super(containerName);
    this.deletedOnce = deletedOnce;
    this.modifiedUpperBound = modifiedUpperBound;
    this.modifiedLowerBound = modifiedLowerBound;
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
