package oakgit.engine.query.analyzer;

import java.util.regex.Pattern;
import oakgit.engine.commands.SelectByModifiedAndSdtypeNullCommand;
import oakgit.engine.query.QueryAnalyzer;
import oakgit.engine.query.QueryMatchResult;

public class SelectByModifiedAndSdtypeNullAnalyzer implements QueryAnalyzer {

  private static final Pattern PATTERN =
      Pattern.compile(
          "select ([\\w\\s*,]+?) from ([\\w_]+) where MODIFIED >= \\? and SDTYPE is null");

  @Override
  public QueryMatchResult matchAndCollect(String sqlQuery) {
    return withPatternMatch(
        sqlQuery,
        PATTERN,
        (result, matcher) -> {
          String fieldDeclaration = matcher.group(1);
          String tableName = matcher.group(2);
          result.setCommandSupplier(
              (placeholderData, selectionLimit) ->
                  new SelectByModifiedAndSdtypeNullCommand(tableName, placeholderData.getLong(1))
                      .setResultFieldList(parseFieldList(fieldDeclaration)));
          return result;
        });
  }
}
