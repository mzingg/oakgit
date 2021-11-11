package oakgit.processor.inmemory;

import oakgit.engine.Command;
import oakgit.engine.CommandProcessor;
import oakgit.engine.CommandResult;
import oakgit.engine.ContainerCommand;
import oakgit.engine.commands.*;
import oakgit.engine.model.ContainerEntry;
import oakgit.engine.model.DocumentEntry;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import static oakgit.engine.CommandResult.NO_RESULT;
import static oakgit.engine.CommandResult.SUCCESSFULL_RESULT_WITHOUT_DATA;

public final class InMemoryCommandProcessor implements CommandProcessor {

  private final Map<String, InMemoryContainer> containerMap = new HashMap<>();

  private ReadWriteLock lock = new ReentrantReadWriteLock();

  @Override
  public CommandResult execute(Command command) {

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
          InsertIntoContainerCommand<?> insertCommand = (InsertIntoContainerCommand<?>) containerCommand;

          container.orElseThrow(IllegalStateException::new)
              .setEntry(insertCommand.getData().copy());

          return SUCCESSFULL_RESULT_WITHOUT_DATA;
        } finally {
          lock.writeLock().unlock();
        }
      } else if (containerCommand instanceof SelectFromContainerByIdCommand) {
        lock.readLock().lock();
        try {
          SelectFromContainerByIdCommand<?> selectCommand = (SelectFromContainerByIdCommand<?>) containerCommand;

          ContainerEntry<?> foundEntry = container.orElseThrow(IllegalStateException::new)
              .findById(selectCommand.getId(), selectCommand.getEntryType())
              .orElse(null);

          return selectCommand.buildResult(foundEntry);
        } finally {
          lock.readLock().unlock();
        }
      } else if (containerCommand instanceof SelectFromContainerByIdRangeCommand) {
        lock.readLock().lock();
        try {
          SelectFromContainerByIdRangeCommand<?> selectCommand = (SelectFromContainerByIdRangeCommand<?>) containerCommand;

          List<?> foundEntries = container.orElseThrow(IllegalStateException::new)
              .findByIdRange(selectCommand.getIdMin(), selectCommand.getIdMax(), selectCommand.getEntryType(), selectCommand.getLimit());

          return selectCommand.buildResult(foundEntries);
        } finally {
          lock.readLock().unlock();
        }
      } else if (containerCommand instanceof SelectFromContainerByMultipleIdsCommand) {
        lock.readLock().lock();
        try {
          SelectFromContainerByMultipleIdsCommand<?> selectCommand = (SelectFromContainerByMultipleIdsCommand<?>) containerCommand;

          List<?> foundEntries = container.orElseThrow(IllegalStateException::new)
              .findByIds(selectCommand.getIds(), selectCommand.getEntryType());

          return selectCommand.buildResult(foundEntries);
        } finally {
          lock.readLock().unlock();
        }
      } else if (containerCommand instanceof UpdateDocumentDataInContainerCommand) {
        lock.writeLock().lock();
        try {
          UpdateDocumentDataInContainerCommand updateCommand = (UpdateDocumentDataInContainerCommand) containerCommand;

          InMemoryContainer containerToUpdate = container.orElseThrow(IllegalStateException::new);
          Optional<DocumentEntry> existingEntry;
          lock.readLock().lock();
          try {
            existingEntry = containerToUpdate
                .findByIdAndModCount(updateCommand.getId(), updateCommand.getModCount(), DocumentEntry.class);
          } finally {
            lock.readLock().unlock();
          }

          if (existingEntry.isPresent()) {
            final DocumentEntry entityToUpdate = existingEntry.get();
            updateCommand.getData().update(entityToUpdate);
            containerToUpdate.setEntry(entityToUpdate.copy());

            return updateCommand.buildResult(entityToUpdate);
          }
        } finally {
          lock.writeLock().unlock();
        }
      } else if (containerCommand instanceof ErrorCommand) {
        System.err.println(containerCommand);
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
