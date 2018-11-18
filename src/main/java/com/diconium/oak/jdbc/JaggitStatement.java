package com.diconium.oak.jdbc;

import com.diconium.oak.jdbc.operation.OperationDetector;
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
        return new OperationDetector().forSql(sql).orElseThrow(() -> new SQLException("unknown operation: " + sql));
    }

    public boolean execute(String sql) {
        System.out.println(sql);
        return new OperationDetector().forSql(sql).isPresent();
    }

    @Override
    public Connection getConnection() {
        return connection;
    }

    public void close() {
    }

}
