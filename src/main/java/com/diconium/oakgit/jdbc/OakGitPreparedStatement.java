package com.diconium.oakgit.jdbc;

import io.vavr.Tuple2;
import lombok.AccessLevel;
import lombok.Getter;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class OakGitPreparedStatement extends UnsupportedPreparedStatement {

    protected OakGitPreparedStatement(OakGitConnection connection, String sql) {
        super(connection, sql);
    }

    @Getter(AccessLevel.PROTECTED)
    protected List<Tuple2<Integer, Object>> parameter;

    @Override
    public ResultSet executeQuery() throws SQLException {
        return getStatement().executeQuery(getSql());
    }

    @Override
    public void addBatch() {
        System.out.println(getSql());
    }

    public int[] executeBatch() {
        return new int[0];
    }

    @Override
    public void setString(int parameterIndex, String x) {
        System.out.println(parameterIndex + "=" + x);
    }

    @Override
    public void setObject(int parameterIndex, Object x) {
        System.out.println(parameterIndex + "=" + x);
    }

    @Override
    public void setObject(int parameterIndex, Object x, int targetSqlType) {
        System.out.println(parameterIndex + "=" + x);
    }

    @Override
    public void setBinaryStream(int parameterIndex, InputStream x, int length) {
        System.out.println(parameterIndex + "=inputstream");
    }

}
