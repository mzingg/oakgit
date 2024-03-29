package oakgit.engine.model;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import oakgit.jdbc.OakGitResultSet;
import oakgit.jdbc.util.SqlType;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Optional;

/**
 * Used for DATASTORE_META
 */
@Getter
@Setter
public class DatastoreMetaEntry implements ContainerEntry<DatastoreMetaEntry> {

  @NonNull
  private String id = "";

  private Long lastmod;

  private Integer lvl;

  @Override
  public DatastoreMetaEntry copy() {
    return new DatastoreMetaEntry()
        .setId(id)
        .setLastmod(lastmod)
        .setLvl(lvl);
  }

  @Override
  public LinkedHashMap<String, OakGitResultSet.Column> getAvailableColumnsByName() {
    LinkedHashMap<String, OakGitResultSet.Column> result = new LinkedHashMap<>();
    result.put("ID", new OakGitResultSet.Column("ID", SqlType.VARCHAR.id, 512, Collections.emptyList()));
    result.put("LASTMOD", new OakGitResultSet.Column("LASTMOD", SqlType.BIGINT.id, 0, Collections.emptyList()));
    result.put("LVL", new OakGitResultSet.Column("LVL", SqlType.SMALLINT.id, 0, Collections.emptyList()));
    return result;
  }

  @Override
  public Optional<ColumnGetterResult> entryGetter(String fieldName) {
    DatastoreMetaEntry accessor = this.copy();
    switch (fieldName) {
      case "ID":
        return Optional.of(new ColumnGetterResult(fieldName, accessor.getId()));
      case "LASTMOD":
        return Optional.of(new ColumnGetterResult(fieldName, accessor.getLastmod()));
      case "LVL":
        return Optional.of(new ColumnGetterResult(fieldName, accessor.getLvl()));
    }
    return Optional.empty();
  }


}
