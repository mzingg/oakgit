package com.diconium.oak.command.processor;

import com.diconium.oak.beans.OakGitContainer;
import com.diconium.oak.command.*;

import java.util.HashMap;
import java.util.Map;

public class InMemoryCommandProcessor implements CommandProcessor {

    private Map<String, OakGitContainer> containerMap = new HashMap<>();

    @Override
    public void execute(Command command) {
        if (command instanceof CreateContainerCommand) {
            String containerName = ((CreateContainerCommand) command).getContainerName();
            containerMap.put(containerName, new OakGitContainer());
        }

        if (command instanceof InsertIntoContainerCommand) {
            String containerName = ((InsertIntoContainerCommand) command).getContainerName();
            String data = ((InsertIntoContainerCommand) command).getData();
            OakGitContainer oakGitContainer = containerMap.get(containerName);
            oakGitContainer.setDATA(data);
        }
    }

}
