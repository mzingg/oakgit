package com.diconium.oakgit.engine;

import com.diconium.oakgit.commons.QueryParser;
import com.diconium.oakgit.commons.QueryParserResult;
import com.diconium.oakgit.engine.commands.*;
import com.diconium.oakgit.model.ContainerEntry;

public class CommandFactory {

    /**
     * Returns a {@link Command} for a given SQL string.
     *
     * @param sqlCommand {@link String}
     * @return {@link Command}, {@link NoOperationCommand} in case the SQL was not recognized as a command.
     */
    public Command getCommandForSql(String sqlCommand) {
    	
        QueryParserResult queryParserResult = new QueryParser().parse(sqlCommand);
        
        if (queryParserResult == QueryParserResult.ERROR_RESULT) {

        	return new ErrorCommand("Error while parsing the query " + sqlCommand);

        }

        if (queryParserResult.getType() == QueryParserResult.ResultType.CREATE) {

            CreateContainerCommand commandObj = new CreateContainerCommand();
            commandObj.setContainerName(queryParserResult.getTableName());
            return commandObj;

        } else if (queryParserResult.getType() == QueryParserResult.ResultType.INSERT) {

            ContainerEntry data = new ContainerEntry(queryParserResult.getId());
            return new InsertIntoContainerCommand(queryParserResult.getTableName(), data);

        } else if (queryParserResult.getType() == QueryParserResult.ResultType.SELECT) {

        	SelectFromContainerByIdCommand commandObj = new SelectFromContainerByIdCommand();
        	commandObj.setContainerName(queryParserResult.getTableName());
        	commandObj.setID(queryParserResult.getId());
        	
            return commandObj;

        }

        return new NoOperationCommand();
    }

}
