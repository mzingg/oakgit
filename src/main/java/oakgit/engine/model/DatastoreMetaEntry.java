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
    public Map<String, OakGitResultSet.Column> getAvailableColumnsByName() {
        Map<String, OakGitResultSet.Column> result = new LinkedHashMap<>();
        result.put("ID", new OakGitResultSet.Column("ID", SqlType.VARCHAR.id, 512, Collections.emptyList()));
        result.put("LASTMOD", new OakGitResultSet.Column("LASTMOD", SqlType.BIGINT.id, 0, Collections.emptyList()));
        result.put("LVL", new OakGitResultSet.Column("LVL", SqlType.SMALLINT.id, 0, Collections.emptyList()));
        return result;
    }

    @Override
    public Optional<ColumnGetterResult> entryGetter(String fieldName) {
        switch (fieldName) {
            case "ID":
                return Optional.of(new ColumnGetterResult(fieldName, id));
            case "LASTMOD":
                return Optional.of(new ColumnGetterResult(fieldName, lastmod));
            case "LVL":
                return Optional.of(new ColumnGetterResult(fieldName, lvl));
        }
        return Optional.empty();
    }


}
