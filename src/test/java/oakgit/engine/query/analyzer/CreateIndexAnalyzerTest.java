package oakgit.engine.query.analyzer;

import static oakgit.util.TestHelpers.testValidQueryMatch;
import static org.assertj.core.api.Assertions.assertThat;

import oakgit.UnitTest;
import oakgit.engine.Command;
import oakgit.engine.commands.CreateIndexCommand;
import oakgit.engine.model.PlaceholderData;
import oakgit.engine.query.QueryMatchResult;

class CreateIndexAnalyzerTest {

  @UnitTest
  void matchAndCollectWithModifiedIndexReturnsInterestedMatch() {
    testValidQueryMatch(new CreateIndexAnalyzer(), "create index NODES_MOD on NODES (MODIFIED)");
  }

  @UnitTest
  void matchAndCollectWithVersionIndexReturnsInterestedMatch() {
    testValidQueryMatch(
        new CreateIndexAnalyzer(), "create index CLUSTERNODES_VSN on CLUSTERNODES (VERSION)");
  }

  @UnitTest
  void matchAndCollectWithSdtypeIndexReturnsInterestedMatch() {
    testValidQueryMatch(new CreateIndexAnalyzer(), "create index JOURNAL_SDT on JOURNAL (SDTYPE)");
  }

  @UnitTest
  void matchAndCollectWithSdmaxrevtimeIndexReturnsInterestedMatch() {
    testValidQueryMatch(
        new CreateIndexAnalyzer(), "create index SETTINGS_SDM on SETTINGS (SDMAXREVTIME)");
  }

  @UnitTest
  void matchAndCollectWithNonIndexQueryReturnsNotInterestedMatch() {
    QueryMatchResult actual =
        new CreateIndexAnalyzer().matchAndCollect("create table NODES (ID varchar(512))");

    assertThat(actual).isNotNull();
    assertThat(actual.isInterested()).isFalse();
    assertThat(actual.getCommandSupplier()).isNull();
  }

  @UnitTest
  void matchAndCollectWithNullQueryReturnsNotInterestedMatch() {
    QueryMatchResult actual = new CreateIndexAnalyzer().matchAndCollect(null);

    assertThat(actual).isNotNull();
    assertThat(actual.isInterested()).isFalse();
    assertThat(actual.getCommandSupplier()).isNull();
  }

  @UnitTest
  void matchAndCollectWithValidQueryReturnsCorrectCommand() {
    QueryMatchResult target =
        testValidQueryMatch(
            new CreateIndexAnalyzer(), "create index NODES_MOD on NODES (MODIFIED)");

    Command actual = target.getCommandSupplier().apply(new PlaceholderData(), Integer.MAX_VALUE);

    assertThat(actual).isInstanceOf(CreateIndexCommand.class);
    var indexCommand = (CreateIndexCommand) actual;
    assertThat(indexCommand.getIndexName()).isEqualTo("NODES_MOD");
    assertThat(indexCommand.getTableName()).isEqualTo("NODES");
  }

  @UnitTest
  void matchAndCollectWithMultiColumnIndexReturnsInterestedMatch() {
    testValidQueryMatch(
        new CreateIndexAnalyzer(), "create index NODES_COMP on NODES (VERSION, SDTYPE)");
  }
}
