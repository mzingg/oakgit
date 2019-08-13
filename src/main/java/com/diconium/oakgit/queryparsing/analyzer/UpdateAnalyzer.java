package com.diconium.oakgit.queryparsing.analyzer;

import com.diconium.oakgit.queryparsing.QueryAnalyzer;
import com.diconium.oakgit.queryparsing.QueryParserResult;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.update.Update;

import java.util.Map;

public class UpdateAnalyzer implements QueryAnalyzer {

    @Override
    public boolean interestedIn(Statement statement) {
        return statement instanceof Update;
    }

    @Override
    public QueryParserResult getParserResult(Statement statement) {
        return whileInterested(statement, Update.class, updateStatement -> {
            QueryParserResult result = new QueryParserResult(this, updateStatement);

            result.setTableName(updateStatement.getTables().iterator().next().getName());

            return result;
        }, () -> QueryParserResult.Error(this, Update.class, "statement must be of type Update"));
    }

    @Override
    public String getId(Statement statement, Map<Integer, Object> placeholderData) {
        return INVALID_ID;
    }

    @Override
    public QueryParserResult.ResultType getResultType() {
        return QueryParserResult.ResultType.UPDATE;
    }
}
