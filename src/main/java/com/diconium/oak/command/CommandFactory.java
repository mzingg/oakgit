package com.diconium.oak.command;

import com.diconium.oak.commons.QueryParser;
import com.diconium.oak.commons.QueryParserResult;

/**
 * Returns a {@link Command} for a given SQL string.
 *
 * @param sqlCommand {@link String}
 * @return {@link Command}, {@link NoOperationCommand} in case the SQL was not recognized as a command.
 */

public class CommandFactory {


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
            InsertIntoContainerCommand commandObj = new InsertIntoContainerCommand();
            commandObj.setContainerName(queryParserResult.getTableName());
            commandObj.setData(queryParserResult.getData());
            return commandObj;

        } else if (queryParserResult.getType() == QueryParserResult.ResultType.SELECT) {
        	
        	SelectFromContainerByIdCommand commandObj = new SelectFromContainerByIdCommand();
        	commandObj.setContainerName(queryParserResult.getTableName());
        	commandObj.setID(queryParserResult.getId());
        	
            return commandObj;
        }

        return new NoOperationCommand();
        
    }
}
