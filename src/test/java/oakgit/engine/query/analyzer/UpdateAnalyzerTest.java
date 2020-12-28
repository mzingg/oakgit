package oakgit.engine.query.analyzer;

import oakgit.UnitTest;
import oakgit.engine.Command;
import oakgit.engine.commands.UpdatDataInContainerCommand;
import oakgit.engine.model.DocumentEntry;
import oakgit.engine.model.PlaceholderData;
import oakgit.engine.query.QueryMatchResult;
import oakgit.util.TestHelpers;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;

class UpdateAnalyzerTest {

  @UnitTest
  void matchAndCollectWithClusternodesAndMultipleInExpressionsReturnsInterestedMatch() {
    TestHelpers.testValidQueryMatch(
        new UpdateAnalyzer(),
        "update CLUSTERNODES set MODIFIED = case when ? > MODIFIED then ? else MODIFIED end, HASBINARY = ?, DELETEDONCE = ?, MODCOUNT = ?, CMODCOUNT = ?, DSIZE = DSIZE + ?, VERSION = 2, DATA = DATA || CAST(? AS varchar(16384)) where ID = ? and MODCOUNT = ?"
    );
  }

  @UnitTest
  void matchAndCollectWithClusternodesAndMultipleInExpressionsReturnsCommandWithCorrectUpdateSet() {
    QueryMatchResult target = new UpdateAnalyzer().matchAndCollect("update NODES set MODIFIED = case when ? > MODIFIED then ? else MODIFIED end, HASBINARY = ?, DELETEDONCE = ?, MODCOUNT = ?, CMODCOUNT = ?, DSIZE = DSIZE + ?, VERSION = 2, DATA = DATA || CAST(? AS varchar(16384)) where ID = ? and MODCOUNT = ?");
    PlaceholderData placeholderData = new PlaceholderData()
        .set(1, 1589793585L)
        .set(2, 1589793585L)
        .set(3, null)
        .set(4, null)
        .set(5, 2)
        .set(6, 0)
        .set(7, 132)
        .set(8, ",[[\"*\",\"_commitRoot\",\"r1722714ffe0-0-1\",null],[\"=\",\"_deleted\",\"r1722714ffe0-0-1\",\"false\"],[\"=\",\"_revisions\",\"r1722714ffe0-0-1\",\"c\"]]")
        .set(9, "0:/")
        .set(10, 1L);
    DocumentEntry existing = new DocumentEntry().setDSize(20L);

    Command command = target.getCommandSupplier().apply(placeholderData);
    ((UpdatDataInContainerCommand) command).getData().update(existing);

    assertThat(command, is(instanceOf(UpdatDataInContainerCommand.class)));
    assertThat(existing.getDSize(), is(152L));
    assertThat(existing.getModified(), is(1589793585L));
  }

}
