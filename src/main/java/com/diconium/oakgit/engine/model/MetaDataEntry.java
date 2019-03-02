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

    private long lastmod = 0L;

    private int lvl = 0;

    @Override
    public Consumer<OakGitResultSet> getResultSetModifier() {
        return result -> {
            result.add("ID", SqlType.VARCHAR.id, 512, id);
            result.add("LASTMOD", SqlType.BIGINT.id, 0, lastmod);
            result.add("LVL", SqlType.SMALLINT.id, 0, lvl);
        };
    }

}
