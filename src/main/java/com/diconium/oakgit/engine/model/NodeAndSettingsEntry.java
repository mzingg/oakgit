package com.diconium.oakgit.engine.model;

import com.diconium.oakgit.commons.QueryParserResult;
import com.diconium.oakgit.jdbc.OakGitResultSet;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.apache.calcite.avatica.SqlType;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;
import java.util.function.Consumer;

@Getter
@Setter
public class NodeAndSettingsEntry implements ContainerEntry<NodeAndSettingsEntry> {

    @NonNull
    private String id = StringUtils.EMPTY;

    private Long modified;

    private Integer hasBinary;

    private Integer deletedOnce;

    private Long modCount;

    private Long cModCount;

    private Long dSize;

    private Integer version;

    private Integer sdType;

    private Long sdMaxRevTime;

    private byte[] data;

    private byte[] bdata;

    @Override
    public String toString() {
        return "NodeAndSettingsEntry{" +
                "id='" + id + '\'' +
                ", modified=" + modified +
                ", hasBinary=" + hasBinary +
                ", deletedOnce=" + deletedOnce +
                ", modCount=" + modCount +
                ", cModCount=" + cModCount +
                ", dSize=" + dSize +
                ", version=" + version +
                ", sdType=" + sdType +
                ", sdMaxRevTime=" + sdMaxRevTime +
                ", data=" + new String(data) +
                ", bdata=" + new String(bdata) +
                '}';
    }
    @Override
    public Consumer<OakGitResultSet> getResultSetTypeModifier() {
        return result -> {
            result.addColumn("MODIFIED", SqlType.BIGINT.id, 0);
            result.addColumn("MODCOUNT", SqlType.BIGINT.id, 0);
            result.addColumn("CMODCOUNT", SqlType.BIGINT.id, 0);
            result.addColumn("HASBINARY", SqlType.SMALLINT.id, 0);
            result.addColumn("DELETEDONCE", SqlType.SMALLINT.id, 0);
            result.addColumn("VERSION", SqlType.SMALLINT.id, 0);
            result.addColumn("SDTYPE", SqlType.SMALLINT.id, 0);
            result.addColumn("DSIZE", SqlType.BIGINT.id, 0);
            result.addColumn("SDMAXREVTIME", SqlType.BIGINT.id, 0);
            result.addColumn("DATA", SqlType.VARCHAR.id, 16384);
            result.addColumn("BDATA", SqlType.BLOB.id, 1073741824);
            result.addColumn("ID", SqlType.VARCHAR.id, 512);
        };
    }

    @Override
    public Consumer<OakGitResultSet> getResultSetModifier() {
        return result -> {
            result.addValue("MODIFIED", modified);
            result.addValue("MODCOUNT", modCount);
            result.addValue("CMODCOUNT", cModCount);
            result.addValue("HASBINARY", hasBinary);
            result.addValue("DELETEDONCE", deletedOnce);
            result.addValue("VERSION", version);
            result.addValue("SDTYPE", sdType);
            result.addValue("DSIZE", dSize);
            result.addValue("SDMAXREVTIME", sdMaxRevTime);
            result.addValue("DATA", data);
            result.addValue("BDATA", bdata);
            result.addValue("ID", id);
        };
    }

    public static NodeAndSettingsEntry buildNodeSettingsDataForInsert(@NonNull Map<Integer, Object> placeholderData, @NonNull QueryParserResult queryParserResult) {
        NodeAndSettingsEntry data = new NodeAndSettingsEntry()
                .setId(queryParserResult.getId(placeholderData));
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
                .ifPresent(data::setModCount);
        queryParserResult
                .getInsertDataField("CMODCOUNT", Long.class, placeholderData)
                .ifPresent(data::setCModCount);
        queryParserResult
                .getInsertDataField("DSIZE", Long.class, placeholderData)
                .ifPresent(data::setDSize);
        queryParserResult
                .getInsertDataField("VERSION", Integer.class, placeholderData)
                .ifPresent(data::setVersion);
        queryParserResult
                .getInsertDataField("SDTYPE", Integer.class, placeholderData)
                .ifPresent(data::setSdType);
        queryParserResult
                .getInsertDataField("SDMAXREVTIME", Long.class, placeholderData)
                .ifPresent(data::setSdMaxRevTime);
        queryParserResult
                .getInsertDataField("DATA", String.class, placeholderData)
                .ifPresent(f -> data.setData(f.getBytes()));
        queryParserResult
                .getInsertDataField("BDATA", String.class, placeholderData)
                .ifPresent(f -> data.setBdata(f.getBytes()));
        return data;
    }


}
