package oakgit.engine.store;

import lombok.Builder;
import lombok.NonNull;

@Builder
public record StorageDocument(
    @NonNull String id,
    Long modified,
    Long modCount,
    Long cModCount,
    Long dSize,
    Long sdMaxRevTime,
    Long lastmod,
    Integer hasBinary,
    Integer deletedOnce,
    Integer version,
    Integer sdType,
    Integer lvl,
    byte[] data,
    byte[] bdata) {

  public StorageDocument withLastmod(Long lastmod) {
    return new StorageDocument(
        id,
        modified,
        modCount,
        cModCount,
        dSize,
        sdMaxRevTime,
        lastmod,
        hasBinary,
        deletedOnce,
        version,
        sdType,
        lvl,
        data,
        bdata);
  }
}
