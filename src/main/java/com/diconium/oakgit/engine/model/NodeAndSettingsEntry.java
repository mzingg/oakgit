package com.diconium.oakgit.engine.model;

import com.diconium.oakgit.jdbc.OakGitResultSet;
import com.diconium.oakgit.jdbc.util.SqlType;
import com.diconium.oakgit.queryparsing.QueryAnalyzer;
import com.diconium.oakgit.queryparsing.SingleValueId;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.ToString;
import net.sf.jsqlparser.statement.Statement;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;
import java.util.function.Consumer;

@Getter
@Setter
@ToString
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

    public static NodeAndSettingsEntry buildNodeSettingsDataForInsert(@NonNull Statement statement, @NonNull Map<Integer, Object> placeholderData, @NonNull QueryAnalyzer analyzer) {
        NodeAndSettingsEntry data = new NodeAndSettingsEntry()
            .setId(analyzer.getId(statement, placeholderData).orElse(SingleValueId.INVALID_ID).value());
        analyzer
            .getDataField(statement, "MODIFIED", Long.class, placeholderData)
            .ifPresent(data::setModified);
        analyzer
            .getDataField(statement, "HASBINARY", Integer.class, placeholderData)
            .ifPresent(data::setHasBinary);
        analyzer
            .getDataField(statement, "DELETEDONCE", Integer.class, placeholderData)
            .ifPresent(data::setDeletedOnce);
        analyzer
            .getDataField(statement, "MODCOUNT", Long.class, placeholderData)
            .ifPresent(data::setModCount);
        analyzer
            .getDataField(statement, "CMODCOUNT", Long.class, placeholderData)
            .ifPresent(data::setCModCount);
        analyzer
            .getDataField(statement, "DSIZE", Long.class, placeholderData)
            .ifPresent(data::setDSize);
        analyzer
            .getDataField(statement, "VERSION", Integer.class, placeholderData)
            .ifPresent(data::setVersion);
        analyzer
            .getDataField(statement, "SDTYPE", Integer.class, placeholderData)
            .ifPresent(data::setSdType);
        analyzer
            .getDataField(statement, "SDMAXREVTIME", Long.class, placeholderData)
            .ifPresent(data::setSdMaxRevTime);
        analyzer
            .getDataField(statement, "DATA", String.class, placeholderData)
            .ifPresent(f -> data.setData(f.getBytes()));
        analyzer
            .getDataField(statement, "BDATA", String.class, placeholderData)
            .ifPresent(f -> data.setBdata(f.getBytes()));
        return data;
    }

    public NodeAndSettingsEntry appendData(byte[] addedData) {
        byte[] appendedData = new byte[data.length + addedData.length];
        System.arraycopy(data, 0, appendedData, 0, data.length);
        System.arraycopy(addedData, 0, appendedData, data.length, addedData.length);

        this.data = appendedData;
        return this;
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


}
