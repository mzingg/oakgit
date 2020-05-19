package com.diconium.oakgit.engine.query.analyzer;

import com.diconium.oakgit.engine.query.QueryAnalyzer;
import com.diconium.oakgit.engine.query.QueryMatchResult;

public class DatastoreMetaInsertAnalyzer implements QueryAnalyzer {

    public static final String METADATA_TABLE_NAME = "DATASTORE_META";

    @Override
    public QueryMatchResult matchAndCollect(String sqlQuery) {
        return null;
    }

}
