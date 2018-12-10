package com.diconium.oak.jdbc.command;

import java.util.ArrayList;
import java.util.List;

public class CommandProcessor {

    List<Command> commandsList = new ArrayList<>();

    public void submitCommand(Command command) {

        commandsList.add(command);
    }

    public void processCommands() {
        for (Command currentCommand : commandsList) {
            execute(currentCommand);
        }

    }

    public void execute(Command command) {

        if (command instanceof CreateTableCommand) {

        }
        if (command instanceof GetByIdCommand) {

        }
        if (command instanceof InsertIntoTableCommand) {

        }
        if (command instanceof NoOperationCommand) {

        }
    }
}
