package oakgit.engine.model;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import oakgit.jdbc.OakGitResultSet;
import oakgit.jdbc.util.SqlType;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Used for CLUSTERNODES, JOURNAL, NODES and SETTINGS
 */
@Getter
@Setter
public class DocumentEntry implements ContainerEntry<DocumentEntry>, ModCountSupport {

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
  public DocumentEntry copy() {
    byte[] dataCopy = null;
    if (data != null) {
      dataCopy = new byte[data.length];
      System.arraycopy(data, 0, dataCopy, 0, data.length);
    }
    byte[] bDataCopy = null;
    if (bdata != null) {
      bDataCopy = new byte[bdata.length];
      System.arraycopy(bdata, 0, bDataCopy, 0, bdata.length);
    }

    return new DocumentEntry()
        .setId(id)
        .setModified(modified)
        .setHasBinary(hasBinary)
        .setDeletedOnce(deletedOnce)
        .setModCount(modCount)
        .setCModCount(cModCount)
        .setDSize(dSize)
        .setVersion(version)
        .setSdType(sdType)
        .setSdMaxRevTime(sdMaxRevTime)
        .setData(dataCopy)
        .setBdata(bDataCopy);
  }

  @Override
  public Map<String, OakGitResultSet.Column> getAvailableColumnsByName() {
    Map<String, OakGitResultSet.Column> result = new LinkedHashMap<>();
    result.put("ID", new OakGitResultSet.Column("ID", SqlType.VARCHAR.id, 512, Collections.emptyList()));
    result.put("MODIFIED", new OakGitResultSet.Column("MODIFIED", SqlType.BIGINT.id, 0, Collections.emptyList()));
    result.put("HASBINARY", new OakGitResultSet.Column("HASBINARY", SqlType.SMALLINT.id, 0, Collections.emptyList()));
    result.put("DELETEDONCE", new OakGitResultSet.Column("DELETEDONCE", SqlType.SMALLINT.id, 0, Collections.emptyList()));
    result.put("CMODCOUNT", new OakGitResultSet.Column("CMODCOUNT", SqlType.BIGINT.id, 0, Collections.emptyList()));
    result.put("MODCOUNT", new OakGitResultSet.Column("MODCOUNT", SqlType.BIGINT.id, 0, Collections.emptyList()));
    result.put("DSIZE", new OakGitResultSet.Column("DSIZE", SqlType.BIGINT.id, 0, Collections.emptyList()));
    result.put("VERSION", new OakGitResultSet.Column("VERSION", SqlType.SMALLINT.id, 0, Collections.emptyList()));
    result.put("SDTYPE", new OakGitResultSet.Column("SDTYPE", SqlType.SMALLINT.id, 0, Collections.emptyList()));
    result.put("SDMAXREVTIME", new OakGitResultSet.Column("SDMAXREVTIME", SqlType.BIGINT.id, 0, Collections.emptyList()));
    result.put("DATA", new OakGitResultSet.Column("DATA", SqlType.VARCHAR.id, 16384, Collections.emptyList()));
    result.put("BDATA", new OakGitResultSet.Column("BDATA", SqlType.BLOB.id, 1073741824, Collections.emptyList()));
    return result;
  }

  @Override
  public Optional<String> typeGetter(String fieldName) {
    String fieldNameToCheck = fieldName;
    Matcher caseMatcher = CASE_PATTERN.matcher(fieldNameToCheck);
    if (caseMatcher.matches()) {
      fieldNameToCheck = caseMatcher.group(3);
    }
    if (getAvailableColumnsByName().containsKey(fieldNameToCheck)) {
      return Optional.of(fieldNameToCheck);
    }
    return Optional.empty();
  }

  @Override
  public Optional<ColumnGetterResult> entryGetter(String fieldName) {
    Matcher caseMatcher = CASE_PATTERN.matcher(fieldName);
    boolean isCaseField = false;
    long modCountCheck = 0L;
    long modifiedCheck = 0L;
    if (caseMatcher.matches()) {
      modCountCheck = Long.parseLong(caseMatcher.group(1));
      modifiedCheck = Long.parseLong(caseMatcher.group(2));
      fieldName = caseMatcher.group(3);
      isCaseField = true;
    }

    DocumentEntry accessor = this.copy();
    switch (fieldName) {
      case "ID":
        return Optional.of(new ColumnGetterResult(fieldName, accessor.getId()));
      case "MODIFIED":
        return Optional.of(new ColumnGetterResult(fieldName, accessor.getModified()));
      case "MODCOUNT":
        return Optional.of(new ColumnGetterResult(fieldName, accessor.getModCount()));
      case "CMODCOUNT":
        return Optional.of(new ColumnGetterResult(fieldName, accessor.getCModCount()));
      case "HASBINARY":
        return Optional.of(new ColumnGetterResult(fieldName, accessor.getHasBinary()));
      case "DELETEDONCE":
        return Optional.of(new ColumnGetterResult(fieldName, accessor.getDeletedOnce()));
      case "VERSION":
        return Optional.of(new ColumnGetterResult(fieldName, accessor.getVersion()));
      case "SDTYPE":
        return Optional.of(new ColumnGetterResult(fieldName, accessor.getSdType()));
      case "SDMAXREVTIME":
        return Optional.of(new ColumnGetterResult(fieldName, accessor.getSdMaxRevTime()));
      case "DSIZE":
        return Optional.of(new ColumnGetterResult(fieldName, accessor.getDSize()));
      case "DATA": {
        if (isCaseField && modCount == modCountCheck && modified == modifiedCheck) {
          return Optional.of(new ColumnGetterResult(fieldName, null));
        }
        return Optional.of(new ColumnGetterResult(fieldName, accessor.getData()));
      }
      case "BDATA": {
        if (isCaseField && modCount == modCountCheck && modified == modifiedCheck) {
          return Optional.of(new ColumnGetterResult(fieldName, null));
        }
        return Optional.of(new ColumnGetterResult(fieldName, accessor.getBdata()));
      }
    }

    return Optional.empty();
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
