package com.diconium.oakgit.jdbc;

import io.vavr.Tuple4;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Getter
@Setter
public class OakGitResultSet extends UnsupportedResultSet {

    public static final OakGitResultSet EMPTY_RESULT_SET = new OakGitResultSet(StringUtils.EMPTY);
    private final String tableName;

    private List<Tuple4<String, Integer, Integer, List<Object>>> columns;
    private int pointer;
    private boolean wasNull;

    public OakGitResultSet(String tableName) {
        this.tableName = tableName;
        columns = new ArrayList<>();
        pointer = -1;
    }

    public OakGitResultSet addColumn(String columnName, int type, int precision) {
        Optional<Integer> colIndex = getColumnIndexForColumnName(columnName);
        if (!colIndex.isPresent()) {
            columns.add(new Tuple4<>(columnName, type, precision, new ArrayList<>()));
        }

        return this;
    }

    public OakGitResultSet addValue(String columnName, Object value) {
        Optional<Integer> colIndex = getColumnIndexForColumnName(columnName);
        int index = colIndex.orElse(columns.size());
        if (!colIndex.isPresent()) {
            throw new IllegalArgumentException("column does not exist");
        }
        columns.get(index)._4.add(value);

        return this;
    }

    public OakGitResultSet addMultiple(String columnName, int type, int precision, List<Object> values) {
        Optional<Integer> colIndex = getColumnIndexForColumnName(columnName);
        int index = colIndex.orElse(columns.size());
        if (!colIndex.isPresent()) {
            columns.add(new Tuple4<>(columnName, type, precision, new ArrayList<>()));
        }
        columns.get(index)._4.addAll(values);

        return this;
    }

    @Override
    public void close() {
    }

    @Override
    public ResultSetMetaData getMetaData() {
        return this;
    }

    @Override
    public int getColumnCount() {
        return columns.size();
    }

    @Override
    public int findColumn(String columnLabel) throws SQLException {
        return getColumnIndexForColumnName(columnLabel).orElseThrow(() -> new SQLException("column not found"));
    }

    @Override
    public String getColumnName(int column) throws SQLException {
        if (column >= 1 && column <= columns.size()) {
            return columns.get(column - 1)._1;
        }
        throw new SQLException("column does not exist");
    }

    @Override
    public int getColumnType(int column) throws SQLException {
        if (column >= 1 && column <= columns.size()) {
            return columns.get(column - 1)._2;
        }
        throw new SQLException("column does not exist");
    }

    @Override
    public String getColumnTypeName(int column) throws SQLException {
        if (column >= 1 && column <= columns.size()) {
            return columns.get(column - 1)._2.toString();
        }
        throw new SQLException("column does not exist");
    }

    @Override
    public int getPrecision(int column) throws SQLException {
        if (column >= 1 && column <= columns.size()) {
            return columns.get(column - 1)._3;
        }
        throw new SQLException("column does not exist");
    }

    @Override
    public String getTableName(int column) throws SQLException {
        if (column >= 1 && column <= columns.size()) {
            return tableName;
        }
        throw new SQLException("column does not exist");
    }

    @Override
    public String getSchemaName(int column) throws SQLException {
        if (column >= 1 && column <= columns.size()) {
            return "oakgit";
        }
        throw new SQLException("column does not exist");
    }

    @Override
    public boolean wasNull() {
        return wasNull;
    }

    @Override
    public long getLong(int columnIndex) {
        Object result = columns.get(columnIndex)._4.get(pointer);
        if (result instanceof Integer) {
            wasNull = false;
            return ((Integer) result).longValue();
        }
        if (result instanceof Long) {
            wasNull = false;
            return (Long) result;
        }
        wasNull = true;
        return 0L;
    }

    @Override
    public String getString(int columnIndex) {
        Object result = columns.get(columnIndex)._4.get(pointer);
        if (result instanceof byte[]) {
            wasNull = false;
            return new String((byte[]) result);
        }
        if (result instanceof String) {
            wasNull = false;
            return (String) result;
        }
        wasNull = true;
        return StringUtils.EMPTY;
    }

    @Override
    public byte[] getBytes(int columnIndex) {
        Object result = columns.get(columnIndex)._4.get(pointer);
        if (result instanceof byte[]) {
            wasNull = false;
            return (byte[]) result;
        }
        wasNull = true;
        return new byte[0];
    }

    @Override
    public boolean next() {
        if (!columns.isEmpty()) {
            if (pointer + 1 >= 0 && pointer + 1 < columns.get(0)._4.size()) {
                pointer = pointer + 1;
                return true;
            }
        }

        return false;
    }

    private Optional<Integer> getColumnIndexForColumnName(String name) {
        for (Tuple4<String, Integer, Integer, List<Object>> column : columns) {
            if (column._1.equals(name)) {
                return Optional.of(columns.indexOf(column));
            }
        }

        return Optional.empty();
    }

}
