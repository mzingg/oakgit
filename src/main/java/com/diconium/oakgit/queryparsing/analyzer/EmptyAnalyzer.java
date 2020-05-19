package com.diconium.oakgit.queryparsing.analyzer;

import com.diconium.oakgit.queryparsing.QueryAnalyzer;
import com.diconium.oakgit.queryparsing.QueryMatchResult;

public class EmptyAnalyzer implements QueryAnalyzer {

    @Override
    public QueryMatchResult matchAndCollect(String sqlQuery) {
        return null;
    }

}
