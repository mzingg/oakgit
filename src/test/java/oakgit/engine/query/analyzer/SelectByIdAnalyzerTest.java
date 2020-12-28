package oakgit.engine.query.analyzer;

import oakgit.UnitTest;
import oakgit.engine.Command;
import oakgit.engine.commands.SelectFromContainerByIdCommand;
import oakgit.engine.model.DocumentEntry;
import oakgit.engine.model.PlaceholderData;
import oakgit.engine.query.QueryMatchResult;
import oakgit.util.TestHelpers;

import java.sql.ResultSet;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

class SelectByIdAnalyzerTest {

  @UnitTest
  void matchAndCollectWithClusternodesCreateReturnsInterestedMatch() {
    TestHelpers.testValidQueryMatch(
        new SelectByIdAnalyzer(),
        "select * from CLUSTERNODES where ID = '0'"
    );
  }

  @UnitTest
  void matchAndCollectWithJournalCreateReturnsInterestedMatch() {
    TestHelpers.testValidQueryMatch(
        new SelectByIdAnalyzer(),
        "select * from JOURNAL where ID = '0'"
    );
  }

  @UnitTest
  void matchAndCollectWithNodeCreateReturnsInterestedMatch() {
    TestHelpers.testValidQueryMatch(
        new SelectByIdAnalyzer(),
        "select * from NODES where ID = '0'"
    );
  }

  @UnitTest
  void matchAndCollectWithSettingsCreateReturnsInterestedMatch() {
    TestHelpers.testValidQueryMatch(
        new SelectByIdAnalyzer(),
        "select * from SETTINGS where ID = '0'"
    );
  }

  @UnitTest
  void matchAndCollectWithDatastoreDataCreateReturnsInterestedMatch() {
    TestHelpers.testValidQueryMatch(
        new SelectByIdAnalyzer(),
        "select ID from DATASTORE_DATA where ID = '0'"
    );
  }

  @UnitTest
  void matchAndCollectWithDatastoreMetaCreateReturnsInterestedMatch() {
    TestHelpers.testValidQueryMatch(
        new SelectByIdAnalyzer(),
        "select ID from DATASTORE_META where ID = '0'"
    );
  }

  @UnitTest
  void matchAndCollectWithQueryContaingSpecialFieldExpressionsReturnsInterestedMatch() {
    TestHelpers.testValidQueryMatch(
        new SelectByIdAnalyzer(),
        "select MODIFIED, MODCOUNT, CMODCOUNT, HASBINARY, DELETEDONCE, VERSION, SDTYPE, SDMAXREVTIME, case when (MODCOUNT = ? and MODIFIED = ?) then null else DATA end as DATA, case when (MODCOUNT = ? and MODIFIED = ?) then null else BDATA end as BDATA from NODES where ID = ?"
    );
  }

  @UnitTest
  void matchAndCollectWithQueryContaingSpecialFieldExpressionsAndPlaceholderDateReturnsFieldListWithReplacedPlaceholders() {
    QueryMatchResult target = new SelectByIdAnalyzer().matchAndCollect("select MODIFIED, MODCOUNT, CMODCOUNT, HASBINARY, DELETEDONCE, VERSION, SDTYPE, SDMAXREVTIME, case when (MODCOUNT = ? and MODIFIED = ?) then null else DATA end as DATA, case when (MODCOUNT = ? and MODIFIED = ?) then null else BDATA end as BDATA from NODES where ID = ?");
    PlaceholderData placeholderData = new PlaceholderData()
        .set(1, 1L)
        .set(2, 1589793585L)
        .set(3, 1L)
        .set(4, 1589793585L)
        .set(5, "0:/");

    Command command = target.getCommandSupplier().apply(placeholderData);
    List<String> actual = ((SelectFromContainerByIdCommand) command).getResultFieldList();

    assertThat(actual.size(), is(10));
    assertThat(actual.get(0), is("MODIFIED"));
    assertThat(actual.get(1), is("MODCOUNT"));
    assertThat(actual.get(2), is("CMODCOUNT"));
    assertThat(actual.get(3), is("HASBINARY"));
    assertThat(actual.get(4), is("DELETEDONCE"));
    assertThat(actual.get(5), is("VERSION"));
    assertThat(actual.get(6), is("SDTYPE"));
    assertThat(actual.get(7), is("SDMAXREVTIME"));
    assertThat(actual.get(8), is("case when (MODCOUNT = 1 and MODIFIED = 1589793585) then null else DATA end as DATA"));
    assertThat(actual.get(9), is("case when (MODCOUNT = 1 and MODIFIED = 1589793585) then null else BDATA end as BDATA"));
  }

  @UnitTest
  void matchAndCollectWithQueryContaingSpecialFieldExpressionsNotFullfilledAndPlaceholderDataReturnsResultSetWithEvaluatedExpressions() throws Exception {
    QueryMatchResult target = new SelectByIdAnalyzer().matchAndCollect("select MODIFIED, MODCOUNT, CMODCOUNT, HASBINARY, DELETEDONCE, VERSION, SDTYPE, SDMAXREVTIME, case when (MODCOUNT = ? and MODIFIED = ?) then null else DATA end as DATA, case when (MODCOUNT = ? and MODIFIED = ?) then null else BDATA end as BDATA from NODES where ID = ?");
    PlaceholderData placeholderData = new PlaceholderData()
        .set(1, 1L)
        .set(2, 1589793585L)
        .set(3, 1L)
        .set(4, 1589793585L)
        .set(5, "0:/");
    DocumentEntry referenceEntry = new DocumentEntry()
        .setId("0:/")
        .setModified(1589793585L)
        .setModCount(2L)
        .setData("testData".getBytes())
        .setBdata("testBigData".getBytes());

    Command command = target.getCommandSupplier().apply(placeholderData);
    ResultSet actual = ((SelectFromContainerByIdCommand) command).buildResult(DocumentEntry.class, referenceEntry).toResultSet();

    assertThat(actual.next(), is(true));
    assertThat(actual.getLong(1), is(1589793585L));
    assertThat(actual.getLong(2), is(2L));
    assertThat(new String(actual.getBytes(9)), is("testData"));
    assertThat(new String(actual.getBytes(10)), is("testBigData"));
  }

  @UnitTest
  void matchAndCollectWithQueryContaingSpecialFieldExpressionsFullfilledAndPlaceholderDataReturnsResultSetWithEvaluatedExpressions() throws Exception {
    QueryMatchResult target = new SelectByIdAnalyzer().matchAndCollect("select MODIFIED, MODCOUNT, CMODCOUNT, HASBINARY, DELETEDONCE, VERSION, SDTYPE, SDMAXREVTIME, case when (MODCOUNT = ? and MODIFIED = ?) then null else DATA end as DATA, case when (MODCOUNT = ? and MODIFIED = ?) then null else BDATA end as BDATA from NODES where ID = ?");
    PlaceholderData placeholderData = new PlaceholderData()
        .set(1, 1L)
        .set(2, 1589793585L)
        .set(3, 1L)
        .set(4, 1589793585L)
        .set(5, "0:/");
    DocumentEntry referenceEntry = new DocumentEntry()
        .setId("0:/")
        .setModified(1589793585L)
        .setModCount(1L)
        .setData("testData".getBytes())
        .setBdata("testBigData".getBytes());

    Command command = target.getCommandSupplier().apply(placeholderData);
    ResultSet actual = ((SelectFromContainerByIdCommand) command).buildResult(DocumentEntry.class, referenceEntry).toResultSet();

    assertThat(actual.next(), is(true));
    assertThat(actual.getLong(1), is(1589793585L));
    assertThat(actual.getLong(2), is(1L));
    assertThat(actual.getBytes(9), is(nullValue()));
    assertThat(actual.getBytes(10), is(nullValue()));
  }

  @UnitTest
  void matchAndCollectWithNonSelectQueryReturnsNotInterestedMatch() {
    QueryMatchResult actual = new SelectByIdAnalyzer().matchAndCollect(
        "select ID, MODIFIED, MODCOUNT, CMODCOUNT, HASBINARY, DELETEDONCE, VERSION, SDTYPE, SDMAXREVTIME, DATA, BDATA from CLUSTERNODES where ID > ? and ID < ? order by ID"
    );

    assertThat(actual, is(not(nullValue())));
    assertThat(actual.isInterested(), is(false));
    assertThat(actual.getCommandSupplier(), is(nullValue()));
  }

  @UnitTest
  void matchAndCollectWithNullQueryReturnsNotInterestedMatch() {
    QueryMatchResult actual = new SelectByIdAnalyzer().matchAndCollect(null);

    assertThat(actual, is(not(nullValue())));
    assertThat(actual.isInterested(), is(false));
    assertThat(actual.getCommandSupplier(), is(nullValue()));
  }

}
