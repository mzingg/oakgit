package com.diconium.oak;

import io.vavr.Tuple;
import io.vavr.Tuple3;
import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.PersonIdent;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class TestHelpers {

    public static final OutputStream DERBY_DEV_NULL = new OutputStream() {
        public void write(int b) {}
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

    public static Tuple3<Git, Path, PersonIdent> aCleanGitEnvironment(String directoryName) throws GitAPIException, IOException {
        Path workspaceDirectory = aCleanTestDirectory(directoryName);
        Git git = Git.init().setDirectory(workspaceDirectory.toFile()).call();
        PersonIdent committer = new PersonIdent("Oak Git", "oak-git@diconium.com");
        return Tuple.of(git, workspaceDirectory, committer);
    }
}
