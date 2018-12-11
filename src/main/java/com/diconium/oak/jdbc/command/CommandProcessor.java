package com.diconium.oak.jdbc.command;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import com.diconium.oak.beans.OakGitContainer;

public class CommandProcessor {

    private List<Command> commandsList = new ArrayList<>();

    private Map<String, OakGitContainer> containerMap = new HashMap<String, OakGitContainer>();

    public void submitCommand(Command command) {

        commandsList.add(command);
    }

    public void processCommands() {
        for (Command currentCommand : commandsList) {
            execute(currentCommand);
        }

    }

    public void execute(Command command) {

        if (command instanceof CreateContainerCommand) {

            String containerName = ((CreateContainerCommand) command).getContainerName();

            OakGitContainer oakGitContainer = new OakGitContainer();
            containerMap.put(containerName, oakGitContainer);

        }
        if (command instanceof GetByIdCommand) {

        }
        if (command instanceof InsertIntoContainerCommand) {

        }
        if (command instanceof NoOperationCommand) {

        }
    }

    protected String getContainerNameFromSqlQuery(String queryString) {
        String word = "where";
        String semicolon = ";";
        Boolean foundWord, foundSemicolon = false;

        Pattern pattern = null;

        foundWord = queryString.contains(word);
        foundSemicolon = queryString.contains(semicolon);

        if (foundWord == false && foundSemicolon == true) {
            pattern = Pattern.compile("select .* from (\\w+) ;?");
        } else if (foundWord == false && foundSemicolon == false) {
            pattern = Pattern.compile("select .* from (\\w+)");
        } else {
            pattern = Pattern.compile("select .* from (\\w+) .*");
        }

        Matcher matcher = pattern.matcher(queryString);

        return matcher.matches() ? matcher.group(1) : StringUtils.EMPTY;
    }
}
