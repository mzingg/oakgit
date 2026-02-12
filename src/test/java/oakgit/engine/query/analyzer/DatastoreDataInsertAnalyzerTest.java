package oakgit.engine.query.analyzer;

import static oakgit.util.TestHelpers.testValidQueryMatch;
import static org.assertj.core.api.Assertions.assertThat;

import oakgit.UnitTest;
import oakgit.engine.Command;
import oakgit.engine.commands.InsertIntoContainerCommand;
import oakgit.engine.model.DatastoreDataEntry;
import oakgit.engine.model.PlaceholderData;
import oakgit.engine.query.QueryMatchResult;

class DatastoreDataInsertAnalyzerTest {

  @UnitTest
  void matchAndCollectWithDatastoreDataInsertReturnsInterestedMatch() {
    testValidQueryMatch(
        new DatastoreDataInsertAnalyzer(), "insert into DATASTORE_DATA (ID, DATA) values (?, ?)");
  }

  @UnitTest
  void matchAndCollectWithNonInsertQueryReturnsNotInterestedMatch() {
    QueryMatchResult actual =
        new DatastoreDataInsertAnalyzer()
            .matchAndCollect("select * from DATASTORE_DATA where ID = ?");

    assertThat(actual).isNotNull();
    assertThat(actual.isInterested()).isFalse();
    assertThat(actual.getCommandSupplier()).isNull();
  }

  @UnitTest
  void matchAndCollectWithNullQueryReturnsNotInterestedMatch() {
    QueryMatchResult actual = new DatastoreDataInsertAnalyzer().matchAndCollect(null);

    assertThat(actual).isNotNull();
    assertThat(actual.isInterested()).isFalse();
    assertThat(actual.getCommandSupplier()).isNull();
  }

  @UnitTest
  void matchAndCollectWithValidQueryReturnsCorrectCommand() {
    QueryMatchResult target =
        testValidQueryMatch(
            new DatastoreDataInsertAnalyzer(),
            "insert into DATASTORE_DATA (ID, DATA) values (?, ?)");

    PlaceholderData placeholderData =
        new PlaceholderData().set(1, "abc123").set(2, "binarydata".getBytes());

    Command actual = target.getCommandSupplier().apply(placeholderData, Integer.MAX_VALUE);

    assertThat(actual).isInstanceOf(InsertIntoContainerCommand.class);
    @SuppressWarnings("unchecked")
    var insertCommand = (InsertIntoContainerCommand<DatastoreDataEntry>) actual;
    assertThat(insertCommand.getContainerName()).isEqualTo("DATASTORE_DATA");
    assertThat(insertCommand.getData().getId()).isEqualTo("abc123");
    assertThat(insertCommand.getData().getData()).isEqualTo("binarydata".getBytes());
  }
}
