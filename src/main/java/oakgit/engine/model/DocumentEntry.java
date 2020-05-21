package oakgit.engine.model;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import oakgit.jdbc.OakGitResultSet;
import oakgit.jdbc.util.SqlType;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Used for CLUSTERNODES, JOURNAL, NODES and SETTINGS
 */
@Getter
@Setter
public class DocumentEntry implements ContainerEntry<DocumentEntry> {

    private static final Pattern CASE_PATTERN = Pattern.compile("case when \\(MODCOUNT = (\\d+) and MODIFIED = (\\d+)\\) then null else (BDATA|DATA) end as (?:BDATA|DATA)");
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
    public Consumer<OakGitResultSet> getResultSetModifier(List<String> fieldList) {
        return result -> fillResultSet(result, fieldList, this::columnGetter);
    }

    private ColumnGetterResult columnGetter(String fieldName) {
        Matcher caseMatcher = CASE_PATTERN.matcher(fieldName);
        boolean isCaseField = false;
        Long modCountCheck = 0L;
        Long modifiedCheck = 0L;
        if (caseMatcher.matches()) {
            modCountCheck = Long.valueOf(caseMatcher.group(1));
            modifiedCheck = Long.valueOf(caseMatcher.group(2));
            fieldName = caseMatcher.group(3);
            isCaseField = true;
        }

        switch (fieldName) {
            case "ID":
                return new ColumnGetterResult(fieldName, id);
            case "MODIFIED":
                return new ColumnGetterResult(fieldName, modified);
            case "MODCOUNT":
                return new ColumnGetterResult(fieldName, modCount);
            case "CMODCOUNT":
                return new ColumnGetterResult(fieldName, cModCount);
            case "HASBINARY":
                return new ColumnGetterResult(fieldName, hasBinary);
            case "DELETEDONCE":
                return new ColumnGetterResult(fieldName, deletedOnce);
            case "VERSION":
                return new ColumnGetterResult(fieldName, version);
            case "SDTYPE":
                return new ColumnGetterResult(fieldName, sdType);
            case "SDMAXREVTIME":
                return new ColumnGetterResult(fieldName, sdMaxRevTime);
            case "DATA": {
                if (isCaseField && Objects.equals(modCount, modCountCheck) && Objects.equals(modified, modifiedCheck)) {
                    return new ColumnGetterResult(fieldName, null);
                }
                return new ColumnGetterResult(fieldName, data);
            }
            case "BDATA": {
                if (isCaseField && Objects.equals(modCount, modCountCheck) && Objects.equals(modified, modifiedCheck)) {
                    return new ColumnGetterResult(fieldName, null);
                }
                return new ColumnGetterResult(fieldName, bdata);
            }
        }
        return null;
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
