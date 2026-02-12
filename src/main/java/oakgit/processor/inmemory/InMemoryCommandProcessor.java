package oakgit.processor.inmemory;

import static oakgit.engine.CommandResult.EMPTY_QUERY_RESULT;
import static oakgit.engine.CommandResult.NO_RESULT;
import static oakgit.engine.CommandResult.SUCCESSFULL_RESULT_WITHOUT_DATA;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import oakgit.engine.Command;
import oakgit.engine.CommandProcessor;
import oakgit.engine.CommandResult;
import oakgit.engine.ContainerCommand;
import oakgit.engine.commands.*;
import oakgit.engine.model.ContainerEntry;
import oakgit.engine.model.DatastoreMetaEntry;
import oakgit.engine.model.DocumentEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class InMemoryCommandProcessor implements CommandProcessor {

  private static final Logger LOG = LoggerFactory.getLogger(InMemoryCommandProcessor.class);

  private final Map<String, InMemoryContainer> containerMap = new HashMap<>();

  private ReadWriteLock lock = new ReentrantReadWriteLock();

  @Override
  public CommandResult execute(Command command) {

    if (command instanceof ErrorCommand errorCommand) {
      LOG.warn("Unrecognized SQL: {}", errorCommand.getErrorMessage());
      return NO_RESULT;
    }

    if (command instanceof CreateIndexCommand) {
      return SUCCESSFULL_RESULT_WITHOUT_DATA;
    }

    if (command instanceof SelectMinModifiedCommand) {
      return EMPTY_QUERY_RESULT;
    }

    if (command instanceof SelectByDeletedOnceAndModifiedRangeCommand selectCmd) {
      lock.readLock().lock();
      try {
        InMemoryContainer container =
            getContainer(selectCmd.getContainerName()).orElseThrow(IllegalStateException::new);
        List<DocumentEntry> found =
            container.findByDeletedOnceAndModifiedRange(
                selectCmd.getDeletedOnce(),
                selectCmd.getModifiedLowerBound(),
                selectCmd.getModifiedUpperBound());
        return selectCmd.buildResult(found);
      } finally {
        lock.readLock().unlock();
      }
    }

    if (command instanceof DeleteByModifiedRangeCommand) {
      return SUCCESSFULL_RESULT_WITHOUT_DATA;
    }

    if (command instanceof DeleteByIdCommand<?> deleteByIdCommand) {
      lock.writeLock().lock();
      try {
        getContainer(deleteByIdCommand.getContainerName())
            .ifPresent(container -> container.removeEntry(deleteByIdCommand.getId()));
        return SUCCESSFULL_RESULT_WITHOUT_DATA;
      } finally {
        lock.writeLock().unlock();
      }
    }

    if (command instanceof DeleteByIdAndModifiedCommand<?> deleteCommand) {
      lock.writeLock().lock();
      try {
        getContainer(deleteCommand.getContainerName())
            .ifPresent(
                container -> {
                  Optional<? extends ContainerEntry<?>> entry =
                      container.findById(deleteCommand.getId(), deleteCommand.getEntryType());
                  entry.ifPresent(
                      e -> {
                        if (e instanceof DocumentEntry doc
                            && doc.getModified() != null
                            && doc.getModified() == deleteCommand.getModified()) {
                          container.removeEntry(deleteCommand.getId());
                        }
                      });
                });
        return SUCCESSFULL_RESULT_WITHOUT_DATA;
      } finally {
        lock.writeLock().unlock();
      }
    }

    if (command instanceof DeleteByIdListCommand<?> deleteListCommand) {
      lock.writeLock().lock();
      try {
        getContainer(deleteListCommand.getContainerName())
            .ifPresent(container -> deleteListCommand.getIds().forEach(container::removeEntry));
        return SUCCESSFULL_RESULT_WITHOUT_DATA;
      } finally {
        lock.writeLock().unlock();
      }
    }

    if (command instanceof UpdateDatastoreMetaLastmodCommand updateLastmodCommand) {
      lock.writeLock().lock();
      try {
        getContainer(updateLastmodCommand.getContainerName())
            .ifPresent(
                container -> {
                  Optional<DatastoreMetaEntry> entry =
                      container.findById(updateLastmodCommand.getId(), DatastoreMetaEntry.class);
                  entry.ifPresent(
                      e -> {
                        Long threshold = updateLastmodCommand.getLastmodThreshold();
                        if (threshold == null
                            || (e.getLastmod() != null && e.getLastmod() < threshold)) {
                          e.setLastmod(updateLastmodCommand.getLastmod());
                          container.setEntry(e);
                        }
                      });
                });
        return SUCCESSFULL_RESULT_WITHOUT_DATA;
      } finally {
        lock.writeLock().unlock();
      }
    }

    if (command instanceof SelectDatastoreMetaByLastmodCommand selectLastmodCommand) {
      lock.readLock().lock();
      try {
        Optional<InMemoryContainer> container =
            getContainer(selectLastmodCommand.getContainerName());
        if (container.isPresent()) {
          List<DatastoreMetaEntry> found =
              container.get().findByLastmodLessThan(selectLastmodCommand.getLastmod());
          return selectLastmodCommand.buildResult(found);
        }
        return selectLastmodCommand.buildResult(List.of());
      } finally {
        lock.readLock().unlock();
      }
    }

    if (command instanceof SelectByRangeAndModifiedCommand<?> selectRangeModCommand) {
      lock.readLock().lock();
      try {
        Optional<InMemoryContainer> container =
            getContainer(selectRangeModCommand.getContainerName());
        if (container.isPresent()) {
          List<?> found =
              container
                  .get()
                  .findByIdRangeAndModified(
                      selectRangeModCommand.getIdMin(),
                      selectRangeModCommand.getIdMax(),
                      selectRangeModCommand.getMinModified(),
                      selectRangeModCommand.getEntryType(),
                      selectRangeModCommand.getLimit());
          return selectRangeModCommand.buildResult(found);
        }
        return selectRangeModCommand.buildResult(List.of());
      } finally {
        lock.readLock().unlock();
      }
    }

    if (!(command instanceof ContainerCommand<?>)) {
      return NO_RESULT;
    }

    ContainerCommand<?> containerCommand = (ContainerCommand<?>) command;

    String containerName = containerCommand.getContainerName();

    if (containerCommand instanceof CreateContainerCommand) {
      lock.writeLock().lock();
      try {
        InMemoryContainer result = new InMemoryContainer(containerName.toUpperCase());
        containerMap.put(containerName.toUpperCase(), result);

        return SUCCESSFULL_RESULT_WITHOUT_DATA;
      } finally {
        lock.writeLock().unlock();
      }
    } else {
      Optional<InMemoryContainer> container = getContainer(containerCommand.getContainerName());

      if (containerCommand instanceof InsertIntoContainerCommand) {
        lock.writeLock().lock();
        try {
          InsertIntoContainerCommand<?> insertCommand =
              (InsertIntoContainerCommand<?>) containerCommand;
          InMemoryContainer target = container.orElseThrow(IllegalStateException::new);
          String entryId = insertCommand.getData().getId();
          if (target.containsEntry(entryId)) {
            throw new IllegalStateException("Duplicate key: " + entryId);
          }
          target.setEntry(insertCommand.getData().copy());
          return SUCCESSFULL_RESULT_WITHOUT_DATA;
        } finally {
          lock.writeLock().unlock();
        }
      } else if (containerCommand instanceof SelectFromContainerByIdCommand) {
        lock.readLock().lock();
        try {
          SelectFromContainerByIdCommand<?> selectCommand =
              (SelectFromContainerByIdCommand<?>) containerCommand;

          ContainerEntry<?> foundEntry =
              container
                  .orElseThrow(IllegalStateException::new)
                  .findById(selectCommand.getId(), selectCommand.getEntryType())
                  .orElse(null);

          return selectCommand.buildResult(foundEntry);
        } finally {
          lock.readLock().unlock();
        }
      } else if (containerCommand instanceof SelectFromContainerByIdRangeCommand) {
        lock.readLock().lock();
        try {
          SelectFromContainerByIdRangeCommand<?> selectCommand =
              (SelectFromContainerByIdRangeCommand<?>) containerCommand;

          List<?> foundEntries =
              container
                  .orElseThrow(IllegalStateException::new)
                  .findByIdRange(
                      selectCommand.getIdMin(),
                      selectCommand.getIdMax(),
                      selectCommand.getEntryType(),
                      selectCommand.getLimit());

          return selectCommand.buildResult(foundEntries);
        } finally {
          lock.readLock().unlock();
        }
      } else if (containerCommand instanceof SelectFromContainerByMultipleIdsCommand) {
        lock.readLock().lock();
        try {
          SelectFromContainerByMultipleIdsCommand<?> selectCommand =
              (SelectFromContainerByMultipleIdsCommand<?>) containerCommand;

          List<?> foundEntries =
              container
                  .orElseThrow(IllegalStateException::new)
                  .findByIds(selectCommand.getIds(), selectCommand.getEntryType());

          return selectCommand.buildResult(foundEntries);
        } finally {
          lock.readLock().unlock();
        }
      } else if (containerCommand instanceof UpdateDocumentDataInContainerCommand) {
        lock.writeLock().lock();
        try {
          UpdateDocumentDataInContainerCommand updateCommand =
              (UpdateDocumentDataInContainerCommand) containerCommand;

          InMemoryContainer containerToUpdate = container.orElseThrow(IllegalStateException::new);
          Optional<DocumentEntry> existingEntry =
              containerToUpdate.findByIdAndModCount(
                  updateCommand.getId(), updateCommand.getModCount(), DocumentEntry.class);

          if (existingEntry.isPresent()) {
            final DocumentEntry entityToUpdate = existingEntry.get();
            updateCommand.getData().update(entityToUpdate);
            containerToUpdate.setEntry(entityToUpdate.copy());

            return updateCommand.buildResult(entityToUpdate);
          }
        } finally {
          lock.writeLock().unlock();
        }
      }
    }

    return NO_RESULT;
  }

  private Optional<InMemoryContainer> getContainer(String name) {
    lock.readLock().lock();
    try {
      if (containerMap.containsKey(name.toUpperCase())) {
        return Optional.of(containerMap.get(name.toUpperCase()));
      }
    } finally {
      lock.readLock().unlock();
    }

    return Optional.empty();
  }
}
