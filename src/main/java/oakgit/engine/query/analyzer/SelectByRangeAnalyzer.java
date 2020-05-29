package oakgit.engine.query.analyzer;

import oakgit.engine.commands.SelectFromContainerByIdRangeCommand;
import oakgit.engine.query.QueryAnalyzer;
import oakgit.engine.query.QueryMatchResult;

import java.util.regex.Pattern;

public class SelectByRangeAnalyzer implements QueryAnalyzer {

    private final Pattern SELECT_BY_RANGE_PATTERN = Pattern.compile("select ([\\w\\s*,]+?) from ([\\w_]+) where ID > \\? and ID < \\? order by ID");

    @Override
    public QueryMatchResult matchAndCollect(String sqlQuery) {
        return withPatternMatch(sqlQuery, SELECT_BY_RANGE_PATTERN, (result, matcher) -> {
            String fieldDeclaration = matcher.group(1);
            String tableName = matcher.group(2);
            result.setCommandSupplier(placeholderData -> new SelectFromContainerByIdRangeCommand(
                    tableName,
                    placeholderData.getString(1),
                    placeholderData.getString(2)
            ).setResultFieldList(parseFieldList(fieldDeclaration)));

            return result;
        });
    }

}
