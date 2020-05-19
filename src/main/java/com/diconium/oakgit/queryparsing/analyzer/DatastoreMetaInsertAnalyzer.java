package com.diconium.oakgit.queryparsing.analyzer;

import com.diconium.oakgit.queryparsing.QueryAnalyzer;
import com.diconium.oakgit.queryparsing.QueryMatchResult;

public class DatastoreMetaInsertAnalyzer implements QueryAnalyzer {

    public static final String METADATA_TABLE_NAME = "DATASTORE_META";

    @Override
    public QueryMatchResult matchAndCollect(String sqlQuery) {
        return null;
    }

}
