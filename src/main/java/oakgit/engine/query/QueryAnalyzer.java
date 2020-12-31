package oakgit.engine.query;

import org.apache.commons.lang3.StringUtils;

import java.util.Collections;
import java.util.List;
import java.util.function.BiFunction;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public interface QueryAnalyzer {

  QueryMatchResult matchAndCollect(String sqlQuery);

  default QueryMatchResult withPatternMatch(String sqlQuery, Pattern pattern, BiFunction<QueryMatchResult, Matcher, QueryMatchResult> transformer) {
    QueryMatchResult result = new QueryMatchResult();
    if (StringUtils.isNotBlank(sqlQuery)) {
      Matcher matcher = pattern.matcher(sqlQuery);
      if (matcher.matches()) {
        result.setInterested(true);
        result.setOriginQuery(sqlQuery);
        return transformer.apply(result, matcher);
      }
    }
    return result;
  }

  default List<String> parseFieldList(String fieldDeclaration) {
    if (StringUtils.isNotBlank(fieldDeclaration) && !"*".equals(fieldDeclaration)) {
      return Stream.of(StringUtils.split(fieldDeclaration, ","))
          .map(StringUtils::trim)
          .collect(Collectors.toList());
    }

    // empty list implies all fields
    return Collections.emptyList();
  }

}
