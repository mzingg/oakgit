package com.diconium.oakgit.queryparsing.analyzer;

import com.diconium.oakgit.engine.Command;
import com.diconium.oakgit.engine.commands.SelectFromContainerByMultipleIdsCommand;
import com.diconium.oakgit.queryparsing.QueryAnalyzer;
import com.diconium.oakgit.queryparsing.QueryId;
import com.diconium.oakgit.queryparsing.QueryMatchResult;
import com.diconium.oakgit.queryparsing.QueryParserResult;
import net.sf.jsqlparser.expression.Parenthesis;
import net.sf.jsqlparser.expression.operators.relational.InExpression;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class SelectInAnalyzer implements QueryAnalyzer {

    private final Pattern SELECT_IN_PATTERN = Pattern.compile("select ([\\w\\s*,]+?) from ([\\w_]+) where \\(?ID in.+");

    @Override
    public QueryMatchResult matchAndCollect(String sqlQuery) {
        return withPatternMatch(sqlQuery, SELECT_IN_PATTERN, (result, matcher) -> {
            String fieldDeclaration = matcher.group(1);
            String tableName = matcher.group(2);

            result.setCommandSupplier(placeholderData -> {
                List<String> idList = placeholderData.values().stream()
                    .map(Object::toString)
                    .collect(Collectors.toList());
                if (idList.size() != StringUtils.countMatches(result.getOriginQuery(), '?')) {
                    throw new IllegalStateException("list of ids does not match ? count in query");
                }

                return new SelectFromContainerByMultipleIdsCommand()
                    .setResultFieldList(parseFieldList(fieldDeclaration))
                    .setContainerName(tableName)
                    .setIds(idList);
            });
            return result;
        });
    }

    @Override
    public boolean interestedIn(Statement statement) {
        return statement instanceof Select &&
            ((Select) statement).getSelectBody() instanceof PlainSelect &&
            (((PlainSelect) ((Select) statement).getSelectBody()).getWhere() instanceof Parenthesis ||
                ((PlainSelect) ((Select) statement).getSelectBody()).getWhere() instanceof InExpression);
    }

    @Override
    public QueryParserResult getParserResult(Statement statement) {
        return queryParserFor(statement, Select.class, QueryParserResult.ResultType.SELECT);
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
            .setContainerName(getTableName(statement))
            .setIds(placeholderData.values().stream().map(o -> (String) o).collect(Collectors.toList()));
    }
}
