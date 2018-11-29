package com.diconium.oak.jdbc;

import lombok.RequiredArgsConstructor;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;


@RequiredArgsConstructor
public class JaggitStatement extends UnsupportedJaggitStatement {

    private final Connection connection;

    @Override
    public ResultSet executeQuery(String sql) throws SQLException {
        System.out.println(sql);
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
