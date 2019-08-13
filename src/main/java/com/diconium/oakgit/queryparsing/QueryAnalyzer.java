package com.diconium.oakgit.queryparsing;

import net.sf.jsqlparser.statement.Statement;

import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

public interface QueryAnalyzer {

    String INVALID_ID = "INVAILD";

    boolean interestedIn(Statement statement);

    QueryParserResult getParserResult(Statement statement);

    String getId(Statement statement, Map<Integer, Object> placeholderData);

    QueryParserResult.ResultType getResultType();

    @SuppressWarnings("unchecked")
    default <T extends Statement, R> R whileInterested(Statement statement, Class<T> statementType, Function<T, R> interestedCallback, Supplier<R> fallback) {
        if (interestedIn(statement) && statement.getClass().isAssignableFrom(statementType)) {
            T typedStatement = (T) statement;
            return interestedCallback.apply(typedStatement);
        }

        return fallback.get();
    }

}
