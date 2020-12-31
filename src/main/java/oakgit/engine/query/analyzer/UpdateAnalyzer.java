package oakgit.engine.query.analyzer;

import oakgit.engine.commands.UpdatDataInContainerCommand;
import oakgit.engine.commands.UpdateSet;
import oakgit.engine.model.DocumentEntry;
import oakgit.engine.query.QueryAnalyzer;
import oakgit.engine.query.QueryMatchResult;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.function.Function;
import java.util.regex.Pattern;

public class UpdateAnalyzer implements QueryAnalyzer {

  private final Pattern UPDATE_PATTERN = Pattern.compile("update ([\\w_]+) set (.+?) where ID = \\? and MODCOUNT = \\?");

  @Override
  public QueryMatchResult matchAndCollect(String sqlQuery) {
    return withPatternMatch(sqlQuery, UPDATE_PATTERN, (result, matcher) -> {
      String tableName = matcher.group(1);
      String setExpressionsString = matcher.group(2);
      result.setCommandSupplier((placeholderData, selectionLimit) -> {
        int lastPlaceholderIndex = placeholderData.maxIndex();

        List<String> setExpressions = parseFieldList(setExpressionsString);
        UpdateSet data = new UpdateSet();
        int placeHolderIndex = 1;
        for (String setExpression : setExpressions) {
          String[] split = StringUtils.splitByWholeSeparator(setExpression, " = ");
          String fieldName = split[0];
          String updateExpression = split[1];
          Object updateValue = null;
          switch (updateExpression) {
            case "?":
              updateValue = placeholderData.get(placeHolderIndex);
              placeHolderIndex++;
              break;
            case "case when ? > MODIFIED then ? else MODIFIED end":
              Long modifiedCheck = (Long) placeholderData.get(placeHolderIndex);
              placeHolderIndex++;
              Long modifiedValue = (Long) placeholderData.get(placeHolderIndex);
              placeHolderIndex++;
              updateValue = (Function<DocumentEntry, Long>) entry ->
                  modifiedCheck > (entry.getModified() != null ? entry.getModified() : 0L) ? modifiedValue : entry.getModified();
              break;
            case "2":
              updateValue = 2;
              break;
            case "DATA || CAST(? AS varchar(16384))":
              String newDataString = (String) placeholderData.get(placeHolderIndex);
              placeHolderIndex++;
              updateValue = (Function<DocumentEntry, byte[]>) entry -> {
                byte[] oldData = entry.getData() != null ? entry.getData() : new byte[0];
                byte[] newData = newDataString != null ? newDataString.getBytes() : new byte[0];
                byte[] combined = new byte[oldData.length + newData.length];
                System.arraycopy(oldData, 0, combined, 0, oldData.length);
                System.arraycopy(newData, 0, combined, oldData.length, newData.length);
                return combined;
              };
              break;
            case "DSIZE + ?":
              Long sizeAddendum = placeholderData.getLong(placeHolderIndex);
              placeHolderIndex++;
              updateValue = (Function<DocumentEntry, Long>) entry ->
                  (entry.getDSize() != null ? entry.getDSize() : 0L) +
                      (sizeAddendum != null ? sizeAddendum : 0L);
              break;
          }
          data.withValue(fieldName.trim(), updateValue);
        }

        return new UpdatDataInContainerCommand(
            tableName,
            placeholderData.getString(lastPlaceholderIndex - 1),
            placeholderData.getLong(lastPlaceholderIndex),
            data
        );
      });
      return result;
    });
  }
}
