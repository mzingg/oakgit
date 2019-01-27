package com.diconium.oakgit.queryparsing.analyzer;

import com.diconium.oakgit.queryparsing.QueryAnalyzer;
import com.diconium.oakgit.queryparsing.QueryId;
import com.diconium.oakgit.queryparsing.QueryParserResult;
import lombok.RequiredArgsConstructor;
import net.sf.jsqlparser.statement.Statement;

import java.util.Map;
import java.util.Optional;

@RequiredArgsConstructor
public class EmptyAnalyzer implements QueryAnalyzer {

    private final QueryParserResult.ResultType resultType;

    @Override
    public boolean interestedIn(Statement statement) {
        throw new UnsupportedOperationException();
    }

    @Override
    public QueryParserResult getParserResult(Statement statement) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getTableName(Statement statement) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Optional<QueryId> getId(Statement statement, Map<Integer, Object> placeholderData) {
        throw new UnsupportedOperationException();
    }

}
