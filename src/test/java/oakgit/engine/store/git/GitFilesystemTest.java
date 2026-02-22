package oakgit.engine.store.git;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import oakgit.UnitTest;
import oakgit.util.TestHelpers;
import oakgit.util.TestHelpers.GitEnv;
import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.api.Status;

class GitFilesystemTest {

  @UnitTest
  void createDirectoryWithNullPathArgumentThrowsException() throws Exception {
    GitEnv gitEnv = TestHelpers.aCleanGitEnvironment("git-filesystem-test");

    assertThatThrownBy(
            () ->
                new GitFilesystem(gitEnv.getGit(), gitEnv.getPersonIdent())
                    .createDirectory((Path) null))
        .isInstanceOf(GitFilesystemException.class);
  }

  @UnitTest
  void createDirectoryWithNullStringArgumentThrowsException() throws Exception {
    GitEnv gitEnv = TestHelpers.aCleanGitEnvironment("git-filesystem-test");

    assertThatThrownBy(
            () ->
                new GitFilesystem(gitEnv.getGit(), gitEnv.getPersonIdent())
                    .createDirectory((String) null))
        .isInstanceOf(GitFilesystemException.class);
  }

  @UnitTest
  void createDirectoryWithParentDirectoryOutsideGitWorkspaceThrowsException() throws Exception {
    GitEnv gitEnv = TestHelpers.aCleanGitEnvironment("git-filesystem-test");
    Path testPath = Files.createTempDirectory("aTestPath");

    try {
      assertThatThrownBy(
              () ->
                  new GitFilesystem(gitEnv.getGit(), gitEnv.getPersonIdent())
                      .createDirectory(testPath.resolve("aDirectoryName")))
          .isInstanceOf(GitFilesystemException.class);
    } finally {
      FileUtils.deleteQuietly(testPath.toFile());
    }
  }

  @UnitTest
  void createDirectoryWithPathOfAlreadyExistingFileThrowsException() throws Exception {
    GitEnv gitEnv = TestHelpers.aCleanGitEnvironment("git-filesystem-test");
    Path existingFilePath = gitEnv.getPath().resolve("anExistingFile");

    try {
      Files.write(existingFilePath, new byte[0]);

      assertThatThrownBy(
              () ->
                  new GitFilesystem(gitEnv.getGit(), gitEnv.getPersonIdent())
                      .createDirectory(existingFilePath))
          .isInstanceOf(GitFilesystemException.class);
    } finally {
      FileUtils.deleteQuietly(existingFilePath.toFile());
    }
  }

  @UnitTest
  void createDirectoryWithPathOfAlreadyExistingDirectoryThrowsException() throws Exception {
    GitEnv gitEnv = TestHelpers.aCleanGitEnvironment("git-filesystem-test");
    Path existingDirectoryPath = gitEnv.getPath().resolve("anExistingDirectory");

    try {
      Files.createDirectory(existingDirectoryPath);

      assertThatThrownBy(
              () ->
                  new GitFilesystem(gitEnv.getGit(), gitEnv.getPersonIdent())
                      .createDirectory(existingDirectoryPath))
          .isInstanceOf(GitFilesystemException.class);
    } finally {
      FileUtils.deleteQuietly(existingDirectoryPath.toFile());
    }
  }

  @UnitTest
  void createDirectoryWithParentDirectoryCreatesDirectoryInGitWorkspace() throws Exception {
    GitEnv gitEnv = TestHelpers.aCleanGitEnvironment("git-filesystem-test");
    Path parentDirectoryPath = gitEnv.getPath().resolve("aParentDirectory");
    Path subDirectoryPath = parentDirectoryPath.resolve("aDirectoryName");
    File expectedDirectory = subDirectoryPath.toFile();

    new GitFilesystem(gitEnv.getGit(), gitEnv.getPersonIdent()).createDirectory("aParentDirectory");
    new GitFilesystem(gitEnv.getGit(), gitEnv.getPersonIdent())
        .createDirectory("aParentDirectory/aDirectoryName");

    assertThat(expectedDirectory).exists().isDirectory();
  }

  @UnitTest
  void
      createDirectoryWithNullParentDirectoryButRelativePathPartsInDirectoryNameCreatesDirectoryInGitWorkspace()
          throws Exception {
    GitEnv gitEnv = TestHelpers.aCleanGitEnvironment("git-filesystem-test");
    String directoryName = "aParentDirectory/aDirectoryName";
    File expectedDirectory = gitEnv.getPath().resolve(directoryName).toFile();

    new GitFilesystem(gitEnv.getGit(), gitEnv.getPersonIdent()).createDirectory(directoryName);

    assertThat(expectedDirectory).exists().isDirectory();
  }

  @UnitTest
  void createDirectoryAddsAndCommitsEmptyGitIgnoreFile() throws Exception {
    GitEnv gitEnv = TestHelpers.aCleanGitEnvironment("git-filesystem-test");

    Path actualDirectoryPath =
        new GitFilesystem(gitEnv.getGit(), gitEnv.getPersonIdent())
            .createDirectory("aDirectoryName");

    File gitIgnoreFile = actualDirectoryPath.resolve(GitFilesystem.GIT_IGNORE_FILENAME).toFile();
    Status status = gitEnv.getGit().status().call();
    assertThat(gitIgnoreFile).exists().isFile();
    assertThat(status.getUntracked()).isEmpty();
    assertThat(status.isClean()).isTrue();
  }
}
