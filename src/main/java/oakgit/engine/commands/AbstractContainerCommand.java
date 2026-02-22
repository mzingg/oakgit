package oakgit.engine.commands;

import lombok.Getter;
import lombok.NonNull;
import oakgit.engine.ContainerCommand;
import oakgit.engine.model.ContainerEntry;
import oakgit.engine.model.ContainerTypes;

@Getter
public abstract class AbstractContainerCommand<T extends ContainerEntry<T>>
    implements ContainerCommand<T> {

  @NonNull private final String containerName;

  @NonNull private final Class<T> entryType;

  @SuppressWarnings("unchecked")
  public AbstractContainerCommand(@NonNull String containerName) {
    this.containerName = containerName;
    try {
      this.entryType = (Class<T>) ContainerTypes.fromName(containerName).getEntryType();
    } catch (IllegalArgumentException e) {
      throw new IllegalArgumentException("unknown container");
    }
  }
}
