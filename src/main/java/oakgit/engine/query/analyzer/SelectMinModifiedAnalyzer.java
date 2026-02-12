package oakgit.engine.query.analyzer;

import java.util.regex.Pattern;
import oakgit.engine.commands.SelectMinModifiedCommand;
import oakgit.engine.query.QueryAnalyzer;
import oakgit.engine.query.QueryMatchResult;

public class SelectMinModifiedAnalyzer implements QueryAnalyzer {

  private static final Pattern SELECT_MIN_MODIFIED_PATTERN =
      Pattern.compile("select MIN\\(MODIFIED\\) from ([\\w_]+) where DELETEDONCE = \\?");

  @Override
  public QueryMatchResult matchAndCollect(String sqlQuery) {
    return withPatternMatch(
        sqlQuery,
        SELECT_MIN_MODIFIED_PATTERN,
        (result, matcher) -> {
          String tableName = matcher.group(1);
          result.setCommandSupplier(
              (placeholderData, selectionLimit) -> new SelectMinModifiedCommand(tableName));
          return result;
        });
  }
}
