package oakgit.engine.commands;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import oakgit.engine.ContainerCommandResult;
import oakgit.engine.model.ContainerEntry;
import oakgit.jdbc.OakGitResultSet;

import java.sql.ResultSet;
import java.util.List;

@RequiredArgsConstructor
@Getter
public class MultipleEntriesResult<T extends ContainerEntry<T>> implements ContainerCommandResult<T> {

  @NonNull
  private final String containerName;

  @NonNull
  private final Class<T> entryType;

  @NonNull
  private final List<T> foundEntries;

  @NonNull
  private final List<String> resultFieldList;

  @Override
  public ResultSet toResultSet(@NonNull OakGitResultSet result, @NonNull T emptyType) {
    List<String> fieldList = getResultFieldList();
    emptyType.getResultSetTypeModifier(fieldList).accept(result);
    if (wasSuccessfull()) {
      getFoundEntries().stream()
          .filter(ContainerEntry::isValidAndNotEmpty)
          .forEach(e -> e.getResultSetModifier(fieldList).accept(result));
    }

    return result;
  }

  @Override
  public boolean wasSuccessfull() {
    return affectedCount() > 0;
  }

  @Override
  public int affectedCount() {
    return foundEntries.size();
  }

}
