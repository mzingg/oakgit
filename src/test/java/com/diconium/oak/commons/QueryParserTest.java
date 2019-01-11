package com.diconium.oak.commons;

import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.insert.Insert;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

class QueryParserTest {


    @Test
    void parseWithEmptySqlQueryReturnsErrorResult() throws Exception {
    	QueryParserResult actual = new QueryParser().parse(StringUtils.EMPTY);
    	
    	assertThat(actual, is(sameInstance(QueryParserResult.ERROR_RESULT)));     	
    }
    
    @Test
    void parseWithNullSqlQueryReturnsErrorResult() throws Exception {
    	QueryParserResult actual = new QueryParser().parse(null);
    	
    	assertThat(actual, is(sameInstance(QueryParserResult.ERROR_RESULT)));  	
    }
    
    @Test
    void parseWithCreateTableQueryReturnResultWithExpectedTableName() {
        QueryParserResult actual = new QueryParser().parse("create table SETTINGS");
        
        assertThat(actual.getTableName(), is("SETTINGS"));
    }

    @Test
    void parseWithCreateQueryReturnTestTableName() {
        QueryParserResult actual = new QueryParser().parse(
        		"create table CLUSTERNODES (ID varchar(512) not null primary key, MODIFIED bigint, HASBINARY smallint, " +
                    "DELETEDONCE smallint, MODCOUNT bigint, CMODCOUNT bigint, DSIZE bigint, VERSION smallint, SDTYPE smallint, " +
                    "SDMAXREVTIME bigint, DATA varchar(16384), BDATA blob(1073741824))");
        
        assertThat(actual.getTableName(), is("CLUSTERNODES"));
    }

    @Test
    void parseWithInsertQueryReturnTestTableName() {
    	QueryParserResult actual = new QueryParser().parse(
    			"INSERT INTO PRODUCTS( ContactName, Address, City, PostalCode, Country)\n" +
    		            "VALUES ('Tom B. Erichsen', 'Skagen 21', 'Stavanger', 200.350, 'Germany')");
    	
    	assertThat(actual.getTableName(), is("PRODUCTS"));
    }

    @Test
    void parseWithSelectQueryReturnTestTableName() {
    	QueryParserResult actual = new QueryParser().parse("select * from expected");
    	
    	assertThat(actual.getTableName(), is("expected"));
    	
    }

    @Test
    void parseWithSelectQueryHavingWhereClauseReturnTestTableName() {
    	QueryParserResult actual = new QueryParser().parse("select ID from DATASTORE_DATA where ID = '0'");
    	
    	assertThat(actual.getTableName(), is("DATASTORE_DATA"));
    	
    }

    @Test
    void parseWithInvalidSelectQueryReturnsException() {
    	QueryParserResult actual = new QueryParser().parse("select ID from DATASTORE_DATA where ID = '0',");
    	
    	assertThat(actual.getTableName(), is(StringUtils.EMPTY));
    }

    @Test
    public void parseWithInsertQueryReturnsInsertObject() throws Exception {
        Statement statement = CCJSqlParserUtil.parse(
        "INSERT INTO PRODUCTS( ContactName, Address, City, PostalCode, Country)\n" +
    		            "VALUES ('Tom B. Erichsen', 'Skagen 21', 'Stavanger', 200.350, 'Germany')");
        
        assertThat(statement, instanceOf(Insert.class));

    }

    @Test
    public void parseWithCreateTableSqlQueryWillNotReturnInsertObject() throws Exception {
        Statement statement = CCJSqlParserUtil.parse(
        "create table CLUSTERNODES (ID varchar(512) not null primary key, MODIFIED bigint, HASBINARY smallint, " +
                    "DELETEDONCE smallint, MODCOUNT bigint, CMODCOUNT bigint, DSIZE bigint, VERSION smallint, SDTYPE smallint, " +
                    "SDMAXREVTIME bigint, DATA varchar(16384), BDATA blob(1073741824))");
        
        assertThat(statement, not(instanceOf(Insert.class)));

    }

    @Test
    public void parseWithInsertSQLQueryWithDataInQueryReturnsData() throws Exception {
        QueryParserResult actual = new QueryParser().parse(
        		"INSERT INTO PRODUCTS( ContactName, Address, City, PostalCode, data)\n" +
                "VALUES ('Tom B. Erichsen', 'Skagen 21', 'Stavanger', 200.350, 'sample Data')");
        
        assertThat(actual.getData(), is("sample Data"));

    }

    @Test
    public void parseWithCreateSQLQueryReturnsNOData() throws Exception {
    	QueryParserResult actual = new QueryParser().parse(
    			"create table CLUSTERNODES (ID varchar(512) not null primary key, MODIFIED bigint, HASBINARY smallint, " +
                        "DELETEDONCE smallint, MODCOUNT bigint, CMODCOUNT bigint, DSIZE bigint, VERSION smallint, SDTYPE smallint, " +
                        "SDMAXREVTIME bigint, DATA varchar(16384), BDATA blob(1073741824))");
    	
    	assertThat(actual.getData(), is(StringUtils.EMPTY));
        
    }

    @Test
    public void parseWithInsertQueryWithoutDataReturnsNoData() throws Exception {
    	QueryParserResult actual = new QueryParser().parse(
    			"INSERT INTO PRODUCTS( ContactName, Address, City, PostalCode, data)\n" +
            "VALUES ('Tom B. Erichsen', 'Skagen 21', 'Stavanger', 200.350)");
    	
    	assertThat(actual.getData(), is(nullValue()));
    }

    @Test
    public void parseWithInsertQueryWithEmptyDataReturnsEmptyData() throws Exception {
    	QueryParserResult actual = new QueryParser().parse(
    			"INSERT INTO PRODUCTS( ContactName, Address, City, PostalCode, data)\n" +
            "VALUES ('Tom B. Erichsen', 'Skagen 21', 'Stavanger', 200.350, '')");
    	
    	assertThat(actual.getData(), is(StringUtils.EMPTY));
    	
    }

    @Test
    public void parseWithInValidInsertQueryReturnsException() {
    	QueryParserResult actual = new QueryParser().parse(
    			"INSERT INTO PRODUCTS( ContactName, Address, City, PostalCode, data,)\n" +
        "VALUES ('Tom B. Erichsen', 'Skagen 21', 'Stavanger', 200.350, '')");
    	
    	assertThat(actual, is(sameInstance(QueryParserResult.ERROR_RESULT))); 
    }


}
