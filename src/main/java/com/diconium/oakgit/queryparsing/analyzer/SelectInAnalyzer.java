package com.diconium.oakgit.queryparsing.analyzer;

import com.diconium.oakgit.engine.Command;
import com.diconium.oakgit.engine.commands.SelectFromContainerByMultipleIdsCommand;
import com.diconium.oakgit.queryparsing.QueryAnalyzer;
import com.diconium.oakgit.queryparsing.QueryId;
import com.diconium.oakgit.queryparsing.QueryParserResult;
import net.sf.jsqlparser.expression.Parenthesis;
import net.sf.jsqlparser.expression.operators.relational.InExpression;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class SelectInAnalyzer implements QueryAnalyzer {

    @Override
    public boolean interestedIn(Statement statement) {
        return statement instanceof Select &&
                ((Select) statement).getSelectBody() instanceof PlainSelect &&
                (((PlainSelect) ((Select) statement).getSelectBody()).getWhere() instanceof Parenthesis ||
                ((PlainSelect) ((Select) statement).getSelectBody()).getWhere() instanceof InExpression);
    }

    @Override
    public QueryParserResult getParserResult(Statement statement) {
        return queryParserFor(statement, Select.class);
    }

    @Override
    public String getTableName(Statement statement) {
        return whileInterestedOrThrow(statement, Select.class, stm -> {
            PlainSelect selectBody = ((PlainSelect) stm.getSelectBody());
            return ((Table) selectBody.getFromItem()).getName();
        });
    }

    @Override
    public Optional<QueryId> getId(Statement statement, Map<Integer, Object> placeholderData) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Command createCommand(Statement statement, Map<Integer, Object> placeholderData) {
        return new SelectFromContainerByMultipleIdsCommand()
                .setOriginSql(statement.toString())
                .setPlaceholderData(placeholderData)
                .setContainerName(getTableName(statement))
                .setIds(placeholderData.values().stream().map(o -> (String) o).collect(Collectors.toList()));
    }
}
