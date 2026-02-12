package oakgit.engine.query.analyzer;

import java.util.regex.Pattern;
import oakgit.engine.commands.SelectByDeletedOnceAndModifiedRangeCommand;
import oakgit.engine.query.QueryAnalyzer;
import oakgit.engine.query.QueryMatchResult;

public class SelectByDeletedOnceAndModifiedRangeAnalyzer implements QueryAnalyzer {

  private static final Pattern SELECT_BY_DELETEDONCE_AND_MODIFIED_RANGE_PATTERN =
      Pattern.compile(
          "select ([\\w\\s*,]+?) from ([\\w_]+)"
              + " where DELETEDONCE = \\? and MODIFIED < \\? and MODIFIED >= \\?");

  @Override
  public QueryMatchResult matchAndCollect(String sqlQuery) {
    return withPatternMatch(
        sqlQuery,
        SELECT_BY_DELETEDONCE_AND_MODIFIED_RANGE_PATTERN,
        (result, matcher) -> {
          String fieldDeclaration = matcher.group(1);
          String tableName = matcher.group(2);

          result.setCommandSupplier(
              (placeholderData, selectionLimit) ->
                  new SelectByDeletedOnceAndModifiedRangeCommand(
                          tableName,
                          placeholderData.getInteger(1),
                          placeholderData.getLong(2),
                          placeholderData.getLong(3))
                      .setResultFieldList(parseFieldList(fieldDeclaration)));
          return result;
        });
  }
}
