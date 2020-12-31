package oakgit.engine.query.analyzer;

import oakgit.engine.commands.SelectFromContainerByModifiedCommand;
import oakgit.engine.query.QueryAnalyzer;
import oakgit.engine.query.QueryMatchResult;

import java.util.List;
import java.util.regex.Pattern;

public class SelectByModifiedAnalyzer implements QueryAnalyzer {

  private static final Pattern SELECT_BY_MODIFIED_PATTERN = Pattern.compile("select ([\\w\\s*,?=()]+?) from ([\\w_]+) where MODIFIED >= (?:'([^?]+)'|\\?) order by ID FETCH FIRST (\\d+) ROWS ONLY");

  @Override
  public QueryMatchResult matchAndCollect(String sqlQuery) {
    return withPatternMatch(sqlQuery, SELECT_BY_MODIFIED_PATTERN, (result, matcher) -> {
      String fieldDeclaration = matcher.group(1);
      String tableName = matcher.group(2);
      String modifiedValue = matcher.group(3);
      String limitValue = matcher.group(4);
      result.setCommandSupplier((placeholderData, selectionLimit) -> {
        List<String> resultFieldList = parseFieldList(fieldDeclaration);
        return new SelectFromContainerByModifiedCommand(
            tableName,
            Long.parseLong(modifiedValue),
            Integer.parseInt(limitValue)
        ).setResultFieldList(resultFieldList);
      });
      return result;
    });
  }

}
