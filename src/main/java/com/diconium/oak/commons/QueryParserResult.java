package com.diconium.oak.commons;

import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

@Getter
public class QueryParserResult {

    public static final QueryParserResult ERROR_RESULT = new QueryParserResult(ResultType.ERROR);

    private ResultType type;

    private String tableName = StringUtils.EMPTY;
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
