package com.diconium.oakgit.processor.inmemory;

import com.diconium.oakgit.engine.Command;
import com.diconium.oakgit.engine.CommandProcessor;
import com.diconium.oakgit.engine.CommandResult;
import com.diconium.oakgit.engine.commands.*;
import com.diconium.oakgit.engine.model.ContainerEntry;
import com.diconium.oakgit.engine.model.DocumentEntry;
import com.diconium.oakgit.engine.model.UpdateSet;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.diconium.oakgit.engine.CommandResult.SUCCESSFULL_RESULT_WITHOUT_DATA;

public class InMemoryCommandProcessor implements CommandProcessor {

    private static final InMemoryContainer NULL_CONTAINER = new InMemoryContainer("null container");
    private Map<String, InMemoryContainer> containerMap = new HashMap<>();


    @Override
    public synchronized CommandResult execute(Command command) {
        System.out.println("Executing " + command);
        if (command instanceof CreateContainerCommand) {

            String containerName = ((CreateContainerCommand) command).getContainerName();
            containerMap.put(containerName, new InMemoryContainer(containerName));

            return SUCCESSFULL_RESULT_WITHOUT_DATA;

        } else if (command instanceof InsertIntoContainerCommand) {

            InsertIntoContainerCommand insertCommand = (InsertIntoContainerCommand) command;

            getContainer(insertCommand.getContainerName())
                    .setEntry(insertCommand.getData());

            return SUCCESSFULL_RESULT_WITHOUT_DATA;

        } else if (command instanceof SelectFromContainerByIdCommand) {

            SelectFromContainerByIdCommand selectCommand = (SelectFromContainerByIdCommand) command;

            ContainerEntry<DocumentEntry> foundEntry = getContainer(selectCommand.getContainerName())
                    .findById(selectCommand.getId(), DocumentEntry.class)
                    .orElse(ContainerEntry.emptyOf(DocumentEntry.class));

            CommandResult commandResult = selectCommand.buildResult(foundEntry);
            System.out.println("commandResult = " + commandResult.toResultSet());
            return commandResult;

        } else if (command instanceof SelectFromContainerByIdRangeCommand) {

            SelectFromContainerByIdRangeCommand selectCommand = (SelectFromContainerByIdRangeCommand) command;

            List<ContainerEntry<DocumentEntry>> foundEntries = getContainer(selectCommand.getContainerName())
                    .findByIdRange(selectCommand.getIdMin(), selectCommand.getIdMax(), DocumentEntry.class);

            CommandResult commandResult = selectCommand.buildResult(foundEntries);
            System.out.println("commandResult = " + commandResult.toResultSet());
            return commandResult;
        } else if (command instanceof SelectFromContainerByMultipleIdsCommand) {

            SelectFromContainerByMultipleIdsCommand selectCommand = (SelectFromContainerByMultipleIdsCommand) command;

            List<ContainerEntry<DocumentEntry>> foundEntries = getContainer(selectCommand.getContainerName())
                .findByIds(selectCommand.getIds(), DocumentEntry.class);

            CommandResult commandResult = selectCommand.buildResult(foundEntries);
            System.out.println("commandResult = " + commandResult.toResultSet());
            return commandResult;
        } else if (command instanceof UpdatDataInContainerCommand) {

            UpdatDataInContainerCommand updateCommand = (UpdatDataInContainerCommand) command;

            InMemoryContainer container = getContainer(updateCommand.getContainerName());
            Optional<ContainerEntry<DocumentEntry>> existingEntry = container
                    .findByIdAndModCount(updateCommand.getId(), updateCommand.getModCount(), DocumentEntry.class);

            if (existingEntry.isPresent()) {

                UpdateSet data = updateCommand.getData();
                final DocumentEntry entityToUpdate = (DocumentEntry) existingEntry.get();

                data
                        .whenHasValue("newModCount", Long.class, entityToUpdate::setModCount)
                        .whenHasValue("newModified", Long.class, v -> {
                            if (v != null && (entityToUpdate.getModified() == null || v > entityToUpdate.getModified())) {
                                entityToUpdate.setModified(v);
                            }
                        })
                        .whenHasValue("newHasBinary", Integer.class, entityToUpdate::setHasBinary)
                        .whenHasValue("newDeletedOnce", Integer.class, entityToUpdate::setDeletedOnce)
                        .whenHasValue("newCModCount", Long.class, entityToUpdate::setCModCount)
                        .whenHasValue("dsizeAddition", Long.class, v -> {
                            if (v != null) {
                                Long existingSize = entityToUpdate.getDSize() != null ? entityToUpdate.getDSize() : 0L;
                                entityToUpdate.setDSize(existingSize + v);
                            } else {
                                entityToUpdate.setDSize(null);
                            }
                        })
                        .whenHasValue("newData", String.class, v -> {
                            if (data.isConcatenateDataField()) {
                                if (v != null) {
                                    byte[] oldData = entityToUpdate.getData();
                                    byte[] newData = v.getBytes();
                                    byte[] combined = new byte[oldData.length + newData.length];
                                    System.arraycopy(oldData, 0, combined, 0, oldData.length);
                                    System.arraycopy(newData, 0, combined, oldData.length, newData.length);
                                    entityToUpdate.setData(combined);
                                }
                            } else {
                                if (v != null) {
                                    entityToUpdate.setData(v.getBytes());
                                } else {
                                    entityToUpdate.setData(null);
                                }
                            }
                        })
                        .whenHasValue("newVersion", Integer.class, entityToUpdate::setVersion);

                System.out.println("entityToUpdate = " + entityToUpdate);
                CommandResult commandResult = updateCommand.buildResult(entityToUpdate);
                System.out.println("commandResult = " + commandResult.toResultSet());
                return commandResult;
            }
        }

        return CommandResult.NO_RESULT;
    }

    private InMemoryContainer getContainer(String name) {
        return containerMap.getOrDefault(name, NULL_CONTAINER);
    }

}
