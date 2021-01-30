package oakgit.engine.commands;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import oakgit.engine.Command;
import oakgit.engine.ContainerCommandResult;
import oakgit.engine.model.ContainerEntry;

import java.util.Collections;
import java.util.List;

/**
 * This class offers a {@link Command} to select an entry by an id range
 */
@Getter
public class SelectFromContainerByIdRangeCommand<T extends ContainerEntry<T>> extends AbstractContainerCommand<T> {

  @NonNull
  private final String idMin;

  @NonNull
  private final String idMax;

  private final int limit;

  @NonNull
  @Setter
  private List<String> resultFieldList = Collections.emptyList();

  public SelectFromContainerByIdRangeCommand(@NonNull String containerName, @NonNull String idMin, @NonNull String idMax, int limit) {
    super(containerName);
    this.idMin = idMin;
    this.idMax = idMax;
    this.limit = limit;
  }

  public ContainerCommandResult buildResult(@NonNull List<T> foundEntries) {
    return new MultipleEntriesResult<>(getContainerName(), getEntryType(), foundEntries, resultFieldList);
  }

}
