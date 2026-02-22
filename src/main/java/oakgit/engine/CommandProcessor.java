package oakgit.engine;

import static oakgit.engine.CommandResult.EMPTY_QUERY_RESULT;
import static oakgit.engine.CommandResult.NO_RESULT;
import static oakgit.engine.CommandResult.SUCCESSFULL_RESULT_WITHOUT_DATA;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import oakgit.engine.commands.*;
import oakgit.engine.commands.SelectCountByDeletedOnceCommand;
import oakgit.engine.model.ContainerEntry;
import oakgit.engine.model.DatastoreMetaEntry;
import oakgit.engine.model.DocumentEntry;
import oakgit.engine.store.DocumentMapper;
import oakgit.engine.store.EntryStore;
import oakgit.engine.store.StorageDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class CommandProcessor implements AutoCloseable {

  private static final Logger LOG = LoggerFactory.getLogger(CommandProcessor.class);

  private final EntryStore store;
  private final ReadWriteLock lock = new ReentrantReadWriteLock();

  public CommandProcessor(EntryStore store) {
    this.store = store;
  }

  @Override
  public void close() throws Exception {
    store.close();
  }

  public void discardAndClose() throws Exception {
    store.discardAndClose();
  }

  public CommandResult execute(Command command) {

    if (command instanceof ErrorCommand errorCommand) {
      LOG.error("Unrecognized SQL: {}", errorCommand.getErrorMessage());
      return NO_RESULT;
    }

    if (command instanceof CreateIndexCommand) {
      return SUCCESSFULL_RESULT_WITHOUT_DATA;
    }

    if (command instanceof SelectCountByDeletedOnceCommand cmd) {
      lock.readLock().lock();
      try {
        String container = cmd.getTableName().toUpperCase();
        if (store.hasContainer(container)) {
          long count = store.countByDeletedOnce(container, cmd.getDeletedOnce());
          return cmd.buildResult(count);
        }
        return cmd.buildResult(0);
      } finally {
        lock.readLock().unlock();
      }
    }

    if (command instanceof SelectMinModifiedCommand) {
      return EMPTY_QUERY_RESULT;
    }

    if (command instanceof SelectBySdtypeCommand cmd) {
      lock.readLock().lock();
      try {
        String container = cmd.getContainerName().toUpperCase();
        if (store.hasContainer(container)) {
          List<StorageDocument> docs =
              store.findBySdtypeAndVersion(
                  container, cmd.getSdTypes(), cmd.getSdMaxRevTime(), cmd.getMinVersion());
          List<DocumentEntry> entries =
              docs.stream().map(d -> DocumentMapper.toEntry(d, DocumentEntry.class)).toList();
          return cmd.buildResult(entries);
        }
        return cmd.buildResult(List.of());
      } finally {
        lock.readLock().unlock();
      }
    }

    if (command instanceof SelectByVersionUpgradeCommand cmd) {
      lock.readLock().lock();
      try {
        String container = cmd.getContainerName().toUpperCase();
        if (store.hasContainer(container)) {
          List<StorageDocument> docs =
              store.findByVersionUpgrade(
                  container, cmd.getExcludedIdPatterns(), cmd.getMaxVersion());
          List<DocumentEntry> entries =
              docs.stream().map(d -> DocumentMapper.toEntry(d, DocumentEntry.class)).toList();
          return cmd.buildResult(entries);
        }
        return cmd.buildResult(List.of());
      } finally {
        lock.readLock().unlock();
      }
    }

    if (command instanceof SelectByModifiedAndSdtypeNullCommand cmd) {
      lock.readLock().lock();
      try {
        String container = cmd.getContainerName().toUpperCase();
        if (store.hasContainer(container)) {
          List<StorageDocument> docs =
              store.findByModifiedAndSdtypeNull(container, cmd.getMinModified());
          List<DocumentEntry> entries =
              docs.stream().map(d -> DocumentMapper.toEntry(d, DocumentEntry.class)).toList();
          return cmd.buildResult(entries);
        }
        return cmd.buildResult(List.of());
      } finally {
        lock.readLock().unlock();
      }
    }

    if (command instanceof SelectByDeletedOnceAndModifiedRangeCommand cmd) {
      lock.readLock().lock();
      try {
        String container = cmd.getContainerName().toUpperCase();
        if (!store.hasContainer(container)) {
          throw new IllegalStateException();
        }
        List<StorageDocument> docs =
            store.findByDeletedOnceAndModifiedRange(
                container,
                cmd.getDeletedOnce(),
                cmd.getModifiedLowerBound(),
                cmd.getModifiedUpperBound());
        List<DocumentEntry> entries =
            docs.stream().map(d -> DocumentMapper.toEntry(d, DocumentEntry.class)).toList();
        return cmd.buildResult(entries);
      } finally {
        lock.readLock().unlock();
      }
    }

    if (command instanceof DeleteByModifiedRangeCommand) {
      return SUCCESSFULL_RESULT_WITHOUT_DATA;
    }

    if (command instanceof DeleteByIdCommand<?> cmd) {
      lock.writeLock().lock();
      try {
        String container = cmd.getContainerName().toUpperCase();
        if (store.hasContainer(container)) {
          store.remove(container, cmd.getId());
        }
        return SUCCESSFULL_RESULT_WITHOUT_DATA;
      } finally {
        lock.writeLock().unlock();
      }
    }

    if (command instanceof DeleteByIdAndModifiedCommand<?> cmd) {
      lock.writeLock().lock();
      try {
        String container = cmd.getContainerName().toUpperCase();
        if (store.hasContainer(container)) {
          store
              .get(container, cmd.getId())
              .ifPresent(
                  doc -> {
                    if (doc.modified() != null && doc.modified() == cmd.getModified()) {
                      store.remove(container, cmd.getId());
                    }
                  });
        }
        return SUCCESSFULL_RESULT_WITHOUT_DATA;
      } finally {
        lock.writeLock().unlock();
      }
    }

    if (command instanceof DeleteByIdListCommand<?> cmd) {
      lock.writeLock().lock();
      try {
        String container = cmd.getContainerName().toUpperCase();
        if (store.hasContainer(container)) {
          store.removeAll(container, cmd.getIds());
        }
        return SUCCESSFULL_RESULT_WITHOUT_DATA;
      } finally {
        lock.writeLock().unlock();
      }
    }

    if (command instanceof UpdateDatastoreMetaLastmodCommand cmd) {
      lock.writeLock().lock();
      try {
        String container = cmd.getContainerName().toUpperCase();
        if (store.hasContainer(container)) {
          store
              .get(container, cmd.getId())
              .ifPresent(
                  doc -> {
                    Long threshold = cmd.getLastmodThreshold();
                    if (threshold == null || (doc.lastmod() != null && doc.lastmod() < threshold)) {
                      store.put(container, doc.withLastmod(cmd.getLastmod()));
                    }
                  });
        }
        return SUCCESSFULL_RESULT_WITHOUT_DATA;
      } finally {
        lock.writeLock().unlock();
      }
    }

    if (command instanceof SelectDatastoreMetaByLastmodCommand cmd) {
      lock.readLock().lock();
      try {
        String container = cmd.getContainerName().toUpperCase();
        if (store.hasContainer(container)) {
          List<StorageDocument> docs = store.findByLastmodLessThan(container, cmd.getLastmod());
          List<DatastoreMetaEntry> entries =
              docs.stream().map(d -> DocumentMapper.toEntry(d, DatastoreMetaEntry.class)).toList();
          return cmd.buildResult(entries);
        }
        return cmd.buildResult(List.of());
      } finally {
        lock.readLock().unlock();
      }
    }

    if (command instanceof SelectByRangeAndModifiedCommand<?> cmd) {
      lock.readLock().lock();
      try {
        String container = cmd.getContainerName().toUpperCase();
        if (store.hasContainer(container)) {
          List<StorageDocument> docs =
              store.findByIdRangeAndModified(
                  container, cmd.getIdMin(), cmd.getIdMax(), cmd.getMinModified(), cmd.getLimit());
          List<?> entries =
              docs.stream().map(d -> DocumentMapper.toEntry(d, cmd.getEntryType())).toList();
          return cmd.buildResult(entries);
        }
        return cmd.buildResult(List.of());
      } finally {
        lock.readLock().unlock();
      }
    }

    if (!(command instanceof ContainerCommand<?>)) {
      return NO_RESULT;
    }

    ContainerCommand<?> containerCommand = (ContainerCommand<?>) command;
    String containerName = containerCommand.getContainerName().toUpperCase();

    if (containerCommand instanceof CreateContainerCommand) {
      lock.writeLock().lock();
      try {
        store.createContainer(containerName);
        return SUCCESSFULL_RESULT_WITHOUT_DATA;
      } finally {
        lock.writeLock().unlock();
      }
    }

    if (containerCommand instanceof InsertIntoContainerCommand<?> cmd) {
      lock.writeLock().lock();
      try {
        if (!store.hasContainer(containerName)) {
          throw new IllegalStateException();
        }
        String entryId = cmd.getData().getId();
        if (store.containsKey(containerName, entryId)) {
          throw new IllegalStateException("Duplicate key: " + entryId);
        }
        store.put(containerName, DocumentMapper.fromEntry(cmd.getData()));
        return SUCCESSFULL_RESULT_WITHOUT_DATA;
      } finally {
        lock.writeLock().unlock();
      }
    }

    if (containerCommand instanceof SelectFromContainerByIdCommand<?> cmd) {
      lock.readLock().lock();
      try {
        if (!store.hasContainer(containerName)) {
          throw new IllegalStateException();
        }
        ContainerEntry<?> foundEntry =
            store
                .get(containerName, cmd.getId())
                .map(doc -> DocumentMapper.toEntry(doc, cmd.getEntryType()))
                .orElse(null);
        return cmd.buildResult(foundEntry);
      } finally {
        lock.readLock().unlock();
      }
    }

    if (containerCommand instanceof SelectFromContainerByIdRangeCommand<?> cmd) {
      lock.readLock().lock();
      try {
        if (!store.hasContainer(containerName)) {
          throw new IllegalStateException();
        }
        List<StorageDocument> docs =
            store.findByIdRange(containerName, cmd.getIdMin(), cmd.getIdMax(), cmd.getLimit());
        List<?> entries =
            docs.stream().map(d -> DocumentMapper.toEntry(d, cmd.getEntryType())).toList();
        return cmd.buildResult(entries);
      } finally {
        lock.readLock().unlock();
      }
    }

    if (containerCommand instanceof SelectFromContainerByMultipleIdsCommand<?> cmd) {
      lock.readLock().lock();
      try {
        if (!store.hasContainer(containerName)) {
          throw new IllegalStateException();
        }
        List<StorageDocument> docs = store.findByIds(containerName, cmd.getIds());
        List<?> entries =
            docs.stream().map(d -> DocumentMapper.toEntry(d, cmd.getEntryType())).toList();
        return cmd.buildResult(entries);
      } finally {
        lock.readLock().unlock();
      }
    }

    if (containerCommand instanceof UpdateDocumentDataInContainerCommand cmd) {
      lock.writeLock().lock();
      try {
        if (!store.hasContainer(containerName)) {
          throw new IllegalStateException();
        }
        Optional<StorageDocument> existing = store.get(containerName, cmd.getId());
        if (existing.isPresent()) {
          StorageDocument doc = existing.get();
          if (doc.modCount() != null && doc.modCount() == cmd.getModCount()) {
            DocumentEntry entity = DocumentMapper.toEntry(doc, DocumentEntry.class);
            cmd.getData().update(entity);
            store.put(containerName, DocumentMapper.fromEntry(entity));
            return cmd.buildResult(entity);
          }
        }
      } finally {
        lock.writeLock().unlock();
      }
    }

    return NO_RESULT;
  }
}
