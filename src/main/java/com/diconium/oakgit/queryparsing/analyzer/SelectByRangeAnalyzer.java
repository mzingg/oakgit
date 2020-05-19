package com.diconium.oakgit.queryparsing.analyzer;

import com.diconium.oakgit.engine.commands.SelectFromContainerByIdRangeCommand;
import com.diconium.oakgit.queryparsing.QueryAnalyzer;
import com.diconium.oakgit.queryparsing.QueryMatchResult;

import java.util.regex.Pattern;

public class SelectByRangeAnalyzer implements QueryAnalyzer {

    private final Pattern SELECT_BY_RANGE_PATTERN = Pattern.compile("select ([\\w\\s*,]+?) from ([\\w_]+) where ID > \\? and ID < \\? order by ID");

    @Override
    public QueryMatchResult matchAndCollect(String sqlQuery) {
        return withPatternMatch(sqlQuery, SELECT_BY_RANGE_PATTERN, (result, matcher) -> {
            String fieldDeclaration = matcher.group(1);
            String tableName = matcher.group(2);
            result.setCommandSupplier(placeholderData -> new SelectFromContainerByIdRangeCommand()
                .setResultFieldList(parseFieldList(fieldDeclaration))
                .setContainerName(tableName)
                .setIdMin(placeholderData.get(1).toString())
                .setIdMax(placeholderData.get(2).toString())
            );
            return result;
        });
    }

}
