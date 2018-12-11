package com.diconium.oak.jdbc.command;

import com.diconium.oak.jdbc.utils.RegularExpressionUtil;

import java.util.regex.Pattern;

public class CommandFactory {

    private static final String SELECT_BY_ID_QUERY = "select \\* from (\\w+) where ID = '(\\d+)'";

    public static Command getCommandForSql(String sqlCommand) {

        if (Pattern.matches("create table .*", sqlCommand)) {

            CreateContainerCommand commandObj = new CreateContainerCommand();
            String containerName = RegularExpressionUtil.getTableName(sqlCommand);
            commandObj.setContainerName(containerName);
            return commandObj;

        } else if (Pattern.matches("insert into .*", sqlCommand)) {
            InsertIntoContainerCommand commandObj = new InsertIntoContainerCommand();
            String containerName = RegularExpressionUtil.getTableName(sqlCommand);
            commandObj.setContainerName(containerName);
            return commandObj;

        } else if (Pattern.matches(SELECT_BY_ID_QUERY, sqlCommand)) {
            return new GetByIdCommand();
        }

        return new NoOperationCommand();

    }
}
