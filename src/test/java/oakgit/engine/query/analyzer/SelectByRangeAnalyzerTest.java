package oakgit.engine.query.analyzer;

import static org.assertj.core.api.Assertions.assertThat;

import oakgit.UnitTest;
import oakgit.engine.commands.SelectFromContainerByIdRangeCommand;
import oakgit.engine.model.DocumentEntry;
import oakgit.engine.model.PlaceholderData;
import oakgit.engine.query.QueryMatchResult;
import oakgit.util.TestHelpers;

class SelectByRangeAnalyzerTest {

  @SuppressWarnings("unchecked")
  @UnitTest
  void matchAndCollectWithClusternodesAndNoLimitReturnsInterestedMatch() {
    QueryMatchResult target =
        TestHelpers.testValidQueryMatch(
            new SelectByRangeAnalyzer(),
            "select ID, MODIFIED, MODCOUNT, CMODCOUNT, HASBINARY, DELETEDONCE, VERSION, SDTYPE,"
                + " SDMAXREVTIME, DATA, BDATA from CLUSTERNODES where ID > ? and ID < ? order by"
                + " ID");

    PlaceholderData placeholderData = new PlaceholderData().set(1, "0").set(2, "100");
    SelectFromContainerByIdRangeCommand<DocumentEntry> command =
        (SelectFromContainerByIdRangeCommand<DocumentEntry>)
            target.getCommandSupplier().apply(placeholderData, Integer.MAX_VALUE);

    assertThat(command.getContainerName()).isEqualTo("CLUSTERNODES");
    assertThat(command.getLimit()).isEqualTo(Integer.MAX_VALUE);
    assertThat(command.getIdMin()).isEqualTo("0");
    assertThat(command.getIdMax()).isEqualTo("100");
    assertThat(command.getResultFieldList())
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

  @SuppressWarnings("unchecked")
  @UnitTest
  void matchAndCollectWithNodesQueryContainingLimitCommandWithPassedLimit() {
    // Also covers the following queries (different limit)
    // select ID, MODIFIED, MODCOUNT, CMODCOUNT, HASBINARY, DELETEDONCE, VERSION, SDTYPE,
    // SDMAXREVTIME, DATA, BDATA from NODES where ID > ? and ID < ? order by ID FETCH FIRST 101 ROWS
    // ONLY
    QueryMatchResult target =
        TestHelpers.testValidQueryMatch(
            new SelectByRangeAnalyzer(),
            "select ID, MODIFIED, MODCOUNT, CMODCOUNT, HASBINARY, DELETEDONCE, VERSION, SDTYPE,"
                + " SDMAXREVTIME, DATA, BDATA from NODES where ID > ? and ID < ? order by ID FETCH"
                + " FIRST 201 ROWS ONLY");

    PlaceholderData placeholderData = new PlaceholderData().set(1, "0").set(2, "100");
    SelectFromContainerByIdRangeCommand<DocumentEntry> command =
        (SelectFromContainerByIdRangeCommand<DocumentEntry>)
            target.getCommandSupplier().apply(placeholderData, Integer.MAX_VALUE);

    assertThat(command.getContainerName()).isEqualTo("NODES");
    assertThat(command.getLimit()).isEqualTo(201);
    assertThat(command.getIdMin()).isEqualTo("0");
    assertThat(command.getIdMax()).isEqualTo("100");
    assertThat(command.getResultFieldList())
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

  @UnitTest
  void matchAndCollectWithNonSelectQueryReturnsNotInterestedMatch() {
    QueryMatchResult actual =
        new SelectByRangeAnalyzer().matchAndCollect("select * from SETTINGS where ID = '0'");

    assertThat(actual).isNotNull();
    assertThat(actual.isInterested()).isFalse();
    assertThat(actual.getCommandSupplier()).isNull();
  }

  @UnitTest
  void matchAndCollectWithNullQueryReturnsNotInterestedMatch() {
    QueryMatchResult actual = new SelectByRangeAnalyzer().matchAndCollect(null);

    assertThat(actual).isNotNull();
    assertThat(actual.isInterested()).isFalse();
    assertThat(actual.getCommandSupplier()).isNull();
  }
}
