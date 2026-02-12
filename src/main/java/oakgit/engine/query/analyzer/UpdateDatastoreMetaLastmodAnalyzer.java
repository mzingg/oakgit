package oakgit.engine.query.analyzer;

import java.util.regex.Pattern;
import oakgit.engine.commands.UpdateDatastoreMetaLastmodCommand;
import oakgit.engine.query.QueryAnalyzer;
import oakgit.engine.query.QueryMatchResult;

public class UpdateDatastoreMetaLastmodAnalyzer implements QueryAnalyzer {

  private static final Pattern UPDATE_DATASTORE_META_LASTMOD_PATTERN =
      Pattern.compile("update ([\\w_]+) set LASTMOD = \\? where ID = \\?( and LASTMOD < \\?)?");

  @Override
  public QueryMatchResult matchAndCollect(String sqlQuery) {
    return withPatternMatch(
        sqlQuery,
        UPDATE_DATASTORE_META_LASTMOD_PATTERN,
        (result, matcher) -> {
          String tableName = matcher.group(1);
          boolean conditional = matcher.group(2) != null;
          result.setCommandSupplier(
              (placeholderData, selectionLimit) -> {
                long lastmod = placeholderData.getLong(1);
                String id = placeholderData.getString(2);
                Long threshold = conditional ? placeholderData.getLong(3) : null;
                return new UpdateDatastoreMetaLastmodCommand(tableName, id, lastmod, threshold);
              });
          return result;
        });
  }
}
