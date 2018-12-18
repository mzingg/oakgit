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
import java.util.regex.Pattern;

public class QueryParser {

    private static final String SELECT_BY_ID_QUERY = "select \\* from (\\w+) where ID = '(\\d+)'";

    // TODO: Refactor unit tests to only test this method
    public QueryParserResult parse(String sqlQuery) {
        try {
            return new QueryParserResult(getType(sqlQuery))
                    .withTableName(getTableName(sqlQuery))
                    .withData(getData(sqlQuery))
                    .withID(getId(sqlQuery));
        } catch (JSQLParserException e) {
            return QueryParserResult.ERROR_RESULT;
        }
    }

    // TODO: Refactor all private methods so that QueryParseResult can be filled in one go.
    //  Do NOT start with this without having unit tests (see todos above)!
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

    // TODO: refactor code so that this method is private
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

    
 // TODO: Re factor code so that this method is private
    protected static String getId(String sqlCommand) throws JSQLParserException {
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
