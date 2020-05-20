package oakgit.engine.query.analyzer;

import oakgit.engine.query.QueryAnalyzer;
import oakgit.engine.query.QueryMatchResult;

public class DatastoreMetaInsertAnalyzer implements QueryAnalyzer {

    public static final String METADATA_TABLE_NAME = "DATASTORE_META";

    @Override
    public QueryMatchResult matchAndCollect(String sqlQuery) {
        return null;
    }

}
