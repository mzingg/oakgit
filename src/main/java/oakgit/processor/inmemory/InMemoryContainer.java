package oakgit.processor.inmemory;

import java.util.*;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import oakgit.engine.model.ContainerEntry;
import oakgit.engine.model.DatastoreMetaEntry;
import oakgit.engine.model.DocumentEntry;
import oakgit.engine.model.ModCountSupport;

@RequiredArgsConstructor
public class InMemoryContainer {

  @NonNull @Getter private final String name;

  private final Map<String, ContainerEntry<?>> entries = new HashMap<>();

  public boolean containsEntry(String id) {
    return entries.containsKey(id);
  }

  public boolean removeEntry(String id) {
    return entries.remove(id) != null;
  }

  public <T extends ContainerEntry<T>> InMemoryContainer setEntry(
      @NonNull ContainerEntry<T> entry) {
    entries.put(entry.getId(), entry);
    return this;
  }

  @SuppressWarnings("unchecked")
  public <T extends ContainerEntry<T>> Optional<T> findById(String id, Class<T> resultType) {
    if (entries.containsKey(id)) {
      ContainerEntry<?> entry = entries.get(id);
      if (resultType.isAssignableFrom(entry.getClass())) {
        return Optional.of((T) entry.copy());
      }
    }
    return Optional.empty();
  }

  @SuppressWarnings("unchecked")
  public <T extends ContainerEntry<T>> Optional<T> findByIdAndModCount(
      String id, long modCount, Class<T> resultType) {
    if (entries.containsKey(id)) {
      ContainerEntry<?> entry = entries.get(id);
      if (resultType.isAssignableFrom(entry.getClass())) {
        if (entry instanceof ModCountSupport) {
          if (((ModCountSupport) entry).getModCount() == modCount) {
            return Optional.of((T) entry.copy());
          }
        } else {
          return Optional.of((T) entry.copy());
        }
      }
    }
    return Optional.empty();
  }

  @SuppressWarnings("unchecked")
  public <T extends ContainerEntry<T>> List<T> findByIdRange(
      String idMin, String idMax, Class<T> resultType, int limit) {
    ArrayList<T> result = new ArrayList<>();

    int count = 0;
    for (String key : entries.keySet()) {
      if (count > limit) {
        break;
      }
      ContainerEntry<?> containerEntry = entries.get(key);
      if (resultType.isAssignableFrom(containerEntry.getClass())) {
        String entryId = containerEntry.getId();
        if (entryId.compareTo(idMin) >= 0 && entryId.compareTo(idMax) <= 0) {
          result.add((T) containerEntry.copy());
          count++;
        }
      }
    }

    return result;
  }

  @SuppressWarnings("unchecked")
  public <T extends ContainerEntry<T>> List<T> findByIds(List<String> ids, Class<T> resultType) {
    ArrayList<T> result = new ArrayList<>();

    for (String id : ids) {
      if (entries.containsKey(id)) {
        ContainerEntry<?> containerEntry = entries.get(id);
        if (resultType.isAssignableFrom(containerEntry.getClass())) {
          result.add((T) containerEntry.copy());
        }
      }
    }

    return result;
  }

  public List<DatastoreMetaEntry> findByLastmodLessThan(long lastmod) {
    ArrayList<DatastoreMetaEntry> result = new ArrayList<>();

    for (ContainerEntry<?> entry : entries.values()) {
      if (entry instanceof DatastoreMetaEntry meta
          && meta.getLastmod() != null
          && meta.getLastmod() < lastmod) {
        result.add(meta.copy());
      }
    }

    return result;
  }

  public List<DocumentEntry> findByDeletedOnceAndModifiedRange(
      int deletedOnce, long modifiedLowerBound, long modifiedUpperBound) {
    return entries.values().stream()
        .filter(DocumentEntry.class::isInstance)
        .map(DocumentEntry.class::cast)
        .filter(e -> e.getDeletedOnce() != null && e.getDeletedOnce() == deletedOnce)
        .filter(
            e ->
                e.getModified() != null
                    && e.getModified() >= modifiedLowerBound
                    && e.getModified() < modifiedUpperBound)
        .map(DocumentEntry::copy)
        .toList();
  }

  public List<DocumentEntry> findByVersionUpgrade(List<String> excludedIdPatterns, int maxVersion) {
    return entries.values().stream()
        .filter(DocumentEntry.class::isInstance)
        .map(DocumentEntry.class::cast)
        .filter(e -> e.getVersion() == null || e.getVersion() < maxVersion)
        .filter(
            e -> excludedIdPatterns.stream().noneMatch(pattern -> sqlLikeMatch(e.getId(), pattern)))
        .map(DocumentEntry::copy)
        .toList();
  }

  private static boolean sqlLikeMatch(String value, String likePattern) {
    String regex = likePattern.replace("%", ".*").replace("_", ".");
    return value.matches(regex);
  }

  public List<DocumentEntry> findBySdtypeAndSdMaxRevTimeAndVersion(
      List<Integer> sdTypes, long sdMaxRevTime, int minVersion) {
    return entries.values().stream()
        .filter(DocumentEntry.class::isInstance)
        .map(DocumentEntry.class::cast)
        .filter(e -> e.getSdType() != null && sdTypes.contains(e.getSdType()))
        .filter(e -> e.getSdMaxRevTime() != null && e.getSdMaxRevTime() <= sdMaxRevTime)
        .filter(e -> e.getVersion() != null && e.getVersion() >= minVersion)
        .map(DocumentEntry::copy)
        .toList();
  }

  @SuppressWarnings("unchecked")
  public <T extends ContainerEntry<T>> List<T> findByIdRangeAndModified(
      String idMin, String idMax, long minModified, Class<T> resultType, int limit) {
    ArrayList<T> result = new ArrayList<>();

    int count = 0;
    for (ContainerEntry<?> containerEntry : entries.values()) {
      if (count >= limit) {
        break;
      }
      if (!resultType.isAssignableFrom(containerEntry.getClass())) {
        continue;
      }
      String entryId = containerEntry.getId();
      if (entryId.compareTo(idMin) > 0 && entryId.compareTo(idMax) < 0) {
        if (containerEntry instanceof DocumentEntry doc
            && doc.getModified() != null
            && doc.getModified() >= minModified) {
          result.add((T) containerEntry.copy());
          count++;
        }
      }
    }

    return result;
  }
}
