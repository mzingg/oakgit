package com.diconium.oakgit.commons;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;

import com.diconium.oakgit.commons.QueryParserResult.ResultType;

import java.util.Collections;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class QueryParserResultTest {


    @Test
    void queryParserResultWithNullReturnsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class,
                () -> new QueryParserResult(null)
        );
    }

    @Test
    void queryParserResultWithSelectReturnsSelectType() {
        QueryParserResult actual = new QueryParserResult(ResultType.SELECT);

        assertThat(actual.getType(), is(sameInstance(QueryParserResult.ResultType.SELECT)));
    }

    @Test
    void queryParserResultWithCreateReturnsCreateType() {
        QueryParserResult actual = new QueryParserResult(ResultType.CREATE);

        assertThat(actual.getType(), is(sameInstance(QueryParserResult.ResultType.CREATE)));
    }

    @Test
    void queryParserResultWithCreateReturnsEmptyId() {
        QueryParserResult actual = new QueryParserResult(ResultType.CREATE);

        assertThat(actual.getId(Collections.emptyMap()), is(StringUtils.EMPTY));
    }

    @Test
    void queryParserResultWithSelectReturnsEmptyTableName() {
        QueryParserResult actual = new QueryParserResult(ResultType.CREATE);

        assertThat(actual.getTableName(), is(StringUtils.EMPTY));
    }
}
