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
 * This class offers a {@link Command} to select an entry by id
 */
@Getter
public class SelectFromContainerByIdCommand<T extends ContainerEntry<T>> extends AbstractContainerCommand<T> {

  @NonNull
  private final String id;

  public SelectFromContainerByIdCommand(@NonNull String containerName, @NonNull String id) {
    super(containerName);
    this.id = id;
  }

  @Setter
  private List<String> resultFieldList = Collections.emptyList();

  @SuppressWarnings("unchecked")
  public ContainerCommandResult<T> buildResult(ContainerEntry<?> foundEntry) {
    if (foundEntry != null && !getEntryType().isAssignableFrom(foundEntry.getClass())) {
      throw new IllegalArgumentException("foundEntry must be of type " + getEntryType());
    }
    return new SingleEntryResult<T>(getContainerName(), getEntryType(), (T) foundEntry, getResultFieldList());
  }

}
