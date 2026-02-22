package oakgit.engine.query.analyzer;

import static oakgit.util.TestHelpers.testValidQueryMatch;
import static org.assertj.core.api.Assertions.assertThat;

import oakgit.UnitTest;
import oakgit.engine.query.QueryMatchResult;

class SelectByVersionUpgradeAnalyzerTest {

  @UnitTest
  void matchAndCollectWithThreeLikePatternsReturnsInterestedMatch() {
    testValidQueryMatch(
        new SelectByVersionUpgradeAnalyzer(),
        "select ID, MODIFIED, MODCOUNT, CMODCOUNT, HASBINARY, DELETEDONCE, VERSION, SDTYPE,"
            + " SDMAXREVTIME, DATA, BDATA from NODES where not (ID like ? or ID like ? or ID like"
            + " ?) and (VERSION is null or VERSION < ?)");
  }

  @UnitTest
  void matchAndCollectWithSingleLikePatternReturnsInterestedMatch() {
    testValidQueryMatch(
        new SelectByVersionUpgradeAnalyzer(),
        "select ID, MODIFIED, MODCOUNT, CMODCOUNT, HASBINARY, DELETEDONCE, VERSION, SDTYPE,"
            + " SDMAXREVTIME, DATA, BDATA from NODES where not (ID like ?) and (VERSION is null"
            + " or VERSION < ?)");
  }

  @UnitTest
  void matchAndCollectWithClusternodesReturnsInterestedMatch() {
    testValidQueryMatch(
        new SelectByVersionUpgradeAnalyzer(),
        "select ID, MODIFIED, MODCOUNT, CMODCOUNT, HASBINARY, DELETEDONCE, VERSION, SDTYPE,"
            + " SDMAXREVTIME, DATA, BDATA from CLUSTERNODES where not (ID like ? or ID like ?)"
            + " and (VERSION is null or VERSION < ?)");
  }

  @UnitTest
  void matchAndCollectWithNonMatchingQueryReturnsNotInterestedMatch() {
    QueryMatchResult actual =
        new SelectByVersionUpgradeAnalyzer().matchAndCollect("select * from NODES where ID = '0'");

    assertThat(actual).isNotNull();
    assertThat(actual.isInterested()).isFalse();
    assertThat(actual.getCommandSupplier()).isNull();
  }

  @UnitTest
  void matchAndCollectWithNullQueryReturnsNotInterestedMatch() {
    QueryMatchResult actual = new SelectByVersionUpgradeAnalyzer().matchAndCollect(null);

    assertThat(actual).isNotNull();
    assertThat(actual.isInterested()).isFalse();
    assertThat(actual.getCommandSupplier()).isNull();
  }
}
