package oakgit.engine.query.analyzer;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import oakgit.engine.commands.SelectByVersionUpgradeCommand;
import oakgit.engine.query.QueryAnalyzer;
import oakgit.engine.query.QueryMatchResult;

public class SelectByVersionUpgradeAnalyzer implements QueryAnalyzer {

  private static final Pattern SELECT_BY_VERSION_UPGRADE_PATTERN =
      Pattern.compile(
          "select ([\\w\\s*,]+?) from ([\\w_]+)"
              + " where not \\((ID like \\?(?:\\s+or ID like \\?)*)\\)"
              + " and \\(VERSION is null or VERSION < \\?\\)");

  @Override
  public QueryMatchResult matchAndCollect(String sqlQuery) {
    return withPatternMatch(
        sqlQuery,
        SELECT_BY_VERSION_UPGRADE_PATTERN,
        (result, matcher) -> {
          String fieldDeclaration = matcher.group(1);
          String tableName = matcher.group(2);
          String likeClause = matcher.group(3);
          int likeCount = likeClause.split("ID like \\?", -1).length - 1;

          result.setCommandSupplier(
              (placeholderData, selectionLimit) -> {
                List<String> excludedPatterns = new ArrayList<>();
                for (int i = 1; i <= likeCount; i++) {
                  excludedPatterns.add(placeholderData.getString(i));
                }
                int maxVersion = placeholderData.getInteger(likeCount + 1);

                return new SelectByVersionUpgradeCommand(tableName, excludedPatterns, maxVersion)
                    .setResultFieldList(parseFieldList(fieldDeclaration));
              });
          return result;
        });
  }
}
