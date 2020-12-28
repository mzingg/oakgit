package oakgit.engine;

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

  static CommandResult emptyResult(String tableName) {
    return new CommandResult() {
      @Override
      public ResultSet toResultSet() {
        return new OakGitResultSet(tableName);
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
