package oakgit.engine.commands;

import lombok.Getter;
import lombok.NonNull;
import oakgit.engine.Command;
import oakgit.engine.ContainerCommandResult;
import oakgit.engine.model.DocumentEntry;
import oakgit.engine.model.DocumentEntryUpdateSet;

import java.util.Collections;

/**
 * This class offers a {@link Command} to update an entry
 */
@Getter
public class UpdateDocumentDataInContainerCommand extends AbstractContainerCommand<DocumentEntry> {

  @NonNull
  private final String id;

  private final long modCount;

  @NonNull
  private final DocumentEntryUpdateSet data;

  public UpdateDocumentDataInContainerCommand(@NonNull String containerName, @NonNull String id, long modCount, @NonNull DocumentEntryUpdateSet data) {
    super(containerName);
    this.id = id;
    this.modCount = modCount;
    this.data = data;
  }

  public ContainerCommandResult<DocumentEntry> buildResult(DocumentEntry updatedEntry) {
    return new SingleEntryResult<>(getContainerName(), getEntryType(), updatedEntry, Collections.emptyList());
  }

}
