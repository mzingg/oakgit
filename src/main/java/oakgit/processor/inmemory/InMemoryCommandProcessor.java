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

import static oakgit.engine.CommandResult.NO_RESULT;
import static oakgit.engine.CommandResult.SUCCESSFULL_RESULT_WITHOUT_DATA;

public final class InMemoryCommandProcessor implements CommandProcessor {

  private final Map<String, InMemoryContainer> containerMap = new HashMap<>();


  @Override
  public synchronized CommandResult execute(Command command) {

    try {
      Thread.sleep(5);
    } catch (InterruptedException ignored) {
    }

    if (!(command instanceof ContainerCommand<?>)) {
      return NO_RESULT;
    }

    ContainerCommand<?> containerCommand = (ContainerCommand<?>) command;

    String containerName = containerCommand.getContainerName();

    if (containerCommand instanceof CreateContainerCommand) {
      InMemoryContainer result = new InMemoryContainer(containerName.toUpperCase());
      containerMap.put(containerName.toUpperCase(), result);

      return SUCCESSFULL_RESULT_WITHOUT_DATA;

    } else {
      Optional<InMemoryContainer> container = getContainer(containerCommand.getContainerName());

      if (containerCommand instanceof InsertIntoContainerCommand) {
        InsertIntoContainerCommand<?> insertCommand = (InsertIntoContainerCommand<?>) containerCommand;

        container.orElseThrow(IllegalStateException::new)
            .setEntry(insertCommand.getData().copy());

        return SUCCESSFULL_RESULT_WITHOUT_DATA;

      } else if (containerCommand instanceof SelectFromContainerByIdCommand) {
        SelectFromContainerByIdCommand<?> selectCommand = (SelectFromContainerByIdCommand<?>) containerCommand;

        ContainerEntry<?> foundEntry = container.orElseThrow(IllegalStateException::new)
            .findById(selectCommand.getId(), selectCommand.getEntryType())
            .orElse(null);

        return selectCommand.buildResult(foundEntry);

      } else if (containerCommand instanceof SelectFromContainerByIdRangeCommand) {
        SelectFromContainerByIdRangeCommand<?> selectCommand = (SelectFromContainerByIdRangeCommand<?>) containerCommand;

        List<?> foundEntries = container.orElseThrow(IllegalStateException::new)
            .findByIdRange(selectCommand.getIdMin(), selectCommand.getIdMax(), selectCommand.getEntryType(), selectCommand.getLimit());

        return selectCommand.buildResult(foundEntries);

      } else if (containerCommand instanceof SelectFromContainerByMultipleIdsCommand) {
        SelectFromContainerByMultipleIdsCommand<?> selectCommand = (SelectFromContainerByMultipleIdsCommand<?>) containerCommand;

        List<?> foundEntries = container.orElseThrow(IllegalStateException::new)
            .findByIds(selectCommand.getIds(), selectCommand.getEntryType());

        return selectCommand.buildResult(foundEntries);

      } else if (containerCommand instanceof UpdatDocumentDataInContainerCommand) {
        UpdatDocumentDataInContainerCommand updateCommand = (UpdatDocumentDataInContainerCommand) containerCommand;

        InMemoryContainer containerToUpdate = container.orElseThrow(IllegalStateException::new);
        Optional<DocumentEntry> existingEntry = containerToUpdate
            .findByIdAndModCount(updateCommand.getId(), updateCommand.getModCount(), DocumentEntry.class);

        if (existingEntry.isPresent()) {
          final DocumentEntry entityToUpdate = existingEntry.get();
          updateCommand.getData().update(entityToUpdate);
          containerToUpdate.setEntry(entityToUpdate.copy());

          return updateCommand.buildResult(entityToUpdate);
        }
      } else if (containerCommand instanceof ErrorCommand) {
        System.err.println(containerCommand);
      }
    }

    return NO_RESULT;
  }

  private Optional<InMemoryContainer> getContainer(String name) {
    if (containerMap.containsKey(name.toUpperCase())) {
      return Optional.of(containerMap.get(name.toUpperCase()));
    }

    return Optional.empty();
  }

}
