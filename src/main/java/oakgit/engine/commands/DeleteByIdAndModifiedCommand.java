package oakgit.engine.commands;

import lombok.Getter;
import lombok.NonNull;
import oakgit.engine.model.ContainerEntry;

@Getter
public class DeleteByIdAndModifiedCommand<T extends ContainerEntry<T>>
    extends AbstractContainerCommand<T> {

  @NonNull private final String id;

  private final long modified;

  public DeleteByIdAndModifiedCommand(
      @NonNull String containerName, @NonNull String id, long modified) {
    super(containerName);
    this.id = id;
    this.modified = modified;
  }
}
