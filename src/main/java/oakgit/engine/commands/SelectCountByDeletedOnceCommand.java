package oakgit.engine.commands;

import java.sql.ResultSet;
import java.sql.Types;
import java.util.ArrayList;
import lombok.Getter;
import lombok.NonNull;
import oakgit.engine.Command;
import oakgit.engine.CommandResult;
import oakgit.jdbc.OakGitResultSet;

@Getter
public class SelectCountByDeletedOnceCommand implements Command {

  @NonNull private final String tableName;
  private final int deletedOnce;

  public SelectCountByDeletedOnceCommand(@NonNull String tableName, int deletedOnce) {
    this.tableName = tableName;
    this.deletedOnce = deletedOnce;
  }

  public CommandResult buildResult(long count) {
    return new CommandResult() {
      @Override
      public ResultSet toResultSet() {
        var rs = new OakGitResultSet(tableName);
        rs.addColumn(new OakGitResultSet.Column("COUNT(*)", Types.BIGINT, 19, new ArrayList<>()));
        rs.addValue("COUNT(*)", count);
        return rs;
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
  }
}
