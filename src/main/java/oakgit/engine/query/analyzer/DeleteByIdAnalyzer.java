package oakgit.engine.query.analyzer;

import java.util.regex.Pattern;
import oakgit.engine.commands.DeleteByIdCommand;
import oakgit.engine.query.QueryAnalyzer;
import oakgit.engine.query.QueryMatchResult;

public class DeleteByIdAnalyzer implements QueryAnalyzer {

  private static final Pattern DELETE_BY_ID_PATTERN =
      Pattern.compile("delete from ([\\w_]+) where ID = \\?");

  @Override
  public QueryMatchResult matchAndCollect(String sqlQuery) {
    return withPatternMatch(
        sqlQuery,
        DELETE_BY_ID_PATTERN,
        (result, matcher) -> {
          String tableName = matcher.group(1);
          result.setCommandSupplier(
              (placeholderData, selectionLimit) ->
                  new DeleteByIdCommand<>(tableName, placeholderData.getString(1)));
          return result;
        });
  }
}
