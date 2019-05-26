package com.diconium.oakgit.engine.inmemory;

import com.diconium.oakgit.engine.Command;
import com.diconium.oakgit.engine.CommandProcessor;
import com.diconium.oakgit.engine.CommandResult;
import com.diconium.oakgit.engine.commands.CreateContainerCommand;
import com.diconium.oakgit.engine.commands.InsertIntoContainerCommand;
import com.diconium.oakgit.engine.commands.SelectFromContainerByIdCommand;
import com.diconium.oakgit.engine.commands.SelectFromContainerByIdRangeCommand;
import com.diconium.oakgit.engine.commands.UpdatDataInContainerCommand;
import com.diconium.oakgit.engine.model.Container;
import com.diconium.oakgit.engine.model.ContainerEntry;
import com.diconium.oakgit.engine.model.NodeAndSettingsEntry;
import com.diconium.oakgit.engine.model.UpdateSet;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.diconium.oakgit.engine.CommandResult.SUCCESSFULL_RESULT_WITHOUT_DATA;

public class InMemoryCommandProcessor implements CommandProcessor {

    private static final Container NULL_CONTAINER = new Container("null container");
    private Map<String, Container> containerMap = new HashMap<>();


    @Override
    public synchronized CommandResult execute(Command command) {
        System.out.println("Executing " + command);
        if (command instanceof CreateContainerCommand) {

            String containerName = ((CreateContainerCommand) command).getContainerName();
            containerMap.put(containerName, new Container(containerName));

            return SUCCESSFULL_RESULT_WITHOUT_DATA;

        } else if (command instanceof InsertIntoContainerCommand) {

            InsertIntoContainerCommand insertCommand = (InsertIntoContainerCommand) command;

            getContainer(insertCommand.getContainerName())
                    .setEntry(insertCommand.getData());

            return SUCCESSFULL_RESULT_WITHOUT_DATA;

        } else if (command instanceof SelectFromContainerByIdCommand) {

            SelectFromContainerByIdCommand selectCommand = (SelectFromContainerByIdCommand) command;

            ContainerEntry<NodeAndSettingsEntry> foundEntry = getContainer(selectCommand.getContainerName())
                    .findById(selectCommand.getId(), NodeAndSettingsEntry.class)
                    .orElse(ContainerEntry.emptyOf(NodeAndSettingsEntry.class));

            return selectCommand.buildResult(foundEntry);

        } else if (command instanceof SelectFromContainerByIdRangeCommand) {

            SelectFromContainerByIdRangeCommand selectCommand = (SelectFromContainerByIdRangeCommand) command;

            List<ContainerEntry<NodeAndSettingsEntry>> foundEntries = getContainer(selectCommand.getContainerName())
                    .findByIdRange(selectCommand.getIdMin(), selectCommand.getIdMax(), NodeAndSettingsEntry.class);

            return selectCommand.buildResult(foundEntries);
        } else if (command instanceof UpdatDataInContainerCommand) {

            UpdatDataInContainerCommand updateCommand = (UpdatDataInContainerCommand) command;

            Container container = getContainer(updateCommand.getContainerName());
            Optional<ContainerEntry<NodeAndSettingsEntry>> existingEntry = container
                    .findByIdAndModCount(updateCommand.getId(), updateCommand.getModCount(), NodeAndSettingsEntry.class);

            if (existingEntry.isPresent()) {

                UpdateSet data = updateCommand.getData();
                final NodeAndSettingsEntry entityToUpdate = (NodeAndSettingsEntry) existingEntry.get();

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
                            if (v != null) {
                                entityToUpdate.setData(v.getBytes());
                            } else {
                                entityToUpdate.setData(null);
                            }
                        })
                        .whenHasValue("newVersion", Integer.class, entityToUpdate::setVersion);

                return updateCommand.buildResult(entityToUpdate);
            }
        }

        return CommandResult.NO_RESULT;
    }

    private Container getContainer(String name) {
        return containerMap.getOrDefault(name, NULL_CONTAINER);
    }

}
