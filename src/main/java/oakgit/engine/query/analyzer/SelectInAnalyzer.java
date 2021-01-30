package oakgit.engine.query.analyzer;

import oakgit.engine.commands.SelectFromContainerByMultipleIdsCommand;
import oakgit.engine.query.QueryAnalyzer;
import oakgit.engine.query.QueryMatchResult;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class SelectInAnalyzer implements QueryAnalyzer {

  private final Pattern SELECT_IN_PATTERN = Pattern.compile("select ([\\w\\s*,]+?) from ([\\w_]+) where \\(?ID in.+");

  @Override
  public QueryMatchResult matchAndCollect(String sqlQuery) {
    return withPatternMatch(sqlQuery, SELECT_IN_PATTERN, (result, matcher) -> {
      String fieldDeclaration = matcher.group(1);
      String tableName = matcher.group(2);

      result.setCommandSupplier((placeholderData, selectionLimit) -> {
        List<String> idList = placeholderData.valueStream()
            .map(Object::toString)
            .collect(Collectors.toList());
        if (idList.size() != StringUtils.countMatches(result.getOriginQuery(), '?')) {
          throw new IllegalStateException("list of ids does not match ? count in query");
        }

        return new SelectFromContainerByMultipleIdsCommand<>(tableName, idList)
            .setResultFieldList(parseFieldList(fieldDeclaration));
      });
      return result;
    });
  }

}
