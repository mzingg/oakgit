package com.diconium.oakgit.queryparsing.analyzer;

import com.diconium.oakgit.queryparsing.QueryAnalyzer;
import com.diconium.oakgit.queryparsing.QueryParserResult;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.JdbcParameter;
import net.sf.jsqlparser.expression.LongValue;
import net.sf.jsqlparser.expression.StringValue;
import net.sf.jsqlparser.expression.operators.relational.EqualsTo;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;
import java.util.Optional;

public class PlainSelectAnalyzer implements QueryAnalyzer {

    private static final String COLUMN_NAME_ID = "ID";
    private static final StringValue NULL_WHERE_EXPRESSION = new StringValue(StringUtils.EMPTY);

    @Override
    public boolean interestedIn(Statement statement) {
        return statement instanceof Select && ((Select) statement).getSelectBody() instanceof PlainSelect;
    }

    @Override
    public QueryParserResult getParserResult(Statement statement) {
        return queryParserFor(statement, Select.class);
    }

    @Override
    public String getTableName(Statement statement) {
        return whileInterestedOrThrow(statement, Select.class, stm -> {
            PlainSelect selectBody = ((PlainSelect) stm.getSelectBody());
            return ((Table) selectBody.getFromItem()).getName();
        });
    }

    @Override
    public String getId(Statement statement, Map<Integer, Object> placeholderData) {
        return whileInterestedOrThrow(statement, Select.class, selectStatement -> {
            PlainSelect selectBody = ((PlainSelect) selectStatement.getSelectBody());
            return extractIdFromWhere(selectBody.getWhere(), placeholderData).orElse(QueryAnalyzer.INVALID_ID);
        });
    }

    @Override
    public QueryParserResult.ResultType getResultType() {
        return QueryParserResult.ResultType.SELECT;
    }

    private Optional<String> extractIdFromWhere(Expression whereExpression, Map<Integer, Object> placeholderData) {
        if (whereExpression instanceof EqualsTo) {
            Expression left = ((EqualsTo) whereExpression).getLeftExpression();
            Expression right = ((EqualsTo) whereExpression).getRightExpression();
            String sRight = StringUtils.EMPTY;
            if (right instanceof StringValue) {
                sRight = ((StringValue) right).getValue();
            } else if (right instanceof LongValue) {
                sRight = ((LongValue) right).getStringValue();
            } else if (right instanceof JdbcParameter) {
                sRight = placeholderData.getOrDefault(1, "#?1").toString();
            }
            if (left instanceof Column && ((Column) left).getColumnName().equals(COLUMN_NAME_ID) && StringUtils.isNotEmpty(sRight)) {
                return Optional.of(sRight);
            }
        }

        return Optional.empty();
    }
}
