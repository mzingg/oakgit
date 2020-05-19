package com.diconium.oakgit.engine;

import com.diconium.oakgit.UnitTest;
import com.diconium.oakgit.engine.commands.CreateContainerCommand;
import com.diconium.oakgit.engine.commands.ErrorCommand;
import com.diconium.oakgit.engine.commands.InsertIntoContainerCommand;
import com.diconium.oakgit.engine.commands.SelectFromContainerByIdCommand;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;

class CommandFactoryTest {

    public static final String CREATE_TABLE_PATTERN_TEST = "create table CLUSTERNODES (ID varchar(512) not null primary key, MODIFIED bigint, HASBINARY smallint, DELETEDONCE smallint, MODCOUNT bigint, CMODCOUNT bigint, DSIZE bigint, VERSION smallint, SDTYPE smallint, SDMAXREVTIME bigint, DATA varchar(16384), BDATA blob(1073741824))";

    public static final String CREATE_TABLE_COLUMNS_PATTERN_TEST =
        "create table CLUSTERNODES (ID varchar(512) not null primary key, MODIFIED bigint, HASBINARY smallint, " +
            "DELETEDONCE smallint, MODCOUNT bigint, CMODCOUNT bigint, DSIZE bigint, VERSION smallint, SDTYPE smallint, " +
            "SDMAXREVTIME bigint, DATA varchar(16384), BDATA blob(1073741824))";

    public static final String INSERT_INTO_TABLE_PATTERN_TEST = "insert into SETTINGS(ID, MODIFIED, HASBINARY, " +
        "DELETEDONCE, MODCOUNT, CMODCOUNT, DSIZE, VERSION, SDTYPE, SDMAXREVTIME, DATA, BDATA) " +
        "values (?, ?, ?, ?, ?, ?, ?, 2, ?, ?, ?, ?)";

    public static final String GET_BY_ID_FROM_TABLE_PATTERN_TEST = "select * from CLUSTERNODES where ID = '0'";

    public static final String UNAVAILABLE_PATTERN_TEST = "update customers SET ContactName = 'Alfred Schmidt', City= 'Frankfurt' where CustomerID = 1;";

    @UnitTest
    void getCommandForSqlWithCreateContainerPatternReturnsInstanceOfCreateTableCommand() {
        Command commandObj = new CommandFactory().getCommandForSql(CREATE_TABLE_PATTERN_TEST);

        assertThat(commandObj, is(instanceOf(CreateContainerCommand.class)));
    }

    @UnitTest
    void getCommandForSqlWithCreateContainerColumnsPatternReturnsInstanceOfCreateTableCommand() {
        Command commandObj = new CommandFactory().getCommandForSql(CREATE_TABLE_COLUMNS_PATTERN_TEST);

        assertThat(commandObj, is(instanceOf(CreateContainerCommand.class)));
    }

    @UnitTest
    void getCommandForSqlWithInsertContainerPatternReturnInstanceOfInsertTableCommand() {
        Command commandObj = new CommandFactory().getCommandForSql(INSERT_INTO_TABLE_PATTERN_TEST);

        assertThat(commandObj, is(instanceOf(InsertIntoContainerCommand.class)));
    }

    @UnitTest
    void getUpdateCommandWithoutPlaceholderDataReturnsErrorCommand() {
        Command commandObj = new CommandFactory().getCommandForSql(UNAVAILABLE_PATTERN_TEST);

        assertThat(commandObj, is(instanceOf(ErrorCommand.class)));
    }

    @UnitTest
    void getCommandForSqlWithSelectIdFromContainerPatternReturnInstanceOfSelectFromContainerByIdCommand() {
        Command commandObj = new CommandFactory().getCommandForSql(GET_BY_ID_FROM_TABLE_PATTERN_TEST);

        assertThat(commandObj, is(instanceOf(SelectFromContainerByIdCommand.class)));
    }

    @UnitTest
    void getCommandForSqlWithNullReturnErrorCommand() {
        Command commandObj = new CommandFactory().getCommandForSql(null);

        assertThat(commandObj, is(instanceOf(ErrorCommand.class)));
    }

}
