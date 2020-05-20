package oakgit.engine.model;

import oakgit.jdbc.OakGitResultSet;
import oakgit.jdbc.util.SqlType;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.List;
import java.util.function.Consumer;

/**
 * Used for CLUSTERNODES, JOURNAL, NODES and SETTINGS
 */
@Getter
@Setter
public class DocumentEntry implements ContainerEntry<DocumentEntry> {

    @NonNull
    private String id;

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
            result.addColumn("ID", SqlType.VARCHAR.id, 512);
            result.addColumn("MODIFIED", SqlType.BIGINT.id, 0);
            result.addColumn("HASBINARY", SqlType.SMALLINT.id, 0);
            result.addColumn("DELETEDONCE", SqlType.SMALLINT.id, 0);
            result.addColumn("MODCOUNT", SqlType.BIGINT.id, 0);
            result.addColumn("CMODCOUNT", SqlType.BIGINT.id, 0);
            result.addColumn("DSIZE", SqlType.BIGINT.id, 0);
            result.addColumn("VERSION", SqlType.SMALLINT.id, 0);
            result.addColumn("SDTYPE", SqlType.SMALLINT.id, 0);
            result.addColumn("SDMAXREVTIME", SqlType.BIGINT.id, 0);
            result.addColumn("DATA", SqlType.VARCHAR.id, 16384);
            result.addColumn("BDATA", SqlType.BLOB.id, 1073741824);
        };
    }

    @Override
    public Consumer<OakGitResultSet> getResultSetModifier(List<String> exclude) {
        return result -> {
            result.addValue("ID", id);
            result.addValue("MODIFIED", modified);
            result.addValue("MODCOUNT", modCount);
            result.addValue("CMODCOUNT", cModCount);
            result.addValue("HASBINARY", hasBinary);
            result.addValue("DELETEDONCE", deletedOnce);
            result.addValue("VERSION", version);
            result.addValue("SDTYPE", sdType);
            result.addValue("SDMAXREVTIME", sdMaxRevTime);
            result.addValue("DATA", data);
            result.addValue("BDATA", bdata);
        };
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SIMPLE_STYLE)
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
            .append("data", data != null ? new String(data) : "null")
            .append("bdata", bdata != null ? new String(bdata) : "null")
            .toString();
    }
}
