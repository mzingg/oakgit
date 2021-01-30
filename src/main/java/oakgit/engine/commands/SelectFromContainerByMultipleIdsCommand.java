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
 * This class offers a {@link Command} to select an entry by a list of ids
 */
@Getter
public class SelectFromContainerByMultipleIdsCommand<T extends ContainerEntry<T>> extends AbstractContainerCommand<T> {

  @NonNull
  private final List<String> ids;

  @NonNull
  @Setter
  private List<String> resultFieldList = Collections.emptyList();

  public SelectFromContainerByMultipleIdsCommand(@NonNull String containerName, @NonNull List<String> ids) {
    super(containerName);
    this.ids = ids;
  }

  @SuppressWarnings("unchecked")
  public ContainerCommandResult<T> buildResult(@NonNull List<?> foundEntries) {
    return new MultipleEntriesResult<T>(getContainerName(), getEntryType(), (List<T>) foundEntries, getResultFieldList());
  }

}
