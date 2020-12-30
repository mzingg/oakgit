package oakgit.engine.query.analyzer;

import oakgit.engine.commands.SelectFromContainerByIdRangeCommand;
import oakgit.engine.query.QueryAnalyzer;
import oakgit.engine.query.QueryMatchResult;

import java.util.regex.Pattern;

public class SelectByRangeAnalyzer implements QueryAnalyzer {

  private final Pattern SELECT_BY_RANGE_PATTERN = Pattern.compile("select ([\\w\\s*,]+?) from ([\\w_]+) where ID > \\? and ID < \\? order by ID(?: FETCH FIRST (\\d+) ROWS ONLY)?");

  @Override
  public QueryMatchResult matchAndCollect(String sqlQuery) {
    return withPatternMatch(sqlQuery, SELECT_BY_RANGE_PATTERN, (result, matcher) -> {
      String fieldDeclaration = matcher.group(1);
      String tableName = matcher.group(2);
      int parsedLimit = Integer.MAX_VALUE;
      if (matcher.group(3) != null) {
        parsedLimit = Integer.parseInt(matcher.group(3));
      }

      final int limitToPass = parsedLimit;
      result.setCommandSupplier(placeholderData -> new SelectFromContainerByIdRangeCommand(
          tableName,
          placeholderData.getString(1),
          placeholderData.getString(2),
          limitToPass
      ).setResultFieldList(parseFieldList(fieldDeclaration)));

      return result;
    });
  }

}
