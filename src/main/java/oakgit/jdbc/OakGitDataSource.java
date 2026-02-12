package oakgit.jdbc;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.Properties;
import java.util.logging.Logger;
import javax.sql.DataSource;

/**
 * Minimal DataSource wrapper around OakGitDriver. Bypasses DriverManager to avoid OSGi classloader
 * isolation issues with DBCP connection pools.
 */
public class OakGitDataSource implements DataSource {

  private final OakGitDriver driver = new OakGitDriver();
  private final String url;

  public OakGitDataSource(String url) {
    this.url = url;
  }

  @Override
  public Connection getConnection() throws SQLException {
    return driver.connect(url, new Properties());
  }

  @Override
  public Connection getConnection(String username, String password) throws SQLException {
    var props = new Properties();
    props.setProperty("user", username);
    props.setProperty("password", password);
    return driver.connect(url, props);
  }

  @Override
  public PrintWriter getLogWriter() {
    return null;
  }

  @Override
  public void setLogWriter(PrintWriter out) {}

  @Override
  public void setLoginTimeout(int seconds) {}

  @Override
  public int getLoginTimeout() {
    return 0;
  }

  @Override
  public Logger getParentLogger() throws SQLFeatureNotSupportedException {
    throw new SQLFeatureNotSupportedException();
  }

  @Override
  public <T> T unwrap(Class<T> iface) throws SQLException {
    if (iface.isAssignableFrom(getClass())) {
      return iface.cast(this);
    }
    throw new SQLException("Cannot unwrap to " + iface.getName());
  }

  @Override
  public boolean isWrapperFor(Class<?> iface) {
    return iface.isAssignableFrom(getClass());
  }
}
