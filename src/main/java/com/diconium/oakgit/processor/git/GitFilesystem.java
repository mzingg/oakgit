package com.diconium.oakgit.processor.git;

import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.PersonIdent;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Getter
public class GitFilesystem {

    public static final String GIT_IGNORE_FILENAME = ".gitignore";

    private final Git git;
    private final Path workspacePath;
    private final PersonIdent committer;

    public GitFilesystem(Git git, PersonIdent committer) {
        this.git = git;
        this.workspacePath = git.getRepository().getWorkTree().toPath().toAbsolutePath();
        this.committer = committer;
    }

    public Path createDirectory(String relativeDirectoryPath) throws GitFilesystemException {
        if (StringUtils.isBlank(relativeDirectoryPath)) {
            throw new GitFilesystemException("relativeDirectoryPath parameter must not be blank");
        }

        return createDirectory(getWorkspacePath().resolve(relativeDirectoryPath));
    }

    public Path createDirectory(Path pathRelativeToWorkspace) throws GitFilesystemException {
        try {
            if (pathRelativeToWorkspace == null) {
                throw new GitFilesystemException("pathRelativeToWorkspace paramter must not be null");
            }

            Path absoluteTargetPath = pathRelativeToWorkspace.toAbsolutePath();
            Path absoluteWorkspacePath = getWorkspacePath();
            if (!absoluteTargetPath.startsWith(absoluteWorkspacePath)) {
                throw new GitFilesystemException("parentDirectory must be a subpath of the git workspace [" + absoluteWorkspacePath + "]");
            }

            if (absoluteTargetPath.toFile().exists()) {
                throw new GitFilesystemException("directory " + absoluteTargetPath.toString() + " already exists");
            }

            Path createdDirectoryPath = Files.createDirectories(absoluteTargetPath);
            Path gitIgnoreFile = createdDirectoryPath.resolve(GIT_IGNORE_FILENAME);
            String relativeDirectoryPath = absoluteWorkspacePath.relativize(createdDirectoryPath).toString();
            String relativeGitIgnorePath = absoluteWorkspacePath.relativize(gitIgnoreFile).toString();
            Git _git = getGit();

            Files.write(gitIgnoreFile, new byte[0]);

            _git.add()
                .addFilepattern(relativeDirectoryPath)
                .addFilepattern(relativeGitIgnorePath)
                .call();

            _git.commit()
                .setCommitter(getCommitter())
                .setMessage(String.format("GitFilesystem: create directory '%s'", relativeDirectoryPath))
                .call();

            return createdDirectoryPath;
        } catch (IOException | GitAPIException e) {
            throw new GitFilesystemException(e);
        }
    }
}
