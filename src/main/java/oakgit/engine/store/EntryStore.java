package oakgit.engine.store;

import java.util.List;
import java.util.Optional;

public interface EntryStore {

  void createContainer(String name);

  boolean hasContainer(String name);

  boolean containsKey(String container, String id);

  void put(String container, StorageDocument doc);

  Optional<StorageDocument> get(String container, String id);

  List<StorageDocument> getAll(String container);

  boolean remove(String container, String id);

  void removeAll(String container, List<String> ids);

  default List<StorageDocument> findByIdRange(
      String container, String idMin, String idMax, int limit) {
    return getAll(container).stream()
        .filter(d -> d.getId().compareTo(idMin) >= 0 && d.getId().compareTo(idMax) <= 0)
        .limit(limit)
        .toList();
  }

  default List<StorageDocument> findByIds(String container, List<String> ids) {
    return ids.stream().map(id -> get(container, id)).flatMap(Optional::stream).toList();
  }

  default List<StorageDocument> findByIdRangeAndModified(
      String container, String idMin, String idMax, long minModified, int limit) {
    return getAll(container).stream()
        .filter(d -> d.getId().compareTo(idMin) > 0 && d.getId().compareTo(idMax) < 0)
        .filter(d -> d.getModified() != null && d.getModified() >= minModified)
        .limit(limit)
        .toList();
  }

  default List<StorageDocument> findByDeletedOnceAndModifiedRange(
      String container, int deletedOnce, long lowerBound, long upperBound) {
    return getAll(container).stream()
        .filter(d -> d.getDeletedOnce() != null && d.getDeletedOnce() == deletedOnce)
        .filter(
            d ->
                d.getModified() != null
                    && d.getModified() >= lowerBound
                    && d.getModified() < upperBound)
        .toList();
  }

  default List<StorageDocument> findBySdtypeAndVersion(
      String container, List<Integer> sdTypes, long sdMaxRevTime, int minVersion) {
    return getAll(container).stream()
        .filter(d -> d.getSdType() != null && sdTypes.contains(d.getSdType()))
        .filter(d -> d.getSdMaxRevTime() != null && d.getSdMaxRevTime() <= sdMaxRevTime)
        .filter(d -> d.getVersion() != null && d.getVersion() >= minVersion)
        .toList();
  }

  default List<StorageDocument> findByVersionUpgrade(
      String container, List<String> excludedIdPatterns, int maxVersion) {
    return getAll(container).stream()
        .filter(d -> d.getVersion() == null || d.getVersion() < maxVersion)
        .filter(d -> excludedIdPatterns.stream().noneMatch(p -> sqlLikeMatch(d.getId(), p)))
        .toList();
  }

  default List<StorageDocument> findByLastmodLessThan(String container, long lastmod) {
    return getAll(container).stream()
        .filter(d -> d.getLastmod() != null && d.getLastmod() < lastmod)
        .toList();
  }

  private static boolean sqlLikeMatch(String value, String likePattern) {
    String regex = likePattern.replace("%", ".*").replace("_", ".");
    return value.matches(regex);
  }
}
