package oakgit.engine.query.analyzer;

import java.util.regex.Pattern;
import oakgit.engine.commands.InsertIntoContainerCommand;
import oakgit.engine.model.DatastoreMetaEntry;
import oakgit.engine.query.QueryAnalyzer;
import oakgit.engine.query.QueryMatchResult;

public class DatastoreMetaInsertAnalyzer implements QueryAnalyzer {

  private final Pattern INSERT_PATTERN =
      Pattern.compile(
          "insert into ([\\w_]+) " + "\\(ID, LVL, LASTMOD\\) " + "values \\(\\?, \\?, \\?\\)");

  @Override
  public QueryMatchResult matchAndCollect(String sqlQuery) {
    return withPatternMatch(
        sqlQuery,
        INSERT_PATTERN,
        (result, matcher) -> {
          String tableName = matcher.group(1);
          result.setCommandSupplier(
              (placeholderData, selectionLimit) -> {
                DatastoreMetaEntry data = new DatastoreMetaEntry();
                data.setId(placeholderData.getString(1));
                data.setLvl(placeholderData.getInteger(2));
                data.setLastmod(placeholderData.getLong(3));

                return new InsertIntoContainerCommand<>(tableName, data);
              });
          return result;
        });
  }
}
