package com.diconium.oak.jdbc;

import io.vavr.Tuple4;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Getter
@Setter
public class JaggitResultSet extends UnsupportedResultSet {

    public static final JaggitResultSet EMPTY_RESULT_SET = new JaggitResultSet(StringUtils.EMPTY);
    private final String tableName;

    private List<Tuple4<String, Integer, Integer, List<Object>>> columns;
    private int pointer;

    public JaggitResultSet(String tableName) {
        this.tableName = tableName;
        columns = new ArrayList<>();
        pointer = 0;
    }

    public JaggitResultSet add(String columnName, int type, int precision, Object... entries) {
        Optional<Integer> colIndex = getColumnIndexForColumnName(columnName);
        int index = colIndex.orElse(columns.size());
        if (!colIndex.isPresent()) {
            columns.add(new Tuple4<>(columnName, type, precision, new ArrayList<>()));
        }
        columns.get(index)._4.addAll(Arrays.asList(entries));

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
            return "jaggit";
        }
        throw new SQLException("column does not exist");
    }

    @Override
    public boolean next() {
        if (!columns.isEmpty()) {
            if (pointer + 1 > 0 && pointer + 1 < columns.get(0)._4.size()) {
                pointer = pointer + 1;
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
