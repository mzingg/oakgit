package oakgit.engine.model;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import oakgit.jdbc.OakGitResultSet;
import oakgit.jdbc.util.SqlType;

import java.util.List;
import java.util.function.Consumer;

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
    public Consumer<OakGitResultSet> getResultSetTypeModifier() {
        return result -> {
            result.addColumn("ID", SqlType.VARCHAR.id, 512);
            result.addColumn("LASTMOD", SqlType.BIGINT.id, 0);
            result.addColumn("LVL", SqlType.SMALLINT.id, 0);
        };
    }

    @Override
    public Consumer<OakGitResultSet> getResultSetModifier(List<String> fieldList) {
        return result -> fillResultSet(result, fieldList, this::columnGetter);
    }

    private ColumnGetterResult columnGetter(String fieldName) {
        switch (fieldName) {
            case "ID":
                return new ColumnGetterResult(fieldName, id);
            case "LASTMOD":
                return new ColumnGetterResult(fieldName, lastmod);
            case "LVL":
                return new ColumnGetterResult(fieldName, lvl);
        }
        return null;
    }


}
