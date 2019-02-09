package com.diconium.oakgit.engine;

import com.diconium.oakgit.commons.QueryParser;
import com.diconium.oakgit.commons.QueryParserResult;
import com.diconium.oakgit.engine.commands.*;
import com.diconium.oakgit.model.NodeAndSettingsEntry;
import com.diconium.oakgit.model.MetaDataEntry;
import io.vavr.Tuple2;
import org.apache.commons.lang3.StringUtils;

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

        QueryParserResult queryParserResult = new QueryParser().parse(sqlCommand);

        if (queryParserResult == QueryParserResult.ERROR_RESULT) {

            return new ErrorCommand("Error while parsing the query " + sqlCommand);

        }

        if (queryParserResult.getType() == QueryParserResult.ResultType.CREATE) {

            return new CreateContainerCommand()
                    .setContainerName(queryParserResult.getTableName());

        } else if (queryParserResult.getType() == QueryParserResult.ResultType.INSERT) {

            if (queryParserResult.getTableName().equals("DATASTORE_META")) {

                MetaDataEntry data = new MetaDataEntry(queryParserResult.getId(placeholderData));
                return new InsertIntoContainerCommand<>(queryParserResult.getTableName(), data);

            } else {

                NodeAndSettingsEntry data = NodeAndSettingsEntry.buildNodeSettingsDataForInsert(placeholderData, queryParserResult);
                return new InsertIntoContainerCommand<>(queryParserResult.getTableName(), data);

            }

        } else if (queryParserResult.getType() == QueryParserResult.ResultType.SELECT) {

            if (StringUtils.isNotBlank(queryParserResult.getId(placeholderData))) {

                return new SelectFromContainerByIdCommand()
                        .setContainerName(queryParserResult.getTableName())
                        .setId(queryParserResult.getId(placeholderData));

            } else {

                Tuple2<String, String> selectIdRange = queryParserResult.getSelectIdRange(placeholderData);
                return new SelectFromContainerByIdRangeCommand()
                        .setContainerName(queryParserResult.getTableName())
                        .setIdMin(selectIdRange._1)
                        .setIdMax(selectIdRange._2);

            }


        }

        return new NoOperationCommand(sqlCommand, placeholderData);
    }

}
