package oakgit.engine.query.analyzer;

import oakgit.UnitTest;
import oakgit.engine.Command;
import oakgit.engine.commands.SelectFromContainerByIdRangeCommand;
import oakgit.engine.model.PlaceholderData;
import oakgit.engine.query.QueryMatchResult;
import oakgit.util.TestHelpers;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

class SelectByRangeAnalyzerTest {

  @UnitTest
  void matchAndCollectWithClusternodesCreateReturnsInterestedMatch() {
    TestHelpers.testValidQueryMatch(
        new SelectByRangeAnalyzer(),
        "select ID, MODIFIED, MODCOUNT, CMODCOUNT, HASBINARY, DELETEDONCE, VERSION, SDTYPE, SDMAXREVTIME, DATA, BDATA from CLUSTERNODES where ID > ? and ID < ? order by ID"
    );
  }

  @Test
  void matchAndCollectWithNodesQueryContainingLimitReturnsInterestedMatch() {
    TestHelpers.testValidQueryMatch(
        new SelectByRangeAnalyzer(),
        "select ID, MODIFIED, MODCOUNT, CMODCOUNT, HASBINARY, DELETEDONCE, VERSION, SDTYPE, SDMAXREVTIME, DATA, BDATA from NODES where ID > ? and ID < ? order by ID FETCH FIRST 201 ROWS ONLY"
    );
  }

  @Test
  void matchAndCollectWithNodesQueryContainingLimitCommandWithPassedLimit() {
    QueryMatchResult target = new SelectByRangeAnalyzer().matchAndCollect(
        "select ID, MODIFIED, MODCOUNT, CMODCOUNT, HASBINARY, DELETEDONCE, VERSION, SDTYPE, SDMAXREVTIME, DATA, BDATA from NODES where ID > ? and ID < ? order by ID FETCH FIRST 201 ROWS ONLY"
    );

    Command actual = target.getCommandSupplier().apply(new PlaceholderData().set(1, "0").set(2, "1"));

    assertThat(actual, instanceOf(SelectFromContainerByIdRangeCommand.class));
    assertThat(((SelectFromContainerByIdRangeCommand)actual).getLimit(), is(201));
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
