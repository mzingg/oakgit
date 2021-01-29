package oakgit.engine.commands;

import lombok.NonNull;
import lombok.ToString;
import oakgit.engine.Command;
import oakgit.engine.model.ContainerEntry;

/**
 * This class offers a {@link Command} to create a new container
 */
@ToString
public class CreateContainerCommand<T extends ContainerEntry<T>> extends AbstractContainerCommand<T> {

  public CreateContainerCommand(@NonNull String containerName) {
    super(containerName);
  }

}
