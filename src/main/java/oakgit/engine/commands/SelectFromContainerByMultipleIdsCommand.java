package oakgit.engine.commands;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.ToString;
import oakgit.engine.Command;
import oakgit.engine.CommandResult;
import oakgit.engine.model.ContainerEntry;

import java.util.Collections;
import java.util.List;

/**
 * This class offers a {@link Command} to select an entry by a list of ids
 */
@Getter
@ToString
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

  public CommandResult buildResult(@NonNull List<T> foundEntries) {
    return new MultipleEntriesResult<>(getContainerName(), getEntryType(), foundEntries, resultFieldList);
  }

}
