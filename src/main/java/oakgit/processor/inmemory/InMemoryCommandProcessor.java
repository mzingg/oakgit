package oakgit.processor.inmemory;

import oakgit.engine.Command;
import oakgit.engine.CommandProcessor;
import oakgit.engine.CommandResult;
import oakgit.engine.ContainerCommand;
import oakgit.engine.commands.*;
import oakgit.engine.model.DocumentEntry;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static oakgit.engine.CommandResult.SUCCESSFULL_RESULT_WITHOUT_DATA;

public final class InMemoryCommandProcessor implements CommandProcessor {

  private final Map<String, InMemoryContainer> containerMap = new HashMap<>();


  @SuppressWarnings("unchecked")
  @Override
  public synchronized CommandResult execute(Command command) {

    if (!(command instanceof ContainerCommand<?>)) {
      return CommandResult.NO_RESULT;
    }

    ContainerCommand<?> containerCommand = (ContainerCommand<?>) command;

    String containerName = containerCommand.getContainerName();

    if (containerCommand instanceof CreateContainerCommand) {
      createContainer(containerName);

      return SUCCESSFULL_RESULT_WITHOUT_DATA;

    } else {
      Optional<InMemoryContainer> container = getContainer(containerCommand.getContainerName());

      if (containerCommand instanceof InsertIntoContainerCommand) {
        InsertIntoContainerCommand<?> insertCommand = (InsertIntoContainerCommand<?>) containerCommand;

        container.orElse(createContainer(containerName))
            .setEntry(insertCommand.getData());

        return SUCCESSFULL_RESULT_WITHOUT_DATA;

      } else if (containerCommand instanceof SelectFromContainerByIdCommand) {
        SelectFromContainerByIdCommand<DocumentEntry> selectCommand = (SelectFromContainerByIdCommand<DocumentEntry>) containerCommand;

        if (container.isEmpty()) {
          return CommandResult.emptyResult(containerName, containerCommand.getEntryType());
        }

        DocumentEntry foundEntry = container.get().findById(selectCommand.getId(), DocumentEntry.class)
            .orElse(null);

        return selectCommand.buildResult(foundEntry);

      } else if (containerCommand instanceof SelectFromContainerByIdRangeCommand) {

        SelectFromContainerByIdRangeCommand<DocumentEntry> selectCommand = (SelectFromContainerByIdRangeCommand<DocumentEntry>) containerCommand;

        List<DocumentEntry> foundEntries = container.orElse(createContainer(containerName))
            .findByIdRange(selectCommand.getIdMin(), selectCommand.getIdMax(), DocumentEntry.class, selectCommand.getLimit());

        return selectCommand.buildResult(foundEntries);
      } else if (containerCommand instanceof SelectFromContainerByMultipleIdsCommand) {

        SelectFromContainerByMultipleIdsCommand<DocumentEntry> selectCommand = (SelectFromContainerByMultipleIdsCommand<DocumentEntry>) containerCommand;

        List<DocumentEntry> foundEntries = container.orElse(createContainer(containerName))
            .findByIds(selectCommand.getIds(), DocumentEntry.class);

        return selectCommand.buildResult(foundEntries);
      } else if (containerCommand instanceof UpdatDataInContainerCommand) {

        UpdatDataInContainerCommand<DocumentEntry> updateCommand = (UpdatDataInContainerCommand<DocumentEntry>) containerCommand;

        InMemoryContainer containerToUpdate = container.orElse(createContainer(containerName));
        Optional<DocumentEntry> existingEntry = containerToUpdate
            .findByIdAndModCount(updateCommand.getId(), updateCommand.getModCount(), DocumentEntry.class);

        if (existingEntry.isPresent()) {
          final DocumentEntry entityToUpdate = existingEntry.get();
          updateCommand.getData().update(entityToUpdate);
          containerToUpdate.setEntry(entityToUpdate);

          return updateCommand.buildResult(entityToUpdate);
        }
      } else if (containerCommand instanceof ErrorCommand) {
        System.err.println(containerCommand);
      }
    }

    return CommandResult.NO_RESULT;
  }

  private InMemoryContainer createContainer(String containerName) {
    InMemoryContainer result = new InMemoryContainer(containerName);
    containerMap.put(containerName, result);

    return result;
  }

  private Optional<InMemoryContainer> getContainer(String name) {
    if (containerMap.containsKey(name)) {
      return Optional.of(containerMap.get(name));
    }

    return Optional.empty();
  }

}
