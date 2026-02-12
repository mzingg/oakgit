package oakgit.engine.query.analyzer;

import java.util.regex.Pattern;
import oakgit.engine.commands.CreateIndexCommand;
import oakgit.engine.query.QueryAnalyzer;
import oakgit.engine.query.QueryMatchResult;

public class CreateIndexAnalyzer implements QueryAnalyzer {

  private final Pattern CREATE_INDEX_PATTERN =
      Pattern.compile("create index ([\\w_]+) on ([\\w_]+) \\([\\w_]+(?:,\\s*[\\w_]+)*\\)");

  @Override
  public QueryMatchResult matchAndCollect(String sqlQuery) {
    return withPatternMatch(
        sqlQuery,
        CREATE_INDEX_PATTERN,
        (result, matcher) -> {
          String indexName = matcher.group(1);
          String tableName = matcher.group(2);
          result.setCommandSupplier(
              (placeholderData, selectionLimit) -> new CreateIndexCommand(indexName, tableName));
          return result;
        });
  }
}
