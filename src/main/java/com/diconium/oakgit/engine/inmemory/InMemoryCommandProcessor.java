package com.diconium.oakgit.engine.inmemory;

import com.diconium.oakgit.engine.Command;
import com.diconium.oakgit.engine.CommandProcessor;
import com.diconium.oakgit.engine.CommandResult;
import com.diconium.oakgit.engine.commands.CreateContainerCommand;
import com.diconium.oakgit.engine.commands.InsertIntoContainerCommand;
import com.diconium.oakgit.engine.commands.SelectFromContainerByIdCommand;
import com.diconium.oakgit.engine.commands.SelectFromContainerByIdRangeCommand;
import com.diconium.oakgit.engine.model.Container;
import com.diconium.oakgit.engine.model.ContainerEntry;
import com.diconium.oakgit.engine.model.NodeAndSettingsEntry;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.diconium.oakgit.engine.CommandResult.SUCCESSFULL_RESULT_WITHOUT_DATA;

public class InMemoryCommandProcessor implements CommandProcessor {

    private static final Container NULL_CONTAINER = new Container("null container");
    private Map<String, Container> containerMap = new HashMap<>();


    @Override
    public CommandResult execute(Command command) {
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
        }

        return CommandResult.NO_RESULT;
    }

    private Container getContainer(String name) {
        return containerMap.getOrDefault(name, NULL_CONTAINER);
    }

}
