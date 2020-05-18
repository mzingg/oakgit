package com.diconium.oakgit.queryparsing;

import com.diconium.oakgit.UnitTest;
import org.apache.commons.lang3.StringUtils;

import java.util.Collections;

import static com.diconium.oakgit.queryparsing.QueryAnalyzer.INVALID_ID;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.sameInstance;

class QueryParserTest {

    @UnitTest
    void parseWithEmptySqlQueryReturnsErrorResult(){
    	QueryParserResult actual = new QueryParser().parse(StringUtils.EMPTY);

    	assertThat(actual.isValid(), is(false));
    }

    @UnitTest
    void parseWithNullSqlQueryReturnsErrorResult(){
    	QueryParserResult actual = new QueryParser().parse(null);

    	assertThat(actual.isValid(), is(false));
    }

    @UnitTest
    void parseWithCreateTableQueryReturnResultWithExpectedTableName() {
        QueryParserResult actual = new QueryParser().parse("create table SETTINGS");

        assertThat(actual.getTableName(), is("SETTINGS"));
    }

    @UnitTest
    void parseWithCreateQueryReturnTestTableName() {
        QueryParserResult actual = new QueryParser().parse(
        		"create table CLUSTERNODES (ID varchar(512) not null primary key, MODIFIED bigint, HASBINARY smallint, " +
                    "DELETEDONCE smallint, MODCOUNT bigint, CMODCOUNT bigint, DSIZE bigint, VERSION smallint, SDTYPE smallint, " +
                    "SDMAXREVTIME bigint, DATA varchar(16384), BDATA blob(1073741824))");

        assertThat(actual.getTableName(), is("CLUSTERNODES"));
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
    public void parseWithInsertQueryReturnsInsertObject(){
        QueryParserResult actual = new QueryParser().parse(
        		"INSERT INTO PRODUCTS( ContactName, Address, City, PostalCode, data)\n" +
                "VALUES ('Tom B. Erichsen', 'Skagen 21', 'Stavanger', 200.350, 'sample Data')");

        assertThat(actual.getResultType(), is(sameInstance(QueryParserResult.ResultType.INSERT)));
    }

    @UnitTest
    public void parseWithCreateQueryReturnsCreateObject(){
        QueryParserResult actual = new QueryParser().parse("create table SETTINGS");

        assertThat(actual.getResultType(), is(sameInstance(QueryParserResult.ResultType.CREATE)));
    }

    @UnitTest
    public void parseWithSelectQueryReturnsSelectObject(){
        QueryParserResult actual = new QueryParser().parse("select ID from DATASTORE_DATA where ID = '0'");

        assertThat(actual.getResultType(), is(sameInstance(QueryParserResult.ResultType.SELECT)));
    }


    public void parseWithSelectQueryAndEmptyIdReturnsInvalidId(){
        QueryParserResult actual = new QueryParser().parse("select ID from DATASTORE_DATA where ID = ''");

        assertThat(actual.getId(Collections.emptyMap()), is(sameInstance(INVALID_ID)));
    }

    @UnitTest
    public void parseWithSelectQueryAndNoIdReturnsError(){
        QueryParserResult actual = new QueryParser().parse("select ID from DATASTORE_DATA where ID = ");

        assertThat(actual.isValid(), is(false));
    }

    @UnitTest
    public void parseWithSelectQueryAndIdStringReturnsString(){
        QueryParserResult actual = new QueryParser().parse("select ID from DATASTORE_DATA where ID = '34'");

        assertThat(actual.getId(Collections.emptyMap()), is("34"));
    }

    @UnitTest
    public void parseWithSelectQueryAndIdIntReturnsEmptyString(){
        QueryParserResult actual = new QueryParser().parse("select ID from DATASTORE_DATA where ID = 34");

        assertThat(actual.getId(Collections.emptyMap()), is("34"));
    }

    @UnitTest
    public void parseWithSelectQueryAndIdNullReturnsEmptyId(){
        QueryParserResult actual = new QueryParser().parse("select ID from DATASTORE_DATA where ID = 'null'");

        assertThat(actual.getId(Collections.emptyMap()), is("null"));
    }

    @UnitTest
    public void parseWithDeleteQueryReturnsDeleteType(){
        QueryParserResult actual = new QueryParser().parse("delete from DATASTORE_DATA where ID = '0'");

        assertThat(actual.getResultType(), is(sameInstance(QueryParserResult.ResultType.DELETE)));
    }


    public void parseWithDeleteQueryAndNoIdReturnsEmptyId(){
        QueryParserResult actual = new QueryParser().parse("delete from DATASTORE_DATA where ID = ''");

        assertThat(actual.getId(Collections.emptyMap()), is(sameInstance(INVALID_ID)));
    }
}
