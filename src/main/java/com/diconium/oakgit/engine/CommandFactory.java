package com.diconium.oakgit.engine;

import com.diconium.oakgit.engine.commands.ErrorCommand;
import com.diconium.oakgit.engine.commands.NoOperationCommand;
import com.diconium.oakgit.queryparsing.QueryParser;
import com.diconium.oakgit.queryparsing.QueryParserResult;

import java.util.Collections;
import java.util.Map;

public class CommandFactory {

    /**
     * Returns a {@link Command} for a given SQL string.
     *
     * @param sqlCommand {@link String}
     * @return {@link Command}, {@link NoOperationCommand} in case the SQL was not recognized as a command.
     */
    public Command getCommandForSql(String sqlCommand) {
        return getCommandForSql(sqlCommand, Collections.emptyMap());
    }

    /**
     * Returns a {@link Command} for a given SQL using placeholder data from prepared statement.
     *
     * @param sqlCommand      {@link String}
     * @param placeholderData {@link Map}
     * @return {@link Command}, {@link NoOperationCommand} in case the SQL was not recognized as a command.
     */
    public Command getCommandForSql(String sqlCommand, Map<Integer, Object> placeholderData) {
        System.out.println("sqlCommand = " + sqlCommand);
        System.out.println("placeholderData = " + placeholderData);
        QueryParserResult queryParserResult = new QueryParser().parse(sqlCommand);
        if (!queryParserResult.isValid()) {
            return new ErrorCommand("Error while parsing the query " + sqlCommand);
        }
        return queryParserResult.createCommand(placeholderData);
    }

}
