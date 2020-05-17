package com.diconium.oakgit.queryparsing.analyzer;

import com.diconium.oakgit.engine.Command;
import com.diconium.oakgit.engine.commands.ErrorCommand;
import com.diconium.oakgit.engine.commands.InsertIntoContainerCommand;
import com.diconium.oakgit.engine.model.MetaDataEntry;
import com.diconium.oakgit.engine.model.NodeAndSettingsEntry;
import com.diconium.oakgit.queryparsing.QueryAnalyzer;
import com.diconium.oakgit.queryparsing.QueryId;
import com.diconium.oakgit.queryparsing.QueryParserResult;
import com.diconium.oakgit.queryparsing.SingleValueId;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.JdbcParameter;
import net.sf.jsqlparser.expression.StringValue;
import net.sf.jsqlparser.expression.operators.relational.ExpressionList;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.insert.Insert;
import org.apache.commons.lang3.StringUtils;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class InsertAnalyzer implements QueryAnalyzer {

    private static final String COLUMN_NAME_ID = "ID";

    @Override
    public boolean interestedIn(Statement statement) {
        return statement instanceof Insert;
    }

    @Override
    public QueryParserResult getParserResult(Statement statement) {
        return queryParserFor(statement, Insert.class);
    }

    @Override
    public Optional<QueryId> getId(Statement statement, Map<Integer, Object> placeholderData) {
        return whileInterestedOrThrow(statement, Insert.class, stm -> getDataField(stm, COLUMN_NAME_ID, String.class, placeholderData).map(SingleValueId::new));
    }

    @Override
    public String getTableName(Statement statement) {
        return whileInterestedOrThrow(statement, Insert.class, stm -> stm.getTable().getName());
    }

    @Override
    public Command createCommand(Statement statement, Map<Integer, Object> placeholderData) {
        Optional<QueryId> id = getId(statement, placeholderData);

        if (id.isPresent()) {
            if (getTableName(statement).equals("DATASTORE_META")) {

                MetaDataEntry data = new MetaDataEntry()
                    .setId(id.get().value());
                return new InsertIntoContainerCommand<>(MetaDataEntry.class)
                    .setOriginSql(statement.toString())
                    .setPlaceholderData(placeholderData)
                    .setContainerName(getTableName(statement))
                    .setData(data);

            } else {

                NodeAndSettingsEntry data = NodeAndSettingsEntry.buildNodeSettingsDataForInsert(statement, placeholderData, this);
                return new InsertIntoContainerCommand<>(NodeAndSettingsEntry.class)
                    .setOriginSql(statement.toString())
                    .setPlaceholderData(placeholderData)
                    .setContainerName(getTableName(statement))
                    .setData(data);

            }
        }

        return new ErrorCommand()
            .setOriginSql(statement.toString())
            .setPlaceholderData(placeholderData)
            .setErrorMessage("Cannot create command with an invalid id");
    }

    @Override
    public Map<Object, Object> getData(Statement statement, Map<Integer, Object> placeholderData) {
        return whileInterestedOrThrow(statement, Insert.class, stm -> {
            Map<Object, Object> result = new LinkedHashMap<>();

            List<Expression> insertExpressions = ((ExpressionList) stm.getItemsList()).getExpressions();
            List<Column> insertColumns = stm.getColumns();

            for (int i = 0; i < insertColumns.size() && i < insertExpressions.size(); i++) {
                String columnName = insertColumns.get(i).getColumnName();
                Object columnValue = StringUtils.EMPTY;
                if (insertExpressions.get(i) instanceof StringValue) {
                    StringValue value = (StringValue) insertExpressions.get(i);
                    columnValue = value.getValue();
                } else if (insertExpressions.get(i) instanceof JdbcParameter) {
                    JdbcParameter value = (JdbcParameter) insertExpressions.get(i);
                    columnValue = placeholderData.getOrDefault(value.getIndex(), "?#" + value.getIndex());
                }

                result.put(columnName.toLowerCase(), columnValue);
            }

            return result;
        });
    }
}
