package oakgit.engine.commands;

import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;
import oakgit.engine.Command;
import oakgit.engine.CommandResult;
import oakgit.engine.model.ContainerEntry;

import java.util.Collections;

/**
 * This class offers a {@link Command} to update an entry
 */
@Getter
@ToString
public class UpdatDataInContainerCommand<T extends ContainerEntry<T>> extends AbstractContainerCommand<T> {

  @NonNull
  private final String id;

  private final long modCount;

  @NonNull
  private final UpdateSet data;

  public UpdatDataInContainerCommand(@NonNull String containerName, @NonNull String id, long modCount, @NonNull UpdateSet data) {
    super(containerName);
    this.id = id;
    this.modCount = modCount;
    this.data = data;
  }

  public CommandResult buildResult(T updatedEntry) {
    return new SingleEntryResult<>(getContainerName(), getEntryType(), updatedEntry, Collections.emptyList());
  }

}
