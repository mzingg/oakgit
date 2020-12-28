package oakgit.jdbc;

import oakgit.engine.CommandFactory;
import oakgit.engine.CommandProcessor;
import oakgit.engine.model.PlaceholderData;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class OakGitPreparedStatement extends UnsupportedPreparedStatement {

  private final List<String> commandList = new ArrayList<>();
  private final PlaceholderData placeholderData = new PlaceholderData();

  protected OakGitPreparedStatement(OakGitConnection connection, String sql) {
    super(connection, sql);
  }

  @Override
  public int executeUpdate() {
    OakGitConnection connection = getConnection();
    CommandProcessor processor = connection.getProcessor();
    CommandFactory factory = connection.getCommandFactory();

    return processor.execute(factory.getCommandForSql(getSql(), placeholderData)).affectedCount();
  }

  @Override
  public void setLong(int parameterIndex, long x) {
    placeholderData.set(parameterIndex, x);
  }

  @Override
  public ResultSet executeQuery() {
    OakGitConnection connection = getConnection();
    CommandProcessor processor = connection.getProcessor();
    CommandFactory factory = connection.getCommandFactory();

    return processor.execute(factory.getCommandForSql(getSql(), placeholderData)).toResultSet();
  }

  @Override
  public void addBatch() {
    commandList.add(getSql());
  }

  public int[] executeBatch() {
    int[] result = new int[commandList.size()];
    for (int i = 0; i < commandList.size(); i++) {
      OakGitConnection connection = getConnection();
      CommandProcessor processor = connection.getProcessor();
      CommandFactory factory = connection.getCommandFactory();

      result[i] = processor.execute(factory.getCommandForSql(commandList.get(i), placeholderData)).affectedCount();
    }
    return result;
  }

  @Override
  public void setString(int parameterIndex, String x) {
    placeholderData.set(parameterIndex, x);
  }

  @Override
  public void setBytes(int parameterIndex, byte[] x) {
    placeholderData.set(parameterIndex, x);
  }

  @Override
  public void setObject(int parameterIndex, Object x) {
    placeholderData.set(parameterIndex, x);
  }

  @Override
  public void setObject(int parameterIndex, Object x, int targetSqlType) {
    placeholderData.set(parameterIndex, x);
  }

  @Override
  public void setBinaryStream(int parameterIndex, InputStream stream, int length) {
    if (stream != null) {
      try {
        byte[] bytes;
        if (length > 0) {
          bytes = IOUtils.toByteArray(stream, length);
        } else {
          bytes = IOUtils.toByteArray(stream);
        }
        placeholderData.set(parameterIndex, bytes);
      } catch (IOException e) {
        throw new IllegalArgumentException(e);
      }
    } else {
      placeholderData.set(parameterIndex, null);
    }
  }

}
