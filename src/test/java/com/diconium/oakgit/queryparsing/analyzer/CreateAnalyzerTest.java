package com.diconium.oakgit.queryparsing.analyzer;

import com.diconium.oakgit.UnitTest;
import com.diconium.oakgit.engine.Command;
import com.diconium.oakgit.engine.commands.CreateContainerCommand;
import com.diconium.oakgit.queryparsing.QueryMatchResult;

import java.util.Collections;

import static com.diconium.oakgit.TestHelpers.testValidQueryMatch;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

class CreateAnalyzerTest {

    @UnitTest
    void matchAndCollectWithDatastoreDataCreateReturnsInterestedMatch() {
        testValidQueryMatch(
            new CreateAnalyzer(), "create table DATASTORE_DATA (ID varchar(64) not null primary key, DATA blob)"
        );
    }

    @UnitTest
    void matchAndCollectWithDatastoreMetaCreateReturnsInterestedMatch() {
        testValidQueryMatch(
            new CreateAnalyzer(), "create table DATASTORE_META (ID varchar(64) not null primary key, LVL int, LASTMOD bigint)"
        );
    }

    @UnitTest
    void matchAndCollectWithClusternodesCreateReturnsInterestedMatch() {
        testValidQueryMatch(
            new CreateAnalyzer(),
            "create table CLUSTERNODES (ID varchar(512) not null primary key, MODIFIED bigint, HASBINARY smallint, DELETEDONCE smallint, MODCOUNT bigint, CMODCOUNT bigint, DSIZE bigint, VERSION smallint, SDTYPE smallint, SDMAXREVTIME bigint, DATA varchar(16384), BDATA blob(1073741824))"
        );
    }

    @UnitTest
    void matchAndCollectWithJournalCreateReturnsInterestedMatch() {
        testValidQueryMatch(
            new CreateAnalyzer(),
            "create table JOURNAL (ID varchar(512) not null primary key, MODIFIED bigint, HASBINARY smallint, DELETEDONCE smallint, MODCOUNT bigint, CMODCOUNT bigint, DSIZE bigint, VERSION smallint, SDTYPE smallint, SDMAXREVTIME bigint, DATA varchar(16384), BDATA blob(1073741824))"
        );
    }

    @UnitTest
    void matchAndCollectWithNodeCreateReturnsInterestedMatch() {
        testValidQueryMatch(
            new CreateAnalyzer(),
            "create table NODES (ID varchar(512) not null primary key, MODIFIED bigint, HASBINARY smallint, DELETEDONCE smallint, MODCOUNT bigint, CMODCOUNT bigint, DSIZE bigint, VERSION smallint, SDTYPE smallint, SDMAXREVTIME bigint, DATA varchar(16384), BDATA blob(1073741824))"
        );
    }

    @UnitTest
    void matchAndCollectWithSettingsCreateReturnsInterestedMatch() {
        testValidQueryMatch(
            new CreateAnalyzer(),
            "create table SETTINGS (ID varchar(512) not null primary key, MODIFIED bigint, HASBINARY smallint, DELETEDONCE smallint, MODCOUNT bigint, CMODCOUNT bigint, DSIZE bigint, VERSION smallint, SDTYPE smallint, SDMAXREVTIME bigint, DATA varchar(16384), BDATA blob(1073741824))"
        );
    }

    @UnitTest
    void matchAndCollectWithNonCreateQueryReturnsNotInterestedMatch() {
        QueryMatchResult actual = new CreateAnalyzer().matchAndCollect("select * from CLUSTERNODES where ID = '0'");

        assertThat(actual, is(not(nullValue())));
        assertThat(actual.isInterested(), is(false));
        assertThat(actual.getCommandSupplier(), is(nullValue()));
    }

    @UnitTest
    void matchAndCollectWithNullQueryReturnsNotInterestedMatch() {
        QueryMatchResult actual = new CreateAnalyzer().matchAndCollect(null);

        assertThat(actual, is(not(nullValue())));
        assertThat(actual.isInterested(), is(false));
        assertThat(actual.getCommandSupplier(), is(nullValue()));
    }

    @UnitTest
    void matchAndCollectReturnsWithValidQueryReturnCorrectCommand() {
        QueryMatchResult target = new CreateAnalyzer().matchAndCollect("create table NODES (ID varchar(512) not null primary key, MODIFIED bigint, HASBINARY smallint, DELETEDONCE smallint, MODCOUNT bigint, CMODCOUNT bigint, DSIZE bigint, VERSION smallint, SDTYPE smallint, SDMAXREVTIME bigint, DATA varchar(16384), BDATA blob(1073741824))");

        Command actual = target.getCommandSupplier().apply(Collections.emptyMap());

        assertThat(actual, is(instanceOf(CreateContainerCommand.class)));
        assertThat(((CreateContainerCommand)actual).getContainerName(), is("NODES"));
    }

}
