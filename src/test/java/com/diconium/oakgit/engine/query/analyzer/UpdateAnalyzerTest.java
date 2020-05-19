package com.diconium.oakgit.engine.query.analyzer;

import com.diconium.oakgit.UnitTest;

import static com.diconium.oakgit.TestHelpers.testValidQueryMatch;

class UpdateAnalyzerTest {

    @UnitTest
    void matchAndCollectWithClusternodesAndMultipleInExpressionsReturnsInterestedMatch() {
        testValidQueryMatch(
            new UpdateAnalyzer(),
            "update CLUSTERNODES set MODIFIED = case when ? > MODIFIED then ? else MODIFIED end, HASBINARY = ?, DELETEDONCE = ?, MODCOUNT = ?, CMODCOUNT = ?, DSIZE = DSIZE + ?, VERSION = 2, DATA = DATA || CAST(? AS varchar(16384)) where ID = ? and MODCOUNT = ?"
        );
    }

}
