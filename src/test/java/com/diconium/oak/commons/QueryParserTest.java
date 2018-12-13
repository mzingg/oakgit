package com.diconium.oak.commons;

import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.insert.Insert;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

class QueryParserTest {

    public static final String CREATE_TABLE_PATTERN_TEST = "create table SETTINGS";

    public static final String CREATE_TABLE_COLUMNS_PATTERN_TEST =
            "create table CLUSTERNODES (ID varchar(512) not null primary key, MODIFIED bigint, HASBINARY smallint, " +
                    "DELETEDONCE smallint, MODCOUNT bigint, CMODCOUNT bigint, DSIZE bigint, VERSION smallint, SDTYPE smallint, " +
                    "SDMAXREVTIME bigint, DATA varchar(16384), BDATA blob(1073741824))";

    public static final String INSERT_QUERY_PATTERN_TEST = ("INSERT INTO PRODUCTS( ContactName, Address, City, PostalCode, Country)\n" +
            "VALUES ('Tom B. Erichsen', 'Skagen 21', 'Stavanger', 200.350, 'Germany')");

    public static final String INSERT_QUERY_PATTERN_WITH_DADA_TEST = ("INSERT INTO PRODUCTS( ContactName, Address, City, PostalCode, data)\n" +
            "VALUES ('Tom B. Erichsen', 'Skagen 21', 'Stavanger', 200.350, 'sample Data')");

    public static final String INSERT_QUERY_PATTERN_WITH_NO_DADA_TEST = ("INSERT INTO PRODUCTS( ContactName, Address, City, PostalCode, data)\n" +
            "VALUES ('Tom B. Erichsen', 'Skagen 21', 'Stavanger', 200.350)");


    public static final String INSERT_QUERY_PATTERN_WITH_EMPTY_DADA_TEST = ("INSERT INTO PRODUCTS( ContactName, Address, City, PostalCode, data)\n" +
            "VALUES ('Tom B. Erichsen', 'Skagen 21', 'Stavanger', 200.350, '')");

    public static final String INVALID_INSERT_QUERY_PATTERN_TEST = ("INSERT INTO PRODUCTS( ContactName, Address, City, PostalCode, data,)\n" +
            "VALUES ('Tom B. Erichsen', 'Skagen 21', 'Stavanger', 200.350, '')");


    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void getTableNameTest() {
        String expectedContainerName = "SETTINGS";
        String containerName = new QueryParser().parse(CREATE_TABLE_PATTERN_TEST).getTableName();
        assertThat(containerName, is(expectedContainerName));
    }

    @Test
    void getTableNameTestWithCoumnNames() {
        String expectedContainerName = "CLUSTERNODES";
        String containerName = new QueryParser().parse(CREATE_TABLE_COLUMNS_PATTERN_TEST).getTableName();
        assertThat(containerName, is(expectedContainerName));
    }

    @Test
    void getTableNameTestWithInsertQuery() {
        String expectedContainerName = "PRODUCTS";
        String containerName = new QueryParser().parse(INSERT_QUERY_PATTERN_WITH_EMPTY_DADA_TEST).getTableName();
        assertThat(containerName, is(expectedContainerName));
    }

    @Test
    void getTableNameTestWithSelectQuery() {
        String query = "select * from expected";

        String containerName = new QueryParser().parse(query).getTableName();
        assertThat(containerName, is("expected"));
    }

    @Test
    void getTableNameTestWithSelectQueryHavingWhereClause() {
        String query = "select ID from DATASTORE_DATA where ID = '0'";

        String containerName = new QueryParser().parse(query).getTableName();
        assertThat(containerName, is("DATASTORE_DATA"));
    }

    @Test
    void getTableNameTestWithInvalidSelectQueryReturnsException() {
        String query = "select ID from DATASTORE_DATA where ID = '0',";

        String containerName = new QueryParser().parse(query).getTableName();
        assertThat(containerName, is(StringUtils.EMPTY));
    }

    @Test
    public void getStatementFromInsertSqlQueryGivesInsertObject() throws Exception {
        Statement statement = CCJSqlParserUtil.parse(INSERT_QUERY_PATTERN_TEST);
        assertThat(statement, instanceOf(Insert.class));

    }

    @Test
    public void getStatementFromCreateTableSqlQueryWillNotGivesInsertObject() throws Exception {
        Statement statement = CCJSqlParserUtil.parse(CREATE_TABLE_COLUMNS_PATTERN_TEST);
        assertThat(statement, not(instanceOf(Insert.class)));

    }

    @Test
    public void getDataWithProperInsertSQLQueryWithDataInQueryReturnsData() throws Exception {
        String data = QueryParser.getData(INSERT_QUERY_PATTERN_WITH_DADA_TEST);
        assertThat(data, is("sample Data"));

    }

    @Test
    public void getDataWithCreateSQLQueryReturnsNOData() throws Exception {
        String data = QueryParser.getData(CREATE_TABLE_COLUMNS_PATTERN_TEST);
        assertThat(data, is(StringUtils.EMPTY));

    }

    @Test
    public void getDataWithInsertQueryWithoutDataReturnsNoData() throws Exception {
        String data = QueryParser.getData(INSERT_QUERY_PATTERN_WITH_NO_DADA_TEST);
        assertThat(data, is(nullValue()));

    }

    @Test
    public void getDataWithInsertQueryWithEmptyDataReturnsEmptyData() throws Exception {
        String data = QueryParser.getData(INSERT_QUERY_PATTERN_WITH_EMPTY_DADA_TEST);
        assertThat(data, is(StringUtils.EMPTY));

    }

    @Test
    public void getDataWithInValidInsertQueryReturnsException() {

        Assertions.assertThrows(JSQLParserException.class, () -> {
            String data = QueryParser.getData(INVALID_INSERT_QUERY_PATTERN_TEST);
        });
    }


}
