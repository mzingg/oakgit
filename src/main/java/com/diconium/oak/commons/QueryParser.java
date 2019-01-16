package com.diconium.oak.commons;

import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.StringValue;
import net.sf.jsqlparser.expression.operators.relational.ExpressionList;
import net.sf.jsqlparser.expression.operators.relational.ItemsList;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.create.table.CreateTable;
import net.sf.jsqlparser.statement.insert.Insert;
import net.sf.jsqlparser.statement.select.FromItem;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.select.SelectBody;
import net.sf.jsqlparser.statement.select.WithItem;

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
        return new QueryParserResult(getTypeFromStatementType(statement))
                .withTableName(getTableName(sqlQuery))
                .withData(getData(sqlQuery))
                .withID(getId(sqlQuery));
    }

    private QueryParserResult.ResultType getTypeFromStatementType(Statement statement) {
        if (statement instanceof Insert) {
            return QueryParserResult.ResultType.INSERT;
        } else if (statement instanceof Select) {
            return QueryParserResult.ResultType.SELECT;
        } else if (statement instanceof CreateTable) {
            return QueryParserResult.ResultType.CREATE;
        }

        return QueryParserResult.ResultType.UNKNOWN;
    }

    private static String getTableName(String command) throws JSQLParserException {
        String tableName = StringUtils.EMPTY;

        Statement statement = CCJSqlParserUtil.parse(command);
        if (statement instanceof Insert) {
            Insert insertStatement = (Insert) statement;
            tableName = insertStatement.getTable().getName();
        } else if (statement instanceof CreateTable) {
            CreateTable createTableStatement = (CreateTable) statement;
            tableName = createTableStatement.getTable().getName();
        } else if (statement instanceof Select) {
            Select selectStatement = (Select) statement;
            SelectBody selectBody = selectStatement.getSelectBody();
            if (selectBody instanceof PlainSelect) {
                PlainSelect plainSelect = (PlainSelect) selectBody;
                FromItem fromItem = plainSelect.getFromItem();
                if (fromItem instanceof Table) {
                    Table table = (Table) fromItem;
                    tableName = table.getName();
                }
            }
        }

        return tableName;
    }

    private static String getData(String command) throws JSQLParserException {
        String data = StringUtils.EMPTY;
        Statement statement = CCJSqlParserUtil.parse(command);
        if (statement instanceof Insert) {
            Insert insertStatement = (Insert) statement;
            List<Column> columnsList = insertStatement.getColumns();
            ItemsList itemsList = insertStatement.getItemsList();
            ExpressionList expressionList = (ExpressionList) itemsList;
            List<Expression> expressionsList = expressionList.getExpressions();
            Map<String, Object> queryMap = new HashMap<>();
            for (int i = 0; i < columnsList.size(); i++) {

                if (i < expressionsList.size() && expressionsList.get(i) instanceof StringValue) {

                    StringValue stringValue = (StringValue) expressionsList.get(i);
                    queryMap.put(columnsList.get(i).getColumnName().toLowerCase(), stringValue.getValue());
                }

            }

            data = (String) queryMap.get("data");
        }

        return data;
    }


    private static String getId(String sqlCommand) throws JSQLParserException {
    	String id = StringUtils.EMPTY;
    	 Statement statement = CCJSqlParserUtil.parse(sqlCommand);
    	 // TODO: write the logic to get the Id from select query
    	if (statement instanceof Select) {

    		 Select selectStatement = (Select) statement;

    		 List<WithItem> itemList = selectStatement.getWithItemsList();




    	}

    	 return id;

    }


}
