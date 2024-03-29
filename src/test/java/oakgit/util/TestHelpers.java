package oakgit.util;

import lombok.Data;
import oakgit.engine.model.PlaceholderData;
import oakgit.engine.query.QueryAnalyzer;
import oakgit.engine.query.QueryMatchResult;
import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.PersonIdent;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class TestHelpers {

  public static final OutputStream DERBY_DEV_NULL = new OutputStream() {
    public void write(int b) {
    }
  };

  public static Path aCleanTestDirectory(String directoryName) throws IOException {
    Path target = aCleanTestDirectory().resolve(directoryName);
    FileUtils.deleteQuietly(target.toFile());
    return Files.createDirectories(target);
  }

  public static Path aCleanTestDirectory() throws IOException {
    Path target = Paths.get("target", "test-resources");
    return Files.createDirectories(target);
  }

  public static GitEnv aCleanGitEnvironment(String directoryName) throws GitAPIException, IOException {
    Path workspaceDirectory = aCleanTestDirectory(directoryName);
    Git git = Git.init().setDirectory(workspaceDirectory.toFile()).call();
    PersonIdent committer = new PersonIdent("Oak Git", "oak-git@somewhere.com");
    return new GitEnv(git, workspaceDirectory, committer);
  }

  public static PlaceholderData placeholderData(Object... values) {
    PlaceholderData result = new PlaceholderData();

    for (int i = 1; i <= values.length; i++) {
      result.set(i, values[i - 1]);
    }

    return result;
  }

  public static QueryMatchResult testValidQueryMatch(QueryAnalyzer analyzer, String sqlQuery) {
    QueryMatchResult actual = analyzer.matchAndCollect(sqlQuery);

    assertThat(actual, is(not(nullValue())));
    assertThat(actual.isInterested(), is(true));
    assertThat(actual.getOriginQuery(), is(sqlQuery));
    assertThat(actual.getCommandSupplier(), is(not(nullValue())));

    return actual;
  }

  @Data
  public static class GitEnv {
    private final Git git;
    private final Path path;
    private final PersonIdent personIdent;
  }

}
