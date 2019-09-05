package com.diconium.oakgit.jdbc;

import com.diconium.oakgit.engine.CommandFactory;
import com.diconium.oakgit.engine.CommandProcessor;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class OakGitPreparedStatement extends UnsupportedPreparedStatement {

    private List<String> commandList = new ArrayList<>();
    private Map<Integer, Object> placeholderData = new LinkedHashMap<>();

    protected OakGitPreparedStatement(OakGitConnection connection, String sql) {
        super(connection, sql);
    }

    @Override
    public int executeUpdate() {
        OakGitConnection connection = getConnection();
        CommandProcessor processor = connection.getProcessor();
        CommandFactory factory = connection.getCommandFactory();

        return processor.execute(factory.getCommandForSql(getSql(), placeholderData)).affectedCount();
    }

    @Override
    public ResultSet executeQuery() throws SQLException {
        OakGitConnection connection = getConnection();
        CommandProcessor processor = connection.getProcessor();
        CommandFactory factory = connection.getCommandFactory();

        return processor.execute(factory.getCommandForSql(getSql(), placeholderData)).toResultSet();
    }

    @Override
    public void addBatch() {
        commandList.add(getSql());
    }

    public int[] executeBatch() {
        int[] result = new int[commandList.size()];
        for (int i = 0; i < commandList.size(); i++) {
            OakGitConnection connection = getConnection();
            CommandProcessor processor = connection.getProcessor();
            CommandFactory factory = connection.getCommandFactory();

            result[i] = processor.execute(factory.getCommandForSql(commandList.get(i), placeholderData)).affectedCount();
        }
        return result;
    }

    @Override
    public void setString(int parameterIndex, String x) {
        placeholderData.put(parameterIndex, x);
    }

    @Override
    public void setLong(int parameterIndex, long x) {
        placeholderData.put(parameterIndex, x);
    }

    @Override
    public void setBytes(int parameterIndex, byte[] x) {
        placeholderData.put(parameterIndex, x);
    }

    @Override
    public void setObject(int parameterIndex, Object x) {
        placeholderData.put(parameterIndex, x);
    }

    @Override
    public void setObject(int parameterIndex, Object x, int targetSqlType) {
        placeholderData.put(parameterIndex, x);
    }

    @Override
    public void setBinaryStream(int parameterIndex, InputStream stream, int length) {
        if (stream != null) {
            try {
                placeholderData.put(parameterIndex, IOUtils.readFully(stream, length));
            } catch (IOException e) {
                throw new IllegalArgumentException(e);
            }
        } else {
            placeholderData.put(parameterIndex, StringUtils.EMPTY);
        }
    }

}
