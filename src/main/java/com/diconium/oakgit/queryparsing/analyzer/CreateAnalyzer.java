package com.diconium.oakgit.queryparsing.analyzer;

import com.diconium.oakgit.queryparsing.QueryAnalyzer;
import com.diconium.oakgit.queryparsing.QueryParserResult;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.create.table.CreateTable;

import java.util.Map;

public class CreateAnalyzer implements QueryAnalyzer {

    @Override
    public boolean interestedIn(Statement statement) {
        return statement instanceof CreateTable;
    }

    @Override
    public QueryParserResult getParserResult(Statement statement) {
        if (interestedIn(statement)) {
            CreateTable createTableStatement = (CreateTable) statement;
            QueryParserResult result = new QueryParserResult(this, createTableStatement);

            result.setTableName(createTableStatement.getTable().getName());

            return result;
        }
        return QueryParserResult.Error(this, CreateTable.class, "statement must be of type CreateTable");
    }

    @Override
    public String getId(Statement statement, Map<Integer, Object> placeholderData) {
        return null;
    }

    @Override
    public QueryParserResult.ResultType getResultType() {
        return QueryParserResult.ResultType.CREATE;
    }

    public String getTableName(CreateTable createTableStatement) {
        return createTableStatement.getTable().getName();
    }
}
