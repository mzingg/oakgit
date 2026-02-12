package oakgit.engine.query.analyzer;

import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import oakgit.engine.commands.DeleteByIdListCommand;
import oakgit.engine.query.QueryAnalyzer;
import oakgit.engine.query.QueryMatchResult;

public class DeleteByIdListAnalyzer implements QueryAnalyzer {

  private static final Pattern DELETE_BY_ID_LIST_PATTERN =
      Pattern.compile("delete from ([\\w_]+) where ID in \\(.+\\)");

  @Override
  public QueryMatchResult matchAndCollect(String sqlQuery) {
    return withPatternMatch(
        sqlQuery,
        DELETE_BY_ID_LIST_PATTERN,
        (result, matcher) -> {
          String tableName = matcher.group(1);
          result.setCommandSupplier(
              (placeholderData, selectionLimit) -> {
                List<String> idList =
                    placeholderData
                        .valueStream()
                        .map(Object::toString)
                        .collect(Collectors.toList());
                return new DeleteByIdListCommand<>(tableName, idList);
              });
          return result;
        });
  }
}
