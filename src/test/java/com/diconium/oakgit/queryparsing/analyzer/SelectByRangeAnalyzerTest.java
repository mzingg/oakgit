package com.diconium.oakgit.queryparsing.analyzer;

import com.diconium.oakgit.UnitTest;
import com.diconium.oakgit.queryparsing.QueryId;
import com.diconium.oakgit.queryparsing.QueryMatchResult;
import com.diconium.oakgit.queryparsing.RangeQueryId;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.Statement;

import java.util.Map;

import static com.diconium.oakgit.TestHelpers.placeholderData;
import static com.diconium.oakgit.TestHelpers.testValidQueryMatch;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

class SelectByRangeAnalyzerTest {

    @UnitTest
    void matchAndCollectWithClusternodesCreateReturnsInterestedMatch() {
        testValidQueryMatch(
            new SelectByRangeAnalyzer(),
            "select ID, MODIFIED, MODCOUNT, CMODCOUNT, HASBINARY, DELETEDONCE, VERSION, SDTYPE, SDMAXREVTIME, DATA, BDATA from CLUSTERNODES where ID > ? and ID < ? order by ID"
        );
    }

    @UnitTest
    void matchAndCollectWithNonSelectQueryReturnsNotInterestedMatch() {
        QueryMatchResult actual = new SelectByRangeAnalyzer().matchAndCollect(
            "select * from SETTINGS where ID = '0'"
        );

        assertThat(actual, is(not(nullValue())));
        assertThat(actual.isInterested(), is(false));
        assertThat(actual.getCommandSupplier(), is(nullValue()));
    }

    @UnitTest
    void matchAndCollectWithNullQueryReturnsNotInterestedMatch() {
        QueryMatchResult actual = new SelectByRangeAnalyzer().matchAndCollect(null);

        assertThat(actual, is(not(nullValue())));
        assertThat(actual.isInterested(), is(false));
        assertThat(actual.getCommandSupplier(), is(nullValue()));
    }

    @UnitTest
    void getIdWithPlaceholderDataReturnsRangeQueryId() throws Exception {
        Statement statement = CCJSqlParserUtil.parse("select ID, MODIFIED, MODCOUNT, CMODCOUNT, HASBINARY, DELETEDONCE, VERSION, SDTYPE, SDMAXREVTIME, DATA, BDATA from CLUSTERNODES where ID > ? and ID < ? order by ID");
        Map<Integer, Object> placeholderData = placeholderData("0", "a");

        QueryId actual = new SelectByRangeAnalyzer().getId(statement, placeholderData)
            .orElseThrow(IllegalStateException::new);

        assertThat(actual, instanceOf(RangeQueryId.class));
        assertThat(((RangeQueryId) actual).leftValue(), is("0"));
        assertThat(((RangeQueryId) actual).rightValue(), is("a"));
    }

}
