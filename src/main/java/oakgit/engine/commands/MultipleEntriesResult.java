package oakgit.engine.commands;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import oakgit.engine.CommandResult;
import oakgit.engine.model.ContainerEntry;
import oakgit.jdbc.OakGitResultSet;

import java.sql.ResultSet;
import java.util.List;

@RequiredArgsConstructor
@Getter
@ToString
public class MultipleEntriesResult<T extends ContainerEntry<T>> implements CommandResult {

  @NonNull
  private final String containerName;

  @NonNull
  private final Class<T> entryType;

  @NonNull
  private final List<T> foundEntries;

  @NonNull
  private final List<String> resultFieldList;

  @Override
  public ResultSet toResultSet() {
    OakGitResultSet result = new OakGitResultSet(containerName);
    ContainerEntry.emptyOf(entryType)
        .getResultSetTypeModifier(resultFieldList).accept(result);
    if (wasSuccessfull()) {
      foundEntries.stream()
          .filter(ContainerEntry::isValidAndNotEmpty)
          .forEach(e -> e.getResultSetModifier(resultFieldList).accept(result));
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
