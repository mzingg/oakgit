package oakgit.engine.query.analyzer;

import static oakgit.util.TestHelpers.testValidQueryMatch;
import static org.assertj.core.api.Assertions.assertThat;

import oakgit.UnitTest;
import oakgit.engine.Command;
import oakgit.engine.commands.SelectFromContainerByModifiedCommand;
import oakgit.engine.model.DocumentEntry;
import oakgit.engine.model.PlaceholderData;
import oakgit.engine.query.QueryMatchResult;

class SelectByModifiedAnalyzerTest {

  @UnitTest
  void matchAndCollectWithLiteralModifiedValueReturnsInterestedMatch() {
    testValidQueryMatch(
        new SelectByModifiedAnalyzer(),
        "select ID, MODIFIED, MODCOUNT, CMODCOUNT, HASBINARY, DELETEDONCE, VERSION, SDTYPE,"
            + " SDMAXREVTIME, DATA, BDATA from NODES where MODIFIED >= '1636643795' order by ID"
            + " FETCH FIRST 100 ROWS ONLY");
  }

  @UnitTest
  void matchAndCollectWithJournalTableReturnsInterestedMatch() {
    testValidQueryMatch(
        new SelectByModifiedAnalyzer(),
        "select ID, MODIFIED, MODCOUNT, CMODCOUNT, HASBINARY, DELETEDONCE, VERSION, SDTYPE,"
            + " SDMAXREVTIME, DATA, BDATA from JOURNAL where MODIFIED >= '1636643795' order by ID"
            + " FETCH FIRST 100 ROWS ONLY");
  }

  @UnitTest
  void matchAndCollectWithNonSelectQueryReturnsNotInterestedMatch() {
    QueryMatchResult actual =
        new SelectByModifiedAnalyzer().matchAndCollect("select * from NODES where ID = '0'");

    assertThat(actual).isNotNull();
    assertThat(actual.isInterested()).isFalse();
    assertThat(actual.getCommandSupplier()).isNull();
  }

  @UnitTest
  void matchAndCollectWithNullQueryReturnsNotInterestedMatch() {
    QueryMatchResult actual = new SelectByModifiedAnalyzer().matchAndCollect(null);

    assertThat(actual).isNotNull();
    assertThat(actual.isInterested()).isFalse();
    assertThat(actual.getCommandSupplier()).isNull();
  }

  @SuppressWarnings("unchecked")
  @UnitTest
  void matchAndCollectWithLiteralModifiedValueReturnsCorrectCommand() {
    QueryMatchResult target =
        testValidQueryMatch(
            new SelectByModifiedAnalyzer(),
            "select ID, MODIFIED, MODCOUNT, CMODCOUNT, HASBINARY, DELETEDONCE, VERSION, SDTYPE,"
                + " SDMAXREVTIME, DATA, BDATA from NODES where MODIFIED >= '1636643795' order by ID"
                + " FETCH FIRST 100 ROWS ONLY");

    Command actual =
        target.getCommandSupplier().apply(new PlaceholderData().set(1, "dummy"), Integer.MAX_VALUE);

    assertThat(actual).isInstanceOf(SelectFromContainerByModifiedCommand.class);
    var selectCommand = (SelectFromContainerByModifiedCommand<DocumentEntry>) actual;
    assertThat(selectCommand.getContainerName()).isEqualTo("NODES");
    assertThat(selectCommand.getModified()).isEqualTo(1636643795L);
    assertThat(selectCommand.getLimit()).isEqualTo(100);
    assertThat(selectCommand.getResultFieldList())
        .containsExactly(
            "ID",
            "MODIFIED",
            "MODCOUNT",
            "CMODCOUNT",
            "HASBINARY",
            "DELETEDONCE",
            "VERSION",
            "SDTYPE",
            "SDMAXREVTIME",
            "DATA",
            "BDATA");
  }
}
