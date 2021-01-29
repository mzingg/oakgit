package oakgit.processor.git;

import oakgit.UnitTest;
import oakgit.util.TestHelpers;
import oakgit.util.TestHelpers.GitEnv;
import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.api.Status;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.fail;

class GitFilesystemTest {

  @UnitTest
  void createDirectoryWithNullPathArgumentThrowsException() throws Exception {
    GitEnv gitEnv = TestHelpers.aCleanGitEnvironment("git-filesystem-test");

    try {
      new GitFilesystem(gitEnv.getGit(), gitEnv.getPersonIdent())
          .createDirectory((Path) null);

    } catch (GitFilesystemException expectedException) {
      return;
    }

    fail("Expected GitFilesystemException");
  }

  @UnitTest
  void createDirectoryWithNullStringArgumentThrowsException() throws Exception {
    GitEnv gitEnv = TestHelpers.aCleanGitEnvironment("git-filesystem-test");

    try {
      new GitFilesystem(gitEnv.getGit(), gitEnv.getPersonIdent())
          .createDirectory((String) null);

    } catch (GitFilesystemException expectedException) {
      return;
    }

    fail("Expected GitFilesystemException");
  }

  @UnitTest
  void createDirectoryWithParentDirectoryOutsideGitWorkspaceThrowsException() throws Exception {
    GitEnv gitEnv = TestHelpers.aCleanGitEnvironment("git-filesystem-test");
    Path testPath = Files.createTempDirectory("aTestPath");

    try {

      new GitFilesystem(gitEnv.getGit(), gitEnv.getPersonIdent())
          .createDirectory(testPath.resolve("aDirectoryName"));

    } catch (GitFilesystemException expectedException) {
      return;
    } finally {
      // always clean up temp directory
      FileUtils.deleteQuietly(testPath.toFile());
    }

    fail("Expected GitFilesystemException");
  }

  @UnitTest
  void createDirectoryWithPathOfAlreadyExistingFileThrowsException() throws Exception {
    GitEnv gitEnv = TestHelpers.aCleanGitEnvironment("git-filesystem-test");
    Path existingFilePath = gitEnv.getPath().resolve("anExistingFile");

    try {
      Files.write(existingFilePath, new byte[0]);

      new GitFilesystem(gitEnv.getGit(), gitEnv.getPersonIdent())
          .createDirectory(existingFilePath);

    } catch (GitFilesystemException expectedException) {
      return;
    } finally {
      // always clean up temp directory
      FileUtils.deleteQuietly(existingFilePath.toFile());
    }

    fail("Expected GitFilesystemException");
  }

  @UnitTest
  void createDirectoryWithPathOfAlreadyExistingDirectoryThrowsException() throws Exception {
    GitEnv gitEnv = TestHelpers.aCleanGitEnvironment("git-filesystem-test");
    Path existingDirectoryPath = gitEnv.getPath().resolve("anExistingDirectory");

    try {
      Files.createDirectory(existingDirectoryPath);

      new GitFilesystem(gitEnv.getGit(), gitEnv.getPersonIdent())
          .createDirectory(existingDirectoryPath);

    } catch (GitFilesystemException expectedException) {
      return;
    } finally {
      // always clean up temp directory
      FileUtils.deleteQuietly(existingDirectoryPath.toFile());
    }

    fail("Expected GitFilesystemException");
  }

  @UnitTest
  void createDirectoryWithParentDirectoryCreatesDirectoryInGitWorkspace() throws Exception {
    GitEnv gitEnv = TestHelpers.aCleanGitEnvironment("git-filesystem-test");
    Path parentDirectoryPath = gitEnv.getPath().resolve("aParentDirectory");
    Path subDirectoryPath = parentDirectoryPath.resolve("aDirectoryName");
    File expectedDirectory = subDirectoryPath.toFile();


    new GitFilesystem(gitEnv.getGit(), gitEnv.getPersonIdent())
        .createDirectory("aParentDirectory");
    new GitFilesystem(gitEnv.getGit(), gitEnv.getPersonIdent())
        .createDirectory("aParentDirectory/aDirectoryName");

    assertThat("directory exists in git workspace path", expectedDirectory.exists() && expectedDirectory.isDirectory());
  }

  @UnitTest
  void createDirectoryWithNullParentDirectoryButRelativePathPartsInDirectoryNameCreatesDirectoryInGitWorkspace() throws Exception {
    GitEnv gitEnv = TestHelpers.aCleanGitEnvironment("git-filesystem-test");
    String directoryName = "aParentDirectory/aDirectoryName";
    File expectedDirectory = gitEnv.getPath().resolve(directoryName).toFile();

    new GitFilesystem(gitEnv.getGit(), gitEnv.getPersonIdent())
        .createDirectory(directoryName);

    assertThat("directory exists in git workspace path", expectedDirectory.exists() && expectedDirectory.isDirectory());
  }

  @UnitTest
  void createDirectoryAddsAndCommitsEmptyGitIgnoreFile() throws Exception {
    GitEnv gitEnv = TestHelpers.aCleanGitEnvironment("git-filesystem-test");

    Path actualDirectoryPath = new GitFilesystem(gitEnv.getGit(), gitEnv.getPersonIdent())
        .createDirectory("aDirectoryName");

    File gitIgnoreFile = actualDirectoryPath.resolve(GitFilesystem.GIT_IGNORE_FILENAME).toFile();
    Status status = gitEnv.getGit().status().call();
    assertThat(".gitignore exists", gitIgnoreFile.exists() && gitIgnoreFile.isFile());
    assertThat("git status has no untracked files", status.getUntracked().isEmpty());
    assertThat("git status is clean", status.isClean());
  }

}
