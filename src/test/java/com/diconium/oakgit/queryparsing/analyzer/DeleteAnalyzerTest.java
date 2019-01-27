package com.diconium.oakgit.queryparsing.analyzer;

import com.diconium.oakgit.UnitTest;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.delete.Delete;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

class DeleteAnalyzerTest {

    @UnitTest
    void interestedInWithNonDeleteQueryReturnsFalse() throws Exception {
        Statement statement = CCJSqlParserUtil.parse("SELECT * FROM SETTINGS");

        assertThat(new DeleteAnalyzer().interestedIn(statement), is(false));
    }

    @UnitTest
    void interestedInWithDeleteQueryReturnsTrue() throws Exception {
        Statement statement = CCJSqlParserUtil.parse("delete from DATASTORE_DATA where ID = '0'");

        assertThat(new DeleteAnalyzer().interestedIn(statement), is(true));
    }

    @UnitTest
    void getParserResultCallsQueryParserForWithDeleteType() throws Exception {
        Statement statement = CCJSqlParserUtil.parse("delete from DATASTORE_DATA where ID = '0'");
        DeleteAnalyzer target = spy(new DeleteAnalyzer());

        target.getParserResult(statement);

        verify(target).queryParserFor(statement, Delete.class);
    }

}