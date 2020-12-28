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
  private int entryPointer;
  private int maxEntries;
  private boolean wasNull;

  public OakGitResultSet(String tableName) {
    this.tableName = tableName;
    columns = new ArrayList<>();
    // init pointer one place before first element, so that first call
    // to next() will point to the first entry
    entryPointer = -1;
    maxEntries = 0;
    wasNull = true;
  }

  public OakGitResultSet addColumn(Column column) {
    Optional<Integer> internalIndex = getInternalColumnIndexForColumnName(column.name);
    if (internalIndex.isEmpty()) {
      columns.add(column);
    }

    return this;
  }

  public OakGitResultSet addValue(String columnName, Object value) {
    Optional<Integer> internalIndex = getInternalColumnIndexForColumnName(columnName);
    if (internalIndex.isEmpty()) {
      throw new IllegalArgumentException("column does not exist");
    }
    columns.get(internalIndex.get()).entries.add(value);
    int entryCount = columns.get(internalIndex.get()).entries.size();
    if (entryCount > maxEntries) {
      maxEntries = entryCount;
    }

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
    if (columnIndex <= 0 || columnIndex > columns.size()) {
      throw new IllegalArgumentException("invalid column index");
    }
    if (entryPointer < columns.get(columnIndex - 1).entries.size()) {
      Object entry = getEntryOrNull(columnIndex);
      if (entry instanceof Integer) {
        wasNull = false;
        return ((Integer) entry).longValue();
      }
      if (entry instanceof Long) {
        wasNull = false;
        return (Long) entry;
      }
    }
    wasNull = true;
    return 0;
  }

  @Override
  public String getString(int columnIndex) {
    if (columnIndex <= 0 || columnIndex > columns.size()) {
      throw new IllegalArgumentException("invalid column index");
    }
    Object entry = getEntryOrNull(columnIndex);
    if (entry instanceof byte[]) {
      wasNull = false;
      return new String((byte[]) entry);
    }
    if (entry instanceof String) {
      wasNull = false;
      return (String) entry;
    }
    if (entry != null) {
      wasNull = false;
      return entry.toString();
    }
    wasNull = true;
    return null;
  }

  @Override
  public byte[] getBytes(int columnIndex) {
    if (columnIndex <= 0 || columnIndex > columns.size()) {
      throw new IllegalArgumentException("invalid column index");
    }
    Object entry = getEntryOrNull(columnIndex);
    if (entry instanceof byte[]) {
      wasNull = false;
      return (byte[]) entry;
    }
    if (entry instanceof String) {
      wasNull = false;
      return ((String) entry).getBytes();
    }
    wasNull = true;
    return null;
  }

  private Object getEntryOrNull(int columnIndex) {
    List<Object> entries = columns.get(columnIndex - 1).entries;
    return (entryPointer >= 0 && entries.size() > entryPointer) ? entries.get(entryPointer) : null;
  }

  @Override
  public boolean next() {
    if (!columns.isEmpty()) {
      if (entryPointer + 1 >= 0 && entryPointer + 1 < maxEntries) {
        entryPointer = entryPointer + 1;
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
  public final static class Column {
    private final String name;
    private final int type;
    private final int precision;
    private final List<Object> entries;

    public Column copy() {
      return new Column(name, type, precision, new ArrayList<>());
    }
  }

}
