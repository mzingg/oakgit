package oakgit.engine.query.analyzer;

import static oakgit.util.TestHelpers.testValidQueryMatch;
import static org.assertj.core.api.Assertions.assertThat;

import oakgit.UnitTest;
import oakgit.engine.query.QueryMatchResult;

class SelectBySdtypeAnalyzerTest {

  @UnitTest
  void matchAndCollectWithNodesRevisionGcQueryReturnsInterestedMatch() {
    testValidQueryMatch(
        new SelectBySdtypeAnalyzer(),
        "select ID, MODIFIED, MODCOUNT, CMODCOUNT, HASBINARY, DELETEDONCE, VERSION, SDTYPE,"
            + " SDMAXREVTIME, DATA, BDATA from NODES where SDTYPE in (?, ?, ?)  and SDMAXREVTIME"
            + " <= ? and VERSION >= ?");
  }

  @UnitTest
  void matchAndCollectWithSingleSdtypeReturnsInterestedMatch() {
    testValidQueryMatch(
        new SelectBySdtypeAnalyzer(),
        "select ID, MODIFIED, MODCOUNT, CMODCOUNT, HASBINARY, DELETEDONCE, VERSION, SDTYPE,"
            + " SDMAXREVTIME, DATA, BDATA from NODES where SDTYPE in (?) and SDMAXREVTIME <= ?"
            + " and VERSION >= ?");
  }

  @UnitTest
  void matchAndCollectWithClusternodesReturnsInterestedMatch() {
    testValidQueryMatch(
        new SelectBySdtypeAnalyzer(),
        "select ID, MODIFIED, MODCOUNT, CMODCOUNT, HASBINARY, DELETEDONCE, VERSION, SDTYPE,"
            + " SDMAXREVTIME, DATA, BDATA from CLUSTERNODES where SDTYPE in (?, ?)  and"
            + " SDMAXREVTIME <= ? and VERSION >= ?");
  }

  @UnitTest
  void matchAndCollectWithNonMatchingQueryReturnsNotInterestedMatch() {
    QueryMatchResult actual =
        new SelectBySdtypeAnalyzer().matchAndCollect("select * from NODES where ID = '0'");

    assertThat(actual).isNotNull();
    assertThat(actual.isInterested()).isFalse();
    assertThat(actual.getCommandSupplier()).isNull();
  }

  @UnitTest
  void matchAndCollectWithNullQueryReturnsNotInterestedMatch() {
    QueryMatchResult actual = new SelectBySdtypeAnalyzer().matchAndCollect(null);

    assertThat(actual).isNotNull();
    assertThat(actual.isInterested()).isFalse();
    assertThat(actual.getCommandSupplier()).isNull();
  }
}
