package oakgit.engine.query.analyzer;

import java.util.regex.Pattern;
import oakgit.engine.commands.SelectCountByDeletedOnceCommand;
import oakgit.engine.query.QueryAnalyzer;
import oakgit.engine.query.QueryMatchResult;

public class SelectCountByDeletedOnceAnalyzer implements QueryAnalyzer {

  private static final Pattern PATTERN =
      Pattern.compile("select COUNT\\(\\*\\) from ([\\w_]+) where DELETEDONCE = \\?");

  @Override
  public QueryMatchResult matchAndCollect(String sqlQuery) {
    return withPatternMatch(
        sqlQuery,
        PATTERN,
        (result, matcher) -> {
          String tableName = matcher.group(1);
          result.setCommandSupplier(
              (placeholderData, selectionLimit) ->
                  new SelectCountByDeletedOnceCommand(tableName, placeholderData.getInteger(1)));
          return result;
        });
  }
}
