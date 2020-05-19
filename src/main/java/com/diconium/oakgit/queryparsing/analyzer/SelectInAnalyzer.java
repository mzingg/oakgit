package com.diconium.oakgit.queryparsing.analyzer;

import com.diconium.oakgit.engine.commands.SelectFromContainerByMultipleIdsCommand;
import com.diconium.oakgit.queryparsing.QueryAnalyzer;
import com.diconium.oakgit.queryparsing.QueryMatchResult;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
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

}
