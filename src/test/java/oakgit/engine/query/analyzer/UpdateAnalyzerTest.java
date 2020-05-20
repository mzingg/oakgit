package oakgit.engine.query.analyzer;

import oakgit.UnitTest;
import oakgit.TestHelpers;

class UpdateAnalyzerTest {

    @UnitTest
    void matchAndCollectWithClusternodesAndMultipleInExpressionsReturnsInterestedMatch() {
        TestHelpers.testValidQueryMatch(
            new UpdateAnalyzer(),
            "update CLUSTERNODES set MODIFIED = case when ? > MODIFIED then ? else MODIFIED end, HASBINARY = ?, DELETEDONCE = ?, MODCOUNT = ?, CMODCOUNT = ?, DSIZE = DSIZE + ?, VERSION = 2, DATA = DATA || CAST(? AS varchar(16384)) where ID = ? and MODCOUNT = ?"
        );
    }

}
