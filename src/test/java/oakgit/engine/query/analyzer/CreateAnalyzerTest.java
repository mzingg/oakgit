package oakgit.engine.query.analyzer;

import static org.assertj.core.api.Assertions.assertThat;

import oakgit.UnitTest;
import oakgit.engine.Command;
import oakgit.engine.commands.CreateContainerCommand;
import oakgit.engine.model.PlaceholderData;
import oakgit.engine.query.QueryMatchResult;
import oakgit.util.TestHelpers;

class CreateAnalyzerTest {

  @UnitTest
  void matchAndCollectWithDatastoreDataCreateReturnsInterestedMatch() {
    TestHelpers.testValidQueryMatch(
        new CreateAnalyzer(),
        "create table DATASTORE_DATA (ID varchar(64) not null primary key, DATA blob)" // this is
        // fine
        );
  }

  @UnitTest
  void matchAndCollectWithDatastoreMetaCreateReturnsInterestedMatch() {
    TestHelpers.testValidQueryMatch(
        new CreateAnalyzer(),
        "create table DATASTORE_META (ID varchar(64) not null primary key, LVL int,"
            + " LASTMOD bigint)" // this is fine
        );
  }

  @UnitTest
  void matchAndCollectWithClusternodesCreateReturnsInterestedMatch() {
    TestHelpers.testValidQueryMatch(
        new CreateAnalyzer(),
        "create table CLUSTERNODES (ID varchar(512) not null primary key, MODIFIED bigint,"
            + " HASBINARY smallint, DELETEDONCE smallint, MODCOUNT bigint, CMODCOUNT bigint, DSIZE"
            + " bigint, VERSION smallint, SDTYPE smallint, SDMAXREVTIME bigint, DATA"
            + " varchar(16384), BDATA blob(1073741824))" // this is fine
        );
  }

  @UnitTest
  void matchAndCollectWithJournalCreateReturnsInterestedMatch() {
    TestHelpers.testValidQueryMatch(
        new CreateAnalyzer(),
        "create table JOURNAL (ID varchar(512) not null primary key, MODIFIED bigint, HASBINARY"
            + " smallint, DELETEDONCE smallint, MODCOUNT bigint, CMODCOUNT bigint, DSIZE bigint,"
            + " VERSION smallint, SDTYPE smallint, SDMAXREVTIME bigint, DATA varchar(16384), BDATA"
            + " blob(1073741824))" // this is fine
        );
  }

  @UnitTest
  void matchAndCollectWithNodeCreateReturnsInterestedMatch() {
    TestHelpers.testValidQueryMatch(
        new CreateAnalyzer(),
        "create table NODES (ID varchar(512) not null primary key, MODIFIED bigint, HASBINARY"
            + " smallint, DELETEDONCE smallint, MODCOUNT bigint, CMODCOUNT bigint, DSIZE bigint,"
            + " VERSION smallint, SDTYPE smallint, SDMAXREVTIME bigint, DATA varchar(16384), BDATA"
            + " blob(1073741824))" // this is fine
        );
  }

  @UnitTest
  void matchAndCollectWithSettingsCreateReturnsInterestedMatch() {
    TestHelpers.testValidQueryMatch(
        new CreateAnalyzer(),
        "create table SETTINGS (ID varchar(512) not null primary key, MODIFIED bigint, HASBINARY"
            + " smallint, DELETEDONCE smallint, MODCOUNT bigint, CMODCOUNT bigint, DSIZE bigint,"
            + " VERSION smallint, SDTYPE smallint, SDMAXREVTIME bigint, DATA varchar(16384), BDATA"
            + " blob(1073741824))" // this is fine
        );
  }

  @UnitTest
  void matchAndCollectWithNonCreateQueryReturnsNotInterestedMatch() {
    QueryMatchResult actual =
        new CreateAnalyzer().matchAndCollect("select * from CLUSTERNODES where ID = '0'");

    assertThat(actual).isNotNull();
    assertThat(actual.isInterested()).isFalse();
    assertThat(actual.getCommandSupplier()).isNull();
  }

  @UnitTest
  void matchAndCollectWithNullQueryReturnsNotInterestedMatch() {
    QueryMatchResult actual = new CreateAnalyzer().matchAndCollect(null);

    assertThat(actual).isNotNull();
    assertThat(actual.isInterested()).isFalse();
    assertThat(actual.getCommandSupplier()).isNull();
  }

  @UnitTest
  void matchAndCollectReturnsWithValidQueryReturnCorrectCommand() {
    QueryMatchResult target =
        new CreateAnalyzer()
            .matchAndCollect(
                "create table NODES (ID varchar(512) not null primary key, MODIFIED bigint,"
                    + " HASBINARY smallint, DELETEDONCE smallint, MODCOUNT bigint, CMODCOUNT"
                    + " bigint, DSIZE bigint, VERSION smallint, SDTYPE smallint, SDMAXREVTIME"
                    + " bigint, DATA varchar(16384), BDATA blob(1073741824))"); // this is fine

    Command actual = target.getCommandSupplier().apply(new PlaceholderData(), Integer.MAX_VALUE);

    assertThat(actual).isInstanceOf(CreateContainerCommand.class);
    assertThat(((CreateContainerCommand) actual).getContainerName()).isEqualTo("NODES");
  }
}
