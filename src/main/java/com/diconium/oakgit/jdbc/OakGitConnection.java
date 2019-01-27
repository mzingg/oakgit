package com.diconium.oakgit.jdbc;

import com.diconium.oakgit.engine.CommandFactory;
import com.diconium.oakgit.engine.CommandProcessor;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jgit.api.Git;

import java.io.IOException;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.Statement;

@RequiredArgsConstructor
@Getter
@Slf4j
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
        // save?
    }

    @Override
    public void rollback() {

    }

}
