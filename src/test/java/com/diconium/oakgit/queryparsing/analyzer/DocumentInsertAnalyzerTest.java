package com.diconium.oakgit.queryparsing.analyzer;

import com.diconium.oakgit.UnitTest;
import com.diconium.oakgit.queryparsing.QueryMatchResult;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.Statement;

import java.util.Collections;
import java.util.Map;

import static com.diconium.oakgit.TestHelpers.testValidQueryMatch;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

class DocumentInsertAnalyzerTest {

    @UnitTest
    void matchAndCollectWithClusternodesCreateReturnsInterestedMatch() {
        testValidQueryMatch(
            new DocumentInsertAnalyzer(),
            "insert into CLUSTERNODES(ID, MODIFIED, HASBINARY, DELETEDONCE, MODCOUNT, CMODCOUNT, DSIZE, VERSION, SDTYPE, SDMAXREVTIME, DATA, BDATA) values (?, ?, ?, ?, ?, ?, ?,  2, ?, ?, ?, ?)"
        );
    }

    @UnitTest
    void matchAndCollectWithJournalCreateReturnsInterestedMatch() {
        testValidQueryMatch(
            new DocumentInsertAnalyzer(),
            "insert into JOURNAL(ID, MODIFIED, HASBINARY, DELETEDONCE, MODCOUNT, CMODCOUNT, DSIZE, VERSION, SDTYPE, SDMAXREVTIME, DATA, BDATA) values (?, ?, ?, ?, ?, ?, ?,  2, ?, ?, ?, ?)"
        );
    }

    @UnitTest
    void matchAndCollectWithNodeCreateReturnsInterestedMatch() {
        testValidQueryMatch(
            new DocumentInsertAnalyzer(),
            "insert into NODES(ID, MODIFIED, HASBINARY, DELETEDONCE, MODCOUNT, CMODCOUNT, DSIZE, VERSION, SDTYPE, SDMAXREVTIME, DATA, BDATA) values (?, ?, ?, ?, ?, ?, ?,  2, ?, ?, ?, ?)"
        );
    }

    @UnitTest
    void matchAndCollectWithSettingsCreateReturnsInterestedMatch() {
        testValidQueryMatch(
            new DocumentInsertAnalyzer(),
            "insert into SETTINGS(ID, MODIFIED, HASBINARY, DELETEDONCE, MODCOUNT, CMODCOUNT, DSIZE, VERSION, SDTYPE, SDMAXREVTIME, DATA, BDATA) values (?, ?, ?, ?, ?, ?, ?,  2, ?, ?, ?, ?)"
        );
    }

    @UnitTest
    void matchAndCollectWithNonInsertQueryReturnsNotInterestedMatch() {
        QueryMatchResult actual = new DocumentInsertAnalyzer().matchAndCollect("select * from CLUSTERNODES where ID = '0'");

        assertThat(actual, is(not(nullValue())));
        assertThat(actual.isInterested(), is(false));
        assertThat(actual.getCommandSupplier(), is(nullValue()));
    }

    @UnitTest
    void matchAndCollectWithNullQueryReturnsNotInterestedMatch() {
        QueryMatchResult actual = new DocumentInsertAnalyzer().matchAndCollect(null);

        assertThat(actual, is(not(nullValue())));
        assertThat(actual.isInterested(), is(false));
        assertThat(actual.getCommandSupplier(), is(nullValue()));
    }

    @UnitTest
    public void parseWithInsertSQLQueryWithValuesReturnsDataWithColumnAndValues() throws Exception {
        Statement statement = CCJSqlParserUtil.parse("insert into SETTINGS(ID, MODIFIED, HASBINARY) values ('anId', 'vModified', 'vhasBinary')");

        Map<Object, Object> actual = new DocumentInsertAnalyzer().getData(statement, Collections.emptyMap());

        assertThat(actual.size(), is(3));
        assertThat(actual.get("ID"), is("anId"));
        assertThat(actual.get("MODIFIED"), is("vModified"));
        assertThat(actual.get("HASBINARY"), is("vhasBinary"));
    }

    @UnitTest
    public void parseWithInsertSQLQueryWithQuestionmarksInDataReturnsQuestionmarks() throws Exception {
        Statement statement = CCJSqlParserUtil.parse("insert into SETTINGS(ID, MODIFIED, HASBINARY) values (?, ?, ?)");

        Map<Object, Object> actual = new DocumentInsertAnalyzer().getData(statement, Collections.emptyMap());

        assertThat(actual.size(), is(3));
        assertThat(actual.get("ID"), is("?#1"));
        assertThat(actual.get("MODIFIED"), is("?#2"));
        assertThat(actual.get("HASBINARY"), is("?#3"));
    }

}
