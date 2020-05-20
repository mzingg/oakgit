package oakgit.engine.query.analyzer;

import oakgit.engine.query.QueryAnalyzer;
import oakgit.engine.query.QueryMatchResult;

public class EmptyAnalyzer implements QueryAnalyzer {

    @Override
    public QueryMatchResult matchAndCollect(String sqlQuery) {
        return null;
    }

}
