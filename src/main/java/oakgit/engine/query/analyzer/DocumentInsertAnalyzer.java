package oakgit.engine.query.analyzer;

import oakgit.engine.commands.InsertIntoContainerCommand;
import oakgit.engine.model.DocumentEntry;
import oakgit.engine.query.QueryAnalyzer;
import oakgit.engine.query.QueryMatchResult;

import java.util.regex.Pattern;

public class DocumentInsertAnalyzer implements QueryAnalyzer {

  private final Pattern INSERT_PATTERN = Pattern.compile("insert into ([\\w_]+)" +
      "\\(ID, MODIFIED, HASBINARY, DELETEDONCE, MODCOUNT, CMODCOUNT, DSIZE, VERSION, SDTYPE, SDMAXREVTIME, DATA, BDATA\\) " +
      "values \\(\\?, \\?, \\?, \\?, \\?, \\?, \\?,\\s+2, \\?, \\?, \\?, \\?\\)"
  );

  @Override
  public QueryMatchResult matchAndCollect(String sqlQuery) {
    return withPatternMatch(sqlQuery, INSERT_PATTERN, (result, matcher) -> {
      String tableName = matcher.group(1);
      result.setCommandSupplier((placeholderData, selectionLimit) -> {
        DocumentEntry data = new DocumentEntry();
        data.setId(placeholderData.getString(1));
        data.setModified(placeholderData.getLong(2));
        data.setHasBinary(placeholderData.getInteger(3));
        data.setDeletedOnce(placeholderData.getInteger(4));
        data.setModCount(placeholderData.getLong(5));
        data.setCModCount(placeholderData.getLong(6));
        data.setDSize(placeholderData.getLong(7));
        data.setVersion(2);
        data.setSdType(placeholderData.getInteger(8));
        data.setSdMaxRevTime(placeholderData.getLong(9));
        data.setData(placeholderData.getBytes(10));
        data.setBdata(placeholderData.getBytes(11));

        return new InsertIntoContainerCommand<>(tableName, data);
      });
      return result;
    });
  }

}
