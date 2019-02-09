package com.diconium.oakgit.model;

import com.diconium.oakgit.commons.QueryParserResult;
import com.diconium.oakgit.jdbc.OakGitResultSet;
import lombok.*;
import lombok.experimental.Wither;
import org.apache.calcite.avatica.SqlType;

import java.util.Map;
import java.util.function.Consumer;

@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
public class NodeAndSettingsEntry implements ContainerEntry<NodeAndSettingsEntry> {

    @NonNull
    @Wither(AccessLevel.NONE)
    private final String id;

    private long modified = 0L;

    private int hasBinary = 0;

    private int deletedOnce = 0;

    private long modcount = 0L;

    private long cmodcount = 0L;

    private long dsize = 0L;

    private int version = 0;

    private int sdtype = 0;

    private long sdmaxrevtime = 0L;

    private byte[] data = new byte[0];

    private byte[] bdata = new byte[0];

    @Override
    public String toString() {
        return "NodeAndSettingsEntry{" +
                "id='" + id + '\'' +
                ", modified=" + modified +
                ", hasBinary=" + hasBinary +
                ", deletedOnce=" + deletedOnce +
                ", modcount=" + modcount +
                ", cmodcount=" + cmodcount +
                ", dsize=" + dsize +
                ", version=" + version +
                ", sdtype=" + sdtype +
                ", sdmaxrevtime=" + sdmaxrevtime +
                ", data=" + new String(data) +
                ", bdata=" + new String(bdata) +
                '}';
    }

    @Override
    public Consumer<OakGitResultSet> getResultSetModifier() {
        return result -> {
            result.add("ID", SqlType.VARCHAR.id, 512, id);
            result.add("MODIFIED", SqlType.BIGINT.id, 0, modified);
            result.add("HASBINARY", SqlType.SMALLINT.id, 0, hasBinary);
            result.add("DELETEDONCE", SqlType.SMALLINT.id, 0, deletedOnce);
            result.add("MODCOUNT", SqlType.BIGINT.id, 0, modcount);
            result.add("CMODCOUNT", SqlType.BIGINT.id, 0, cmodcount);
            result.add("DSIZE", SqlType.BIGINT.id, 0, dsize);
            result.add("VERSION", SqlType.SMALLINT.id, 0, version);
            result.add("SDTYPE", SqlType.SMALLINT.id, 0, sdtype);
            result.add("SDMAXREVTIME", SqlType.BIGINT.id, 0, sdmaxrevtime);
            result.add("DATA", SqlType.VARCHAR.id, 16384, data);
            result.add("BDATA", SqlType.BLOB.id, 1073741824, bdata);
        };
    }

    public static NodeAndSettingsEntry buildNodeSettingsDataForInsert(@NonNull Map<Integer, Object> placeholderData, @NonNull QueryParserResult queryParserResult) {
        NodeAndSettingsEntry data = new NodeAndSettingsEntry(queryParserResult.getId(placeholderData));
        queryParserResult
                .getInsertDataField("MODIFIED", Long.class, placeholderData)
                .ifPresent(data::setModified);
        queryParserResult
                .getInsertDataField("HASBINARY", Integer.class, placeholderData)
                .ifPresent(data::setHasBinary);
        queryParserResult
                .getInsertDataField("DELETEDONCE", Integer.class, placeholderData)
                .ifPresent(data::setDeletedOnce);
        queryParserResult
                .getInsertDataField("MODCOUNT", Long.class, placeholderData)
                .ifPresent(data::setModcount);
        queryParserResult
                .getInsertDataField("CMODCOUNT", Long.class, placeholderData)
                .ifPresent(data::setCmodcount);
        queryParserResult
                .getInsertDataField("DSIZE", Long.class, placeholderData)
                .ifPresent(data::setDsize);
        queryParserResult
                .getInsertDataField("VERSION", Integer.class, placeholderData)
                .ifPresent(data::setVersion);
        queryParserResult
                .getInsertDataField("SDTYPE", Integer.class, placeholderData)
                .ifPresent(data::setSdtype);
        queryParserResult
                .getInsertDataField("SDMAXREVTIME", Long.class, placeholderData)
                .ifPresent(data::setSdmaxrevtime);
        queryParserResult
                .getInsertDataField("DATA", String.class, placeholderData)
                .ifPresent(f -> data.setData(f.getBytes()));
        queryParserResult
                .getInsertDataField("BDATA", String.class, placeholderData)
                .ifPresent(f -> data.setBdata(f.getBytes()));
        return data;
    }
}
