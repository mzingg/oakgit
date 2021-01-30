package oakgit.engine;

import lombok.NonNull;
import oakgit.engine.model.ContainerEntry;
import oakgit.jdbc.OakGitResultSet;

import java.sql.ResultSet;

public interface ContainerCommandResult<T extends ContainerEntry<T>> extends CommandResult {

  static <T extends ContainerEntry<T>> ContainerCommandResult<T> emptyResult(@NonNull String tableName, Class<T> entryType) {
    return new ContainerCommandResult<T>() {
      @Override
      public ResultSet toResultSet(@NonNull OakGitResultSet result, @NonNull T emptyType) {
        emptyType
            .getResultSetTypeModifier()
            .accept(result);

        return result;
      }

      @Override
      public String getContainerName() {
        return tableName;
      }

      @Override
      public Class<T> getEntryType() {
        return entryType;
      }

      @Override
      public boolean wasSuccessfull() {
        return true;
      }

      @Override
      public int affectedCount() {
        return 0;
      }
    };
  }

  default ResultSet toResultSet() {
    return toResultSet(new OakGitResultSet(getContainerName()), ContainerEntry.emptyOf(getEntryType()));
  }

  Class<T> getEntryType();

  String getContainerName();

  ResultSet toResultSet(@NonNull OakGitResultSet result, @NonNull T emptyType);

}
