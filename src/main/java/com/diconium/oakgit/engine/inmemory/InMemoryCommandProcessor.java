package com.diconium.oakgit.engine.inmemory;

import com.diconium.oakgit.engine.Command;
import com.diconium.oakgit.engine.CommandProcessor;
import com.diconium.oakgit.engine.CommandResult;
import com.diconium.oakgit.engine.commands.CreateContainerCommand;
import com.diconium.oakgit.engine.commands.InsertIntoContainerCommand;
import com.diconium.oakgit.engine.commands.SelectFromContainerByIdCommand;
import com.diconium.oakgit.engine.commands.SelectFromContainerByIdRangeCommand;
import com.diconium.oakgit.model.Container;
import com.diconium.oakgit.model.ContainerEntry;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class InMemoryCommandProcessor implements CommandProcessor {

    private static final Container NULL_CONTAINER = new Container("null container");
    private Map<String, Container> containerMap = new HashMap<>();


    @Override
    public CommandResult execute(Command command) {
        System.out.println("Executing " + command);
        if (command instanceof CreateContainerCommand) {

            String containerName = ((CreateContainerCommand) command).getContainerName();
            containerMap.put(containerName, new Container(containerName));

        } else if (command instanceof InsertIntoContainerCommand) {

            InsertIntoContainerCommand insertCommand = (InsertIntoContainerCommand) command;

            getContainer(insertCommand.getContainerName())
                    .setEntry(insertCommand.getData());

        } else if (command instanceof SelectFromContainerByIdCommand) {

            SelectFromContainerByIdCommand selectCommand = (SelectFromContainerByIdCommand) command;

            Optional<ContainerEntry> foundEntry = getContainer(selectCommand.getContainerName())
                    .findById(selectCommand.getId());

            return selectCommand.buildResult(foundEntry);

        } else if (command instanceof SelectFromContainerByIdRangeCommand) {

            SelectFromContainerByIdRangeCommand selectCommand = (SelectFromContainerByIdRangeCommand) command;

            List<ContainerEntry> foundEntry = getContainer(selectCommand.getContainerName())
                    .findByIdRange(selectCommand.getIdMin(), selectCommand.getIdMax());

            return selectCommand.buildResult(foundEntry);
        }

        return CommandResult.NO_RESULT;
    }

    private Container getContainer(String name) {
        return Optional.ofNullable(containerMap.get(name)).orElse(NULL_CONTAINER);
    }

}
