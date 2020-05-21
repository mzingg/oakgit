package oakgit.engine.model;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import oakgit.jdbc.OakGitResultSet;
import oakgit.jdbc.util.SqlType;

import java.util.List;
import java.util.function.Consumer;

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
    public Consumer<OakGitResultSet> getResultSetTypeModifier() {
        return result -> {
            result.addColumn("ID", SqlType.VARCHAR.id, 64);
            result.addColumn("DATA", SqlType.BLOB.id, 0);
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
            case "DATA":
                return new ColumnGetterResult(fieldName, data);
        }
        return null;
    }
}
