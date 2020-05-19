package com.diconium.oakgit.queryparsing.analyzer;

import com.diconium.oakgit.engine.Command;
import com.diconium.oakgit.engine.commands.CreateContainerCommand;
import com.diconium.oakgit.queryparsing.QueryAnalyzer;
import com.diconium.oakgit.queryparsing.QueryId;
import com.diconium.oakgit.queryparsing.QueryMatchResult;
import com.diconium.oakgit.queryparsing.QueryParserResult;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.create.table.CreateTable;

import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;

public class CreateAnalyzer implements QueryAnalyzer {

    private final Pattern CREATE_PATTERN = Pattern.compile("create table ([\\w_]+) \\(.+\\)");

    @Override
    public QueryMatchResult matchAndCollect(String sqlQuery) {
        return withPatternMatch(sqlQuery, CREATE_PATTERN, (result, matcher) -> {
            String tableName = matcher.group(1);
            result.setCommandSupplier(placeholderData -> new CreateContainerCommand().setContainerName(tableName));
            return result;
        });
    }

    @Override
    public boolean interestedIn(Statement statement) {
        return statement instanceof CreateTable;
    }

    @Override
    public QueryParserResult getParserResult(Statement statement) {
        return queryParserFor(statement, CreateTable.class, QueryParserResult.ResultType.CREATE);
    }

    @Override
    public String getTableName(Statement statement) {
        return whileInterestedOrThrow(statement, CreateTable.class,
            stm -> stm.getTable().getName()
        );
    }

    @Override
    public Optional<QueryId> getId(Statement statement, Map<Integer, Object> placeholderData) {
        return whileInterestedOrThrow(statement, CreateTable.class,
            stm -> Optional.empty()
        );
    }

    @Override
    public Command createCommand(Statement statement, Map<Integer, Object> placeholderData) {
        return whileInterestedOrThrow(statement, CreateTable.class,
            stm -> new CreateContainerCommand().setContainerName(stm.getTable().getName())
        );
    }
}
