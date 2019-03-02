package com.diconium.oakgit.commons;

import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.LongValue;
import net.sf.jsqlparser.expression.StringValue;
import net.sf.jsqlparser.expression.operators.relational.EqualsTo;
import net.sf.jsqlparser.expression.operators.relational.ExpressionList;
import net.sf.jsqlparser.expression.operators.relational.ItemsList;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.create.table.CreateTable;
import net.sf.jsqlparser.statement.insert.Insert;
import net.sf.jsqlparser.statement.select.*;

import net.sf.jsqlparser.statement.update.Update;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QueryParser {

    public QueryParserResult parse(String sqlQuery) {
        if (sqlQuery != null) {
            try {
                return buildQueryResult(sqlQuery);
            } catch (JSQLParserException ignored) {
                // pass through to default error result
            }
        }

        return QueryParserResult.ERROR_RESULT;
    }

    private QueryParserResult buildQueryResult(String sqlQuery) throws JSQLParserException {
        Statement statement = CCJSqlParserUtil.parse(sqlQuery);
        QueryParserResult result = new QueryParserResult(getTypeFromStatementType(statement));
        setTypeSpecificAttributes(result, statement);
        return result;
    }

    private void setTypeSpecificAttributes(QueryParserResult result, Statement statement) {
        if (statement instanceof Insert) {
            Insert insertStatement = (Insert) statement;
            result.setTableName(insertStatement.getTable().getName());
            result.setInsertColumns(insertStatement.getColumns());
            result.setInsertExpressions(((ExpressionList) insertStatement.getItemsList()).getExpressions());
        } else if (statement instanceof CreateTable) {
            CreateTable createTableStatement = (CreateTable) statement;
            result.setTableName(createTableStatement.getTable().getName());
        } else if (statement instanceof Select) {
            Select selectStatement = (Select) statement;
            if (selectStatement.getSelectBody() instanceof PlainSelect) {
                PlainSelect selectBody = ((PlainSelect) selectStatement.getSelectBody());
                result.setTableName(((Table) selectBody.getFromItem()).getName());
                result.setSelectItems(selectBody.getSelectItems());
                if (selectBody.getWhere() != null) {
                    result.setWhereExpression(selectBody.getWhere());
                }
            }
        } else if (statement instanceof Update) {
            Update updateStatement = (Update) statement;
            result.setTableName(updateStatement.getTables().iterator().next().getName());
        }
    }

    private QueryParserResult.ResultType getTypeFromStatementType(Statement statement) {
        if (statement instanceof Insert) {
            return QueryParserResult.ResultType.INSERT;
        } else if (statement instanceof Select) {
            return QueryParserResult.ResultType.SELECT;
        } else if (statement instanceof CreateTable) {
            return QueryParserResult.ResultType.CREATE;
        } else if (statement instanceof Update) {
            return QueryParserResult.ResultType.UPDATE;
        }

        return QueryParserResult.ResultType.UNKNOWN;
    }

}
