package oakgit.engine;

import java.sql.ResultSet;
import oakgit.jdbc.OakGitResultSet;

public interface CommandResult {

  CommandResult NO_RESULT =
      new CommandResult() {
        @Override
        public ResultSet toResultSet() {
          throw new UnsupportedOperationException(
              "This result can not be expressed as a ResultSet");
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

  CommandResult SUCCESSFULL_RESULT_WITHOUT_DATA =
      new CommandResult() {
        @Override
        public ResultSet toResultSet() {
          throw new UnsupportedOperationException(
              "This result can not be expressed as a ResultSet");
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

  CommandResult EMPTY_QUERY_RESULT =
      new CommandResult() {
        @Override
        public ResultSet toResultSet() {
          return OakGitResultSet.EMPTY_RESULT_SET;
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

  ResultSet toResultSet();

  boolean wasSuccessfull();

  int affectedCount();
}
