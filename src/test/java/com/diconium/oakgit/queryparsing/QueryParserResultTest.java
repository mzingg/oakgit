package com.diconium.oakgit.queryparsing;

import com.diconium.oakgit.UnitTest;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class QueryParserResultTest {

    @UnitTest
    void ErrorWithoutAnalyzerReturnsQueryParserResultWithErrorStateSet() {
        QueryParserResult actual = QueryParserResult.Error("aMessage");

        assertThat(actual.isErrorState(), is(true));
    }
}
