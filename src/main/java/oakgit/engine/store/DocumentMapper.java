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
          StorageDocument.builder()
              .id(e.getId())
              .modified(e.getModified())
              .modCount(e.getModCount())
              .cModCount(e.getCModCount())
              .dSize(e.getDSize())
              .sdMaxRevTime(e.getSdMaxRevTime())
              .hasBinary(e.getHasBinary())
              .deletedOnce(e.getDeletedOnce())
              .version(e.getVersion())
              .sdType(e.getSdType())
              .data(cloneBytes(e.getData()))
              .bdata(cloneBytes(e.getBdata()))
              .build();
      case DatastoreDataEntry e ->
          StorageDocument.builder().id(e.getId()).data(cloneBytes(e.getData())).build();
      case DatastoreMetaEntry e ->
          StorageDocument.builder().id(e.getId()).lastmod(e.getLastmod()).lvl(e.getLvl()).build();
      default ->
          throw new IllegalArgumentException("Unknown entry type: " + entry.getClass().getName());
    };
  }

  @SuppressWarnings("unchecked")
  public static <T extends ContainerEntry<T>> T toEntry(StorageDocument doc, Class<T> type) {
    // No byte[] cloning on read path — StorageDocument is an immutable record,
    // and OAK deserializes bytes into its own objects without mutating the source.
    if (type == DocumentEntry.class) {
      return (T)
          new DocumentEntry()
              .setId(doc.id())
              .setModified(doc.modified())
              .setModCount(doc.modCount())
              .setCModCount(doc.cModCount())
              .setDSize(doc.dSize())
              .setSdMaxRevTime(doc.sdMaxRevTime())
              .setHasBinary(doc.hasBinary())
              .setDeletedOnce(doc.deletedOnce())
              .setVersion(doc.version())
              .setSdType(doc.sdType())
              .setData(doc.data())
              .setBdata(doc.bdata());
    }
    if (type == DatastoreDataEntry.class) {
      return (T) new DatastoreDataEntry().setId(doc.id()).setData(doc.data());
    }
    if (type == DatastoreMetaEntry.class) {
      return (T)
          new DatastoreMetaEntry().setId(doc.id()).setLastmod(doc.lastmod()).setLvl(doc.lvl());
    }
    throw new IllegalArgumentException("Unknown entry type: " + type.getName());
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
