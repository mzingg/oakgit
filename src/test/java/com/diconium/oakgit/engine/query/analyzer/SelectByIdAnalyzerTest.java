package com.diconium.oakgit.engine.query.analyzer;

import com.diconium.oakgit.UnitTest;
import com.diconium.oakgit.engine.query.QueryMatchResult;

import static com.diconium.oakgit.TestHelpers.testValidQueryMatch;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

class SelectByIdAnalyzerTest {

    @UnitTest
    void matchAndCollectWithClusternodesCreateReturnsInterestedMatch() {
        testValidQueryMatch(
            new SelectByIdAnalyzer(),
            "select * from CLUSTERNODES where ID = '0'"
        );
    }

    @UnitTest
    void matchAndCollectWithJournalCreateReturnsInterestedMatch() {
        testValidQueryMatch(
            new SelectByIdAnalyzer(),
            "select * from JOURNAL where ID = '0'"
        );
    }

    @UnitTest
    void matchAndCollectWithNodeCreateReturnsInterestedMatch() {
        testValidQueryMatch(
            new SelectByIdAnalyzer(),
            "select * from NODES where ID = '0'"
        );
    }

    @UnitTest
    void matchAndCollectWithSettingsCreateReturnsInterestedMatch() {
        testValidQueryMatch(
            new SelectByIdAnalyzer(),
            "select * from SETTINGS where ID = '0'"
        );
    }

    @UnitTest
    void matchAndCollectWithDatastoreDataCreateReturnsInterestedMatch() {
        testValidQueryMatch(
            new SelectByIdAnalyzer(),
            "select ID from DATASTORE_DATA where ID = '0'"
        );
    }

    @UnitTest
    void matchAndCollectWithDatastoreMetaCreateReturnsInterestedMatch() {
        testValidQueryMatch(
            new SelectByIdAnalyzer(),
            "select ID from DATASTORE_META where ID = '0'"
        );
    }

    @UnitTest
    void matchAndCollectWithQueryContaingSpecialFieldExpressionsReturnsInterestedMatch() {
        testValidQueryMatch(
            new SelectByIdAnalyzer(),
            "select MODIFIED, MODCOUNT, CMODCOUNT, HASBINARY, DELETEDONCE, VERSION, SDTYPE, SDMAXREVTIME, case when (MODCOUNT = ? and MODIFIED = ?) then null else DATA end as DATA, case when (MODCOUNT = ? and MODIFIED = ?) then null else BDATA end as BDATA from NODES where ID = ?"
        );
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
