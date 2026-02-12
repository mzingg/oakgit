package oakgit.engine.query.analyzer;

import java.util.regex.Pattern;
import oakgit.engine.commands.DeleteByModifiedRangeCommand;
import oakgit.engine.query.QueryAnalyzer;
import oakgit.engine.query.QueryMatchResult;

public class DeleteByModifiedRangeAnalyzer implements QueryAnalyzer {

  private static final Pattern DELETE_BY_MODIFIED_RANGE_PATTERN =
      Pattern.compile("delete from ([\\w_]+) where MODIFIED > \\? and MODIFIED < \\?");

  @Override
  public QueryMatchResult matchAndCollect(String sqlQuery) {
    return withPatternMatch(
        sqlQuery,
        DELETE_BY_MODIFIED_RANGE_PATTERN,
        (result, matcher) -> {
          String tableName = matcher.group(1);
          result.setCommandSupplier(
              (placeholderData, selectionLimit) -> new DeleteByModifiedRangeCommand(tableName));
          return result;
        });
  }
}
