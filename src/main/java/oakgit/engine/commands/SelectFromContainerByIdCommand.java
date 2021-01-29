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
 * This class offers a {@link Command} to select an entry by id
 */
@Getter
@ToString
public class SelectFromContainerByIdCommand<T extends ContainerEntry<T>> extends AbstractContainerCommand<T> {

  @NonNull
  private final String id;

  public SelectFromContainerByIdCommand(@NonNull String containerName, @NonNull String id) {
    super(containerName);
    this.id = id;
  }

  @Setter
  private List<String> resultFieldList = Collections.emptyList();


  public CommandResult buildResult(T foundEntry) {
    return new SingleEntryResult<>(getContainerName(), getEntryType(), foundEntry, resultFieldList);
  }

}
