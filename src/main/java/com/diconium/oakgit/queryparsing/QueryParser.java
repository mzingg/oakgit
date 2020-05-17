package com.diconium.oakgit.queryparsing;

import com.diconium.oakgit.queryparsing.analyzer.*;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.Statement;

import java.util.ArrayList;
import java.util.List;

public class QueryParser {

    private static final List<QueryAnalyzer> DEFAULT_ANALYZERS = new ArrayList<>();
    static {
        DEFAULT_ANALYZERS.add(new CreateAnalyzer());
        DEFAULT_ANALYZERS.add(new DeleteAnalyzer());
        DEFAULT_ANALYZERS.add(new MetaDataInsertAnalyzer());
        DEFAULT_ANALYZERS.add(new NodeAndSettingsInsertAnalyzer());
        DEFAULT_ANALYZERS.add(new SelectByIdAnalyzer());
        DEFAULT_ANALYZERS.add(new SelectByRangeAnalyzer());
        DEFAULT_ANALYZERS.add(new SelectInAnalyzer());
        DEFAULT_ANALYZERS.add(new UpdateAnalyzer());
    }

    private final List<QueryAnalyzer> analyzers;

    public QueryParser() {
        this.analyzers = DEFAULT_ANALYZERS;
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

        return  QueryParserResult.Error("sqlQuery must not be null");
    }

}
