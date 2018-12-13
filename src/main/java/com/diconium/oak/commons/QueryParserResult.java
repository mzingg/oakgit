package com.diconium.oak.commons;

import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

@Getter
// Todo: Create Unit Tests
public class QueryParserResult {

    public static final QueryParserResult ERROR_RESULT = new QueryParserResult(ResultType.ERROR);

    private ResultType type;

    private String tableName = StringUtils.EMPTY;

    // Todo: Refactor the data field so that it can be used by all types of result to transport their data
    private String data = StringUtils.EMPTY;

    public QueryParserResult(ResultType type) {
        this.type = type;
    }

    public QueryParserResult withTableName(String tableName) {
        this.tableName = tableName;
        return this;
    }

    public QueryParserResult withData(String data) {
        this.data = data;
        return this;
    }

    public enum ResultType {
        INSERT,
        CREATE,
        SELECT,
        ERROR,
        UNKNOWN;
    }
}
