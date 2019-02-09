package com.diconium.oakgit.model;

import com.diconium.oakgit.jdbc.OakGitResultSet;
import lombok.*;
import lombok.experimental.Wither;
import org.apache.calcite.avatica.SqlType;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

@Getter
@Wither
@RequiredArgsConstructor
@AllArgsConstructor
public class MetaDataEntry implements ContainerEntry<MetaDataEntry> {

    @NonNull
    @Wither(AccessLevel.NONE)
    private final String id;

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
