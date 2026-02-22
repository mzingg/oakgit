package oakgit.jdbc;

import java.sql.*;
import java.util.Optional;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;
import oakgit.engine.CommandProcessor;
import oakgit.engine.store.FileEntryStore;

public class OakGitDriver implements Driver {

  private static final String DEFAULT_VERSION = "0.1.0";
  private static final String ARTIFACT_ID = "oakgit-persistence";
  private static final ConcurrentHashMap<String, CommandProcessor> PROCESSORS =
      new ConcurrentHashMap<>();
  private static final DriverVersion VERSION =
      DriverVersion.parse(
          Optional.ofNullable(System.getenv("OAKGIT_VERSION")).orElse(DEFAULT_VERSION));

  static {
    try {
      DriverManager.registerDriver(new OakGitDriver());
    } catch (SQLException e) {
      throw new RuntimeException("Can't register oakgit driver!");
    }
  }

  @Override
  public Connection connect(String url, Properties info) throws SQLException {
    OakGitDriverConfiguration configuration =
        OakGitDriverConfiguration.fromUrl(url, VERSION, ARTIFACT_ID);
    if (configuration != OakGitDriverConfiguration.INVALID_CONFIGURATION) {
      CommandProcessor processor =
          PROCESSORS.computeIfAbsent(
              configuration.getUrl(),
              k -> new CommandProcessor(new FileEntryStore(configuration.getGitDirectory())));
      return new OakGitConnection(configuration, processor, processor.getCommandFactory());
    }

    throw new SQLException("Invalid connection url");
  }

  /**
   * Resets the processor for the given URL, discarding all in-memory state. Subsequent connections
   * to the same URL will use a fresh processor.
   */
  public static void resetProcessor(String url) {
    var processor = PROCESSORS.remove(url);
    if (processor != null) {
      try {
        processor.discardAndClose();
      } catch (Exception e) {
        // best-effort cleanup
      }
    }
  }

  /** Closes all processors, flushing pending writes. Called during bundle shutdown. */
  static void closeAll() {
    PROCESSORS.forEach(
        (url, processor) -> {
          try {
            processor.close();
          } catch (Exception e) {
            // best-effort cleanup
          }
        });
    PROCESSORS.clear();
  }

  @Override
  public boolean acceptsURL(String url) {
    return OakGitDriverConfiguration.fromUrl(url, VERSION, ARTIFACT_ID)
        != OakGitDriverConfiguration.INVALID_CONFIGURATION;
  }

  @Override
  public DriverPropertyInfo[] getPropertyInfo(String url, Properties info) {
    return new DriverPropertyInfo[0];
  }

  @Override
  public int getMajorVersion() {
    return VERSION.major();
  }

  @Override
  public int getMinorVersion() {
    return VERSION.minor();
  }

  @Override
  public boolean jdbcCompliant() {
    return false;
  }

  @Override
  public Logger getParentLogger() throws SQLFeatureNotSupportedException {
    throw new SQLFeatureNotSupportedException();
  }
}
