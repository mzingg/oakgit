package com.diconium.oakgit.queryparsing.analyzer;

import com.diconium.oakgit.UnitTest;
import com.diconium.oakgit.queryparsing.QueryMatchResult;

import static com.diconium.oakgit.TestHelpers.testValidQueryMatch;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

class SelectByRangeAnalyzerTest {

    @UnitTest
    void matchAndCollectWithClusternodesCreateReturnsInterestedMatch() {
        testValidQueryMatch(
            new SelectByRangeAnalyzer(),
            "select ID, MODIFIED, MODCOUNT, CMODCOUNT, HASBINARY, DELETEDONCE, VERSION, SDTYPE, SDMAXREVTIME, DATA, BDATA from CLUSTERNODES where ID > ? and ID < ? order by ID"
        );
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
