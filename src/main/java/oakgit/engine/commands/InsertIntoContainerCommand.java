package oakgit.engine.commands;

import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;
import oakgit.engine.Command;
import oakgit.engine.model.ContainerEntry;

/**
 * This class offers a {@link Command} to insert data in a container.
 */
@Getter
@ToString
public class InsertIntoContainerCommand<T extends ContainerEntry<T>> extends AbstractContainerCommand<T> {

  @NonNull
  private final T data;

  public InsertIntoContainerCommand(@NonNull String containerName, @NonNull T data) {
    super(containerName);
    this.data = data;
  }

}
