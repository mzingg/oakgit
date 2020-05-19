package com.diconium.oakgit.queryparsing;

import com.diconium.oakgit.engine.Command;
import com.diconium.oakgit.engine.model.ContainerEntry;
import com.diconium.oakgit.engine.model.DatastoreDataEntry;
import com.diconium.oakgit.engine.model.DatastoreMetaEntry;
import com.diconium.oakgit.engine.model.DocumentEntry;
import net.sf.jsqlparser.statement.Statement;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public interface QueryAnalyzer {

    String INVALID_ID = "INVAILD";

    QueryMatchResult matchAndCollect(String sqlQuery);

    boolean interestedIn(Statement statement);

    QueryParserResult getParserResult(Statement statement);

    String getTableName(Statement statement);

    Optional<QueryId> getId(Statement statement, Map<Integer, Object> placeholderData);

    Command createCommand(Statement statement, Map<Integer, Object> placeholderData);

    default QueryMatchResult withPatternMatch(String sqlQuery, Pattern pattern, BiFunction<QueryMatchResult, Matcher, QueryMatchResult> transformer) {
        QueryMatchResult result = new QueryMatchResult();
        if (StringUtils.isNotBlank(sqlQuery)) {
            Matcher matcher = pattern.matcher(sqlQuery);
            if (matcher.matches()) {
                result.setInterested(true);
                result.setOriginQuery(sqlQuery);
                return transformer.apply(result, matcher);
            }
        }
        return result;
    }

    default List<String> parseFieldList(String fieldDeclaration) {
        if (StringUtils.isNotBlank(fieldDeclaration) && !"*".equals(fieldDeclaration)) {
            return Arrays.asList(StringUtils.split(fieldDeclaration, ","));
        }

        // empty list implies all fields
        return Collections.emptyList();
    }

    default Class<? extends ContainerEntry<?>> typeByTableName(String tableName) {
        switch (tableName) {
            case "DATASTORE_DATA":
                return DatastoreDataEntry.class;
            case "DATASTORE_META":
                return DatastoreMetaEntry.class;
            case "CLUSTERNODES":
            case "JOURNAL":
            case "NODES":
            case "SETTINGS":
                return DocumentEntry.class;
        }
        throw new IllegalArgumentException("Unknown table name: " + tableName);
    }

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
