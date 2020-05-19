package com.diconium.oakgit.queryparsing;

import com.diconium.oakgit.engine.model.ContainerEntry;
import com.diconium.oakgit.engine.model.DatastoreDataEntry;
import com.diconium.oakgit.engine.model.DatastoreMetaEntry;
import com.diconium.oakgit.engine.model.DocumentEntry;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.BiFunction;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public interface QueryAnalyzer {

    QueryMatchResult matchAndCollect(String sqlQuery);

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

}
