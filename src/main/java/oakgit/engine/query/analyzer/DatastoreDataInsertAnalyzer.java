package oakgit.engine.query.analyzer;

import oakgit.engine.commands.InsertIntoContainerCommand;
import oakgit.engine.model.DatastoreDataEntry;
import oakgit.engine.query.QueryAnalyzer;
import oakgit.engine.query.QueryMatchResult;

import java.util.regex.Pattern;

public class DatastoreDataInsertAnalyzer implements QueryAnalyzer {

  private final Pattern INSERT_PATTERN = Pattern.compile("insert into ([\\w_]+) " +
      "\\(ID, DATA\\) " +
      "values \\(\\?, \\?\\)"
  );

  @Override
  public QueryMatchResult matchAndCollect(String sqlQuery) {
    return withPatternMatch(sqlQuery, INSERT_PATTERN, (result, matcher) -> {
      String tableName = matcher.group(1);
      result.setCommandSupplier((placeholderData, selectionLimit) -> {
        DatastoreDataEntry data = new DatastoreDataEntry();
        data.setId(placeholderData.getString(1));
        data.setData(placeholderData.getBytes(2));

        return new InsertIntoContainerCommand<>(tableName, data);
      });
      return result;
    });
  }

}
