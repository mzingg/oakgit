package oakgit.processor.inmemory;

import oakgit.engine.Command;
import oakgit.engine.CommandProcessor;
import oakgit.engine.CommandResult;
import oakgit.engine.commands.*;
import oakgit.engine.model.DocumentEntry;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static oakgit.engine.CommandResult.SUCCESSFULL_RESULT_WITHOUT_DATA;

public final class InMemoryCommandProcessor implements CommandProcessor {

  private final Map<String, InMemoryContainer> containerMap = new HashMap<>();


  @Override
  public synchronized CommandResult execute(Command command) {
    System.err.println("T[" + Thread.currentThread().getName() + "] Executing " + command);
    if (command instanceof CreateContainerCommand) {

      String containerName = ((CreateContainerCommand) command).getContainerName();
      containerMap.put(containerName, new InMemoryContainer(containerName));

      return SUCCESSFULL_RESULT_WITHOUT_DATA;

    } else if (command instanceof InsertIntoContainerCommand) {

      InsertIntoContainerCommand<?> insertCommand = (InsertIntoContainerCommand<?>) command;

      getContainer(insertCommand.getContainerName())
          .setEntry(insertCommand.getData());

      return SUCCESSFULL_RESULT_WITHOUT_DATA;

    } else if (command instanceof SelectFromContainerByIdCommand) {

      SelectFromContainerByIdCommand selectCommand = (SelectFromContainerByIdCommand) command;

      DocumentEntry foundEntry = getContainer(selectCommand.getContainerName())
          .findById(selectCommand.getId(), DocumentEntry.class)
          .orElse(null);

      CommandResult commandResult = selectCommand.buildResult(DocumentEntry.class, foundEntry);
      // System.out.println("commandResult = " + commandResult.toResultSet());
      return commandResult;

    } else if (command instanceof SelectFromContainerByIdRangeCommand) {

      SelectFromContainerByIdRangeCommand selectCommand = (SelectFromContainerByIdRangeCommand) command;

      List<DocumentEntry> foundEntries = getContainer(selectCommand.getContainerName())
          .findByIdRange(selectCommand.getIdMin(), selectCommand.getIdMax(), DocumentEntry.class);

      CommandResult commandResult = selectCommand.buildResult(DocumentEntry.class, foundEntries);
      // System.out.println("commandResult = " + commandResult.toResultSet());
      return commandResult;
    } else if (command instanceof SelectFromContainerByMultipleIdsCommand) {

      SelectFromContainerByMultipleIdsCommand selectCommand = (SelectFromContainerByMultipleIdsCommand) command;

      List<DocumentEntry> foundEntries = getContainer(selectCommand.getContainerName())
          .findByIds(selectCommand.getIds(), DocumentEntry.class);

      CommandResult commandResult = selectCommand.buildResult(DocumentEntry.class, foundEntries);
      // System.out.println("commandResult = " + commandResult.toResultSet());
      return commandResult;
    } else if (command instanceof UpdatDataInContainerCommand) {

      UpdatDataInContainerCommand updateCommand = (UpdatDataInContainerCommand) command;

      InMemoryContainer container = getContainer(updateCommand.getContainerName());
      Optional<DocumentEntry> existingEntry = container
          .findByIdAndModCount(updateCommand.getId(), updateCommand.getModCount(), DocumentEntry.class);

      if (existingEntry.isPresent()) {
        final DocumentEntry entityToUpdate = existingEntry.get();
        updateCommand.getData().update(entityToUpdate);
        container.setEntry(entityToUpdate);

        // System.out.println("entityToUpdate = " + entityToUpdate);
        CommandResult commandResult = updateCommand.buildResult(DocumentEntry.class, entityToUpdate);
        // System.out.println("commandResult = " + commandResult.toResultSet());
        return commandResult;
      }
    }

    return CommandResult.NO_RESULT;
  }

  private InMemoryContainer getContainer(String name) {
    if (!containerMap.containsKey(name)) {
      containerMap.put(name, new InMemoryContainer(name));
    }

    return containerMap.get(name);
  }

}
