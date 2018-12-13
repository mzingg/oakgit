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
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class QueryParser {

    private static final String SELECT_BY_ID_QUERY = "select \\* from (\\w+) where ID = '(\\d+)'";

    public QueryParserResult parse(String sqlQuery) {
        try {
            return new QueryParserResult(getType(sqlQuery))
                    .withTableName(getTableName(sqlQuery))
                    .withData(getData(sqlQuery));
        } catch (JSQLParserException e) {
            return QueryParserResult.ERROR_RESULT;
        }
    }

    private QueryParserResult.ResultType getType(String sqlQuery) {
        QueryParserResult.ResultType type = QueryParserResult.ResultType.UNKNOWN;
        if (Pattern.matches("create table .*", sqlQuery)) {
            type = QueryParserResult.ResultType.CREATE;
        } else if (Pattern.matches("insert into .*", sqlQuery)) {
            type = QueryParserResult.ResultType.INSERT;
        } else if (Pattern.matches(SELECT_BY_ID_QUERY, sqlQuery)) {
            type = QueryParserResult.ResultType.SELECT;
        }
        return type;
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

    protected static String getData(String command) throws JSQLParserException {
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

}
