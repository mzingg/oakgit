package com.diconium.oakgit.engine;

import com.diconium.oakgit.queryparsing.QueryParser;
import com.diconium.oakgit.queryparsing.QueryParserResult;
import com.diconium.oakgit.engine.commands.*;
import com.diconium.oakgit.engine.model.NodeAndSettingsEntry;
import com.diconium.oakgit.engine.model.MetaDataEntry;
import com.diconium.oakgit.engine.model.UpdateSet;
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

        if (!queryParserResult.isValid()) {
            return new ErrorCommand("Error while parsing the query " + sqlCommand);
        }

        if (queryParserResult.getType() == QueryParserResult.ResultType.CREATE) {

            return new CreateContainerCommand()
                    .setContainerName(queryParserResult.getTableName());

        } else if (queryParserResult.getType() == QueryParserResult.ResultType.INSERT) {

            if (queryParserResult.getTableName().equals("DATASTORE_META")) {

                MetaDataEntry data = new MetaDataEntry()
                        .setId(queryParserResult.getId(placeholderData));
                return new InsertIntoContainerCommand<>(MetaDataEntry.class)
                        .setContainerName(queryParserResult.getTableName())
                        .setData(data);

            } else {

                NodeAndSettingsEntry data = NodeAndSettingsEntry.buildNodeSettingsDataForInsert(placeholderData, queryParserResult);
                return new InsertIntoContainerCommand<>(NodeAndSettingsEntry.class)
                        .setContainerName(queryParserResult.getTableName())
                        .setData(data);

            }

        } else if (queryParserResult.getType() == QueryParserResult.ResultType.SELECT) {

            if (StringUtils.isNotBlank(queryParserResult.getId(placeholderData))) {

                return new SelectFromContainerByIdCommand()
                        .setContainerName(queryParserResult.getTableName())
                        .setId(queryParserResult.getId(placeholderData));

            } else {

//                Tuple2<String, String> selectIdRange = queryParserResult.getSelectIdRange(placeholderData);
//                return new SelectFromContainerByIdRangeCommand()
//                        .setContainerName(queryParserResult.getTableName())
//                        .setIdMin(selectIdRange._1)
//                        .setIdMax(selectIdRange._2);

            }

        } else if (queryParserResult.getType() == QueryParserResult.ResultType.UPDATE) {

            UpdateSet data = new UpdateSet()
                    .withValue("newModified", placeholderData.get(1)) // placeholder 2 is the same as 1
                    .withValue("newHasBinary", placeholderData.get(3))
                    .withValue("newDeletedOnce", placeholderData.get(4))
                    .withValue("newModCount", placeholderData.get(5))
                    .withValue("newCModCount", placeholderData.get(6))
                    .withValue("dsizeAddition", new Long((Integer)placeholderData.get(7)))
                    .withValue("newData", placeholderData.get(8))
                    .withValue("newVersion", 2);

            return new UpdatDataInContainerCommand()
                    .setId((String) placeholderData.get(9))
                    .setModCount((Long) placeholderData.get(10))
                    .setContainerName(queryParserResult.getTableName())
                    .setData(data);

        }

        return new NoOperationCommand(sqlCommand, placeholderData);
    }

}
