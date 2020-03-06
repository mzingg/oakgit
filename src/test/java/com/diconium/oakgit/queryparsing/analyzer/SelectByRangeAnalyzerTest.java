package com.diconium.oakgit.queryparsing.analyzer;

import com.diconium.oakgit.UnitTest;
import com.diconium.oakgit.queryparsing.QueryId;
import com.diconium.oakgit.queryparsing.RangeQueryId;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.Statement;

import java.util.Map;

import static com.diconium.oakgit.TestHelpers.placeholderData;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;

class SelectByRangeAnalyzerTest {

    @UnitTest
    void getIdWithPlaceholderDataReturnsRangeQueryId() throws Exception {
        Statement statement = CCJSqlParserUtil.parse("select ID, MODIFIED, MODCOUNT, CMODCOUNT, HASBINARY, DELETEDONCE, VERSION, SDTYPE, SDMAXREVTIME, DATA, BDATA from CLUSTERNODES where ID > ? and ID < ? order by ID");
        Map<Integer, Object> placeholderData = placeholderData("0", "a");

        QueryId actual = new SelectByRangeAnalyzer().getId(statement, placeholderData).get();

        assertThat(actual, instanceOf(RangeQueryId.class));
        assertThat(((RangeQueryId) actual).leftValue(), is("0"));
        assertThat(((RangeQueryId) actual).rightValue(), is("a"));
    }

}
