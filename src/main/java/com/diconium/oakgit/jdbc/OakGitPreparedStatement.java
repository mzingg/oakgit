package com.diconium.oakgit.jdbc;

import io.vavr.Tuple2;
import lombok.AccessLevel;
import lombok.Getter;

import java.io.InputStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class OakGitPreparedStatement extends UnsupportedPreparedStatement {

    protected OakGitPreparedStatement(OakGitConnection connection, String sql) {
        super(connection, sql);
    }

    private List<String> commandList = new ArrayList<>();

    @Getter(AccessLevel.PROTECTED)
    protected List<Tuple2<Integer, Object>> parameter;

    @Override
    public int executeUpdate() {
        try {
            getStatement().executeQuery(getSql());
            return 1;
        } catch (SQLException e) {

        }

        return 0;
    }

    @Override
    public ResultSet executeQuery() throws SQLException {
        System.out.println("PREPARED SQL: " + getSql());
        return getStatement().executeQuery(getSql());
    }

    @Override
    public void addBatch() {
        System.out.println("ADD BATCH SQL: " + getSql());
        commandList.add(getSql());
    }

    public int[] executeBatch() {
        int[] result = new int[commandList.size()];
        for (int i = 0; i < commandList.size(); i++) {
            System.out.println("EXECUTE BATCH SQL: " + commandList.get(i));
            try {
                getStatement().executeQuery(commandList.get(i));
                result[i] = 1;
            } catch (SQLException e) {

            }
        }
        return result;
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
