package com.diconium.oakgit.queryparsing.analyzer;

import com.diconium.oakgit.queryparsing.QueryAnalyzer;
import com.diconium.oakgit.queryparsing.QueryId;
import com.diconium.oakgit.queryparsing.QueryParserResult;
import io.vavr.Tuple;
import io.vavr.Tuple2;
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

    @Override
    public boolean interestedIn(Statement statement) {
        return statement instanceof Insert;
    }

    @Override
    public QueryParserResult getParserResult(Statement statement) {
        return queryParserFor(statement, Insert.class, QueryParserResult.ResultType.INSERT);
    }


    //            result.setInsertColumns(insertStatement.getColumns());
//            result.setInsertExpressions(((ExpressionList) insertStatement.getItemsList()).getExpressions());
    @Override
    public Optional<QueryId> getId(Statement statement, Map<Integer, Object> placeholderData) {
        return whileInterestedOrThrow(statement, Insert.class, stm -> Optional.empty());
    }

    @Override
    public String getTableName(Statement statement) {
        return whileInterestedOrThrow(statement, Insert.class, stm -> stm.getTable().getName());
    }

    public Tuple2<String, String> getSelectIdRange(Map<Integer, Object> placeholderData) {
//        if (getType() == ResultType.SELECT) {
//            if (whereExpression instanceof AndExpression) {
//                AndExpression andExpression = (AndExpression)whereExpression;
//                if (andExpression.getLeftExpression() instanceof GreaterThan && andExpression.getRightExpression() instanceof MinorThan) {
//                    GreaterThan leftExpression = (GreaterThan) andExpression.getLeftExpression();
//                    MinorThan rightExpression = (MinorThan) andExpression.getRightExpression();
//
//                    String leftColumnName = ((Column) leftExpression.getLeftExpression()).getColumnName();
//                    Object leftColumnValue = StringUtils.EMPTY;
//                    if (leftExpression.getRightExpression() instanceof StringValue) {
//                        StringValue value = (StringValue) leftExpression.getRightExpression();
//                        leftColumnValue = value.getValue();
//                    } else if (leftExpression.getRightExpression() instanceof JdbcParameter) {
//                        JdbcParameter value = (JdbcParameter) leftExpression.getRightExpression();
//                        leftColumnValue = placeholderData.getOrDefault(value.getIndex(), "?#" + value.getIndex());
//                    }
//
//                    String rightColumnName = ((Column) rightExpression.getLeftExpression()).getColumnName();
//                    Object rightColumnValue = StringUtils.EMPTY;
//                    if (rightExpression.getRightExpression() instanceof StringValue) {
//                        StringValue value = (StringValue) rightExpression.getRightExpression();
//                        rightColumnValue = value.getValue();
//                    } else if (rightExpression.getRightExpression() instanceof JdbcParameter) {
//                        JdbcParameter value = (JdbcParameter) rightExpression.getRightExpression();
//                        rightColumnValue = placeholderData.getOrDefault(value.getIndex(), "?#" + value.getIndex());
//                    }
//
//                    if (leftColumnName.equals(COLUMN_NAME_ID) && rightColumnName.equals(COLUMN_NAME_ID)) {
//                        return Tuple.of(leftColumnValue.toString(), rightColumnValue.toString());
//                    }
//
//                }
//            }
//        }

        return Tuple.of("0", "0");
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

                result.put(columnName, columnValue);
            }

            return result;
        });
    }
}
