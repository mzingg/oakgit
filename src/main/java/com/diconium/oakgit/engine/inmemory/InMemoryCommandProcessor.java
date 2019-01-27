package com.diconium.oakgit.engine.inmemory;

import com.diconium.oakgit.engine.Command;
import com.diconium.oakgit.engine.CommandProcessor;
import com.diconium.oakgit.engine.commands.CreateContainerCommand;
import com.diconium.oakgit.engine.commands.InsertIntoContainerCommand;
import com.diconium.oakgit.model.Container;

import java.util.HashMap;
import java.util.Map;

public class InMemoryCommandProcessor implements CommandProcessor {

    private Map<String, Container> containerMap = new HashMap<>();

    @Override
    public void execute(Command command) {
        if (command instanceof CreateContainerCommand) {
            String containerName = ((CreateContainerCommand) command).getContainerName();
            containerMap.put(containerName, new Container(containerName));
        }

        if (command instanceof InsertIntoContainerCommand) {
            InsertIntoContainerCommand insertCommand = (InsertIntoContainerCommand) command;

            Container container = containerMap.get(insertCommand.getContainerName());
            container.setEntry(insertCommand.getData());
        }
    }

}
