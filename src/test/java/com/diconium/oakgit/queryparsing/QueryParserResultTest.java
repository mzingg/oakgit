package com.diconium.oakgit.queryparsing;

import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class QueryParserResultTest {

    @Test
    void ErrorWithoutAnalyzerReturnsQueryParserResultWithErrorStateSet() {
        QueryParserResult actual = QueryParserResult.Error("aMessage");

        assertThat(actual.isErrorState(), is(true));
    }
}
