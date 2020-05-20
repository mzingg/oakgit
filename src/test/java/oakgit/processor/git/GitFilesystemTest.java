package oakgit.processor.git;

import oakgit.UnitTest;
import io.vavr.Tuple3;
import oakgit.TestHelpers;
import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.Status;
import org.eclipse.jgit.lib.PersonIdent;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.fail;

class GitFilesystemTest {

    @UnitTest
    void createDirectoryWithNullPathArgumentThrowsException() throws Exception {
        Tuple3<Git, Path, PersonIdent> gitEnv = TestHelpers.aCleanGitEnvironment("git-filesystem-test");

        try {
            new GitFilesystem(gitEnv._1, gitEnv._3)
                .createDirectory((Path) null);

        } catch (GitFilesystemException expectedException) {
            return;
        }

        fail("Expected GitFilesystemException");
    }

    @UnitTest
    void createDirectoryWithNullStringArgumentThrowsException() throws Exception {
        Tuple3<Git, Path, PersonIdent> gitEnv = TestHelpers.aCleanGitEnvironment("git-filesystem-test");

        try {
            new GitFilesystem(gitEnv._1, gitEnv._3)
                .createDirectory((String) null);

        } catch (GitFilesystemException expectedException) {
            return;
        }

        fail("Expected GitFilesystemException");
    }

    @UnitTest
    void createDirectoryWithParentDirectoryOutsideGitWorkspaceThrowsException() throws Exception {
        Tuple3<Git, Path, PersonIdent> gitEnv = TestHelpers.aCleanGitEnvironment("git-filesystem-test");
        Path testPath = Files.createTempDirectory("aTestPath");

        try {

            new GitFilesystem(gitEnv._1, gitEnv._3)
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
        Tuple3<Git, Path, PersonIdent> gitEnv = TestHelpers.aCleanGitEnvironment("git-filesystem-test");
        Path existingFilePath = gitEnv._2.resolve("anExistingFile");

        try {
            Files.write(existingFilePath, new byte[0]);

            new GitFilesystem(gitEnv._1, gitEnv._3)
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
        Tuple3<Git, Path, PersonIdent> gitEnv = TestHelpers.aCleanGitEnvironment("git-filesystem-test");
        Path existingDirectoryPath = gitEnv._2.resolve("anExistingDirectory");

        try {
            Files.createDirectory(existingDirectoryPath);

            new GitFilesystem(gitEnv._1, gitEnv._3)
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
        Tuple3<Git, Path, PersonIdent> gitEnv = TestHelpers.aCleanGitEnvironment("git-filesystem-test");
        Path parentDirectoryPath = gitEnv._2.resolve("aParentDirectory");
        Path subDirectoryPath = parentDirectoryPath.resolve("aDirectoryName");
        File expectedDirectory = subDirectoryPath.toFile();


        new GitFilesystem(gitEnv._1, gitEnv._3)
            .createDirectory("aParentDirectory");
        new GitFilesystem(gitEnv._1, gitEnv._3)
            .createDirectory("aParentDirectory/aDirectoryName");

        assertThat("directory exists in git workspace path", expectedDirectory.exists() && expectedDirectory.isDirectory());
    }

    @UnitTest
    void createDirectoryWithNullParentDirectoryButRelativePathPartsInDirectoryNameCreatesDirectoryInGitWorkspace() throws Exception {
        Tuple3<Git, Path, PersonIdent> gitEnv = TestHelpers.aCleanGitEnvironment("git-filesystem-test");
        String directoryName = "aParentDirectory/aDirectoryName";
        File expectedDirectory = gitEnv._2.resolve(directoryName).toFile();

        new GitFilesystem(gitEnv._1, gitEnv._3)
            .createDirectory(directoryName);

        assertThat("directory exists in git workspace path", expectedDirectory.exists() && expectedDirectory.isDirectory());
    }

    @UnitTest
    void createDirectoryAddsAndCommitsEmptyGitIgnoreFile() throws Exception {
        Tuple3<Git, Path, PersonIdent> gitEnv = TestHelpers.aCleanGitEnvironment("git-filesystem-test");

        Path actualDirectoryPath = new GitFilesystem(gitEnv._1, gitEnv._3)
            .createDirectory("aDirectoryName");

        File gitIgnoreFile = actualDirectoryPath.resolve(GitFilesystem.GIT_IGNORE_FILENAME).toFile();
        Status status = gitEnv._1.status().call();
        assertThat(".gitignore exists", gitIgnoreFile.exists() && gitIgnoreFile.isFile());
        assertThat("git status has no untracked files", status.getUntracked().isEmpty());
        assertThat("git status is clean", status.isClean());
    }

}
