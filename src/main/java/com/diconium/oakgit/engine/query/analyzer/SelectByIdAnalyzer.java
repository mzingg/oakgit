package com.diconium.oakgit.engine.query.analyzer;

import com.diconium.oakgit.engine.commands.SelectFromContainerByIdCommand;
import com.diconium.oakgit.engine.query.QueryAnalyzer;
import com.diconium.oakgit.engine.query.QueryMatchResult;
import org.apache.commons.lang3.StringUtils;

import java.util.regex.Pattern;

public class SelectByIdAnalyzer implements QueryAnalyzer {

    private final Pattern SELECT_BY_ID_PATTERN = Pattern.compile("select ([\\w\\s*,?=()]+?) from ([\\w_]+) where ID = (?:'([^?]+)'|\\?)");

    @Override
    public QueryMatchResult matchAndCollect(String sqlQuery) {
        return withPatternMatch(sqlQuery, SELECT_BY_ID_PATTERN, (result, matcher) -> {
            String fieldDeclaration = matcher.group(1);
            String tableName = matcher.group(2);
            String idValue = matcher.groupCount() == 3 ? matcher.group(3) : "";
            result.setCommandSupplier(placeholderData -> {
                String replacement = placeholderData.containsKey(1) ? placeholderData.get(1).toString() : "?#1";
                return new SelectFromContainerByIdCommand()
                    .setResultFieldList(parseFieldList(fieldDeclaration))
                    .setContainerName(tableName)
                    .setId(StringUtils.isNotBlank(idValue) ? idValue : replacement);
            });
            return result;
        });
    }

}
