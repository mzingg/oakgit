package oakgit.engine.store;

import oakgit.engine.model.ContainerEntry;
import oakgit.engine.model.DatastoreDataEntry;
import oakgit.engine.model.DatastoreMetaEntry;
import oakgit.engine.model.DocumentEntry;

public final class DocumentMapper {

  private DocumentMapper() {}

  public static StorageDocument fromEntry(ContainerEntry<?> entry) {
    return switch (entry) {
      case DocumentEntry e ->
          new StorageDocument()
              .setId(e.getId())
              .setModified(e.getModified())
              .setModCount(e.getModCount())
              .setCModCount(e.getCModCount())
              .setDSize(e.getDSize())
              .setSdMaxRevTime(e.getSdMaxRevTime())
              .setHasBinary(e.getHasBinary())
              .setDeletedOnce(e.getDeletedOnce())
              .setVersion(e.getVersion())
              .setSdType(e.getSdType())
              .setData(cloneBytes(e.getData()))
              .setBdata(cloneBytes(e.getBdata()));
      case DatastoreDataEntry e ->
          new StorageDocument().setId(e.getId()).setData(cloneBytes(e.getData()));
      case DatastoreMetaEntry e ->
          new StorageDocument().setId(e.getId()).setLastmod(e.getLastmod()).setLvl(e.getLvl());
      default ->
          throw new IllegalArgumentException("Unknown entry type: " + entry.getClass().getName());
    };
  }

  @SuppressWarnings("unchecked")
  public static <T extends ContainerEntry<T>> T toEntry(StorageDocument doc, Class<T> type) {
    if (type == DocumentEntry.class) {
      return (T)
          new DocumentEntry()
              .setId(doc.getId())
              .setModified(doc.getModified())
              .setModCount(doc.getModCount())
              .setCModCount(doc.getCModCount())
              .setDSize(doc.getDSize())
              .setSdMaxRevTime(doc.getSdMaxRevTime())
              .setHasBinary(doc.getHasBinary())
              .setDeletedOnce(doc.getDeletedOnce())
              .setVersion(doc.getVersion())
              .setSdType(doc.getSdType())
              .setData(cloneBytes(doc.getData()))
              .setBdata(cloneBytes(doc.getBdata()));
    }
    if (type == DatastoreDataEntry.class) {
      return (T) new DatastoreDataEntry().setId(doc.getId()).setData(cloneBytes(doc.getData()));
    }
    if (type == DatastoreMetaEntry.class) {
      return (T)
          new DatastoreMetaEntry()
              .setId(doc.getId())
              .setLastmod(doc.getLastmod())
              .setLvl(doc.getLvl());
    }
    throw new IllegalArgumentException("Unknown entry type: " + type.getName());
  }

  public static StorageDocument deepCopy(StorageDocument doc) {
    return new StorageDocument()
        .setId(doc.getId())
        .setModified(doc.getModified())
        .setModCount(doc.getModCount())
        .setCModCount(doc.getCModCount())
        .setDSize(doc.getDSize())
        .setSdMaxRevTime(doc.getSdMaxRevTime())
        .setLastmod(doc.getLastmod())
        .setHasBinary(doc.getHasBinary())
        .setDeletedOnce(doc.getDeletedOnce())
        .setVersion(doc.getVersion())
        .setSdType(doc.getSdType())
        .setLvl(doc.getLvl())
        .setData(cloneBytes(doc.getData()))
        .setBdata(cloneBytes(doc.getBdata()));
  }

  private static byte[] cloneBytes(byte[] source) {
    if (source == null) {
      return null;
    }
    var copy = new byte[source.length];
    System.arraycopy(source, 0, copy, 0, source.length);
    return copy;
  }
}
