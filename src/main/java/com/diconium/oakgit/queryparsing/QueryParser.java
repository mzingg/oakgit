package com.diconium.oakgit.queryparsing;

import com.diconium.oakgit.queryparsing.analyzer.*;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.Statement;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class QueryParser {

    private static final List<QueryAnalyzer> DEFAULT_ANALYZERS = new ArrayList<>();

    static {
        DEFAULT_ANALYZERS.add(new CreateAnalyzer());
        DEFAULT_ANALYZERS.add(new DeleteAnalyzer());
        DEFAULT_ANALYZERS.add(new DatastoreMetaInsertAnalyzer());
        DEFAULT_ANALYZERS.add(new DocumentInsertAnalyzer());
        DEFAULT_ANALYZERS.add(new SelectByIdAnalyzer());
        DEFAULT_ANALYZERS.add(new SelectByRangeAnalyzer());
        DEFAULT_ANALYZERS.add(new SelectInAnalyzer());
        DEFAULT_ANALYZERS.add(new UpdateAnalyzer());
    }

    private final List<QueryAnalyzer> analyzers;

    public QueryParser() {
        this.analyzers = DEFAULT_ANALYZERS;
    }

    public Optional<QueryMatchResult> match(String sqlQuery) {
        for (QueryAnalyzer analyzer : analyzers) {
            var matchResult = analyzer.matchAndCollect(sqlQuery);
            if (matchResult.isInterested()) {
                return Optional.of(matchResult);
            }
        }

        return Optional.empty();
    }

    public QueryParserResult parse(String sqlQuery) {
        if (sqlQuery != null) {
            try {
                Statement statement = CCJSqlParserUtil.parse(sqlQuery);

                return analyzers.stream()
                    .filter(queryAnalyzer -> queryAnalyzer.interestedIn(statement))
                    .findFirst()
                    .map(firstFoundQueryAnalyzer -> firstFoundQueryAnalyzer.getParserResult(statement))
                    .orElse(QueryParserResult.Unknown("No analyzer for query [%s] found", sqlQuery));

            } catch (JSQLParserException e) {
                QueryParserResult.Error("Error while parsing sqlQuery: %s", e.getMessage());
            }
        }

        return QueryParserResult.Error("sqlQuery must not be null");
    }

}
