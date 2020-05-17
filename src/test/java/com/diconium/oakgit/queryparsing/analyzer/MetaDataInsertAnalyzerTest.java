package com.diconium.oakgit.queryparsing.analyzer;

import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.Statement;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

class MetaDataInsertAnalyzerTest {

    @Test
    public void parseWithInsertSQLQueryWithValuesReturnsDataWithColumnAndValues() throws Exception {
        Statement statement = CCJSqlParserUtil.parse("insert into SETTINGS(ID, MODIFIED, HASBINARY) values ('anId', 'vModified', 'vhasBinary')");

        Map<Object, Object> actual = new MetaDataInsertAnalyzer().getData(statement, Collections.emptyMap());

        assertThat(actual.size(), is(3));
        assertThat(actual.get("ID"), is("anId"));
        assertThat(actual.get("MODIFIED"), is("vModified"));
        assertThat(actual.get("HASBINARY"), is("vhasBinary"));
    }

    @Test
    public void parseWithInsertSQLQueryWithQuestionmarksInDataReturnsQuestionmarks() throws Exception {
        Statement statement = CCJSqlParserUtil.parse("insert into SETTINGS(ID, MODIFIED, HASBINARY) values (?, ?, ?)");

        Map<Object, Object> actual = new MetaDataInsertAnalyzer().getData(statement, Collections.emptyMap());

        assertThat(actual.size(), is(3));
        assertThat(actual.get("ID"), is("?#1"));
        assertThat(actual.get("MODIFIED"), is("?#2"));
        assertThat(actual.get("HASBINARY"), is("?#3"));
    }

}
