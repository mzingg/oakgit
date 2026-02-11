package oakgit.engine.query.analyzer;

import static org.assertj.core.api.Assertions.assertThat;

import java.sql.ResultSet;
import java.util.List;
import oakgit.UnitTest;
import oakgit.engine.Command;
import oakgit.engine.commands.SelectFromContainerByIdCommand;
import oakgit.engine.model.DocumentEntry;
import oakgit.engine.model.PlaceholderData;
import oakgit.engine.query.QueryMatchResult;
import oakgit.util.TestHelpers;

class SelectByIdAnalyzerTest {

  // select MODIFIED, MODCOUNT, CMODCOUNT, HASBINARY, DELETEDONCE, VERSION, SDTYPE, SDMAXREVTIME,
  // DATA, BDATA from NODES where ID = ?
  // select MODIFIED, MODCOUNT, CMODCOUNT, HASBINARY, DELETEDONCE, VERSION, SDTYPE, SDMAXREVTIME,
  // DATA, BDATA from CLUSTERNODES where ID = ?
  // select MODIFIED, MODCOUNT, CMODCOUNT, HASBINARY, DELETEDONCE, VERSION, SDTYPE, SDMAXREVTIME,
  // DATA, BDATA from SETTINGS where ID = ?

  @UnitTest
  void matchAndCollectWithClusternodesCreateReturnsInterestedMatch() {
    TestHelpers.testValidQueryMatch(
        new SelectByIdAnalyzer(), "select * from CLUSTERNODES where ID = '0'");
  }

  @UnitTest
  void matchAndCollectWithJournalCreateReturnsInterestedMatch() {
    TestHelpers.testValidQueryMatch(
        new SelectByIdAnalyzer(), "select * from JOURNAL where ID = '0'");
  }

  @UnitTest
  void matchAndCollectWithNodeCreateReturnsInterestedMatch() {
    TestHelpers.testValidQueryMatch(new SelectByIdAnalyzer(), "select * from NODES where ID = '0'");
  }

  @UnitTest
  void matchAndCollectWithSettingsCreateReturnsInterestedMatch() {
    TestHelpers.testValidQueryMatch(
        new SelectByIdAnalyzer(), "select * from SETTINGS where ID = '0'");
  }

  @UnitTest
  void matchAndCollectWithDatastoreDataCreateReturnsInterestedMatch() {
    TestHelpers.testValidQueryMatch(
        new SelectByIdAnalyzer(),
        "select ID, MODIFIED, MODCOUNT, CMODCOUNT, HASBINARY, DELETEDONCE, VERSION, SDTYPE,"
            + " SDMAXREVTIME, DATA, BDATA from NODES where ID = ?");
  }

  @UnitTest
  void matchAndCollectWithDatastoreMetaCreateReturnsInterestedMatch() {
    TestHelpers.testValidQueryMatch(
        new SelectByIdAnalyzer(),
        "select MODIFIED, MODCOUNT, CMODCOUNT, HASBINARY, DELETEDONCE, VERSION, SDTYPE,"
            + " SDMAXREVTIME, DATA, BDATA from JOURNAL where ID = ?");
  }

  @UnitTest
  void matchAndCollectWithQueryContaingSpecialFieldExpressionsReturnsInterestedMatch() {
    TestHelpers.testValidQueryMatch(
        new SelectByIdAnalyzer(),
        "select MODIFIED, MODCOUNT, CMODCOUNT, HASBINARY, DELETEDONCE, VERSION, SDTYPE,"
            + " SDMAXREVTIME, case when (MODCOUNT = ? and MODIFIED = ?) then null else DATA end as"
            + " DATA, case when (MODCOUNT = ? and MODIFIED = ?) then null else BDATA end as BDATA"
            + " from NODES where ID = ?");
  }

  @UnitTest
  void
      matchAndCollectWithQueryContaingSpecialFieldExpressionsAndPlaceholderDateReturnsFieldListWithReplacedPlaceholders() {
    QueryMatchResult target =
        new SelectByIdAnalyzer()
            .matchAndCollect(
                "select MODIFIED, MODCOUNT, CMODCOUNT, HASBINARY, DELETEDONCE, VERSION, SDTYPE,"
                    + " SDMAXREVTIME, case when (MODCOUNT = ? and MODIFIED = ?) then null else DATA"
                    + " end as DATA, case when (MODCOUNT = ? and MODIFIED = ?) then null else BDATA"
                    + " end as BDATA from NODES where ID = ?");
    PlaceholderData placeholderData =
        new PlaceholderData()
            .set(1, 1L)
            .set(2, 1589793585L)
            .set(3, 1L)
            .set(4, 1589793585L)
            .set(5, "0:/");

    Command command = target.getCommandSupplier().apply(placeholderData, Integer.MAX_VALUE);
    List<String> actual = ((SelectFromContainerByIdCommand<?>) command).getResultFieldList();

    assertThat(actual).hasSize(10);
    assertThat(actual)
        .containsExactly(
            "MODIFIED",
            "MODCOUNT",
            "CMODCOUNT",
            "HASBINARY",
            "DELETEDONCE",
            "VERSION",
            "SDTYPE",
            "SDMAXREVTIME",
            "case when (MODCOUNT = 1 and MODIFIED = 1589793585) then null else DATA end as DATA",
            "case when (MODCOUNT = 1 and MODIFIED = 1589793585) then null else BDATA end as BDATA");
  }

  @UnitTest
  void
      matchAndCollectWithQueryContaingSpecialFieldExpressionsNotFullfilledAndPlaceholderDataReturnsResultSetWithEvaluatedExpressions()
          throws Exception {
    QueryMatchResult target =
        new SelectByIdAnalyzer()
            .matchAndCollect(
                "select MODIFIED, MODCOUNT, CMODCOUNT, HASBINARY, DELETEDONCE, VERSION, SDTYPE,"
                    + " SDMAXREVTIME, case when (MODCOUNT = ? and MODIFIED = ?) then null else DATA"
                    + " end as DATA, case when (MODCOUNT = ? and MODIFIED = ?) then null else BDATA"
                    + " end as BDATA from NODES where ID = ?");
    PlaceholderData placeholderData =
        new PlaceholderData()
            .set(1, 1L)
            .set(2, 1589793585L)
            .set(3, 1L)
            .set(4, 1589793585L)
            .set(5, "0:/");
    DocumentEntry referenceEntry =
        new DocumentEntry()
            .setId("0:/")
            .setModified(1589793585L)
            .setModCount(2L)
            .setData("testData".getBytes())
            .setBdata("testBigData".getBytes());

    Command command = target.getCommandSupplier().apply(placeholderData, Integer.MAX_VALUE);
    ResultSet actual =
        ((SelectFromContainerByIdCommand<?>) command).buildResult(referenceEntry).toResultSet();

    assertThat(actual.next()).isTrue();
    assertThat(actual.getLong(1)).isEqualTo(1589793585L);
    assertThat(actual.getLong(2)).isEqualTo(2L);
    assertThat(new String(actual.getBytes(9))).isEqualTo("testData");
    assertThat(new String(actual.getBytes(10))).isEqualTo("testBigData");
  }

  @UnitTest
  void
      matchAndCollectWithQueryContaingSpecialFieldExpressionsFullfilledAndPlaceholderDataReturnsResultSetWithEvaluatedExpressions()
          throws Exception {
    QueryMatchResult target =
        new SelectByIdAnalyzer()
            .matchAndCollect(
                "select MODIFIED, MODCOUNT, CMODCOUNT, HASBINARY, DELETEDONCE, VERSION, SDTYPE,"
                    + " SDMAXREVTIME, case when (MODCOUNT = ? and MODIFIED = ?) then null else DATA"
                    + " end as DATA, case when (MODCOUNT = ? and MODIFIED = ?) then null else BDATA"
                    + " end as BDATA from NODES where ID = ?");
    PlaceholderData placeholderData =
        new PlaceholderData()
            .set(1, 1L)
            .set(2, 1589793585L)
            .set(3, 1L)
            .set(4, 1589793585L)
            .set(5, "0:/");
    DocumentEntry referenceEntry =
        new DocumentEntry()
            .setId("0:/")
            .setModified(1589793585L)
            .setModCount(1L)
            .setData("testData".getBytes())
            .setBdata("testBigData".getBytes());

    Command command = target.getCommandSupplier().apply(placeholderData, Integer.MAX_VALUE);
    ResultSet actual =
        ((SelectFromContainerByIdCommand<?>) command).buildResult(referenceEntry).toResultSet();

    assertThat(actual.next()).isTrue();
    assertThat(actual.getLong(1)).isEqualTo(1589793585L);
    assertThat(actual.getLong(2)).isEqualTo(1L);
    assertThat(actual.getBytes(9)).isNull();
    assertThat(actual.getBytes(10)).isNull();
  }

  @UnitTest
  void matchAndCollectWithNonSelectQueryReturnsNotInterestedMatch() {
    QueryMatchResult actual =
        new SelectByIdAnalyzer()
            .matchAndCollect(
                "select ID, MODIFIED, MODCOUNT, CMODCOUNT, HASBINARY, DELETEDONCE, VERSION, SDTYPE,"
                    + " SDMAXREVTIME, DATA, BDATA from CLUSTERNODES where ID > ? and ID < ? order"
                    + " by ID");

    assertThat(actual).isNotNull();
    assertThat(actual.isInterested()).isFalse();
    assertThat(actual.getCommandSupplier()).isNull();
  }

  @UnitTest
  void matchAndCollectWithNullQueryReturnsNotInterestedMatch() {
    QueryMatchResult actual = new SelectByIdAnalyzer().matchAndCollect(null);

    assertThat(actual).isNotNull();
    assertThat(actual.isInterested()).isFalse();
    assertThat(actual.getCommandSupplier()).isNull();
  }
}
