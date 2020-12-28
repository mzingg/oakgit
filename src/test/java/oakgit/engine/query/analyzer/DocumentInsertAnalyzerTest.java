package oakgit.engine.query.analyzer;

import oakgit.UnitTest;
import oakgit.engine.query.QueryMatchResult;

import static oakgit.util.TestHelpers.testValidQueryMatch;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

class DocumentInsertAnalyzerTest {

    @UnitTest
    void matchAndCollectWithClusternodesCreateReturnsInterestedMatch() {
        testValidQueryMatch(
                new DocumentInsertAnalyzer(),
                "insert into CLUSTERNODES(ID, MODIFIED, HASBINARY, DELETEDONCE, MODCOUNT, CMODCOUNT, DSIZE, VERSION, SDTYPE, SDMAXREVTIME, DATA, BDATA) values (?, ?, ?, ?, ?, ?, ?,  2, ?, ?, ?, ?)"
        );
    }

    @UnitTest
    void matchAndCollectWithJournalCreateReturnsInterestedMatch() {
        testValidQueryMatch(
                new DocumentInsertAnalyzer(),
                "insert into JOURNAL(ID, MODIFIED, HASBINARY, DELETEDONCE, MODCOUNT, CMODCOUNT, DSIZE, VERSION, SDTYPE, SDMAXREVTIME, DATA, BDATA) values (?, ?, ?, ?, ?, ?, ?,  2, ?, ?, ?, ?)"
        );
    }

    @UnitTest
    void matchAndCollectWithNodeCreateReturnsInterestedMatch() {
        testValidQueryMatch(
                new DocumentInsertAnalyzer(),
                "insert into NODES(ID, MODIFIED, HASBINARY, DELETEDONCE, MODCOUNT, CMODCOUNT, DSIZE, VERSION, SDTYPE, SDMAXREVTIME, DATA, BDATA) values (?, ?, ?, ?, ?, ?, ?,  2, ?, ?, ?, ?)"
        );
    }

    @UnitTest
    void matchAndCollectWithSettingsCreateReturnsInterestedMatch() {
        testValidQueryMatch(
                new DocumentInsertAnalyzer(),
                "insert into SETTINGS(ID, MODIFIED, HASBINARY, DELETEDONCE, MODCOUNT, CMODCOUNT, DSIZE, VERSION, SDTYPE, SDMAXREVTIME, DATA, BDATA) values (?, ?, ?, ?, ?, ?, ?,  2, ?, ?, ?, ?)"
        );
    }

    @UnitTest
    void matchAndCollectWithNonInsertQueryReturnsNotInterestedMatch() {
        QueryMatchResult actual = new DocumentInsertAnalyzer().matchAndCollect("select * from CLUSTERNODES where ID = '0'");

        assertThat(actual, is(not(nullValue())));
        assertThat(actual.isInterested(), is(false));
        assertThat(actual.getCommandSupplier(), is(nullValue()));
    }

    @UnitTest
    void matchAndCollectWithNullQueryReturnsNotInterestedMatch() {
        QueryMatchResult actual = new DocumentInsertAnalyzer().matchAndCollect(null);

        assertThat(actual, is(not(nullValue())));
        assertThat(actual.isInterested(), is(false));
        assertThat(actual.getCommandSupplier(), is(nullValue()));
    }

}
