package com.diconium.oakgit.engine.model;

import com.diconium.oakgit.jdbc.OakGitResultSet;
import com.diconium.oakgit.jdbc.util.SqlType;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

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
    public Consumer<OakGitResultSet> getResultSetModifier(List<String> exclude) {
        return result -> {
            result.addValue("ID", id);
        };
    }

}
