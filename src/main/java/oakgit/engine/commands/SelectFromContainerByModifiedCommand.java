package oakgit.engine.commands;

import lombok.*;
import oakgit.engine.model.ContainerEntry;

import java.util.Collections;
import java.util.List;

@Getter
public class SelectFromContainerByModifiedCommand<T extends ContainerEntry<T>> extends AbstractContainerCommand<T> {

  @NonNull
  private final Long modified;

  private final int limit;

  @Setter
  private List<String> resultFieldList = Collections.emptyList();

  public SelectFromContainerByModifiedCommand(@NonNull String containerName, @NonNull Long modified, int limit) {
    super(containerName);
    this.modified = modified;
    this.limit = limit;
  }

}
