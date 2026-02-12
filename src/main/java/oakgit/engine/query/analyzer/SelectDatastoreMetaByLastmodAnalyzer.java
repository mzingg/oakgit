package oakgit.engine.query.analyzer;

import java.util.regex.Pattern;
import oakgit.engine.commands.SelectDatastoreMetaByLastmodCommand;
import oakgit.engine.query.QueryAnalyzer;
import oakgit.engine.query.QueryMatchResult;

public class SelectDatastoreMetaByLastmodAnalyzer implements QueryAnalyzer {

  private static final Pattern SELECT_DATASTORE_META_BY_LASTMOD_PATTERN =
      Pattern.compile("select ([\\w\\s*,]+?) from ([\\w_]+) where LASTMOD < \\?");

  @Override
  public QueryMatchResult matchAndCollect(String sqlQuery) {
    return withPatternMatch(
        sqlQuery,
        SELECT_DATASTORE_META_BY_LASTMOD_PATTERN,
        (result, matcher) -> {
          String fieldDeclaration = matcher.group(1);
          String tableName = matcher.group(2);
          result.setCommandSupplier(
              (placeholderData, selectionLimit) ->
                  new SelectDatastoreMetaByLastmodCommand(tableName, placeholderData.getLong(1))
                      .setResultFieldList(parseFieldList(fieldDeclaration)));
          return result;
        });
  }
}
