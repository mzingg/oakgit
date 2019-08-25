package com.diconium.oakgit.queryparsing;

import com.diconium.oakgit.engine.Command;
import net.sf.jsqlparser.statement.Statement;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

public interface QueryAnalyzer {

    boolean interestedIn(Statement statement);

    QueryParserResult getParserResult(Statement statement);

    String getTableName(Statement statement);

    Optional<QueryId> getId(Statement statement, Map<Integer, Object> placeholderData);

    Command createCommand(Statement statement, Map<Integer, Object> placeholderData);

    default Map<Object, Object> getData(Statement statement, Map<Integer, Object> placeholderData) {
        return Collections.emptyMap();
    }


    default <T> Optional<T> getDataField(Statement statement, String fieldName, Class<T> targetType, Map<Integer, Object> placeholderData) {
        Object fieldValue = getData(statement, placeholderData).getOrDefault(fieldName.toLowerCase(), null);
        if (fieldValue != null) {
            return Optional.ofNullable(convertToTargetTypeOrNull(fieldValue, targetType));
        }

        return Optional.empty();
    }

    @SuppressWarnings("unchecked")
    default <T> T convertToTargetTypeOrNull(Object value, Class<T> targetType) {
        if (targetType.isAssignableFrom(value.getClass())) {
            return (T) value;
        }

        if (targetType.equals(Long.class) && value.getClass().equals(Integer.class)) {
            return (T) new Long(((Integer) value).longValue());
        }

        return null;
    }

    @SuppressWarnings("unchecked")
    default <T extends Statement, R> R whileInterested(Statement statement, Class<T> statementType, Function<T, R> interestedCallback, Supplier<R> fallback) {
        if (interestedIn(statement) && statement.getClass().isAssignableFrom(statementType)) {
            T typedStatement = (T) statement;
            return interestedCallback.apply(typedStatement);
        }

        return fallback.get();
    }

    default <T extends Statement> QueryParserResult queryParserFor(Statement statement, Class<T> statementType) {
        return whileInterested(statement, statementType,
                typedStatement -> new QueryParserResult(this, typedStatement),
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
