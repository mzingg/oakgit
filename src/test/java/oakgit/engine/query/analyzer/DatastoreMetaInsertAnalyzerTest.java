package oakgit.engine.query.analyzer;

import static oakgit.util.TestHelpers.testValidQueryMatch;
import static org.assertj.core.api.Assertions.assertThat;

import oakgit.UnitTest;
import oakgit.engine.Command;
import oakgit.engine.commands.InsertIntoContainerCommand;
import oakgit.engine.model.DatastoreMetaEntry;
import oakgit.engine.model.PlaceholderData;
import oakgit.engine.query.QueryMatchResult;

class DatastoreMetaInsertAnalyzerTest {

  @UnitTest
  void matchAndCollectWithDatastoreMetaInsertReturnsInterestedMatch() {
    testValidQueryMatch(
        new DatastoreMetaInsertAnalyzer(),
        "insert into DATASTORE_META (ID, LVL, LASTMOD) values (?, ?, ?)");
  }

  @UnitTest
  void matchAndCollectWithNonInsertQueryReturnsNotInterestedMatch() {
    QueryMatchResult actual =
        new DatastoreMetaInsertAnalyzer()
            .matchAndCollect("select * from DATASTORE_META where ID = ?");

    assertThat(actual).isNotNull();
    assertThat(actual.isInterested()).isFalse();
    assertThat(actual.getCommandSupplier()).isNull();
  }

  @UnitTest
  void matchAndCollectWithNullQueryReturnsNotInterestedMatch() {
    QueryMatchResult actual = new DatastoreMetaInsertAnalyzer().matchAndCollect(null);

    assertThat(actual).isNotNull();
    assertThat(actual.isInterested()).isFalse();
    assertThat(actual.getCommandSupplier()).isNull();
  }

  @UnitTest
  void matchAndCollectWithValidQueryReturnsCorrectCommand() {
    QueryMatchResult target =
        testValidQueryMatch(
            new DatastoreMetaInsertAnalyzer(),
            "insert into DATASTORE_META (ID, LVL, LASTMOD) values (?, ?, ?)");

    PlaceholderData placeholderData =
        new PlaceholderData().set(1, "abc123").set(2, 0).set(3, 1636643795L);

    Command actual = target.getCommandSupplier().apply(placeholderData, Integer.MAX_VALUE);

    assertThat(actual).isInstanceOf(InsertIntoContainerCommand.class);
    @SuppressWarnings("unchecked")
    var insertCommand = (InsertIntoContainerCommand<DatastoreMetaEntry>) actual;
    assertThat(insertCommand.getContainerName()).isEqualTo("DATASTORE_META");
    assertThat(insertCommand.getData().getId()).isEqualTo("abc123");
    assertThat(insertCommand.getData().getLvl()).isEqualTo(0);
    assertThat(insertCommand.getData().getLastmod()).isEqualTo(1636643795L);
  }
}
