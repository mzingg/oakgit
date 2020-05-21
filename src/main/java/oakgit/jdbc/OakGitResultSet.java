package oakgit.jdbc;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import oakgit.jdbc.util.SqlType;

import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Getter
@Setter
@ToString
public class OakGitResultSet extends UnsupportedResultSet {

    public static final OakGitResultSet EMPTY_RESULT_SET = new OakGitResultSet("");
    private static final String DEFAULT_SCHEMA_NAME = "oakgit";
    private final String tableName;

    private List<Column> columns;
    private int pointer;
    private boolean wasNull;

    public OakGitResultSet(String tableName) {
        this.tableName = tableName;
        columns = new ArrayList<>();
        pointer = -1;
        wasNull = true;
    }

    public OakGitResultSet addColumn(String columnName, int type, int precision) {
        Optional<Integer> colIndex = getInternalColumnIndexForColumnName(columnName);
        if (colIndex.isEmpty()) {
            columns.add(new Column(columnName, type, precision, new ArrayList<>()));
        }

        return this;
    }

    public OakGitResultSet addValue(String columnName, Object value) {
        Optional<Integer> colIndex = getInternalColumnIndexForColumnName(columnName);
        int index = colIndex.orElse(columns.size());
        if (colIndex.isEmpty()) {
            throw new IllegalArgumentException("column does not exist");
        }
        columns.get(index).entries.add(value);

        return this;
    }

    public OakGitResultSet addMultiple(String columnName, int type, int precision, List<Object> values) {
        Optional<Integer> colIndex = getInternalColumnIndexForColumnName(columnName);
        int index = colIndex.orElse(columns.size());
        if (colIndex.isEmpty()) {
            columns.add(new Column(columnName, type, precision, new ArrayList<>()));
        }
        columns.get(index).entries.addAll(values);

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
        return getInternalColumnIndexForColumnName(columnLabel).map(internalIndex -> internalIndex + 1).orElseThrow(() -> new SQLException("column not found"));
    }

    @Override
    public String getColumnName(int columnIndex) throws SQLException {
        if (columnIndex >= 1 && columnIndex <= columns.size()) {
            return columns.get(columnIndex - 1).name;
        }
        throw new SQLException("column does not exist");
    }

    @Override
    public int getColumnType(int columnIndex) throws SQLException {
        if (columnIndex >= 1 && columnIndex <= columns.size()) {
            return columns.get(columnIndex - 1).type;
        }
        throw new SQLException("column does not exist");
    }

    @Override
    public String getColumnTypeName(int columnIndex) throws SQLException {
        if (columnIndex >= 1 && columnIndex <= columns.size()) {
            return SqlType.valueOf(columns.get(columnIndex - 1).type).name();
        }
        throw new SQLException("column does not exist");
    }

    @Override
    public int getPrecision(int columnIndex) throws SQLException {
        if (columnIndex >= 1 && columnIndex <= columns.size()) {
            return columns.get(columnIndex - 1).precision;
        }
        throw new SQLException("column does not exist");
    }

    @Override
    public String getTableName(int columnIndex) throws SQLException {
        if (columnIndex >= 1 && columnIndex <= columns.size()) {
            return tableName;
        }
        throw new SQLException("column does not exist");
    }

    @Override
    public String getSchemaName(int columnIndex) throws SQLException {
        if (columnIndex >= 1 && columnIndex <= columns.size()) {
            return DEFAULT_SCHEMA_NAME;
        }
        throw new SQLException("column does not exist");
    }

    @Override
    public boolean wasNull() {
        return wasNull;
    }

    @Override
    public long getLong(int columnIndex) {
        if (pointer < columns.get(columnIndex - 1).entries.size()) {
            Object result = columns.get(columnIndex - 1).entries.get(pointer);
            if (result instanceof Integer) {
                wasNull = false;
                return ((Integer) result).longValue();
            }
            if (result instanceof Long) {
                wasNull = false;
                return (Long) result;
            }
        }
        wasNull = true;
        return 0L;
    }

    @Override
    public String getString(int columnIndex) {
        Object result = columns.get(columnIndex - 1).entries.get(pointer);
        if (result instanceof byte[]) {
            wasNull = false;
            return new String((byte[]) result);
        }
        if (result instanceof String) {
            wasNull = false;
            return (String) result;
        }
        if (result != null) {
            wasNull = false;
            return result.toString();
        }
        wasNull = true;
        return null;
    }

    @Override
    public byte[] getBytes(int columnIndex) {
        Object result = columns.get(columnIndex - 1).entries.get(pointer);
        if (result instanceof byte[]) {
            wasNull = false;
            return (byte[]) result;
        }
        if (result instanceof String) {
            wasNull = false;
            return ((String) result).getBytes();
        }
        wasNull = true;
        return null;
    }

    @Override
    public boolean next() {
        if (!columns.isEmpty()) {
            if (pointer + 1 >= 0 && pointer + 1 < columns.get(0).entries.size()) {
                pointer = pointer + 1;
                return true;
            }
        }

        return false;
    }

    private Optional<Integer> getInternalColumnIndexForColumnName(String name) {
        for (Column column : columns) {
            if (column.name.equals(name)) {
                return Optional.of(columns.indexOf(column));
            }
        }

        return Optional.empty();
    }

    @RequiredArgsConstructor
    @ToString
    private final static class Column {
        private final String name;
        private final int type;
        private final int precision;
        private final List<Object> entries;
    }

}
