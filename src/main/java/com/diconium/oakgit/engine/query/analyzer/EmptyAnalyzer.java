package com.diconium.oakgit.engine.query.analyzer;

import com.diconium.oakgit.engine.query.QueryAnalyzer;
import com.diconium.oakgit.engine.query.QueryMatchResult;

public class EmptyAnalyzer implements QueryAnalyzer {

    @Override
    public QueryMatchResult matchAndCollect(String sqlQuery) {
        return null;
    }

}
