package com.diconium.oakgit.queryparsing.analyzer;

import com.diconium.oakgit.engine.Command;
import com.diconium.oakgit.engine.commands.UpdatDataInContainerCommand;
import com.diconium.oakgit.engine.model.UpdateSet;
import com.diconium.oakgit.queryparsing.*;
import net.sf.jsqlparser.expression.BinaryExpression;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.JdbcParameter;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.update.Update;

import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;

public class UpdateAnalyzer implements QueryAnalyzer {

    private final Pattern UPDATE_PATTERN = Pattern.compile("update ([\\w_]+) set (.+?) where ID = \\? and MODCOUNT = \\?");

    @Override
    public QueryMatchResult matchAndCollect(String sqlQuery) {
        return withPatternMatch(sqlQuery, UPDATE_PATTERN, (result, matcher) -> {
            String tableName = matcher.group(1);
            String setExpressions = matcher.group(2);
            result.setCommandSupplier(placeholderData -> {
                int lastPlaceholderIndex = placeholderData.size();
                boolean concatMode = result.getOriginQuery().contains("DATA || ");

                UpdateSet data = new UpdateSet().setConcatenateDataField(concatMode);
                if (concatMode) {
                    data
                        .withValue("newModified", placeholderData.get(1)) // placeholder 2 is the same as 1
                        .withValue("newHasBinary", placeholderData.get(3))
                        .withValue("newDeletedOnce", placeholderData.get(4))
                        .withValue("newModCount", placeholderData.get(5))
                        .withValue("newCModCount", placeholderData.get(6))
                        .withValue("dsizeAddition", Long.valueOf(placeholderData.get(7).toString()))
                        .withValue("newData", placeholderData.get(8))
                        .withValue("newVersion", 2);
                } else {
                    data
                        .withValue("newModified", placeholderData.get(1))
                        .withValue("newHasBinary", placeholderData.get(2))
                        .withValue("newDeletedOnce", placeholderData.get(3))
                        .withValue("newModCount", placeholderData.get(4))
                        .withValue("newCModCount", placeholderData.get(5))
                        .withValue("dsizeAddition", Long.valueOf(placeholderData.get(6).toString()))
                        .withValue("newData", placeholderData.get(7))
                        .withValue("newVersion", 2);
                }

                return new UpdatDataInContainerCommand()
                    .setContainerName(tableName)
                    .setSetExpressions(parseFieldList(setExpressions))
                    .setData(data)
                    .setId(placeholderData.get(lastPlaceholderIndex - 1).toString())
                    .setModCount((Long) placeholderData.get(lastPlaceholderIndex));
            });
            return result;
        });
    }

    @Override
    public boolean interestedIn(Statement statement) {
        return statement instanceof Update && statement.toString().contains("WHERE ID = ? AND MODCOUNT = ?");
    }

    @Override
    public QueryParserResult getParserResult(Statement statement) {
        return queryParserFor(statement, Update.class, QueryParserResult.ResultType.UPDATE);
    }

    @Override
    public String getTableName(Statement statement) {
        return whileInterestedOrThrow(statement, Update.class, stm -> stm.getTables().iterator().next().getName());
    }

    @Override
    public Optional<QueryId> getId(Statement statement, Map<Integer, Object> placeholderData) {
        return whileInterestedOrThrow(statement, Update.class, stm -> {
            AndExpression where = (AndExpression) stm.getWhere();

            Expression leftExpression = where.getLeftExpression();
            Expression rightExpression = where.getRightExpression();
            JdbcParameter leftVal = (JdbcParameter) ((BinaryExpression) leftExpression).getRightExpression();
            JdbcParameter rightVal = (JdbcParameter) ((BinaryExpression) rightExpression).getRightExpression();

            return Optional.of(new SingleValueAndModCountId(
                placeholderData.get(leftVal.getIndex()).toString(),
                placeholderData.get(rightVal.getIndex()).toString()
            ));
        });
    }

    @Override
    public Command createCommand(Statement statement, Map<Integer, Object> placeholderData) {
        return whileInterestedOrThrow(statement, Update.class, stm -> {
            boolean concatMode = statement.toString().contains("DATA || ");

            UpdateSet data = new UpdateSet()
                .setConcatenateDataField(concatMode);
            if (concatMode) {
                data
                    .withValue("newModified", placeholderData.get(1)) // placeholder 2 is the same as 1
                    .withValue("newHasBinary", placeholderData.get(3))
                    .withValue("newDeletedOnce", placeholderData.get(4))
                    .withValue("newModCount", placeholderData.get(5))
                    .withValue("newCModCount", placeholderData.get(6))
                    .withValue("dsizeAddition", Long.valueOf(placeholderData.get(7).toString()))
                    .withValue("newData", placeholderData.get(8))
                    .withValue("newVersion", 2);
            } else {
                data
                    .withValue("newModified", placeholderData.get(1))
                    .withValue("newHasBinary", placeholderData.get(2))
                    .withValue("newDeletedOnce", placeholderData.get(3))
                    .withValue("newModCount", placeholderData.get(4))
                    .withValue("newCModCount", placeholderData.get(5))
                    .withValue("dsizeAddition", Long.valueOf(placeholderData.get(6).toString()))
                    .withValue("newData", placeholderData.get(7))
                    .withValue("newVersion", 2);
            }

            return new UpdatDataInContainerCommand()
                .setId((String) placeholderData.get(9))
                .setModCount((Long) placeholderData.get(10))
                .setContainerName(stm.getTables().iterator().next().getName())
                .setData(data);
        });
    }
}
