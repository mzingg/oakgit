package com.diconium.oakgit.jdbc;

import com.diconium.oakgit.engine.CommandFactory;
import lombok.RequiredArgsConstructor;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;


@RequiredArgsConstructor
public class OakGitStatement extends UnsupportedOakGitStatement {

    private final OakGitConnection connection;

    @Override
    public ResultSet executeQuery(String sql) throws SQLException {
        connection.getProcessor().execute(new CommandFactory().getCommandForSql(sql));
        return null;
    }

    public boolean execute(String sql) {
        System.out.println(sql);
        return false;
    }

    @Override
    public Connection getConnection() {
        return connection;
    }

    public void close() {
    }

}
