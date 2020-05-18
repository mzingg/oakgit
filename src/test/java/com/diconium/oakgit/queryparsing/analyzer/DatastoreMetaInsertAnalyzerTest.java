package com.diconium.oakgit.queryparsing.analyzer;

import com.diconium.oakgit.UnitTest;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.Statement;

import java.util.Collections;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

class DatastoreMetaInsertAnalyzerTest {

    @UnitTest
    public void parseWithInsertSQLQueryWithValuesReturnsDataWithColumnAndValues() throws Exception {
        Statement statement = CCJSqlParserUtil.parse("insert into DATASTORE_META(ID, LVL, LASTMOD) values ('anId', '2', '1234')");

        Map<Object, Object> actual = new DatastoreMetaInsertAnalyzer().getData(statement, Collections.emptyMap());

        assertThat(actual.size(), is(3));
        assertThat(actual.get("ID"), is("anId"));
        assertThat(actual.get("LVL"), is("2"));
        assertThat(actual.get("LASTMOD"), is("1234"));
    }

    @UnitTest
    public void parseWithInsertSQLQueryWithQuestionmarksInDataReturnsQuestionmarks() throws Exception {
        Statement statement = CCJSqlParserUtil.parse("insert into DATASTORE_META(ID, LVL, LASTMOD) values (?, ?, ?)");

        Map<Object, Object> actual = new DatastoreMetaInsertAnalyzer().getData(statement, Collections.emptyMap());

        assertThat(actual.size(), is(3));
        assertThat(actual.get("ID"), is("?#1"));
        assertThat(actual.get("LVL"), is("?#2"));
        assertThat(actual.get("LASTMOD"), is("?#3"));
    }

}
