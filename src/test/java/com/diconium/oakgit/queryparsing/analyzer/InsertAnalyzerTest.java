package com.diconium.oakgit.queryparsing.analyzer;

import com.diconium.oakgit.TestHelpers;
import com.diconium.oakgit.UnitTest;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.Statement;

import java.util.Collections;
import java.util.Map;

import static com.diconium.oakgit.queryparsing.SingleValueId.INVALID_ID;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

class InsertAnalyzerTest {

    @UnitTest
    void getIdWithPlaceholderDataReturnsReplacedId() throws Exception {
        Statement statement = CCJSqlParserUtil.parse("insert into SETTINGS(ID, MODIFIED, HASBINARY, DELETEDONCE, MODCOUNT, CMODCOUNT, DSIZE, VERSION, SDTYPE, SDMAXREVTIME, DATA, BDATA) values (?, ?, ?, ?, ?, ?, ?,  2, ?, ?, ?, ?)");
        Map<Integer, Object> placeholderData = TestHelpers.placeholderData("version", null, null, null, 1, 0, 14, null, null, "{\"_v\":\"1.8.0\"}", "}");

        String actual = new InsertAnalyzer().getId(statement, placeholderData).orElse(INVALID_ID).value();

        assertThat(actual, is("version"));
    }

    @UnitTest
    public void parseWithInsertSQLQueryWithValuesReturnsDataWithColumnAndValues() throws Exception {
        Statement statement = CCJSqlParserUtil.parse("insert into SETTINGS(ID, MODIFIED, HASBINARY) values ('anId', 'vModified', 'vhasBinary')");

        Map<Object, Object> actual = new InsertAnalyzer().getData(statement, Collections.emptyMap());

        assertThat(actual.size(), is(3));
        assertThat(actual.get("id"), is("anId"));
        assertThat(actual.get("modified"), is("vModified"));
        assertThat(actual.get("hasbinary"), is("vhasBinary"));
    }

    @UnitTest
    public void parseWithInsertSQLQueryWithQuestionmarksInDataReturnsQuestionmarks() throws Exception {
        Statement statement = CCJSqlParserUtil.parse("insert into SETTINGS(ID, MODIFIED, HASBINARY) values (?, ?, ?)");

        Map<Object, Object> actual = new InsertAnalyzer().getData(statement, Collections.emptyMap());

        assertThat(actual.size(), is(3));
        assertThat(actual.get("id"), is("?#1"));
        assertThat(actual.get("modified"), is("?#2"));
        assertThat(actual.get("hasbinary"), is("?#3"));
    }

}
