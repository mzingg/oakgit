package oakgit.engine.query.analyzer;

import java.util.regex.Pattern;
import oakgit.engine.commands.SelectByRangeAndModifiedCommand;
import oakgit.engine.query.QueryAnalyzer;
import oakgit.engine.query.QueryMatchResult;

public class SelectByRangeAndModifiedAnalyzer implements QueryAnalyzer {

  private static final Pattern SELECT_BY_RANGE_AND_MODIFIED_PATTERN =
      Pattern.compile(
          "select ([\\w\\s*,]+?) from ([\\w_]+) where ID > \\? and ID < \\? and MODIFIED >= \\?"
              + " order by ID FETCH FIRST (\\d+) ROWS ONLY");

  @Override
  public QueryMatchResult matchAndCollect(String sqlQuery) {
    return withPatternMatch(
        sqlQuery,
        SELECT_BY_RANGE_AND_MODIFIED_PATTERN,
        (result, matcher) -> {
          String fieldDeclaration = matcher.group(1);
          String tableName = matcher.group(2);
          int limit = Integer.parseInt(matcher.group(3));

          result.setCommandSupplier(
              (placeholderData, selectionLimit) ->
                  new SelectByRangeAndModifiedCommand<>(
                          tableName,
                          placeholderData.getString(1),
                          placeholderData.getString(2),
                          placeholderData.getLong(3),
                          Integer.min(limit, selectionLimit))
                      .setResultFieldList(parseFieldList(fieldDeclaration)));
          return result;
        });
  }
}
