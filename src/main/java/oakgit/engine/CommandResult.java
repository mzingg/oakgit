package oakgit.engine;

import lombok.NonNull;
import oakgit.engine.model.ContainerEntry;
import oakgit.jdbc.OakGitResultSet;

import java.sql.ResultSet;

public interface CommandResult {

  CommandResult NO_RESULT = new CommandResult() {
    @Override
    public ResultSet toResultSet() {
      return null;
    }

    @Override
    public boolean wasSuccessfull() {
      return false;
    }

    @Override
    public int affectedCount() {
      return 0;
    }
  };

  CommandResult SUCCESSFULL_RESULT_WITHOUT_DATA = new CommandResult() {
    @Override
    public ResultSet toResultSet() {
      throw new IllegalArgumentException("This result can not be expressed as a ResultSet");
    }

    @Override
    public boolean wasSuccessfull() {
      return true;
    }

    @Override
    public int affectedCount() {
      return 1;
    }
  };

  static <T extends ContainerEntry<T>> CommandResult emptyResult(@NonNull String tableName, Class<T> entryType) {
    return new CommandResult() {
      @Override
      public ResultSet toResultSet() {
        OakGitResultSet resultSet = new OakGitResultSet(tableName);

        ContainerEntry.emptyOf(entryType)
            .getResultSetTypeModifier()
            .accept(resultSet);

        return resultSet;
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

  ResultSet toResultSet();

  boolean wasSuccessfull();

  int affectedCount();
}
