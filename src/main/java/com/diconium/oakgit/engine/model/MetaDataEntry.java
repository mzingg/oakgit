package com.diconium.oakgit.engine.model;

import com.diconium.oakgit.jdbc.OakGitResultSet;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.apache.calcite.avatica.SqlType;
import org.apache.commons.lang3.StringUtils;

import java.util.function.Consumer;

@Getter
@Setter
public class MetaDataEntry implements ContainerEntry<MetaDataEntry> {

    @NonNull
    private String id = StringUtils.EMPTY;

    private Long lastmod;

    private Integer lvl;

    @Override
    public Long getModCount() {
        return null;
    }

    @Override
    public Consumer<OakGitResultSet> getResultSetTypeModifier() {
        return result -> {
            result.addColumn("ID", SqlType.VARCHAR.id, 512);
            result.addColumn("LASTMOD", SqlType.BIGINT.id, 0);
            result.addColumn("LVL", SqlType.SMALLINT.id, 0);
        };
    }

    @Override
    public Consumer<OakGitResultSet> getResultSetModifier() {
        return result -> {
            result.addValue("ID", id);
            result.addValue("LASTMOD", lastmod);
            result.addValue("LVL", lvl);
        };
    }

}
