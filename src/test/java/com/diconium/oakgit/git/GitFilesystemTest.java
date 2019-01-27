package com.diconium.oakgit.git;

import io.vavr.Tuple3;
import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.Status;
import org.eclipse.jgit.lib.PersonIdent;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

import static com.diconium.oakgit.TestHelpers.aCleanGitEnvironment;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.fail;

class GitFilesystemTest {

    @Test
    void createDirectoryWithNullPathArgumentThrowsException() throws Exception {
        Tuple3<Git, Path, PersonIdent> gitEnv = aCleanGitEnvironment("git-filesystem-test");

        try {
            new GitFilesystem(gitEnv._1, gitEnv._3)
                    .createDirectory((Path) null);

        } catch (GitFilesystemException expectedException) {
            return;
        }

        fail("Expected GitFilesystemException");
    }

    @Test
    void createDirectoryWithNullStringArgumentThrowsException() throws Exception {
        Tuple3<Git, Path, PersonIdent> gitEnv = aCleanGitEnvironment("git-filesystem-test");

        try {
            new GitFilesystem(gitEnv._1, gitEnv._3)
                    .createDirectory((String) null);

        } catch (GitFilesystemException expectedException) {
            return;
        }

        fail("Expected GitFilesystemException");
    }

    @Test
    void createDirectoryWithParentDirectoryOutsideGitWorkspaceThrowsException() throws Exception {
        Tuple3<Git, Path, PersonIdent> gitEnv = aCleanGitEnvironment("git-filesystem-test");
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

    @Test
    void createDirectoryWithPathOfAlreadyExistingFileThrowsException() throws Exception {
        Tuple3<Git, Path, PersonIdent> gitEnv = aCleanGitEnvironment("git-filesystem-test");
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

    @Test
    void createDirectoryWithPathOfAlreadyExistingDirectoryThrowsException() throws Exception {
        Tuple3<Git, Path, PersonIdent> gitEnv = aCleanGitEnvironment("git-filesystem-test");
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

    @Test
    void createDirectoryWithParentDirectoryCreatesDirectoryInGitWorkspace() throws Exception {
        Tuple3<Git, Path, PersonIdent> gitEnv = aCleanGitEnvironment("git-filesystem-test");
        Path parentDirectoryPath = gitEnv._2.resolve("aParentDirectory");
        Path subDirectoryPath = parentDirectoryPath.resolve("aDirectoryName");
        File expectedDirectory = subDirectoryPath.toFile();


        new GitFilesystem(gitEnv._1, gitEnv._3)
                .createDirectory("aParentDirectory");
        new GitFilesystem(gitEnv._1, gitEnv._3)
                .createDirectory("aParentDirectory/aDirectoryName");

        assertThat("directory exists in git workspace path", expectedDirectory.exists() && expectedDirectory.isDirectory());
    }

    @Test
    void createDirectoryWithNullParentDirectoryButRelativePathPartsInDirectoryNameCreatesDirectoryInGitWorkspace() throws Exception {
        Tuple3<Git, Path, PersonIdent> gitEnv = aCleanGitEnvironment("git-filesystem-test");
        String directoryName = "aParentDirectory/aDirectoryName";
        File expectedDirectory = gitEnv._2.resolve(directoryName).toFile();

        new GitFilesystem(gitEnv._1, gitEnv._3)
                .createDirectory(directoryName);

        assertThat("directory exists in git workspace path", expectedDirectory.exists() && expectedDirectory.isDirectory());
    }

    @Test
    void createDirectoryAddsAndCommitsEmptyGitIgnoreFile() throws Exception {
        Tuple3<Git, Path, PersonIdent> gitEnv = aCleanGitEnvironment("git-filesystem-test");

        Path actualDirectoryPath = new GitFilesystem(gitEnv._1, gitEnv._3)
                .createDirectory("aDirectoryName");

        File gitIgnoreFile = actualDirectoryPath.resolve(GitFilesystem.GIT_IGNORE_FILENAME).toFile();
        Status status = gitEnv._1.status().call();
        assertThat(".gitignore exists", gitIgnoreFile.exists() && gitIgnoreFile.isFile());
        assertThat("git status has no untracked files", status.getUntracked().isEmpty());
        assertThat("git status is clean", status.isClean());
    }

}
