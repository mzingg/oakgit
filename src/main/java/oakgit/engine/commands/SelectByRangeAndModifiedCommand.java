package oakgit.engine.commands;

import java.util.Collections;
import java.util.List;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import oakgit.engine.ContainerCommandResult;
import oakgit.engine.model.ContainerEntry;

@Getter
public class SelectByRangeAndModifiedCommand<T extends ContainerEntry<T>>
    extends AbstractContainerCommand<T> {

  @NonNull private final String idMin;

  @NonNull private final String idMax;

  private final long minModified;

  private final int limit;

  @NonNull @Setter private List<String> resultFieldList = Collections.emptyList();

  public SelectByRangeAndModifiedCommand(
      @NonNull String containerName,
      @NonNull String idMin,
      @NonNull String idMax,
      long minModified,
      int limit) {
    super(containerName);
    this.idMin = idMin;
    this.idMax = idMax;
    this.minModified = minModified;
    this.limit = limit;
  }

  @SuppressWarnings("unchecked")
  public ContainerCommandResult<T> buildResult(@NonNull List<?> foundEntries) {
    return new MultipleEntriesResult<>(
        getContainerName(), getEntryType(), (List<T>) foundEntries, getResultFieldList());
  }
}
