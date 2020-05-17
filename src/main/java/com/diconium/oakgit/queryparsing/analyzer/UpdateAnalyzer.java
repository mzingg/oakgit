package com.diconium.oakgit.queryparsing.analyzer;

import com.diconium.oakgit.engine.Command;
import com.diconium.oakgit.engine.commands.ErrorCommand;
import com.diconium.oakgit.engine.commands.UpdatDataInContainerCommand;
import com.diconium.oakgit.engine.model.UpdateSet;
import com.diconium.oakgit.queryparsing.QueryAnalyzer;
import com.diconium.oakgit.queryparsing.QueryId;
import com.diconium.oakgit.queryparsing.QueryParserResult;
import com.diconium.oakgit.queryparsing.SingleValueAndModCountId;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.JdbcParameter;
import net.sf.jsqlparser.expression.LongValue;
import net.sf.jsqlparser.expression.StringValue;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.expression.operators.relational.EqualsTo;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.update.Update;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;
import java.util.Optional;

public class UpdateAnalyzer implements QueryAnalyzer {

    private static final String COLUMN_NAME_ID = "ID";

    private static final String COLUMN_NAME_MODCOUNT = "MODCOUNT";

    @Override
    public boolean interestedIn(Statement statement) {
        return statement instanceof Update;
    }

    @Override
    public QueryParserResult getParserResult(Statement statement) {
        return queryParserFor(statement, Update.class);
    }

    @Override
    public String getTableName(Statement statement) {
        return whileInterestedOrThrow(statement, Update.class, stm -> stm.getTables().iterator().next().getName());
    }

    @Override
    public Optional<QueryId> getId(Statement statement, Map<Integer, Object> placeholderData) {
        return whileInterestedOrThrow(statement, Update.class, stm -> extractIdFromWhere(stm.getWhere(), placeholderData));
    }

    @Override
    public Command createCommand(Statement statement, Map<Integer, Object> placeholderData) {
        if (isValidPlacholderData(placeholderData)) {
            UpdateSet data = new UpdateSet()
                .withValue("newModified", placeholderData.get(1)) // placeholder 2 is the same as 1
                .withValue("newHasBinary", placeholderData.get(3))
                .withValue("newDeletedOnce", placeholderData.get(4))
                .withValue("newModCount", placeholderData.get(5))
                .withValue("newCModCount", placeholderData.get(6))
                .withValue("dsizeAddition", Long.valueOf((Integer) placeholderData.get(7)))
                .withValue("newData", placeholderData.get(8))
                .withValue("newVersion", 2);

            return new UpdatDataInContainerCommand()
                .setOriginSql(statement.toString())
                .setPlaceholderData(placeholderData)
                .setId((String) placeholderData.get(9))
                .setModCount((Long) placeholderData.get(10))
                .setContainerName(getTableName(statement))
                .setData(data);
        }

        return new ErrorCommand()
            .setOriginSql(statement.toString())
            .setPlaceholderData(placeholderData)
            .setErrorMessage("Cannot create command with invalid placeholder data");
    }

    private boolean isValidPlacholderData(Map<Integer, Object> placeholderData) {
        return placeholderData.size() == 10;
    }

    private Optional<QueryId> extractIdFromWhere(Expression whereExpression, Map<Integer, Object> placeholderData) {
        if (whereExpression instanceof AndExpression) {
            AndExpression andExpression = (AndExpression) whereExpression;
            if (andExpression.getLeftExpression() instanceof EqualsTo && andExpression.getRightExpression() instanceof EqualsTo) {
                EqualsTo leftExpression = (EqualsTo) andExpression.getLeftExpression();
                EqualsTo rightExpression = (EqualsTo) andExpression.getRightExpression();

                String idValue = extractEqualsToValue(COLUMN_NAME_ID, 9, leftExpression, placeholderData);
                String modCountValue = extractEqualsToValue(COLUMN_NAME_MODCOUNT, 10, rightExpression, placeholderData);

                if (StringUtils.isNotEmpty(idValue) && StringUtils.isNotEmpty(modCountValue)) {
                    return Optional.of(new SingleValueAndModCountId(idValue, modCountValue));
                }
            }
        }

        return Optional.empty();
    }

    private String extractEqualsToValue(String columnName, int placeholderIndex, EqualsTo leftExpression, Map<Integer, Object> placeholderData) {
        Expression left = leftExpression.getLeftExpression();
        Expression right = leftExpression.getRightExpression();
        String sRight = null;
        if (right instanceof StringValue) {
            sRight = ((StringValue) right).getValue();
        } else if (right instanceof LongValue) {
            sRight = ((LongValue) right).getStringValue();
        } else if (right instanceof JdbcParameter) {
            sRight = placeholderData.getOrDefault(placeholderIndex, "#?" + placeholderIndex).toString();
        }
        if (left instanceof Column && ((Column) left).getColumnName().equals(columnName) && StringUtils.isNotEmpty(sRight)) {
            return sRight;
        }
        return null;
    }

}
