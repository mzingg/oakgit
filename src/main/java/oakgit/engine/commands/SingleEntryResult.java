package oakgit.engine.commands;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import oakgit.engine.ContainerCommandResult;
import oakgit.engine.model.ContainerEntry;
import oakgit.jdbc.OakGitResultSet;

import java.sql.ResultSet;
import java.util.List;

import static oakgit.engine.model.ContainerEntry.isValidAndNotEmpty;

@RequiredArgsConstructor
@Getter
public class SingleEntryResult<T extends ContainerEntry<T>> implements ContainerCommandResult<T> {

  @NonNull
  private final String containerName;

  @NonNull
  private final Class<T> entryType;

  private final T foundEntry;

  @NonNull
  private final List<String> resultFieldList;

  @Override
  public ResultSet toResultSet(@NonNull OakGitResultSet result, @NonNull T emptyType) {
    emptyType.getResultSetTypeModifier(getResultFieldList()).accept(result);
    if (wasSuccessfull()) {
      getFoundEntry().getResultSetModifier(getResultFieldList()).accept(result);
      result.setWasNull(false);
    } else {
      result.setWasNull(true);
    }
    return result;
  }

  @Override
  public boolean wasSuccessfull() {
    return isValidAndNotEmpty(foundEntry);
  }

  @Override
  public int affectedCount() {
    return isValidAndNotEmpty(foundEntry) ? 1 : 0;
  }
}
