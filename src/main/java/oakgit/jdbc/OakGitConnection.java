package oakgit.jdbc;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import oakgit.engine.CommandFactory;
import oakgit.engine.CommandProcessor;
import org.eclipse.jgit.api.Git;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.Statement;

@RequiredArgsConstructor
@Getter
public class OakGitConnection extends DefaultOakGitConnection {

  @NonNull
  private final OakGitDriverConfiguration configuration;

  @NonNull
  private final CommandProcessor processor;

  @NonNull
  private final CommandFactory commandFactory;

  private Git git;

  public Git getGit() throws IOException {
    if (git == null) {
      git = Git.open(getConfiguration().getGitDirectory().toFile());
    }
    return git;
  }

  public void queryLog(String sql) {
    try {
      Files.writeString(
          getConfiguration().getGitDirectory().resolve("query.log"),
          sql + "\r\n",
          StandardCharsets.UTF_8,
          StandardOpenOption.CREATE, StandardOpenOption.APPEND
      );
    } catch (IOException ioException) {
      throw new IllegalStateException(ioException);
    }
  }

  @Override
  public DatabaseMetaData getMetaData() {
    return new OakGitDatabaseMetadata(configuration.getUrl(), configuration.getArtifactId(), configuration.getVersion());
  }

  @Override
  public Statement createStatement() {
    return new OakGitStatement(this);
  }

  @Override
  public PreparedStatement prepareStatement(String sql) {
    return new OakGitPreparedStatement(this, sql);
  }

  @Override
  public String getCatalog() {
    return configuration.getDirectoryName();
  }

  @Override
  public void commit() {

  }

  @Override
  public void rollback() {

  }

}
