package oakgit.engine.commands;

import java.sql.ResultSet;
import java.util.List;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import oakgit.engine.ContainerCommandResult;
import oakgit.engine.model.ContainerEntry;
import oakgit.jdbc.OakGitResultSet;

@RequiredArgsConstructor
@Getter
public class MultipleEntriesResult<T extends ContainerEntry<T>>
    implements ContainerCommandResult<T> {

  @NonNull private final String containerName;

  @NonNull private final Class<T> entryType;

  @NonNull private final List<T> foundEntries;

  @NonNull private final List<String> resultFieldList;

  @Override
  public ResultSet toResultSet(@NonNull OakGitResultSet result, @NonNull T emptyType) {
    List<String> fieldList = getResultFieldList();
    emptyType.getResultSetTypeModifier(fieldList).accept(result);
    if (wasSuccessfull()) {
      List<String> fields = emptyType.expandOrReturnFieldList(fieldList);
      for (T entry : getFoundEntries()) {
        if (ContainerEntry.isValidAndNotEmpty(entry)) {
          for (String fieldName : fields) {
            ContainerEntry.ColumnGetterResult getter =
                entry
                    .entryGetter(fieldName)
                    .orElseThrow(
                        () ->
                            new IllegalStateException(
                                "could not assign entry to fieldName: " + fieldName));
            result.addValue(getter.getFieldName(), getter.getValue());
          }
        }
      }
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
