package com.diconium.oakgit.engine.model;

import com.diconium.oakgit.jdbc.OakGitResultSet;
import com.diconium.oakgit.jdbc.util.SqlType;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.function.Consumer;

/**
 * DATASTORE_DATA
 */
@Getter
@Setter
public class DatastoreDataEntry implements ContainerEntry<DatastoreDataEntry> {

    @NonNull
    private String id = StringUtils.EMPTY;

    private byte[] data;

    @Override
    public Consumer<OakGitResultSet> getResultSetTypeModifier() {
        return result -> {
            result.addColumn("ID", SqlType.VARCHAR.id, 64);
            result.addColumn("DATA", SqlType.BLOB.id, 0);
        };
    }

    @Override
    public Consumer<OakGitResultSet> getResultSetModifier(List<String> exclude) {
        return result -> {
            result.addValue("ID", id);
        };
    }

}
