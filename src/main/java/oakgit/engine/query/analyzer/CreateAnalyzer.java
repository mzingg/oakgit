package oakgit.engine.query.analyzer;

import oakgit.engine.commands.CreateContainerCommand;
import oakgit.engine.query.QueryAnalyzer;
import oakgit.engine.query.QueryMatchResult;

import java.util.regex.Pattern;

public class CreateAnalyzer implements QueryAnalyzer {

  private final Pattern CREATE_PATTERN = Pattern.compile("create table ([\\w_]+) \\(.+\\)");

  @SuppressWarnings("rawtypes")
  @Override
  public QueryMatchResult matchAndCollect(String sqlQuery) {
    return withPatternMatch(sqlQuery, CREATE_PATTERN, (result, matcher) -> {
      String tableName = matcher.group(1);
      result.setCommandSupplier((placeholderData, selectionLimit) -> new CreateContainerCommand(tableName));
      return result;
    });
  }

}
