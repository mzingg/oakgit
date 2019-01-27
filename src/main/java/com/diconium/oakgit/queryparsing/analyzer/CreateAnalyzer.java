package com.diconium.oakgit.queryparsing.analyzer;

import com.diconium.oakgit.engine.Command;
import com.diconium.oakgit.engine.commands.CreateContainerCommand;
import com.diconium.oakgit.queryparsing.QueryAnalyzer;
import com.diconium.oakgit.queryparsing.QueryId;
import com.diconium.oakgit.queryparsing.QueryParserResult;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.create.table.CreateTable;

import java.util.Map;
import java.util.Optional;

public class CreateAnalyzer implements QueryAnalyzer {

    @Override
    public boolean interestedIn(Statement statement) {
        return statement instanceof CreateTable;
    }

    @Override
    public QueryParserResult getParserResult(Statement statement) {
        return queryParserFor(statement, CreateTable.class);
    }

    @Override
    public String getTableName(Statement statement) {
        return whileInterestedOrThrow(statement, CreateTable.class,
                stm -> stm.getTable().getName()
        );
    }

    @Override
    public Optional<QueryId> getId(Statement statement, Map<Integer, Object> placeholderData) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Command createCommand(Statement statement, Map<Integer, Object> placeholderData) {
        return new CreateContainerCommand()
                .setOriginSql(statement.toString())
                .setPlaceholderData(placeholderData)
                .setContainerName(getTableName(statement));
    }

}
