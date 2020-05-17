package com.diconium.oakgit.engine.model;

import com.diconium.oakgit.jdbc.OakGitResultSet;
import com.diconium.oakgit.jdbc.util.SqlType;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;

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

    @Override
    public String toString() {
        return new ToStringBuilder(this)
            .append("id", id)
            .append("modified", modified)
            .append("hasBinary", hasBinary)
            .append("deletedOnce", deletedOnce)
            .append("modCount", modCount)
            .append("cModCount", cModCount)
            .append("dSize", dSize)
            .append("version", version)
            .append("sdType", sdType)
            .append("sdMaxRevTime", sdMaxRevTime)
            .append("data", new String(data))
            .append("bdata", new String(bdata))
            .toString();
    }
}
