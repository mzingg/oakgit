package oakgit.engine.commands;

import java.util.List;
import lombok.Getter;
import lombok.NonNull;
import oakgit.engine.model.ContainerEntry;

@Getter
public class DeleteByIdListCommand<T extends ContainerEntry<T>>
    extends AbstractContainerCommand<T> {

  @NonNull private final List<String> ids;

  public DeleteByIdListCommand(@NonNull String containerName, @NonNull List<String> ids) {
    super(containerName);
    this.ids = ids;
  }
}
