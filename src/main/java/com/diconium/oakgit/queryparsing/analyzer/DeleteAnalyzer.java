package com.diconium.oakgit.queryparsing.analyzer;

import com.diconium.oakgit.engine.Command;
import com.diconium.oakgit.queryparsing.QueryAnalyzer;
import com.diconium.oakgit.queryparsing.QueryId;
import com.diconium.oakgit.queryparsing.QueryParserResult;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.delete.Delete;

import java.util.Map;
import java.util.Optional;

public class DeleteAnalyzer implements QueryAnalyzer {

    @Override
    public boolean interestedIn(Statement statement) {
        return statement instanceof Delete;
    }

    @Override
    public QueryParserResult getParserResult(Statement statement) {
        return queryParserFor(statement, Delete.class, QueryParserResult.ResultType.DELETE);
    }

    @Override
    public String getTableName(Statement statement) {
        return whileInterestedOrThrow(statement, Delete.class,
                stm -> stm.getTable().getName()
        );
    }

    @Override
    public Optional<QueryId> getId(Statement statement, Map<Integer, Object> placeholderData) {
        return whileInterestedOrThrow(statement, Delete.class,
                stm -> Optional.empty()
        );
    }

    @Override
    public Command createCommand(Statement statement, Map<Integer, Object> placeholderData) {
        return null;
    }
}
