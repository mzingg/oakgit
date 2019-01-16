package com.diconium.oak.commons;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

class QueryParserTest {


    @Test
    void parseWithEmptySqlQueryReturnsErrorResult(){
    	QueryParserResult actual = new QueryParser().parse(StringUtils.EMPTY);
    	
    	assertThat(actual, is(sameInstance(QueryParserResult.ERROR_RESULT)));     	
    }
    
    @Test
    void parseWithNullSqlQueryReturnsErrorResult(){
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
    void parseWithInvalidSelectQueryReturnsEmptyTableName() {
    	QueryParserResult actual = new QueryParser().parse("select ID from DATASTORE_DATA where ID = '0',");
    	
    	assertThat(actual.getTableName(), is(StringUtils.EMPTY));
    }
    
    @Test
    void parseWithInvalidSelectQueryReturnsException() {
    	QueryParserResult actual = new QueryParser().parse("select ID from DATASTORE_DATA where ID = '0',");
    	
    	assertThat(actual, is(sameInstance(QueryParserResult.ERROR_RESULT)));
    }

    @Test
    public void parseWithInsertSQLQueryWithDataInQueryReturnsData(){
        QueryParserResult actual = new QueryParser().parse(
        		"INSERT INTO PRODUCTS( ContactName, Address, City, PostalCode, data)\n" +
                "VALUES ('Tom B. Erichsen', 'Skagen 21', 'Stavanger', 200.350, 'sample Data')");
        
        assertThat(actual.getData(), is("sample Data"));

    }

    @Test
    public void parseWithCreateSQLQueryReturnsNoData(){
    	QueryParserResult actual = new QueryParser().parse(
    			"create table CLUSTERNODES (ID varchar(512) not null primary key, MODIFIED bigint, HASBINARY smallint, " +
                        "DELETEDONCE smallint, MODCOUNT bigint, CMODCOUNT bigint, DSIZE bigint, VERSION smallint, SDTYPE smallint, " +
                        "SDMAXREVTIME bigint, DATA varchar(16384), BDATA blob(1073741824))");
    	
    	assertThat(actual.getData(), is(StringUtils.EMPTY));
        
    }

    @Test
    public void parseWithInsertQueryWithoutDataReturnsNoData(){
    	QueryParserResult actual = new QueryParser().parse(
    			"INSERT INTO PRODUCTS( ContactName, Address, City, PostalCode, data)\n" +
            "VALUES ('Tom B. Erichsen', 'Skagen 21', 'Stavanger', 200.350)");
    	
    	assertThat(actual.getData(), is(nullValue()));
    }

    @Test
    public void parseWithInsertQueryWithEmptyDataReturnsEmptyData(){
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
    
    @Test
    public void parseWithInsertQueryReturnsInsertObject(){
        QueryParserResult actual = new QueryParser().parse(
        		"INSERT INTO PRODUCTS( ContactName, Address, City, PostalCode, data)\n" +
                "VALUES ('Tom B. Erichsen', 'Skagen 21', 'Stavanger', 200.350, 'sample Data')");
        
        assertThat(actual.getType(), is(sameInstance(QueryParserResult.ResultType.INSERT)));
    }
    
    @Test
    public void parseWithCreateQueryReturnsCreateObject(){
        QueryParserResult actual = new QueryParser().parse("create table SETTINGS");
        
        assertThat(actual.getType(), is(sameInstance(QueryParserResult.ResultType.CREATE)));
    }
    
    @Test
    public void parseWithSelectQueryReturnsSelectObject(){
        QueryParserResult actual = new QueryParser().parse("select ID from DATASTORE_DATA where ID = '0'");
        
        assertThat(actual.getType(), is(sameInstance(QueryParserResult.ResultType.SELECT)));
    }
    
    @Test
    public void parseWithDeleteQueryReturnsUnknownObject(){
        QueryParserResult actual = new QueryParser().parse("delete from DATASTORE_DATA where ID = '0'");
        
        assertThat(actual.getType(), is(sameInstance(QueryParserResult.ResultType.UNKNOWN)));
    }
    


}
