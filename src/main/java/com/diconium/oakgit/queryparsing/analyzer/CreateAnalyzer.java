package com.diconium.oakgit.queryparsing.analyzer;

import com.diconium.oakgit.engine.commands.CreateContainerCommand;
import com.diconium.oakgit.queryparsing.QueryAnalyzer;
import com.diconium.oakgit.queryparsing.QueryMatchResult;

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

}
