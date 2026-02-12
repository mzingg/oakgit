package oakgit.engine.commands;

import lombok.Getter;
import lombok.NonNull;
import oakgit.engine.model.ContainerEntry;

@Getter
public class DeleteByIdCommand<T extends ContainerEntry<T>> extends AbstractContainerCommand<T> {

  @NonNull private final String id;

  public DeleteByIdCommand(@NonNull String containerName, @NonNull String id) {
    super(containerName);
    this.id = id;
  }
}
