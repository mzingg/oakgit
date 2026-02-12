package oakgit.engine.query.analyzer;

import java.util.regex.Pattern;
import oakgit.engine.commands.DeleteByIdAndModifiedCommand;
import oakgit.engine.query.QueryAnalyzer;
import oakgit.engine.query.QueryMatchResult;

public class DeleteByIdAndModifiedAnalyzer implements QueryAnalyzer {

  private static final Pattern DELETE_BY_ID_AND_MODIFIED_PATTERN =
      Pattern.compile("delete from ([\\w_]+) where ID = \\? and MODIFIED = \\?");

  @Override
  public QueryMatchResult matchAndCollect(String sqlQuery) {
    return withPatternMatch(
        sqlQuery,
        DELETE_BY_ID_AND_MODIFIED_PATTERN,
        (result, matcher) -> {
          String tableName = matcher.group(1);
          result.setCommandSupplier(
              (placeholderData, selectionLimit) ->
                  new DeleteByIdAndModifiedCommand<>(
                      tableName, placeholderData.getString(1), placeholderData.getLong(2)));
          return result;
        });
  }
}
