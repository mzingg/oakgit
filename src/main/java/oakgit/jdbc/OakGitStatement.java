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
    CommandProcessor processor = connection.getProcessor();
    CommandFactory factory = connection.getCommandFactory();

    this.resultSet = processor.execute(factory.getCommandForSql(sql)).toResultSet();
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
