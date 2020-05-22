package oakgit.engine.query.analyzer;

import oakgit.TestHelpers;
import oakgit.UnitTest;
import oakgit.engine.Command;
import oakgit.engine.commands.UpdatDataInContainerCommand;
import oakgit.engine.model.DocumentEntry;
import oakgit.engine.query.QueryMatchResult;

import java.util.HashMap;
import java.util.Map;

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
        Map<Integer, Object> placeHolderData = new HashMap<>();
        placeHolderData.put(1, 1589793585L);
        placeHolderData.put(2, 1589793585L);
        placeHolderData.put(3, null);
        placeHolderData.put(4, null);
        placeHolderData.put(5, 2);
        placeHolderData.put(6, 0);
        placeHolderData.put(7, 132);
        placeHolderData.put(8, ",[[\"*\",\"_commitRoot\",\"r1722714ffe0-0-1\",null],[\"=\",\"_deleted\",\"r1722714ffe0-0-1\",\"false\"],[\"=\",\"_revisions\",\"r1722714ffe0-0-1\",\"c\"]]");
        placeHolderData.put(9, "0:/");
        placeHolderData.put(10, 1L);
        DocumentEntry existing = new DocumentEntry().setDSize(20L);

        Command command = target.getCommandSupplier().apply(placeHolderData);
        ((UpdatDataInContainerCommand) command).getData().update(existing);

        assertThat(command, is(instanceOf(UpdatDataInContainerCommand.class)));
        assertThat(existing.getDSize(), is(152L));
        assertThat(existing.getModified(), is(1589793585L));
    }

}
