package com.diconium.oakgit.engine.inmemory;

import com.diconium.oakgit.engine.Command;
import com.diconium.oakgit.engine.CommandProcessor;
import com.diconium.oakgit.engine.CommandResult;
import com.diconium.oakgit.engine.commands.*;
import com.diconium.oakgit.engine.model.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;

import static com.diconium.oakgit.engine.CommandResult.SUCCESSFULL_RESULT_WITHOUT_DATA;

@Slf4j
public class InMemoryCommandProcessor implements CommandProcessor {

    private static final Container NULL_CONTAINER = new Container("null container");
    private final Map<String, Container> containerMap = new HashMap<>();

    private final ConcurrentLinkedQueue<String> logBuffer = new ConcurrentLinkedQueue<>();

    public InMemoryCommandProcessor() {
        this(StringUtils.EMPTY);
    }

    public InMemoryCommandProcessor(String sqlCommandLogfileName) {
        if (StringUtils.isNotEmpty(sqlCommandLogfileName)) {
            Executors.newSingleThreadExecutor().submit(() -> {
                Path sqlLogPath = Paths.get(sqlCommandLogfileName);
                try (BufferedWriter sqlLog = new BufferedWriter(new FileWriter(sqlLogPath.toFile(), true))) {
                    while (true) {
                        if (!logBuffer.isEmpty()) {
                            sqlLog.write(logBuffer.poll());
                            sqlLog.newLine();
                            sqlLog.flush();
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });

        }
    }

    @Override
    public synchronized CommandResult execute(Command command) {
        if (!(command instanceof SelectFromContainerByIdCommand)) {
            logBuffer.add(command.toString());
        }
        return _internalExecute(command);
    }

    private CommandResult _internalExecute(Command command) {
        if (command instanceof CreateContainerCommand) {

            String containerName = ((CreateContainerCommand) command).getContainerName();
            containerMap.put(containerName, new Container(containerName));

            return SUCCESSFULL_RESULT_WITHOUT_DATA;

        } else if (command instanceof InsertIntoContainerCommand) {

            InsertIntoContainerCommand<?> insertCommand = (InsertIntoContainerCommand<?>) command;

            getOrCreateContainer(insertCommand.getContainerName())
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

            List<ContainerEntry<QueriedSettingsEntry>> foundEntries = getContainer(selectCommand.getContainerName())
                .findByIdRange(selectCommand.getIdMin(), selectCommand.getIdMax(), QueriedSettingsEntry.class);
            logBuffer.add(String.format("Found %d entries", foundEntries.size()));

            return selectCommand.buildResult(foundEntries);
        } else if (command instanceof SelectFromContainerByMultipleIdsCommand) {

            SelectFromContainerByMultipleIdsCommand selectCommand = (SelectFromContainerByMultipleIdsCommand) command;

            List<ContainerEntry<QueriedSettingsEntry>> foundEntries = getContainer(selectCommand.getContainerName())
                .findByIds(selectCommand.getIds(), QueriedSettingsEntry.class);
            logBuffer.add(String.format("Found %d entries", foundEntries.size()));

            return selectCommand.buildResult(foundEntries);
        } else if (command instanceof UpdatDataInContainerCommand) {

            UpdatDataInContainerCommand updateCommand = (UpdatDataInContainerCommand) command;

            Container container = getOrCreateContainer(updateCommand.getContainerName());
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
                            entityToUpdate.appendData(v.getBytes());
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

    private Container getOrCreateContainer(String containerName) {
        containerMap.putIfAbsent(containerName, new Container(containerName));
        return getContainer(containerName);
    }

    private Container getContainer(String name) {
        return containerMap.getOrDefault(name, NULL_CONTAINER);
    }

}
