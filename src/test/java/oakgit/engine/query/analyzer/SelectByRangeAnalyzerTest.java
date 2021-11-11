package oakgit.engine.query.analyzer;

import oakgit.UnitTest;
import oakgit.engine.Command;
import oakgit.engine.commands.SelectFromContainerByIdRangeCommand;
import oakgit.engine.model.DocumentEntry;
import oakgit.engine.model.PlaceholderData;
import oakgit.engine.query.QueryMatchResult;
import oakgit.util.TestHelpers;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

class SelectByRangeAnalyzerTest {

  @SuppressWarnings("unchecked")
  @UnitTest
  void matchAndCollectWithClusternodesAndNoLimitReturnsInterestedMatch() {
    QueryMatchResult target = TestHelpers.testValidQueryMatch(
        new SelectByRangeAnalyzer(),
        "select ID, MODIFIED, MODCOUNT, CMODCOUNT, HASBINARY, DELETEDONCE, VERSION, SDTYPE, SDMAXREVTIME, DATA, BDATA from CLUSTERNODES where ID > ? and ID < ? order by ID"
    );

    PlaceholderData placeholderData = new PlaceholderData().set(1, "0").set(2, "100");
    SelectFromContainerByIdRangeCommand<DocumentEntry> command = (SelectFromContainerByIdRangeCommand<DocumentEntry>) target.getCommandSupplier().apply(
        placeholderData, Integer.MAX_VALUE
    );

    assertThat(command.getContainerName(), is("CLUSTERNODES"));
    assertThat(command.getLimit(), is(Integer.MAX_VALUE));
    assertThat(command.getIdMin(), is("0"));
    assertThat(command.getIdMax(), is("100"));
    List<String> fieldList = command.getResultFieldList();
    assertThat(fieldList.size(), is(11));
    assertThat(fieldList.get(0), is("ID"));
    assertThat(fieldList.get(1), is("MODIFIED"));
    assertThat(fieldList.get(2), is("MODCOUNT"));
    assertThat(fieldList.get(3), is("CMODCOUNT"));
    assertThat(fieldList.get(4), is("HASBINARY"));
    assertThat(fieldList.get(5), is("DELETEDONCE"));
    assertThat(fieldList.get(6), is("VERSION"));
    assertThat(fieldList.get(7), is("SDTYPE"));
    assertThat(fieldList.get(8), is("SDMAXREVTIME"));
    assertThat(fieldList.get(9), is("DATA"));
    assertThat(fieldList.get(10), is("BDATA"));

  }

  @SuppressWarnings("unchecked")
  @UnitTest
  void matchAndCollectWithNodesQueryContainingLimitCommandWithPassedLimit() {
    // Also covers the following queries (different limit)
    // select ID, MODIFIED, MODCOUNT, CMODCOUNT, HASBINARY, DELETEDONCE, VERSION, SDTYPE, SDMAXREVTIME, DATA, BDATA from NODES where ID > ? and ID < ? order by ID FETCH FIRST 101 ROWS ONLY
    QueryMatchResult target = TestHelpers.testValidQueryMatch(
        new SelectByRangeAnalyzer(),
        "select ID, MODIFIED, MODCOUNT, CMODCOUNT, HASBINARY, DELETEDONCE, VERSION, SDTYPE, SDMAXREVTIME, DATA, BDATA from NODES where ID > ? and ID < ? order by ID FETCH FIRST 201 ROWS ONLY"
    );

    PlaceholderData placeholderData = new PlaceholderData().set(1, "0").set(2, "100");
    SelectFromContainerByIdRangeCommand<DocumentEntry> command = (SelectFromContainerByIdRangeCommand<DocumentEntry>) target.getCommandSupplier().apply(
        placeholderData, Integer.MAX_VALUE
    );

    assertThat(command.getContainerName(), is("NODES"));
    assertThat(command.getLimit(), is(201));
    assertThat(command.getIdMin(), is("0"));
    assertThat(command.getIdMax(), is("100"));
    List<String> fieldList = command.getResultFieldList();
    assertThat(fieldList.size(), is(11));
    assertThat(fieldList.get(0), is("ID"));
    assertThat(fieldList.get(1), is("MODIFIED"));
    assertThat(fieldList.get(2), is("MODCOUNT"));
    assertThat(fieldList.get(3), is("CMODCOUNT"));
    assertThat(fieldList.get(4), is("HASBINARY"));
    assertThat(fieldList.get(5), is("DELETEDONCE"));
    assertThat(fieldList.get(6), is("VERSION"));
    assertThat(fieldList.get(7), is("SDTYPE"));
    assertThat(fieldList.get(8), is("SDMAXREVTIME"));
    assertThat(fieldList.get(9), is("DATA"));
    assertThat(fieldList.get(10), is("BDATA"));
  }

  @UnitTest
  void matchAndCollectWithNonSelectQueryReturnsNotInterestedMatch() {
    QueryMatchResult actual = new SelectByRangeAnalyzer().matchAndCollect(
        "select * from SETTINGS where ID = '0'"
    );

    assertThat(actual, is(not(nullValue())));
    assertThat(actual.isInterested(), is(false));
    assertThat(actual.getCommandSupplier(), is(nullValue()));
  }

  @UnitTest
  void matchAndCollectWithNullQueryReturnsNotInterestedMatch() {
    QueryMatchResult actual = new SelectByRangeAnalyzer().matchAndCollect(null);

    assertThat(actual, is(not(nullValue())));
    assertThat(actual.isInterested(), is(false));
    assertThat(actual.getCommandSupplier(), is(nullValue()));
  }

}
