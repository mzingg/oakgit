package oakgit.engine.store;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

@Getter
@Setter
public class StorageDocument {

  @NonNull private String id;

  private Long modified;
  private Long modCount;
  private Long cModCount;
  private Long dSize;
  private Long sdMaxRevTime;
  private Long lastmod;
  private Integer hasBinary;
  private Integer deletedOnce;
  private Integer version;
  private Integer sdType;
  private Integer lvl;

  private byte[] data;
  private byte[] bdata;
}
