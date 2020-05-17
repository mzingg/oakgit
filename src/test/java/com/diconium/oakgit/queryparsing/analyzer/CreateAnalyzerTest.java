package com.diconium.oakgit.queryparsing.analyzer;

import com.diconium.oakgit.UnitTest;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.create.table.CreateTable;

import java.util.Map;

import static com.diconium.oakgit.TestHelpers.placeholderData;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

class CreateAnalyzerTest {

    @UnitTest
    void interestedInWithNonCreateQueryReturnsFalse() throws Exception {
        Statement statement = CCJSqlParserUtil.parse("SELECT * FROM SETTINGS");

        assertThat(new CreateAnalyzer().interestedIn(statement), is(false));
    }

    @UnitTest
    void interestedInWithCreateQueryReturnsTrue() throws Exception {
        Statement statement = CCJSqlParserUtil.parse("create table SETTINGS");

        assertThat(new CreateAnalyzer().interestedIn(statement), is(true));
    }

    @UnitTest
    void getParserResultCallsQueryParserForWitCreateTableType() throws Exception {
        Statement statement = CCJSqlParserUtil.parse("create table SETTINGS");
        CreateAnalyzer target = spy(new CreateAnalyzer());

        target.getParserResult(statement);

        verify(target).queryParserFor(statement, CreateTable.class);
    }

    @UnitTest
    void getTableNameWithReturnsExpectedValue() throws Exception {
        Statement statement = CCJSqlParserUtil.parse("create table CLUSTERNODES (ID varchar(512) not null primary key, MODIFIED bigint, HASBINARY smallint, " +
            "DELETEDONCE smallint, MODCOUNT bigint, CMODCOUNT bigint, DSIZE bigint, VERSION smallint, SDTYPE smallint, " +
            "SDMAXREVTIME bigint, DATA varchar(16384), BDATA blob(1073741824))");

        assertThat(new CreateAnalyzer().getTableName(statement), is("CLUSTERNODES"));
    }

    @UnitTest
    void getTableNameWithNonCreateQueryThrowsParseException() throws Exception {
        Statement statement = CCJSqlParserUtil.parse("SELECT * FROM SETTINGS");

        assertThrows(IllegalStateException.class, () -> new CreateAnalyzer().getTableName(statement));
    }

    @UnitTest
    void getTableNameWithQueryMissingTableDefinitionReturnTableName() throws Exception {
        Statement statement = CCJSqlParserUtil.parse("create table SETTINGS");

        assertThat(new CreateAnalyzer().getTableName(statement), is("SETTINGS"));
    }

    @UnitTest
    void getIdThrowsUnsupportedOperationException() throws Exception {
        Statement statement = CCJSqlParserUtil.parse("create table SETTINGS");
        Map<Integer, Object> placeholderData = placeholderData();

        assertThrows(UnsupportedOperationException.class, () -> new CreateAnalyzer().getId(statement, placeholderData));
    }
}
