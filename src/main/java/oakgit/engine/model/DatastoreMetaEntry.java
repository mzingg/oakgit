package oakgit.engine.model;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Optional;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import oakgit.jdbc.OakGitResultSet;
import oakgit.jdbc.util.SqlType;

/** Used for DATASTORE_META */
@Getter
@Setter
public class DatastoreMetaEntry implements ContainerEntry<DatastoreMetaEntry> {

  private static final LinkedHashMap<String, OakGitResultSet.Column> COLUMNS;

  static {
    COLUMNS = new LinkedHashMap<>();
    COLUMNS.put(
        "ID", new OakGitResultSet.Column("ID", SqlType.VARCHAR.id, 512, Collections.emptyList()));
    COLUMNS.put(
        "LASTMOD",
        new OakGitResultSet.Column("LASTMOD", SqlType.BIGINT.id, 0, Collections.emptyList()));
    COLUMNS.put(
        "LVL", new OakGitResultSet.Column("LVL", SqlType.SMALLINT.id, 0, Collections.emptyList()));
  }

  @NonNull private String id = "";

  private Long lastmod;

  private Integer lvl;

  @Override
  public DatastoreMetaEntry copy() {
    return new DatastoreMetaEntry().setId(id).setLastmod(lastmod).setLvl(lvl);
  }

  @Override
  public LinkedHashMap<String, OakGitResultSet.Column> getAvailableColumnsByName() {
    return COLUMNS;
  }

  @Override
  public Optional<ColumnGetterResult> entryGetter(String fieldName) {
    switch (fieldName) {
      case "ID":
        return Optional.of(new ColumnGetterResult(fieldName, getId()));
      case "LASTMOD":
        return Optional.of(new ColumnGetterResult(fieldName, getLastmod()));
      case "LVL":
        return Optional.of(new ColumnGetterResult(fieldName, getLvl()));
    }
    return Optional.empty();
  }
}
