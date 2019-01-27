package com.diconium.oakgit.queryparsing;

import net.sf.jsqlparser.statement.Statement;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

public interface QueryAnalyzer {

    String INVALID_ID = "INVAILD";

    boolean interestedIn(Statement statement);

    QueryParserResult getParserResult(Statement statement);

    String getTableName(Statement statement);

    Optional<QueryId> getId(Statement statement, Map<Integer, Object> placeholderData);


    default Map<Object, Object> getData(Statement statement, Map<Integer, Object> placeholderData) {
        return Collections.emptyMap();
    }

    @SuppressWarnings("unchecked")
    default <T> Optional<T> getDataField(Statement statement, String fieldName, Class<T> targetType, Map<Integer, Object> placeholderData) {
        Object fieldValue = getData(statement, placeholderData).getOrDefault(fieldName, null);
        if (fieldValue != null && targetType.isAssignableFrom(fieldValue.getClass())) {
            return Optional.of((T) fieldValue);
        }

        return Optional.empty();
    }

    @SuppressWarnings("unchecked")
    default <T extends Statement, R> R whileInterested(Statement statement, Class<T> statementType, Function<T, R> interestedCallback, Supplier<R> fallback) {
        if (interestedIn(statement) && statement.getClass().isAssignableFrom(statementType)) {
            T typedStatement = (T) statement;
            return interestedCallback.apply(typedStatement);
        }

        return fallback.get();
    }

    default <T extends Statement> QueryParserResult queryParserFor(Statement statement, Class<T> statementType, QueryParserResult.ResultType resultType) {
        return whileInterested(statement, statementType,
                typedStatement -> new QueryParserResult(this, typedStatement, resultType),
                () -> QueryParserResult.Error(this, statementType, "statement must be of type {}", statementType.getName()));
    }

    default <T extends Statement, R> R whileInterestedOrThrow(Statement statement, Class<T> statementType, Function<T, R> interestedCallback, RuntimeException exception) {
       return whileInterested(statement, statementType, interestedCallback, () -> {
           throw exception;
       });
    }

    default <T extends Statement, R> R whileInterestedOrThrow(Statement statement, Class<T> statementType, Function<T, R> interestedCallback) {
       return whileInterestedOrThrow(statement, statementType, interestedCallback, new IllegalStateException());
    }

}
