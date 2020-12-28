package oakgit.engine.model;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import oakgit.jdbc.OakGitResultSet;
import oakgit.jdbc.util.SqlType;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

/**
 * DATASTORE_DATA
 */
@Getter
@Setter
public class DatastoreDataEntry implements ContainerEntry<DatastoreDataEntry> {

  @NonNull
  private String id = "";

  private byte[] data;

  @Override
  public Map<String, OakGitResultSet.Column> getAvailableColumnsByName() {
    Map<String, OakGitResultSet.Column> result = new LinkedHashMap<>();
    result.put("ID", new OakGitResultSet.Column("ID", SqlType.VARCHAR.id, 64, Collections.emptyList()));
    result.put("DATA", new OakGitResultSet.Column("DATA", SqlType.BLOB.id, 0, Collections.emptyList()));
    return result;
  }

  @Override
  public Optional<ColumnGetterResult> entryGetter(String fieldName) {
    switch (fieldName) {
      case "ID":
        return Optional.of(new ColumnGetterResult(fieldName, id));
      case "DATA":
        return Optional.of(new ColumnGetterResult(fieldName, data));
    }
    return Optional.empty();
  }
}
