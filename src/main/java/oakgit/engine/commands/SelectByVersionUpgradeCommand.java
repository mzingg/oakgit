package oakgit.engine.commands;

import java.util.Collections;
import java.util.List;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import oakgit.engine.ContainerCommandResult;
import oakgit.engine.model.DocumentEntry;

@Getter
public class SelectByVersionUpgradeCommand extends AbstractContainerCommand<DocumentEntry> {

  @NonNull private final List<String> excludedIdPatterns;

  private final int maxVersion;

  @NonNull @Setter private List<String> resultFieldList = Collections.emptyList();

  public SelectByVersionUpgradeCommand(
      @NonNull String containerName, @NonNull List<String> excludedIdPatterns, int maxVersion) {
    super(containerName);
    this.excludedIdPatterns = excludedIdPatterns;
    this.maxVersion = maxVersion;
  }

  @SuppressWarnings("unchecked")
  public ContainerCommandResult<DocumentEntry> buildResult(@NonNull List<?> foundEntries) {
    return new MultipleEntriesResult<>(
        getContainerName(),
        getEntryType(),
        (List<DocumentEntry>) foundEntries,
        getResultFieldList());
  }
}
