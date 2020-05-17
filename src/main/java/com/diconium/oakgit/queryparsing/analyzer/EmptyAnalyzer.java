package com.diconium.oakgit.queryparsing.analyzer;

import com.diconium.oakgit.engine.Command;
import com.diconium.oakgit.engine.commands.NoOperationCommand;
import com.diconium.oakgit.queryparsing.QueryAnalyzer;
import com.diconium.oakgit.queryparsing.QueryId;
import com.diconium.oakgit.queryparsing.QueryParserResult;
import net.sf.jsqlparser.statement.Statement;

import java.util.Map;
import java.util.Optional;

public class EmptyAnalyzer implements QueryAnalyzer {

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

    @Override
    public Command createCommand(Statement statement, Map<Integer, Object> placeholderData) {
        return new NoOperationCommand()
            .setOriginSql(statement.toString())
            .setPlaceholderData(placeholderData);
    }

}
