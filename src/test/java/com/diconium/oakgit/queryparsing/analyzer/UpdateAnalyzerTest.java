package com.diconium.oakgit.queryparsing.analyzer;

import com.diconium.oakgit.UnitTest;
import com.diconium.oakgit.queryparsing.QueryId;
import com.diconium.oakgit.queryparsing.QueryParserResult;
import com.diconium.oakgit.queryparsing.SingleValueAndModCountId;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.update.Update;

import java.util.Map;

import static com.diconium.oakgit.TestHelpers.placeholderData;
import static com.diconium.oakgit.TestHelpers.testValidQueryMatch;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

class UpdateAnalyzerTest {

    @UnitTest
    void matchAndCollectWithClusternodesAndMultipleInExpressionsReturnsInterestedMatch() {
        testValidQueryMatch(
            new UpdateAnalyzer(),
            "update CLUSTERNODES set MODIFIED = case when ? > MODIFIED then ? else MODIFIED end, HASBINARY = ?, DELETEDONCE = ?, MODCOUNT = ?, CMODCOUNT = ?, DSIZE = DSIZE + ?, VERSION = 2, DATA = DATA || CAST(? AS varchar(16384)) where ID = ? and MODCOUNT = ?"
        );
    }

    @UnitTest
    void getParserResultCallsQueryParserForWitUpdateTableType() throws Exception {
        Statement statement = CCJSqlParserUtil.parse("update NODES set MODIFIED = case when ? > MODIFIED then ? else MODIFIED end, HASBINARY = ?, DELETEDONCE = ?, MODCOUNT = ?, CMODCOUNT = ?, DSIZE = DSIZE + ?, VERSION = 2, DATA = DATA || CAST(? AS varchar(16384)) where ID = ? and MODCOUNT = ?");
        UpdateAnalyzer target = spy(new UpdateAnalyzer());

        target.getParserResult(statement);

        verify(target).queryParserFor(statement, Update.class, QueryParserResult.ResultType.UPDATE);
    }

    @UnitTest
    void interestedInWithNonUpdateQueryReturnsFalse() throws Exception {
        Statement statement = CCJSqlParserUtil.parse("create table SETTINGS");

        assertThat(new UpdateAnalyzer().interestedIn(statement), is(false));
    }

    @UnitTest
    void interestedInWithUpdateQueryHavingOnlyOneWhereReturnsFalse() throws Exception {
        Statement statement = CCJSqlParserUtil.parse("update NODES set MODIFIED = case when ? > MODIFIED then ? else MODIFIED end, HASBINARY = ?, DELETEDONCE = ?, MODCOUNT = ?, CMODCOUNT = ?, DSIZE = DSIZE + ?, VERSION = 2, DATA = DATA || CAST(? AS varchar(16384)) where ID = ?");

        assertThat(new UpdateAnalyzer().interestedIn(statement), is(false));
    }

    @UnitTest
    void interestedInWithUpdateQueryHavingAndedWhereReturnsFalse() throws Exception {
        Statement statement = CCJSqlParserUtil.parse("update NODES set MODIFIED = case when ? > MODIFIED then ? else MODIFIED end, HASBINARY = ?, DELETEDONCE = ?, MODCOUNT = ?, CMODCOUNT = ?, DSIZE = DSIZE + ?, VERSION = 2, DATA = DATA || CAST(? AS varchar(16384)) where ID = ? and MODCOUNT = ?");

        assertThat(new UpdateAnalyzer().interestedIn(statement), is(true));
    }

    @UnitTest
    void getTableNameReturnsCorrectValue() throws Exception {
        Statement statement = CCJSqlParserUtil.parse("update NODES set MODIFIED = case when ? > MODIFIED then ? else MODIFIED end, HASBINARY = ?, DELETEDONCE = ?, MODCOUNT = ?, CMODCOUNT = ?, DSIZE = DSIZE + ?, VERSION = 2, DATA = DATA || CAST(? AS varchar(16384)) where ID = ? and MODCOUNT = ?");

        assertThat(new UpdateAnalyzer().getTableName(statement), is("NODES"));
    }

    @UnitTest
    void getIdWithPlaceholderDataReturnsQueryIdWithModcount() throws Exception {
        Statement statement = CCJSqlParserUtil.parse("update NODES set MODIFIED = case when ? > MODIFIED then ? else MODIFIED end, HASBINARY = ?, DELETEDONCE = ?, MODCOUNT = ?, CMODCOUNT = ?, DSIZE = DSIZE + ?, VERSION = 2, DATA = DATA || CAST(? AS varchar(16384)) where ID = ? and MODCOUNT = ?");
        Map<Integer, Object> placeholderData = placeholderData(0, 0, null, 1, 1, 0, 86, ",[[\"*\",\"_deleted\",\"r16ca60b4f42-0-1\",null],[\"*\",\"_revisions\",\"r16ca60b4f42-0-1\",null]]", "0:/", 0);

        QueryId actual = new UpdateAnalyzer().getId(statement, placeholderData)
            .orElseThrow(IllegalStateException::new);

        assertThat(actual, instanceOf(SingleValueAndModCountId.class));
        assertThat(actual.value(), is("0:/"));
        assertThat(((SingleValueAndModCountId) actual).getModCount(), is("0"));
    }

}
