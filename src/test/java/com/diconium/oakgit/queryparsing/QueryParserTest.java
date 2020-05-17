package com.diconium.oakgit.queryparsing;

import com.diconium.oakgit.UnitTest;
import com.diconium.oakgit.queryparsing.analyzer.CreateAnalyzer;
import com.diconium.oakgit.queryparsing.analyzer.DeleteAnalyzer;
import com.diconium.oakgit.queryparsing.analyzer.InsertAnalyzer;
import com.diconium.oakgit.queryparsing.analyzer.SelectByIdAnalyzer;
import org.apache.commons.lang3.StringUtils;

import java.util.Collections;

import static com.diconium.oakgit.queryparsing.SingleValueId.INVALID_ID;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.isA;

class QueryParserTest {

    @UnitTest
    void parseWithEmptySqlQueryReturnsErrorResult() {
        QueryParserResult actual = new QueryParser().parse(StringUtils.EMPTY);

        assertThat(actual.isValid(), is(false));
    }

    @UnitTest
    void parseWithNullSqlQueryReturnsErrorResult() {
        QueryParserResult actual = new QueryParser().parse(null);

        assertThat(actual.isValid(), is(false));
    }

    @UnitTest
    void parseWithInsertQueryReturnTestTableName() {
        QueryParserResult actual = new QueryParser().parse(
            "INSERT INTO PRODUCTS( ContactName, Address, City, PostalCode, Country)\n" +
                "VALUES ('Tom B. Erichsen', 'Skagen 21', 'Stavanger', 200.350, 'Germany')");

        assertThat(actual.getTableName(), is("PRODUCTS"));
    }

    @UnitTest
    void parseWithSelectQueryHavingWhereClauseReturnTestTableName() {
        QueryParserResult actual = new QueryParser().parse("select ID from DATASTORE_DATA where ID = '0'");

        assertThat(actual.getTableName(), is("DATASTORE_DATA"));

    }

    @UnitTest
    void parseWithInvalidSelectQueryReturnsInvalid() {
        QueryParserResult actual = new QueryParser().parse("select ID from DATASTORE_DATA where ID = '0',");

        assertThat(actual.isValid(), is(false));
    }

    @UnitTest
    void parseWithInvalidSelectQueryReturnsException() {
        QueryParserResult actual = new QueryParser().parse("select ID from DATASTORE_DATA where ID = '0',");

        assertThat(actual.isValid(), is(false));
    }


    @UnitTest
    public void parseWithInValidInsertQueryReturnsException() {
        QueryParserResult actual = new QueryParser().parse(
            "INSERT INTO PRODUCTS( ContactName, Address, City, PostalCode, data,)\n" +
                "VALUES ('Tom B. Erichsen', 'Skagen 21', 'Stavanger', 200.350, '')");

        assertThat(actual.isValid(), is(false));
    }

    @UnitTest
    public void parseWithInsertQueryReturnsInsertObject() {
        QueryParserResult actual = new QueryParser().parse(
            "INSERT INTO PRODUCTS( ContactName, Address, City, PostalCode, data)\n" +
                "VALUES ('Tom B. Erichsen', 'Skagen 21', 'Stavanger', 200.350, 'sample Data')");

        assertThat(actual.getAnalyzer(), isA(InsertAnalyzer.class));
    }

    @UnitTest
    public void parseWithCreateQueryReturnsCreateObject() {
        QueryParserResult actual = new QueryParser().parse("create table SETTINGS");

        assertThat(actual.getAnalyzer(), isA(CreateAnalyzer.class));
    }

    @UnitTest
    public void parseWithSelectQueryReturnsSelectObject() {
        QueryParserResult actual = new QueryParser().parse("select ID from DATASTORE_DATA where ID = '0'");

        assertThat(actual.getAnalyzer(), isA(SelectByIdAnalyzer.class));
    }

    @UnitTest
    public void parseWithSelectQueryAndEmptyIdReturnsInvalidId() {
        QueryParserResult actual = new QueryParser().parse("select ID from DATASTORE_DATA where ID = ''");

        assertThat(actual.getId(Collections.emptyMap()).isPresent(), is(false));
    }

    @UnitTest
    public void parseWithSelectQueryAndNoIdReturnsError() {
        QueryParserResult actual = new QueryParser().parse("select ID from DATASTORE_DATA where ID = ");

        assertThat(actual.isValid(), is(false));
    }

    @UnitTest
    public void parseWithSelectQueryAndIdStringReturnsString() {
        QueryParserResult actual = new QueryParser().parse("select ID from DATASTORE_DATA where ID = '34'");

        assertThat(actual.getId(Collections.emptyMap()).orElse(INVALID_ID).value(), is("34"));
    }

    @UnitTest
    public void parseWithSelectQueryAndIdIntReturnsEmptyString() {
        QueryParserResult actual = new QueryParser().parse("select ID from DATASTORE_DATA where ID = 34");

        assertThat(actual.getId(Collections.emptyMap()).orElse(INVALID_ID).value(), is("34"));
    }

    @UnitTest
    public void parseWithDeleteQueryReturnsDeleteType() {
        QueryParserResult actual = new QueryParser().parse("delete from DATASTORE_DATA where ID = '0'");

        assertThat(actual.getAnalyzer(), isA(DeleteAnalyzer.class));
    }

    @UnitTest
    public void parseWithDeleteQueryAndNoIdReturnsEmptyId() {
        QueryParserResult actual = new QueryParser().parse("delete from DATASTORE_DATA where ID = ''");

        assertThat(actual.getId(Collections.emptyMap()).isPresent(), is(false));
    }
}
