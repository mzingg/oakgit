package com.diconium.oakgit.jdbc;

import com.diconium.oakgit.engine.CommandFactory;
import com.diconium.oakgit.engine.CommandProcessor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.sql.ResultSet;
import java.sql.SQLException;

@Getter
@RequiredArgsConstructor
public class OakGitStatement extends UnsupportedOakGitStatement {

    private final OakGitConnection connection;

    @Override
    public ResultSet executeQuery(String sql) throws SQLException {
        OakGitConnection connection = getConnection();
        CommandProcessor processor = connection.getProcessor();
        CommandFactory factory = connection.getCommandFactory();

        return processor.execute(factory.getCommandForSql(sql)).toResultSet();
    }

    public boolean execute(String sql) {
        OakGitConnection connection = getConnection();
        CommandProcessor processor = connection.getProcessor();
        CommandFactory factory = connection.getCommandFactory();

        return processor.execute(factory.getCommandForSql(sql)).wasSuccessfull();
    }

    public void close() {
    }

    public boolean isPoolable() {
        return false;
    }

    public void setPoolable(boolean poolable) {

    }
}
