package com.diconium.oakgit.queryparsing.analyzer;

import com.diconium.oakgit.UnitTest;
import com.diconium.oakgit.queryparsing.QueryId;
import com.diconium.oakgit.queryparsing.SingleValueAndModCountId;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.Statement;

import java.util.Map;

import static com.diconium.oakgit.TestHelpers.placeholderData;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;

class UpdateAnalyzerTest {

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
