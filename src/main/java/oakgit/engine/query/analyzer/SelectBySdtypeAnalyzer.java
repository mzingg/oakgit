package oakgit.engine.query.analyzer;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import oakgit.engine.commands.SelectBySdtypeCommand;
import oakgit.engine.query.QueryAnalyzer;
import oakgit.engine.query.QueryMatchResult;

public class SelectBySdtypeAnalyzer implements QueryAnalyzer {

  private static final Pattern SELECT_BY_SDTYPE_PATTERN =
      Pattern.compile(
          "select ([\\w\\s*,]+?) from ([\\w_]+)"
              + " where SDTYPE in \\((\\?(?:,\\s*\\?)*)\\)"
              + "\\s+and SDMAXREVTIME <= \\?"
              + " and VERSION >= \\?");

  @Override
  public QueryMatchResult matchAndCollect(String sqlQuery) {
    return withPatternMatch(
        sqlQuery,
        SELECT_BY_SDTYPE_PATTERN,
        (result, matcher) -> {
          String fieldDeclaration = matcher.group(1);
          String tableName = matcher.group(2);
          String inPlaceholders = matcher.group(3);
          int sdTypeCount = inPlaceholders.split("\\?", -1).length - 1;

          result.setCommandSupplier(
              (placeholderData, selectionLimit) -> {
                List<Integer> sdTypes = new ArrayList<>();
                for (int i = 1; i <= sdTypeCount; i++) {
                  sdTypes.add(placeholderData.getInteger(i));
                }
                long sdMaxRevTime = placeholderData.getLong(sdTypeCount + 1);
                int minVersion = placeholderData.getInteger(sdTypeCount + 2);

                return new SelectBySdtypeCommand(tableName, sdTypes, sdMaxRevTime, minVersion)
                    .setResultFieldList(parseFieldList(fieldDeclaration));
              });
          return result;
        });
  }
}
