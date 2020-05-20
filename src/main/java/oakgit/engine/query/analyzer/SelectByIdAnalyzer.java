package oakgit.engine.query.analyzer;

import oakgit.engine.commands.SelectFromContainerByIdCommand;
import oakgit.engine.query.QueryAnalyzer;
import oakgit.engine.query.QueryMatchResult;
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
                return new SelectFromContainerByIdCommand(
                    tableName,
                    StringUtils.isNotBlank(idValue) ? idValue : replacement
                ).setResultFieldList(parseFieldList(fieldDeclaration));
            });
            return result;
        });
    }

}
