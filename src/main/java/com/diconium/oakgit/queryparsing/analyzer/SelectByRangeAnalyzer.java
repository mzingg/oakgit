package com.diconium.oakgit.queryparsing.analyzer;

import com.diconium.oakgit.engine.Command;
import com.diconium.oakgit.engine.commands.ErrorCommand;
import com.diconium.oakgit.engine.commands.SelectFromContainerByIdRangeCommand;
import com.diconium.oakgit.queryparsing.*;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.JdbcParameter;
import net.sf.jsqlparser.expression.StringValue;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.expression.operators.relational.GreaterThan;
import net.sf.jsqlparser.expression.operators.relational.MinorThan;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;

public class SelectByRangeAnalyzer implements QueryAnalyzer {

    private static final String COLUMN_NAME_ID = "ID";
    private final Pattern SELECT_BY_RANGE_PATTERN = Pattern.compile("select ([\\w\\s*,]+?) from ([\\w_]+) where ID > \\? and ID < \\? order by ID");

    @Override
    public QueryMatchResult matchAndCollect(String sqlQuery) {
        return withPatternMatch(sqlQuery, SELECT_BY_RANGE_PATTERN, (result, matcher) -> {
            String fieldDeclaration = matcher.group(1);
            String tableName = matcher.group(2);
            result.setCommandSupplier(placeholderData -> new SelectFromContainerByIdRangeCommand()
                .setResultFieldList(parseFieldList(fieldDeclaration))
                .setContainerName(tableName)
                .setIdMin(placeholderData.get(1).toString())
                .setIdMax(placeholderData.get(2).toString())
            );
            return result;
        });
    }

    @Override
    public boolean interestedIn(Statement statement) {
        return statement instanceof Select &&
            ((Select) statement).getSelectBody() instanceof PlainSelect &&
            ((PlainSelect) ((Select) statement).getSelectBody()).getWhere() instanceof AndExpression;
    }

    @Override
    public QueryParserResult getParserResult(Statement statement) {
        return queryParserFor(statement, Select.class, QueryParserResult.ResultType.SELECT);
    }

    @Override
    public String getTableName(Statement statement) {
        return whileInterestedOrThrow(statement, Select.class, stm -> {
            PlainSelect selectBody = ((PlainSelect) stm.getSelectBody());
            return ((Table) selectBody.getFromItem()).getName();
        });
    }

    @Override
    public Optional<QueryId> getId(Statement statement, Map<Integer, Object> placeholderData) {
        return whileInterestedOrThrow(statement, Select.class, selectStatement -> {
            PlainSelect selectBody = ((PlainSelect) selectStatement.getSelectBody());
            return extractIdFromWhere(selectBody.getWhere(), placeholderData);
        });
    }

    @Override
    public Command createCommand(Statement statement, Map<Integer, Object> placeholderData) {
        Optional<RangeQueryId> id = getId(statement, placeholderData).map(i -> (RangeQueryId) i);

        if (id.isPresent()) {
            RangeQueryId rangeQueryId = id.get();
            return new SelectFromContainerByIdRangeCommand()
                .setContainerName(getTableName(statement))
                .setIdMin(rangeQueryId.leftValue())
                .setIdMax(rangeQueryId.rightValue());
        }

        return new ErrorCommand("Cannot create command with an invalid id");
    }

    private Optional<QueryId> extractIdFromWhere(Expression whereExpression, Map<Integer, Object> placeholderData) {
        if (whereExpression instanceof AndExpression) {
            AndExpression andExpression = (AndExpression) whereExpression;
            if (andExpression.getLeftExpression() instanceof GreaterThan && andExpression.getRightExpression() instanceof MinorThan) {
                GreaterThan leftExpression = (GreaterThan) andExpression.getLeftExpression();
                MinorThan rightExpression = (MinorThan) andExpression.getRightExpression();

                String leftColumnName = ((Column) leftExpression.getLeftExpression()).getColumnName();
                Object leftColumnValue = StringUtils.EMPTY;
                if (leftExpression.getRightExpression() instanceof StringValue) {
                    StringValue value = (StringValue) leftExpression.getRightExpression();
                    leftColumnValue = value.getValue();
                } else if (leftExpression.getRightExpression() instanceof JdbcParameter) {
                    JdbcParameter value = (JdbcParameter) leftExpression.getRightExpression();
                    leftColumnValue = placeholderData.getOrDefault(value.getIndex(), "?#" + value.getIndex());
                }

                String rightColumnName = ((Column) rightExpression.getLeftExpression()).getColumnName();
                Object rightColumnValue = StringUtils.EMPTY;
                if (rightExpression.getRightExpression() instanceof StringValue) {
                    StringValue value = (StringValue) rightExpression.getRightExpression();
                    rightColumnValue = value.getValue();
                } else if (rightExpression.getRightExpression() instanceof JdbcParameter) {
                    JdbcParameter value = (JdbcParameter) rightExpression.getRightExpression();
                    rightColumnValue = placeholderData.getOrDefault(value.getIndex(), "?#" + value.getIndex());
                }

                if (leftColumnName.equals(COLUMN_NAME_ID) && rightColumnName.equals(COLUMN_NAME_ID)) {
                    return Optional.of(new RangeQueryId(leftColumnValue.toString(), rightColumnValue.toString()));
                }
            }
        }

        return Optional.empty();
    }

}
