package oakgit.engine.query.analyzer;

import oakgit.engine.commands.SelectFromContainerByIdCommand;
import oakgit.engine.query.QueryAnalyzer;
import oakgit.engine.query.QueryMatchResult;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SelectByIdAnalyzer implements QueryAnalyzer {

  private static final Pattern SELECT_BY_ID_PATTERN = Pattern.compile("select ([\\w\\s*,?=()]+?) from ([\\w_]+) where ID = (?:'([^?]+)'|\\?)");
  private static final Pattern CASE_PATTERN = Pattern.compile("case when \\(MODCOUNT = \\? and MODIFIED = \\?\\) then null else (BDATA|DATA) end as (?:BDATA|DATA)");

  @Override
  public QueryMatchResult matchAndCollect(String sqlQuery) {
    return withPatternMatch(sqlQuery, SELECT_BY_ID_PATTERN, (result, matcher) -> {
      String fieldDeclaration = matcher.group(1);
      String tableName = matcher.group(2);
      String idValue = matcher.groupCount() == 3 ? matcher.group(3) : "";
      result.setCommandSupplier(placeholderData -> {
        List<String> resultFieldList = parseFieldList(fieldDeclaration);
        int placeHolderIndex = 1;
        for (int i = 0; i < resultFieldList.size(); i++) {
          Matcher fieldMatcher = CASE_PATTERN.matcher(resultFieldList.get(i));
          if (fieldMatcher.matches()) {
            Long modCountCheck = placeholderData.getLong(placeHolderIndex);
            placeHolderIndex++;
            Long modifiedCheck = placeholderData.getLong(placeHolderIndex);
            placeHolderIndex++;
            resultFieldList.set(i, fieldMatcher.replaceAll(String.format(
                "case when (MODCOUNT = %d and MODIFIED = %d) then null else $1 end as $1",
                modCountCheck,
                modifiedCheck
            )));
          }
        }
        String idByPlaceholder = placeholderData.hasIndex(placeHolderIndex) ? placeholderData.getString(placeHolderIndex) : null;
        return new SelectFromContainerByIdCommand(
            tableName,
            StringUtils.isNotBlank(idValue) ? idValue : idByPlaceholder
        ).setResultFieldList(resultFieldList);
      });
      return result;
    });
  }

}
