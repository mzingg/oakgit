package oakgit.jdbc;

import oakgit.engine.CommandFactory;
import oakgit.engine.CommandProcessor;
import oakgit.engine.model.PlaceholderData;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class OakGitPreparedStatement extends UnsupportedPreparedStatement {

  private final List<PlaceholderData> dataList = new ArrayList<>();
  private PlaceholderData placeholderData = new PlaceholderData();
  private int maxRows;

  protected OakGitPreparedStatement(OakGitConnection connection, String sql) {
    super(connection, sql);
  }

  @Override
  public int executeUpdate() {
    OakGitConnection connection = getConnection();
    CommandProcessor processor = connection.getProcessor();
    CommandFactory factory = connection.getCommandFactory();

    connection.queryLog("P/U>>" + getSql());
    connection.queryLog("P/U>>" + placeholderData);

    return processor.execute(factory.getCommandForSql(getSql(), placeholderData, maxRows)).affectedCount();
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

    connection.queryLog("P/Q>>" + getSql());
    connection.queryLog("P/Q>>" + placeholderData);

    return processor.execute(factory.getCommandForSql(getSql(), placeholderData, maxRows)).toResultSet();
  }

  @Override
  public void addBatch() {
    dataList.add(placeholderData);
    placeholderData = new PlaceholderData();
  }

  public int[] executeBatch() {
    OakGitConnection connection = getConnection();
    CommandProcessor processor = connection.getProcessor();
    CommandFactory factory = connection.getCommandFactory();

    connection.queryLog("P/B>>" + getSql());

    int[] result = new int[dataList.size()];
    for (int i = 0; i < dataList.size(); i++) {

      connection.queryLog("P/B(" + i + ")>>" + dataList.get(i));

      try {
        result[i] = processor.execute(factory.getCommandForSql(getSql(), dataList.get(i), maxRows)).affectedCount();
      } catch (IllegalStateException stateException) {
        result[i] = Statement.EXECUTE_FAILED;
      }
    }
    return result;
  }

  @Override
  public void clearParameters() {
    dataList.clear();
    placeholderData = new PlaceholderData();
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

  @Override
  public void setFetchSize(int rows) {
    this.maxRows = rows;
  }
}
