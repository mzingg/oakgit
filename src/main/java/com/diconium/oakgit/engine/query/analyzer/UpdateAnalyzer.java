package com.diconium.oakgit.engine.query.analyzer;

import com.diconium.oakgit.engine.commands.UpdatDataInContainerCommand;
import com.diconium.oakgit.engine.model.UpdateSet;
import com.diconium.oakgit.engine.query.QueryAnalyzer;
import com.diconium.oakgit.engine.query.QueryMatchResult;

import java.util.regex.Pattern;

public class UpdateAnalyzer implements QueryAnalyzer {

    private final Pattern UPDATE_PATTERN = Pattern.compile("update ([\\w_]+) set (.+?) where ID = \\? and MODCOUNT = \\?");

    @Override
    public QueryMatchResult matchAndCollect(String sqlQuery) {
        return withPatternMatch(sqlQuery, UPDATE_PATTERN, (result, matcher) -> {
            String tableName = matcher.group(1);
            String setExpressions = matcher.group(2);
            result.setCommandSupplier(placeholderData -> {
                int lastPlaceholderIndex = placeholderData.size();
                boolean concatMode = result.getOriginQuery().contains("DATA || ");

                UpdateSet data = new UpdateSet().setConcatenateDataField(concatMode);
                if (concatMode) {
                    data
                        .withValue("newModified", placeholderData.get(1)) // placeholder 2 is the same as 1
                        .withValue("newHasBinary", placeholderData.get(3))
                        .withValue("newDeletedOnce", placeholderData.get(4))
                        .withValue("newModCount", placeholderData.get(5))
                        .withValue("newCModCount", placeholderData.get(6))
                        .withValue("dsizeAddition", Long.valueOf(placeholderData.get(7).toString()))
                        .withValue("newData", placeholderData.get(8))
                        .withValue("newVersion", 2);
                } else {
                    data
                        .withValue("newModified", placeholderData.get(1))
                        .withValue("newHasBinary", placeholderData.get(2))
                        .withValue("newDeletedOnce", placeholderData.get(3))
                        .withValue("newModCount", placeholderData.get(4))
                        .withValue("newCModCount", placeholderData.get(5))
                        .withValue("dsizeAddition", Long.valueOf(placeholderData.get(6).toString()))
                        .withValue("newData", placeholderData.get(7))
                        .withValue("newVersion", 2);
                }

                return new UpdatDataInContainerCommand()
                    .setContainerName(tableName)
                    .setSetExpressions(parseFieldList(setExpressions))
                    .setData(data)
                    .setId(placeholderData.get(lastPlaceholderIndex - 1).toString())
                    .setModCount((Long) placeholderData.get(lastPlaceholderIndex));
            });
            return result;
        });
    }
}
