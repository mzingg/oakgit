package com.diconium.oakgit.queryparsing.analyzer;

import com.diconium.oakgit.queryparsing.QueryAnalyzer;
import com.diconium.oakgit.queryparsing.QueryParserResult;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.delete.Delete;
import net.sf.jsqlparser.statement.update.Update;

import java.util.Map;

public class DeleteAnalyzer implements QueryAnalyzer {

    @Override
    public boolean interestedIn(Statement statement) {
        return statement instanceof Delete;
    }

    @Override
    public QueryParserResult getParserResult(Statement statement) {
        return whileInterested(statement, Delete.class, deleteStatement -> {
            QueryParserResult result = new QueryParserResult(this, deleteStatement);

            return result;
        }, () -> QueryParserResult.Error(this, Delete.class, "statement must be of type Delete"));
    }

    @Override
    public String getId(Statement statement, Map<Integer, Object> placeholderData) {
        return INVALID_ID;
    }

    @Override
    public QueryParserResult.ResultType getResultType() {
        return QueryParserResult.ResultType.DELETE;
    }

}
