package oakgit.jdbc;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import oakgit.engine.CommandFactory;
import oakgit.engine.CommandProcessor;

import java.sql.ResultSet;
import java.sql.SQLException;

@Getter
@RequiredArgsConstructor
public class OakGitStatement extends UnsupportedOakGitStatement {

  private final OakGitConnection connection;

  private ResultSet resultSet;

  @Override
  public ResultSet executeQuery(String sql) throws SQLException {
    OakGitConnection connection = getConnection();
    connection.queryLog("S>>" + sql);
    CommandProcessor processor = connection.getProcessor();
    CommandFactory factory = connection.getCommandFactory();

    try {
      this.resultSet = processor.execute(factory.getCommandForSql(sql)).toResultSet();
    } catch (IllegalStateException stateException) {
      throw new SQLException(stateException);
    }
    return resultSet;
  }

  @Override
  public boolean execute(String sql) {
    OakGitConnection connection = getConnection();
    CommandProcessor processor = connection.getProcessor();
    CommandFactory factory = connection.getCommandFactory();

    return processor.execute(factory.getCommandForSql(sql)).wasSuccessfull();
  }

  @Override
  public boolean isClosed() throws SQLException {
    return false;
  }

  @Override
  public void close() {
  }

  @Override
  public boolean isPoolable() {
    return false;
  }

  @Override
  public void setPoolable(boolean poolable) {

  }
}
